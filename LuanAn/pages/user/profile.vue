<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container class="d-flex justify-center">
    <!-- Thẻ thông tin người dùng -->
    <v-card max-width="800" class="pa-6 elevation-12 rounded-lg">
      <v-card-title
        class="text-center py-4"
        style="
          background-color: #1976d2;
          color: white;
          border-top-left-radius: 16px;
          border-top-right-radius: 16px;
        "
      >
        <v-icon class="mr-2">mdi-account-circle</v-icon>
        Thông tin tài khoản
      </v-card-title>

      <v-row>
        <!-- Avatar và Tên người dùng -->
        <v-col cols="12" md="4" class="text-center">
          <v-avatar size="120" class="mb-4">
            <v-img
              src="https://cdn-icons-png.flaticon.com/512/149/149071.png"
            />
          </v-avatar>
          <div class="text-h6 font-weight-bold">{{ user.fullName }}</div>
          <div class="text-subtitle-2 text-grey">{{ user.role }}</div>
        </v-col>

        <!-- Thông tin chi tiết người dùng -->
        <v-col cols="12" md="8">
          <v-row dense>
            <v-col
              v-for="(value, label) in userFields"
              :key="label"
              cols="12"
              sm="6"
            >
              <div class="text-subtitle-2 text-grey font-weight-medium mb-1">
                {{ label }}
              </div>
              <div class="text-body-1 font-weight-regular">{{ value }}</div>
            </v-col>
          </v-row>

          <v-divider class="my-4" />

          <!-- Các nút hành động -->
          <div class="d-flex justify-end flex-wrap gap-2">
            <v-btn
              color="primary"
              variant="elevated"
              @click="editDialog = true"
            >
              <v-icon start>mdi-pencil</v-icon> Chỉnh sửa
            </v-btn>
            <v-btn color="deep-purple accent-4" dark @click="fetchArticles">
              <v-icon start>mdi-file-document-outline</v-icon> Lịch sử bài viết
            </v-btn>
            <v-btn color="cyan darken-3" dark @click="fetchRepairHistory">
              <v-icon start>mdi-tools</v-icon> Lịch sử đặt lịch
            </v-btn>
          </div>
        </v-col>
      </v-row>

      <!-- Các nút hành động cho dịch vụ -->
      <v-row v-for="(service, index) in completedServices" :key="index">
        <v-col cols="12" md="6">
          <v-card class="pa-4">
            <v-card-title>
              {{ service.name }}
            </v-card-title>
            <v-card-subtitle>
              Thiết bị: {{ service.appliance }}<br />
              Trạng thái: {{ service.status }}
            </v-card-subtitle>

            <v-card-actions>
              <v-btn color="green" @click="openReviewDialog(service)">
                <v-icon start>mdi-star</v-icon> Đánh giá
              </v-btn>

              <v-btn color="blue" @click="viewReviews(service)">
                <v-icon start>mdi-eye</v-icon> Xem đánh giá
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-card>

    <!-- Dialog chỉnh sửa thông tin cá nhân -->
    <v-dialog v-model="editDialog" max-width="600">
      <v-card class="rounded-lg pa-4">
        <v-card-title class="text-h6 font-weight-bold text-primary">
          Chỉnh sửa thông tin cá nhân
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="editForm.fullName" label="Họ tên" outlined />
          <v-text-field v-model="editForm.email" label="Email" outlined />
          <v-text-field
            v-model="editForm.phoneNumber"
            label="Số điện thoại"
            outlined
          />
          <v-text-field v-model="editForm.address" label="Địa chỉ" outlined />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="editDialog = false">Hủy</v-btn>
          <v-btn color="primary" @click="updateUser">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog lịch sử bài viết -->
    <v-dialog v-model="historyDialog" max-width="800">
      <v-card class="rounded-lg pa-4">
        <v-card-title class="text-h6 font-weight-bold text-primary">
          Lịch sử đăng bài
        </v-card-title>
        <v-card-text>
          <v-text-field
            v-model="searchQuery"
            label="Tìm kiếm bài viết"
            append-icon="mdi-magnify"
            single-line
            clearable
          />

          <v-data-table
            :headers="historyHeaders"
            :items="filteredArticles"
            class="elevation-2 rounded-lg"
            :items-per-page="5"
            :footer-props="{
              'items-per-page-options': [5, 10, 15],
              'items-per-page': 5,
            }"
          >
            <template #item.createdAt="{ item }">
              {{ formatDateTime(item.createdAt) }}
            </template>

            <template #item.status="{ item }">
              <v-chip
                :color="statusColor(item.status)"
                class="mx-auto"
                dark
                small
              >
                {{ item.status }}
              </v-chip>
            </template>

            <template #item.actions="{ item }">
              <v-btn icon color="info" @click="viewDetails(item)">
                <v-icon>mdi-eye</v-icon>
              </v-btn>
            </template>

            <template #no-data>
              <v-alert
                type="info"
                border="left"
                color="blue lighten-3"
                class="ma-4"
              >
                Không có bài viết nào phù hợp.
              </v-alert>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="historyDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog chi tiết bài viết -->
    <v-dialog v-model="detailDialog" max-width="700">
      <v-card class="rounded-lg pa-4">
        <v-card-title class="text-h6 font-weight-bold text-primary">
          Chi tiết bài viết
        </v-card-title>
        <v-divider />
        <v-card-text>
          <v-container fluid>
            <v-row>
              <v-col cols="12" class="mb-4">
                <div class="text-subtitle-1 font-weight-medium mb-1">
                  Mô tả:
                </div>
                <div class="text-body-1">
                  {{ selectedArticle?.description }}
                </div>
              </v-col>

              <v-col cols="12">
                <div class="text-subtitle-1 font-weight-medium mb-1">
                  Nội dung:
                </div>
                <div class="text-body-1" style="white-space: pre-line">
                  {{ selectedArticle?.content }}
                </div>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-divider />
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" variant="text" @click="detailDialog = false"
            >Đóng</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog lịch sử sửa chữa -->
    <v-dialog v-model="repairDialog" max-width="900">
      <v-card class="rounded-lg pa-4">
        <v-card-title class="text-h6 font-weight-bold text-primary">
          Lịch sử đặt lịch sửa chữa
        </v-card-title>
        <v-card-text>
          <v-data-table
            :headers="repairHeaders"
            :items="repairRequests"
            class="elevation-2 rounded-lg"
            :items-per-page="5"
            :footer-props="{
              'items-per-page-options': [5, 10],
              'items-per-page': 5,
            }"
          >
            <template #item.appointmentDate="{ item }">
              {{ formatDateTime(item.appointmentDate) }}
            </template>

            <template #item.status="{ item }">
              <v-chip :color="statusColor(item.status)" dark small>
                {{ item.status }}
              </v-chip>
            </template>

            <template #item.service="{ item }">
              {{ item.service.serviceName }}
            </template>

            <template #item.appliance="{ item }">
              {{ item.appliance?.applianceName || "Không rõ" }}
            </template>

            <template #no-data>
              <v-alert
                type="info"
                border="left"
                color="blue-lighten-3"
                class="ma-4"
              >
                Không có lịch sử sửa chữa nào.
              </v-alert>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="repairDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog Đánh giá -->
    <v-dialog v-model="reviewDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h6">Đánh giá dịch vụ</v-card-title>
        <v-card-text>
          <v-rating v-model="reviewForm.rating" color="amber" dense />
          <v-textarea
            v-model="reviewForm.comment"
            label="Nhận xét (Tuỳ chọn)"
            outlined
            rows="3"
          />
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="reviewDialog = false">Hủy</v-btn>
          <v-btn color="primary" @click="submitReview">Lưu đánh giá</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xem đánh giá -->
    <v-dialog v-model="reviewListDialog" max-width="600">
      <v-card class="rounded-lg">
        <v-card-title class="text-h6 font-weight-bold text-primary">
          Danh sách đánh giá dịch vụ
        </v-card-title>
        <v-divider />
        <v-card-text>
          <v-container v-if="serviceReviews.length > 0">
            <v-row
              v-for="(review, index) in serviceReviews"
              :key="index"
              class="mb-4"
            >
              <v-col cols="12">
                <!-- Dòng trên cùng: avatar + tên + ngày bên trái, rating bên phải -->
                <div
                  class="d-flex align-center justify-space-between mb-2 w-100"
                >
                  <!-- Bên trái -->
                  <div class="d-flex align-center">
                    <v-avatar size="40" class="mr-3">
                      <v-img
                        src="https://cdn-icons-png.flaticon.com/512/149/149071.png"
                        alt="Avatar"
                      />
                    </v-avatar>
                    <div>
                      <div class="font-weight-bold text-body-1">
                        {{ review.customer.fullName || "Ẩn danh" }}
                      </div>
                      <div class="text-caption grey--text">
                        {{ formatDateTime(review.createdAt) }}
                      </div>
                    </div>
                  </div>

                  <!-- Bên phải: rating -->
                  <div class="d-flex align-center">
                    <v-rating
                      :model-value="review.rating"
                      color="amber"
                      dense
                      readonly
                    />
                    <span class="ml-2 grey--text text--darken-1">
                      ({{ review.rating }}/5)
                    </span>
                  </div>
                </div>

                <!-- Comment -->
                <div class="text-body-1 mb-2">
                  {{ review.comment || "Không có nhận xét." }}
                </div>

                <v-divider class="my-2" />
              </v-col>
            </v-row>
          </v-container>

          <!-- Không có đánh giá -->
          <v-alert
            v-else
            type="info"
            border="left"
            color="blue-lighten-3"
            class="ma-2"
          >
            Chưa có đánh giá nào cho dịch vụ này.
          </v-alert>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn text @click="reviewListDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useCookie } from "#app";

