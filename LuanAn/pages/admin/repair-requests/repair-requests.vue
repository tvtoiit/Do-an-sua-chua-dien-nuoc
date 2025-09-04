<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý yêu cầu sửa chữa</title>
    <h1 class="text-h5 mb-4">Quản lý yêu cầu sửa chữa</h1>
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>
    <v-data-table
      :headers="headers"
      :items="requests"
      item-value="requestId"
      :loading="loading"
      loading-text="Đang tải dữ liệu..."
    >
      <template #item.status="{ item }">
        <v-chip :color="statusColor(item.status)" dark>
          {{ item.status }}
        </v-chip>
      </template>

      <template #item.actions="{ item }">
        <v-btn icon @click="viewDetails(item)">
          <v-icon>mdi-eye</v-icon>
        </v-btn>
        <v-btn icon @click="openEditDialog(item)">
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <!-- Dialog chi tiết -->
    <v-dialog v-model="dialog" width="600">
      <v-card>
        <v-card-title class="text-h6">Chi tiết yêu cầu</v-card-title>
        <v-card-text>
          <p><strong>ID:</strong> {{ selectedRequest?.requestId }}</p>
          <p>
            <strong>Khách hàng:</strong>
            {{ selectedRequest?.customer?.fullName }}
          </p>
          <p>
            <strong>Dịch vụ:</strong>
            {{ selectedRequest?.service?.serviceName }}
          </p>
          <p>
            <strong>Thiết bị:</strong>
            {{ selectedRequest?.appliance?.applianceName }}
          </p>
          <p>
            <strong>Ngày hẹn:</strong> {{ selectedRequest?.appointmentDate }}
          </p>
          <p><strong>Ghi chú:</strong> {{ selectedRequest?.notes }}</p>
          <p><strong>Trạng thái:</strong> {{ selectedRequest?.status }}</p>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="dialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog chỉnh sửa trạng thái + thợ -->
    <v-dialog v-model="editDialog" width="500">
      <v-card>
        <v-card-title class="text-h6">Cập nhật trạng thái và thợ</v-card-title>
        <v-card-text>
          <v-select
            v-model="editStatus"
            label="Trạng thái"
            :items="availableStatuses"
            required
          />
          <v-select
            v-model="editTechnicianId"
            label="Chọn thợ"
            :items="technicians"
            item-title="user.fullName"
            item-value="user.userId"
            clearable
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue" @click="confirmStatusUpdate">Xác nhận</v-btn>
          <v-btn text @click="editDialog = false">Hủy</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const requests = ref([]);
const loading = ref(false);
const dialog = ref(false);
const editDialog = ref(false);
const selectedRequest = ref(null);

const headers = [
  { title: "ID", key: "requestId" },
  { title: "Khách hàng", key: "customer.fullName" },
  { title: "Dịch vụ", key: "service.serviceName" },
  { title: "Thiết bị", key: "appliance.applianceName" },
  { title: "Ngày hẹn", key: "appointmentDate" },
  { title: "Trạng thái", key: "status" },
  { title: "Hành động", key: "actions", sortable: false },
];

const availableStatuses = ["Pending", "Processing", "Completed", "Rejected"];

const statusColor = (status) => {
  switch (status) {
    case "Pending":
      return "grey";
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

const loadRequests = async () => {
  loading.value = true;
  try {
    const res = await axios.get(
      "http://localhost:9090/api/repair-requests/all"
    );
    requests.value = res.data;
  } catch (error) {
    console.error("Lỗi tải yêu cầu:", error);
  } finally {
    loading.value = false;
  }
};

const viewDetails = async (item) => {
  try {
    const res = await axios.get(
      `http://localhost:9090/api/repair-requests/details/${item.requestId}`
    );
    selectedRequest.value = res.data;
    dialog.value = true;
  } catch (error) {
    console.error("Không thể lấy chi tiết:", error);
  }
};

// Danh sách thợ sửa chữa
const technicians = ref([]);
const loadTechnicians = async () => {
  try {
    const res = await axios.get(
      "http://localhost:9090/api/technicians"
    );
    technicians.value = res.data;
  } catch (err) {
    console.error("Không tải được danh sách thợ:", err);
  }
};

// Biến dùng cho chỉnh sửa
const editStatus = ref("");
const editTechnicianId = ref(null);
const editingRequestId = ref(null);

const openEditDialog = (item) => {
  editingRequestId.value = item.requestId;
  editStatus.value = item.status;
  editTechnicianId.value = technicians.value.find(t => t.technicianId === item.technicianId)?.user?.userId || null;
  editDialog.value = true;
};

const confirmStatusUpdate = async () => {
  try {
    await axios.put(
      `http://localhost:9090/api/repair-requests/${editingRequestId.value}/status`,
      null,
      {
        params: {
          status: editStatus.value,
          rejectReason: "",
          technicianId: editTechnicianId.value,
        },
      }
    );
    editDialog.value = false;
    loadRequests();
  } catch (error) {
    console.error("Lỗi cập nhật trạng thái:", error);
  }
};

onMounted(() => {
  loadRequests();
  loadTechnicians();
});

function goBack() {
  router.back();
}
</script>
