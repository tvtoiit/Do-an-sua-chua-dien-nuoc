<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container fluid class="pa-6">
    <title>Công việc</title>
    <v-card class="elevation-3 rounded-lg">
      <v-card-title
        class="d-flex align-center py-4 px-6 bg-primary text-white rounded-t-lg"
      >
        <v-icon size="28" class="mr-3">mdi-clipboard-text</v-icon>
        <span class="text-h5">Công việc được giao</span>
        <v-spacer />
        <v-btn
          prepend-icon="mdi-refresh"
          variant="tonal"
          color="white"
          size="small"
          class="text-caption"
          @click="refreshJobs"
        >
          Làm mới
        </v-btn>
      </v-card-title>

      <v-card-text class="pa-0">
        <!-- Filter section -->
        <v-row class="px-6 py-3 align-center">
          <v-col cols="12" sm="4">
            <v-select
              v-model="statusFilter"
              :items="statusOptions"
              label="Lọc theo trạng thái"
              variant="outlined"
              density="compact"
              hide-details
              clearable
              class="rounded-lg"
            />
          </v-col>
          <v-col cols="12" sm="5">
            <v-text-field
              v-model="searchQuery"
              prepend-inner-icon="mdi-magnify"
              label="Tìm kiếm thiết bị, dịch vụ hoặc khách hàng"
              variant="outlined"
              density="compact"
              hide-details
              clearable
              class="rounded-lg"
            />
          </v-col>
          <v-col cols="12" sm="3">
            <v-select
              v-model="sortBy"
              :items="sortOptions"
              label="Sắp xếp theo"
              variant="outlined"
              density="compact"
              hide-details
              class="rounded-lg"
            />
          </v-col>
        </v-row>

        <!-- Stats summary -->
        <v-row class="px-6 mb-2">
          <v-col v-for="stat in jobStats" :key="stat.label" cols="6" sm="3">
            <v-card variant="flat" class="pa-3 rounded-lg" :color="stat.color">
              <div class="d-flex align-center">
                <v-avatar :color="stat.avatarColor" size="40" class="mr-3">
                  <v-icon dark>{{ stat.icon }}</v-icon>
                </v-avatar>
                <div>
                  <div class="text-h5 font-weight-bold">{{ stat.count }}</div>
                  <div class="text-caption text-medium-emphasis">
                    {{ stat.label }}
                  </div>
                </div>
              </div>
            </v-card>
          </v-col>
        </v-row>

        <!-- Data table for jobs -->
        <div v-if="loading" class="d-flex justify-center align-center my-8">
          <v-progress-circular indeterminate color="primary" size="64" />
        </div>

        <div
          v-else-if="filteredJobs.length === 0"
          class="d-flex flex-column align-center justify-center my-12"
        >
          <v-icon size="64" color="grey-lighten-1"
            >mdi-clipboard-text-off</v-icon
          >
          <div class="text-subtitle-1 mt-4 text-grey">
            {{
              statusFilter
                ? `Không có công việc nào ở trạng thái "${statusFilter}"`
                : "Không tìm thấy công việc nào"
            }}
          </div>
          <v-btn
            class="mt-4"
            variant="tonal"
            color="primary"
            @click="clearFilters"
          >
            Xóa bộ lọc
          </v-btn>
        </div>

        <v-data-table
          v-else
          :headers="headers"
          :items="filteredJobs"
          :items-per-page="5"
          :items-per-page-options="[5, 10, 20]"
          class="elevation-0 job-table"
        >
          <template #item.appointmentDate="{ item }">
            <div>{{ formatDate(item.appointmentDate) }}</div>
            <div class="text-caption text-grey">
              {{ formatTime(item.appointmentDate) }}
            </div>
          </template>

          <template #item.status="{ item }">
            <v-chip
              :color="getStatusColor(item.status)"
              text-color="white"
              size="small"
              variant="elevated"
              class="font-weight-medium"
            >
              {{ getStatusText(item.status) }}
            </v-chip>
          </template>

          <template #item.service="{ item }">
            <div class="d-flex align-center">
              <v-avatar
                size="28"
                class="mr-2"
                :color="getServiceColor(item.service) + '-lighten-4'"
              >
                <v-icon
                  size="16"
                  :color="getServiceColor(item.service) + '-darken-2'"
                  >{{ getServiceIcon(item.service) }}</v-icon
                >
              </v-avatar>
              {{ item.service }}
            </div>
          </template>

          <template #item.actions="{ item }">
            <div class="d-flex gap-2">
              <v-tooltip location="top" text="Nhận công việc">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    size="small"
                    color="primary"
                    variant="flat"
                    :disabled="item.status !== 'Pending'"
                    @click="acceptJob(item)"
                  >
                    <v-icon>mdi-check-circle</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip location="top" text="Từ chối công việc">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    size="small"
                    color="error"
                    variant="flat"
                    :disabled="
                      item.status === 'Rejected' || item.status === 'Completed'
                    "
                    @click="openRejectDialog(item)"
                  >
                    <v-icon>mdi-close-circle</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip location="top" text="Xem chi tiết">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    size="small"
                    color="info"
                    variant="flat"
                    @click="viewJobDetails(item)"
                  >
                    <v-icon>mdi-eye</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>
            </div>
          </template>

          <!-- Customer column with avatar -->
          <template #item.customerName="{ item }">
            <div class="d-flex align-center">
              <v-avatar size="32" color="grey-lighten-3" class="mr-2">
                <span class="text-caption font-weight-medium">{{
                  getInitials(item.customerName)
                }}</span>
              </v-avatar>
              <div>
                <div>{{ item.customerName }}</div>
                <div v-if="item.customerPhone" class="text-caption text-grey">
                  {{ formatPhone(item.customerPhone) }}
                </div>
              </div>
            </div>
          </template>

          <!-- Appliance column with icon -->
          <template #item.appliance="{ item }">
            <div class="d-flex align-center">
              <v-avatar
                size="32"
                :color="getApplianceColor(item.appliance)"
                class="mr-2"
              >
                <v-icon size="16" color="white">{{
                  getApplianceIcon(item.appliance)
                }}</v-icon>
              </v-avatar>
              {{ item.appliance }}
            </div>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>

    <!-- Popup nhập lý do từ chối -->
    <v-dialog v-model="rejectDialog" max-width="500" class="rounded-lg">
      <v-card class="rounded-lg">
        <v-card-title class="bg-error text-white px-6 py-4 rounded-t-lg">
          <v-icon class="mr-2">mdi-alert-circle</v-icon>
          Từ chối công việc
        </v-card-title>

        <v-card-text class="pa-6">
          <p class="text-body-2 mb-4">
            Vui lòng cung cấp lý do từ chối công việc này:
          </p>
          <v-textarea
            v-model="rejectReason"
            label="Lý do từ chối"
            placeholder="Nhập lý do chi tiết tại đây..."
            variant="outlined"
            rows="3"
            autofocus
            hide-details
            class="rounded-lg"
          />
        </v-card-text>

        <v-card-actions class="pa-6 pt-0">
          <v-spacer />
          <v-btn color="grey" variant="tonal" @click="closeRejectDialog"
            >Hủy</v-btn
          >
          <v-btn
            color="error"
            variant="elevated"
            :disabled="!rejectReason.trim()"
            @click="confirmReject"
          >
            Xác nhận từ chối
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Job detail dialog -->
    <v-dialog v-model="detailDialog" max-width="700" class="rounded-lg">
      <v-card v-if="selectedJob" class="rounded-lg">
        <v-card-title
          class="bg-primary text-white px-6 py-4 rounded-t-lg d-flex align-center"
        >
          <v-icon class="mr-2">mdi-clipboard-text-outline</v-icon>
          Chi tiết công việc
          <v-spacer />
          <v-chip
            :color="getStatusColor(selectedJob.status) + '70'"
            class="font-weight-medium"
          >
            {{ getStatusText(selectedJob.status) }}
          </v-chip>
        </v-card-title>

        <v-card-text class="pa-6">
          <v-row>
            <v-col cols="12" md="6">
              <div class="text-overline text-grey mb-1">Thiết bị</div>
              <div
                class="text-subtitle-1 font-weight-medium d-flex align-center mb-4"
              >
                <v-avatar
                  size="24"
                  :color="getApplianceColor(selectedJob.appliance)"
                  class="mr-2"
                >
                  <v-icon size="14" color="white">{{
                    getApplianceIcon(selectedJob.appliance)
                  }}</v-icon>
                </v-avatar>
                {{ selectedJob.appliance }}
              </div>

              <div class="text-overline text-grey mb-1">Dịch vụ</div>
              <div
                class="text-subtitle-1 font-weight-medium d-flex align-center mb-4"
              >
                <v-avatar
                  size="24"
                  :color="getServiceColor(selectedJob.service) + '-lighten-4'"
                  class="mr-2"
                >
                  <v-icon
                    size="14"
                    :color="getServiceColor(selectedJob.service) + '-darken-2'"
                    >{{ getServiceIcon(selectedJob.service) }}</v-icon
                  >
                </v-avatar>
                {{ selectedJob.service }}
              </div>

              <div class="text-overline text-grey mb-1">Thời gian hẹn</div>
              <div class="text-subtitle-1 font-weight-medium mb-4">
                {{ formatFullDate(selectedJob.appointmentDate) }}
              </div>

              <div class="text-overline text-grey mb-1">Địa chỉ</div>
              <div class="text-subtitle-1 font-weight-medium mb-4">
                {{ selectedJob.address || "123 Đường Lê Lợi, Quận 1, TP.HCM" }}
              </div>
            </v-col>

            <v-col cols="12" md="6">
              <div class="text-overline text-grey mb-1">Khách hàng</div>
              <div class="d-flex align-center mb-4">
                <v-avatar size="40" color="primary" class="mr-3">
                  <span class="text-caption font-weight-medium">{{
                    getInitials(selectedJob.customerName)
                  }}</span>
                </v-avatar>
                <div>
                  <div class="text-subtitle-1 font-weight-medium">
                    {{ selectedJob.customerName }}
                  </div>
                  <div v-if="selectedJob.customerPhone" class="text-caption">
                    {{ formatPhone(selectedJob.customerPhone) }}
                  </div>
                </div>
              </div>

              <div class="text-overline text-grey mb-1">Mô tả vấn đề</div>
              <div
                class="text-subtitle-1 font-weight-medium mb-4 pa-3 bg-grey-lighten-4 rounded-lg"
              >
                {{
                  selectedJob.description ||
                  "Thiết bị không hoạt động bình thường, cần kiểm tra và sửa chữa."
                }}
              </div>

              <div v-if="selectedJob.rejectReason" class="mt-2">
                <div class="text-overline text-error mb-1">Lý do từ chối</div>
                <div class="text-subtitle-1 pa-3 bg-error-lighten-5 rounded-lg">
                  {{ selectedJob.rejectReason }}
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>

        <v-divider />

        <v-card-actions class="pa-6">
          <v-spacer />
          <v-btn color="grey" variant="tonal" @click="detailDialog = false"
            >Đóng</v-btn
          >
          <v-btn
            v-if="selectedJob.status === 'Pending'"
            color="primary"
            variant="elevated"
            @click="acceptJobFromDetail"
          >
            Nhận công việc
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Snackbar notification -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.text }}
      <template #actions>
        <v-btn variant="text" @click="snackbar.show = false"> Đóng </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useCookie } from "#app";