const user = ref({});
const formattedCreatedAt = ref("");
const editDialog = ref(false);
const historyDialog = ref(false);
const searchQuery = ref(""); // Tìm kiếm lịch sử bài viết
const editForm = ref({
  fullName: "",
  email: "",
  phoneNumber: "",
  address: "",
});

const userFields = ref({});
const userArticles = ref([]); // Lưu trữ các bài viết của người dùng
const historyHeaders = ref([
  {
    text: "Tiêu đề bài viết",
    title: "Tiêu đề bài viết",
    align: "start",
    value: "title",
  },
  { text: "Ngày đăng", title: "Ngày đăng", value: "createdAt" },
  { text: "Trạng thái", title: "Trạng thái", value: "status" },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
]);

const repairDialog = ref(false);
const repairRequests = ref([]);
const repairHeaders = [
  { text: "Dịch vụ", title: "Dịch vụ", value: "service" },
  { text: "Thiết bị", title: "Thiết bị", value: "appliance" },
  { text: "Ngày hẹn", title: "Ngày hẹn", value: "appointmentDate" },
  { text: "Trạng thái", title: "Trạng thái", value: "status" },
];

const selectedArticle = ref(null);
const detailDialog = ref(false);

const reviewDialog = ref(false);
const reviewForm = ref({
  rating: 0,
  comment: "",
});
const selectedService = ref(null);
const completedServices = ref([]);
const reviewListDialog = ref(false); // Dialog xem đánh giá
const serviceReviews = ref([]); // Danh sách đánh giá của dịch vụ được chọn