// State
const jobs = ref([]);
const loading = ref(true);
const rejectDialog = ref(false);
const detailDialog = ref(false);
const rejectReason = ref("");
const selectedJob = ref(null);
const searchQuery = ref("");
const statusFilter = ref("");
const sortBy = ref("appointmentDate_asc");
const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

// Options
const statusOptions = [
  { title: "Đang chờ", value: "Pending" },
  { title: "Đang xử lý", value: "Processing" },
  { title: "Hoàn thành", value: "Completed" },
  { title: "Đã từ chối", value: "Rejected" },
];

const sortOptions = [
  { title: "Ngày hẹn (sớm nhất)", value: "appointmentDate_asc" },
  { title: "Ngày hẹn (muộn nhất)", value: "appointmentDate_desc" },
  { title: "Trạng thái", value: "status" },
  { title: "Thiết bị (A-Z)", value: "appliance_asc" },
  { title: "Dịch vụ (A-Z)", value: "service_asc" },
];

const headers = [
  { title: "Thiết bị", align: "start", key: "appliance" },
  { title: "Dịch vụ", align: "start", key: "service" },
  { title: "Ngày hẹn", align: "start", key: "appointmentDate" },
  { title: "Khách hàng", align: "start", key: "customerName" },
  { title: "Trạng thái", align: "start", key: "status", width: "150px" },
  {
    title: "Hành động",
    align: "center",
    key: "actions",
    sortable: false,
    width: "150px",
  },
];

// Get userId from cookie
const customerId = useCookie("userId");
const userId = customerId.value;

// Computed properties
const filteredJobs = computed(() => {
  let result = [...jobs.value];

  // Apply search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (job) =>
        job.appliance.toLowerCase().includes(query) ||
        job.customerName.toLowerCase().includes(query) ||
        job.service.toLowerCase().includes(query)
    );
  }

  // Apply status filter
  if (statusFilter.value) {
    result = result.filter((job) => job.status === statusFilter.value);
  }

  // Apply sorting
  const [sortField, sortDirection] = sortBy.value.split("_");
  result.sort((a, b) => {
    let compareResult = 0;

    if (sortField === "appointmentDate") {
      compareResult = new Date(a.appointmentDate) - new Date(b.appointmentDate);
    } else if (sortField === "appliance") {
      compareResult = a.appliance.localeCompare(b.appliance);
    } else if (sortField === "status") {
      compareResult = a.status.localeCompare(b.status);
    }

    return sortDirection === "desc" ? -compareResult : compareResult;
  });

  return result;
});

// Job statistics
const jobStats = computed(() => {
  const pendingCount = jobs.value.filter(
    (job) => job.status === "Pending"
  ).length;
  const processingCount = jobs.value.filter(
    (job) => job.status === "Processing"
  ).length;
  const completedCount = jobs.value.filter(
    (job) => job.status === "Completed"
  ).length;
  const rejectedCount = jobs.value.filter(
    (job) => job.status === "Rejected"
  ).length;

  return [
    {
      label: "Đang chờ",
      count: pendingCount,
      color: "orange-lighten-5",
      avatarColor: "orange",
      icon: "mdi-clock-outline",
    },
    {
      label: "Đang xử lý",
      count: processingCount,
      color: "blue-lighten-5",
      avatarColor: "blue",
      icon: "mdi-progress-wrench",
    },
    {
      label: "Hoàn thành",
      count: completedCount,
      color: "green-lighten-5",
      avatarColor: "green",
      icon: "mdi-check-circle-outline",
    },
    {
      label: "Đã từ chối",
      count: rejectedCount,
      color: "red-lighten-5",
      avatarColor: "red",
      icon: "mdi-close-circle-outline",
    },
  ];
});