const fetchCompletedServices = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    const res = await axios.get(
      `http://localhost:9090/api/repair-requests/customer/${userId}`
    );
    const allRequests = res.data;

    completedServices.value = allRequests
      .filter((req) => req.status === "Completed")
      .map((req) => ({
        requestId: req.requestId,
        serviceId: req.service.serviceId,
        name: req.service.serviceName,
        appliance: req.appliance?.applianceName || "Không rõ",
        status: req.status,
      }));
  } catch (error) {
    console.error("Lỗi khi lấy danh sách dịch vụ hoàn thành:", error);
  }
};

// Hàm tải thông tin người dùng
const loadUser = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    const res = await axios.get(`http://localhost:9090/api/users/${userId}`);
    user.value = res.data;
    formattedCreatedAt.value = formatDateTime(user.value.createdAt);

    userFields.value = {
      Email: user.value.email,
      "Số điện thoại": user.value.phoneNumber,
      "Địa chỉ": user.value.address,
      "Ngày tạo": formattedCreatedAt.value,
    };

    Object.assign(editForm.value, {
      fullName: user.value.fullName,
      email: user.value.email,
      phoneNumber: user.value.phoneNumber,
      address: user.value.address,
    });
  } catch (err) {
    console.error("Lỗi khi tải thông tin người dùng:", err);
  }
};