// Methods
const fetchJobs = async () => {
  loading.value = true;
  try {
    const [pendingRes, myJobsRes] = await Promise.all([
      axios.get("http://localhost:9090/api/repair-requests/technician"),
      axios.get(
        `http://localhost:9090/api/repair-requests/technician/${userId}`
      ),
    ]);

    // Gộp danh sách và loại trùng
    const combined = [...pendingRes.data, ...myJobsRes.data].reduce(
      (acc, curr) => {
        if (!acc.some((item) => item.requestId === curr.requestId)) {
          acc.push(curr);
        }
        return acc;
      },
      []
    );

    jobs.value = combined.map((req) => ({
      id: req.requestId,
      appliance: req.appliance?.applianceName || "N/A",
      service: req.service?.serviceName || "N/A",
      appointmentDate: req.appointmentDate,
      customerName: req.customer?.fullName || "Không rõ",
      customerPhone: req.customer?.phoneNumber,
      address: req.address,
      description: req.description,
      status: req.status,
      rejectReason: req.rejectReason,
    }));

    showNotification("Dữ liệu đã được cập nhật", "info");
  } catch (error) {
    console.error("Lỗi khi tải công việc:", error);
    showNotification("Không thể tải dữ liệu. Vui lòng thử lại sau.", "error");
  } finally {
    loading.value = false;
  }
};

const refreshJobs = () => {
  fetchJobs();
};

const getStatusColor = (status) => {
  switch (status) {
    case "Pending":
      return "orange";
    case "Processing":
      return "blue";
    case "Completed":
      return "green";
    case "Rejected":
      return "red";
    default:
      return "grey";
  }
};

const getStatusText = (status) => {
  switch (status) {
    case "Pending":
      return "Đang chờ";
    case "Processing":
      return "Đang xử lý";
    case "Completed":
      return "Hoàn thành";
    case "Rejected":
      return "Đã từ chối";
    default:
      return status;
  }
};

const getApplianceIcon = (appliance) => {
  const applianceLower = appliance.toLowerCase();
  if (applianceLower.includes("tủ lạnh")) return "mdi-fridge";
  if (applianceLower.includes("máy giặt")) return "mdi-washing-machine";
  if (
    applianceLower.includes("điều hòa") ||
    applianceLower.includes("máy lạnh")
  )
    return "mdi-air-conditioner";
  if (applianceLower.includes("tivi") || applianceLower.includes("tv"))
    return "mdi-television";
  if (applianceLower.includes("lò") || applianceLower.includes("bếp"))
    return "mdi-stove";
  return "mdi-tools";
};

const getApplianceColor = (appliance) => {
  const applianceLower = appliance.toLowerCase();
  if (applianceLower.includes("tủ lạnh")) return "indigo";
  if (applianceLower.includes("máy giặt")) return "cyan";
  if (
    applianceLower.includes("điều hòa") ||
    applianceLower.includes("máy lạnh")
  )
    return "light-blue";
  if (applianceLower.includes("tivi") || applianceLower.includes("tv"))
    return "purple";
  if (applianceLower.includes("lò") || applianceLower.includes("bếp"))
    return "deep-orange";
  return "grey";
};

// Get service color based on service name
const getServiceColor = (service) => {
  if (!service) return "grey";

  const serviceLower = service.toLowerCase();
  if (serviceLower.includes("sửa chữa")) return "purple";
  if (serviceLower.includes("bảo trì")) return "cyan";
  if (serviceLower.includes("thay thế")) return "orange";
  if (serviceLower.includes("lắp đặt")) return "green";
  if (serviceLower.includes("tư vấn")) return "blue";
  return "deep-purple";
};

// Get service icon based on service name
const getServiceIcon = (service) => {
  if (!service) return "mdi-cog";

  const serviceLower = service.toLowerCase();
  if (serviceLower.includes("sửa chữa")) return "mdi-wrench";
  if (serviceLower.includes("bảo trì")) return "mdi-tools";
  if (serviceLower.includes("thay thế")) return "mdi-swap-horizontal";
  if (serviceLower.includes("lắp đặt")) return "mdi-screwdriver";
  if (serviceLower.includes("tư vấn")) return "mdi-account-question";
  return "mdi-cog";
};