// Hàm lấy lịch sử bài viết
const fetchArticles = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    const res = await axios.get(
      `http://localhost:9090/api/articles/user/${userId}`
    );
    userArticles.value = res.data;
    historyDialog.value = true; // Mở dialog lịch sử bài viết
  } catch (err) {
    console.error("Lỗi khi tải bài viết của người dùng:", err);
  }
};

// Hàm filter các bài viết theo tìm kiếm
const filteredArticles = computed(() => {
  if (searchQuery.value) {
    return userArticles.value.filter((article) =>
      article.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }
  return userArticles.value;
});

// Hàm format lại ngày tháng theo định dạng dd/MM/yyyy HH:mm
const formatDateTime = (dateStr) => {
  const date = new Date(dateStr);
  const yyyy = date.getFullYear();
  const MM = String(date.getMonth() + 1).padStart(2, "0");
  const dd = String(date.getDate()).padStart(2, "0");
  const hh = String(date.getHours()).padStart(2, "0");
  const mm = String(date.getMinutes()).padStart(2, "0");
  const ss = String(date.getSeconds()).padStart(2, "0");
  return `${dd}-${MM}-${yyyy} ${hh}:${mm}:${ss}`;
};

// Hàm lấy màu sắc cho trạng thái bài viết
const statusColor = (status) => {
  switch (status) {
    case "Processing":
      return "green";
    case "Pending":
      return "orange";
    case "Rejected":
      return "red";
    case "Completed":
      return "blue";
    default:
      return "grey";
  }
};

// Hàm cập nhật thông tin người dùng
const updateUser = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    await axios.put(`http://localhost:9090/api/users/${userId}`, {
      ...user.value,
      ...editForm.value,
    });
    editDialog.value = false;
    loadUser(); // Reload
  } catch (err) {
    console.error("Lỗi khi cập nhật thông tin:", err);
  }
};

// Hàm lấy lịch sử sửa chữa
const fetchRepairHistory = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    const res = await axios.get(
      `http://localhost:9090/api/repair-requests/customer/${userId}`
    );
    repairRequests.value = res.data;
    repairDialog.value = true;
  } catch (err) {
    console.error("Lỗi khi tải lịch sử sửa chữa:", err);
  }
};

// Hàm xem chi tiết bài viết
const viewDetails = (article) => {
  selectedArticle.value = article;
  detailDialog.value = true;
};

// Mở dialog đánh giá
const openReviewDialog = (service) => {
  selectedService.value = service;
  reviewForm.value = {
    rating: 0,
    comment: "",
  };
  reviewDialog.value = true;
};

// Gửi đánh giá
const submitReview = async () => {
  try {
    const customerId = useCookie("userId");
    const userId = customerId.value;
    const payload = {
      customerId: parseInt(userId),
      serviceId: selectedService.value.serviceId,
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment,
    };

    await axios.post("http://localhost:9090/api/reviews", payload);
    reviewDialog.value = false;
    alert("Gửi đánh giá thành công!");
  } catch (error) {
    console.error("Lỗi khi gửi đánh giá:", error);
    alert("Gửi đánh giá thất bại!");
  }
};

// Xem các đánh giá của dịch vụ
const viewReviews = async (service) => {
  try {
    const res = await axios.get(
      `http://localhost:9090/api/reviews/service/${service.serviceId}`
    );
    serviceReviews.value = res.data;
    reviewListDialog.value = true;
  } catch (error) {
    console.error("Lỗi khi lấy đánh giá:", error);
    alert("Không thể tải đánh giá.");
  }
};

onMounted(() => {
  loadUser();
  fetchCompletedServices(); // gọi khi component mount
});
</script>

<style scoped>
.history-table-item:hover {
  background-color: #f5f5f5;
}

.v-data-table th {
  background-color: #f5f5f5;
  font-weight: bold;
}

.v-chip {
  font-size: 0.875rem;
  font-weight: 500;
  text-transform: capitalize;
}

.v-btn {
  text-transform: capitalize;
  border-radius: 8px;
}

.v-avatar {
  border: 3px solid #1976d2;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.v-data-table {
  font-size: 0.95rem;
}

.v-data-table .v-chip {
  text-transform: none;
}

.v-data-table .v-data-table__wrapper tr:hover {
  background-color: #f0f8ff;
}
</style>