const acceptJob = async (item) => {
  try {
    await axios.put(
      `http://localhost:9090/api/repair-requests/${item.id}/status`,
      null,
      {
        params: {
          status: "Processing",
          technicianId: userId,
        },
      }
    );
    item.status = "Processing";
    showNotification("Đã nhận công việc thành công", "success");
  } catch (error) {
    console.error("Không thể nhận công việc:", error);
    showNotification("Không thể nhận công việc. Vui lòng thử lại.", "error");
  }
};

const acceptJobFromDetail = async () => {
  if (selectedJob.value) {
    await acceptJob(selectedJob.value);
    detailDialog.value = false;
  }
};

const openRejectDialog = (item) => {
  selectedJob.value = item;
  rejectReason.value = "";
  rejectDialog.value = true;
};

const closeRejectDialog = () => {
  rejectDialog.value = false;
  rejectReason.value = "";
};

const confirmReject = async () => {
  if (selectedJob.value) {
    try {
      await axios.put(
        `http://localhost:9090/api/repair-requests/${selectedJob.value.id}/status`,
        null,
        {
          params: {
            status: "Rejected",
            rejectReason: rejectReason.value,
          },
        }
      );
      selectedJob.value.status = "Rejected";
      selectedJob.value.rejectReason = rejectReason.value;
      showNotification("Đã từ chối công việc", "info");
    } catch (error) {
      console.error("Không thể từ chối công việc:", error);
      showNotification(
        "Không thể từ chối công việc. Vui lòng thử lại.",
        "error"
      );
    }
  }
  rejectDialog.value = false;
};

const viewJobDetails = (item) => {
  selectedJob.value = item;
  detailDialog.value = true;
};

const clearFilters = () => {
  searchQuery.value = "";
  statusFilter.value = "";
};

const formatDate = (dateString) => {
  if (!dateString) return "N/A";
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  } catch (e) {
    console.error("Lỗi:", e);
    return dateString || "N/A";
  }
};

const formatTime = (dateString) => {
  if (!dateString) return "";
  try {
    const date = new Date(dateString);
    return date.toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (e) {
    console.error("Lỗi:", e);
    return "";
  }
};

const formatFullDate = (dateString) => {
  if (!dateString) return "N/A";
  try {
    const date = new Date(dateString);
    const weekday = [
      "Chủ Nhật",
      "Thứ Hai",
      "Thứ Ba",
      "Thứ Tư",
      "Thứ Năm",
      "Thứ Sáu",
      "Thứ Bảy",
    ];
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");

    return `${
      weekday[date.getDay()]
    }, ${day}/${month}/${year} - ${hours}:${minutes}`;
  } catch (e) {
    console.error("Lỗi:", e);
    return dateString || "N/A";
  }
};

const formatPhone = (phone) => {
  if (!phone) return "";
  // Format phone number as XXX.XXX.XXXX
  return phone.replace(/(\d{3})(\d{3})(\d{4})/, "$1.$2.$3");
};

const getInitials = (name) => {
  if (!name) return "N/A";
  return name
    .split(" ")
    .map((word) => word[0])
    .join("")
    .substring(0, 2)
    .toUpperCase();
};

const showNotification = (text, color = "success") => {
  snackbar.value = {
    show: true,
    text,
    color,
  };
};

// Initialize data
onMounted(() => {
  fetchJobs();
});
</script>

<style scoped>
.job-table :deep(th) {
  font-weight: 600 !important;
  color: rgba(0, 0, 0, 0.7) !important;
  font-size: 0.85rem !important;
  text-transform: uppercase !important;
  letter-spacing: 0.5px !important;
}

.job-table :deep(.v-data-table-footer) {
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.job-table :deep(tr:hover) {
  background-color: rgba(25, 118, 210, 0.04) !important;
}
</style>
