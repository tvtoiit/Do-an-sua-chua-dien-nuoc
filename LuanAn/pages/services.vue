<template>
  <v-container fluid class="service-container pa-4">
    <title>Dịch vụ sửa chữa</title>
    <!-- Header với nền gradient -->
    <v-sheet color="primary" class="py-6 px-4 mb-8 rounded-lg elevation-3">
      <v-row no-gutters align="center" justify="space-between">
        <v-col cols="12" md="6">
          <h1 class="text-h4 font-weight-bold text-white mb-2">
            Danh Sách Dịch Vụ
          </h1>
          <p class="text-subtitle-1 text-white text-opacity-80 mb-0">
            Các dịch vụ sửa chữa điện gia dụng uy tín, chất lượng
          </p>
        </v-col>
        <v-col cols="12" md="4" sm="6" class="mt-4 mt-md-0">
          <v-select
            v-model="selectedAppliance"
            :items="appliances"
            item-title="name"
            item-value="id"
            label="Lọc theo thiết bị"
            variant="outlined"
            density="comfortable"
            bg-color="white"
            color="primary"
            hide-details
            clearable
            class="service-filter"
            @update:model-value="loadAllServices"
          >
            <template #prepend>
              <v-icon color="primary">mdi-filter-variant</v-icon>
            </template>
          </v-select>
        </v-col>
      </v-row>
    </v-sheet>

    <!-- Hiển thị số lượng kết quả và sắp xếp -->
    <v-row no-gutters class="mb-4">
      <v-col cols="12" sm="6">
        <p class="text-body-1 font-weight-medium">
          {{ services.length }} dịch vụ được tìm thấy
        </p>
      </v-col>
      <v-col cols="12" sm="6" class="d-flex justify-end">
        <v-select
          v-model="sortOption"
          :items="sortOptions"
          density="compact"
          variant="plain"
          hide-details
          class="sort-select"
          style="max-width: 200px"
        />
      </v-col>
    </v-row>

    <!-- Loading skeleton -->
    <v-row v-if="loading">
      <v-col
        v-for="i in 6"
        :key="`skeleton-${i}`"
        cols="12"
        sm="6"
        md="4"
        lg="3"
        class="d-flex"
      >
        <v-sheet class="pa-3 w-100 rounded-lg" height="400">
          <v-skeleton-loader type="card, article" height="100%" />
        </v-sheet>
      </v-col>
    </v-row>

    <!-- Danh sách dịch vụ -->
    <v-row v-else>
      <v-col
        v-for="service in sortedServices"
        :key="service.serviceId"
        cols="12"
        sm="6"
        md="4"
        lg="3"
        class="d-flex"
      >
        <v-card
          class="service-card w-100 transition-swing d-flex flex-column"
          :elevation="hoveredCard === service.serviceId ? 8 : 2"
          @mouseenter="hoveredCard = service.serviceId"
          @mouseleave="hoveredCard = null"
        >
          <!-- Phần hình ảnh có chiều cao cố định -->
          <div class="position-relative">
            <v-img
              :src="
                service.imageUrl ||
                'https://suadodien.com/wp-content/uploads/2021/12/Sua-chua-lo-vi-song-tai-nha-ha-noi.png'
              "
              height="180px"
              cover
              class="service-img rounded-t-lg"
              :gradient="
                hoveredCard === service.serviceId
                  ? 'to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.6)'
                  : undefined
              "
            >
              <template #placeholder>
                <v-row class="fill-height ma-0" align="center" justify="center">
                  <v-progress-circular indeterminate color="primary" />
                </v-row>
              </template>
            </v-img>

            <!-- Badge nổi bật nếu có rating cao -->
            <v-chip
              v-if="service.rating && service.rating.average >= 4.5"
              color="success"
              size="small"
              class="position-absolute font-weight-bold"
              style="top: 10px; right: 10px"
            >
              <v-icon start size="small">mdi-star</v-icon>
              Nổi bật
            </v-chip>
          </div>

          <!-- Phần nội dung thông tin dịch vụ với chiều cao cố định -->
          <div class="service-info d-flex flex-column flex-grow-1">
            <v-card-title
              class="text-primary pt-4 pb-1 text-wrap font-weight-bold text-truncate-2 service-title"
            >
              {{ service.serviceName }}
            </v-card-title>

            <v-card-subtitle class="pb-0">
              <div class="d-flex align-center">
                <v-icon size="small" color="amber-darken-2" class="mr-1"
                  >mdi-currency-usd</v-icon
                >
                <span class="font-weight-medium">{{
                  formatCurrency(service.price)
                }}</span>
              </div>
            </v-card-subtitle>

            <v-card-text class="py-2 flex-grow-1 d-flex flex-column">
              <p class="text-truncate-3 mb-auto">{{ service.description }}</p>

              <div v-if="service.rating" class="d-flex align-center mt-2">
                <v-rating
                  :model-value="service.rating.average"
                  color="amber-darken-1"
                  half-increments
                  readonly
                  density="compact"
                  size="small"
                />
                <span class="text-caption grey--text text--darken-1 ml-2">
                  {{ service.rating.average.toFixed(1) }} ({{
                    service.rating.count
                  }})
                </span>
              </div>
              <div v-else class="text-caption text-grey mt-2">
                Chưa có đánh giá
              </div>
            </v-card-text>
          </div>

          <!-- Phần footer luôn nằm ở cuối -->
          <div class="service-footer">
            <v-divider />

            <v-card-actions>
              <v-btn
                color="primary"
                variant="text"
                class="px-2"
                @click="openBookingDialog(service.serviceId)"
              >
                <v-icon size="small" class="mr-1">mdi-cart-plus</v-icon>
                Đặt dịch vụ
              </v-btn>

              <v-spacer />
              <v-btn
                color="primary"
                variant="text"
                @click="openReviewDialog(service)"
              >
                <v-icon size="small" class="mr-1"
                  >mdi-comment-text-outline</v-icon
                >
                Đánh giá
              </v-btn>
            </v-card-actions>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Empty state -->
    <v-row v-if="!loading && services.length === 0">
      <v-col cols="12" class="text-center py-8">
        <v-icon size="64" color="grey-lighten-1"
          >mdi-package-variant-closed-remove</v-icon
        >
        <h3 class="text-h5 mt-4 font-weight-medium text-grey-darken-1">
          Không tìm thấy dịch vụ
        </h3>
        <p class="text-body-1 text-grey">
          Không có dịch vụ nào phù hợp với bộ lọc của bạn
        </p>
        <v-btn
          color="primary"
          prepend-icon="mdi-refresh"
          class="mt-4"
          @click="resetFilters"
        >
          Xóa bộ lọc
        </v-btn>
      </v-col>
    </v-row>

    <!-- Dialog đánh giá đã được cải tiến -->
    <v-dialog
      v-model="dialog"
      max-width="700px"
      transition="dialog-bottom-transition"
    >
      <v-card class="rounded-lg">
        <v-toolbar color="primary" class="elevation-0 rounded-t-lg">
          <v-toolbar-title class="text-white font-weight-bold">
            {{ selectedService?.serviceName }}
          </v-toolbar-title>
          <v-spacer />
          <v-btn icon @click="dialog = false">
            <v-icon color="white">mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-card-text class="px-4 py-6">
          <div
            v-if="selectedService?.rating"
            class="text-center mb-6 pa-4 bg-grey-lighten-4 rounded-lg"
          >
            <div class="text-h2 font-weight-bold text-primary mb-2">
              {{ selectedService.rating.average.toFixed(1) }}
            </div>
            <v-rating
              :model-value="selectedService.rating.average"
              color="amber-darken-1"
              half-increments
              readonly
              size="small"
              class="justify-center mb-1"
            />
            <div class="text-caption text-grey">
              Dựa trên {{ selectedService.rating.count }} đánh giá
            </div>
          </div>

          <v-container v-if="reviews.length > 0" class="px-0">
            <v-row>
              <v-col cols="12">
                <h3 class="text-h6 font-weight-medium mb-4">
                  <v-icon color="primary">mdi-comment-multiple-outline</v-icon>
                  Nhận xét từ khách hàng
                </h3>
              </v-col>
            </v-row>

            <v-divider class="mb-4" />

            <v-row v-for="(review, index) in reviews" :key="index" class="mb-1">
              <v-col cols="12">
                <div class="d-flex align-center justify-space-between mb-2">
                  <div class="d-flex align-center">
                    <v-avatar size="40" color="primary" class="mr-3 text-white">
                      {{ getInitials(review.customer.fullName || "Ẩn danh") }}
                    </v-avatar>
                    <div>
                      <div class="font-weight-bold text-body-1">
                        {{ review.customer.fullName || "Ẩn danh" }}
                      </div>
                      <div class="text-caption text-grey">
                        {{ formatDate(review.createdAt) }}
                      </div>
                    </div>
                  </div>

                  <v-chip
                    :color="getRatingColor(review.rating)"
                    size="small"
                    class="font-weight-medium"
                  >
                    <v-icon start size="x-small">mdi-star</v-icon>
                    {{ review.rating }}/5
                  </v-chip>
                </div>

                <div
                  class="text-body-1 mb-4 px-3 py-3 bg-grey-lighten-5 rounded-lg"
                >
                  <v-icon color="grey" size="small" class="mr-1"
                    >mdi-format-quote-open</v-icon
                  >
                  {{ review.comment || "Không có nhận xét." }}
                </div>

                <v-divider v-if="index < reviews.length - 1" class="my-4" />
              </v-col>
            </v-row>
          </v-container>

          <v-alert
            v-else
            type="info"
            variant="tonal"
            border="start"
            class="mt-4"
            icon="mdi-information-outline"
          >
            Chưa có đánh giá nào cho dịch vụ này. Hãy là người đầu tiên đánh
            giá!
          </v-alert>
        </v-card-text>

        <v-divider />

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn color="grey-darken-1" variant="text" @click="dialog = false">
            Đóng
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="bookingDialog" max-width="500px">
      <v-card>
        <v-card-title class="font-weight-bold">
          Xác nhận đặt dịch vụ
        </v-card-title>
        <v-card-text>
          <v-form ref="bookingForm" v-model="bookingFormValid">
            <v-text-field
              v-model="appointmentDate"
              label="Ngày hẹn sửa chữa"
              type="datetime-local"
              :rules="[(v) => !!v || 'Chọn ngày hẹn']"
              required
            />
            <v-textarea
              v-model="notes"
              label="Ghi chú thêm (không bắt buộc)"
              auto-grow
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" @click="confirmBooking">Xác nhận</v-btn>
          <v-btn text @click="bookingDialog = false">Hủy</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useCookie } from "#app";
import { useRouter } from "vue-router";

const services = ref([]);
const reviews = ref([]);
const appliances = ref([]);
const selectedAppliance = ref(null);
const dialog = ref(false);
const selectedService = ref(null);
const loading = ref(true);
const hoveredCard = ref(null);
const sortOption = ref("default");

const sortOptions = [
  { title: "Mặc định", value: "default" },
  { title: "Giá: Thấp đến cao", value: "priceLow" },
  { title: "Giá: Cao đến thấp", value: "priceHigh" },
  { title: "Đánh giá: Cao nhất", value: "ratingHigh" },
];

const bookingDialog = ref(false);
const selectedServiceId = ref(null);
const selectedApplianceId = ref(null);
const appointmentDate = ref(null);
const notes = ref("");
const bookingFormValid = ref(false);
const bookingForm = ref(null);

const userId = useCookie("userId");
const customerId = userId.value;
const router = useRouter();

async function openBookingDialog(serviceId) {
  if (!customerId) {
    alert("Vui lòng đăng nhập để đặt dịch vụ.");
        setTimeout(() => {
      router.push("/auth/login");
    }, 100);
    return;
  }

  selectedServiceId.value = serviceId;
  bookingDialog.value = true;

  // Tìm thông tin dịch vụ từ danh sách đã có
  const service = services.value.find((s) => s.serviceId === serviceId);
  if (service && service.applianceId) {
    selectedApplianceId.value = service.applianceId;
  } else {
    // Nếu thiếu thông tin, có thể load lại từ API chi tiết
    try {
      const res = await axios.get(
        `http://localhost:9090/api/services/${serviceId}`
      );
      selectedApplianceId.value = res.data.appliance.applianceId;
    } catch (err) {
      console.error("Không thể lấy ApplianceId từ dịch vụ:", err);
    }
  }
}

async function confirmBooking() {
  if (!bookingForm.value?.validate()) return;

  try {
    const payload = {
      customerId,
      serviceId: selectedServiceId.value,
      applianceId: selectedApplianceId.value,
      appointmentDate: appointmentDate.value,
      notes: notes.value,
    };
    await axios.post("http://localhost:9090/api/repair-requests", payload);

    bookingDialog.value = false;
    selectedApplianceId.value = null;
    appointmentDate.value = null;
    notes.value = "";
    alert("Đặt dịch vụ thành công!");
  } catch (error) {
    console.error(error);
    // Lấy thông báo lỗi từ backend nếu có
    const errorMessage =
      error.response?.data || "Có lỗi xảy ra khi đặt dịch vụ.";

    alert(errorMessage); // hoặc dùng snackbar/toast tùy UI framework
  }
}

const sortedServices = computed(() => {
  if (!services.value) return [];

  const result = [...services.value];

  switch (sortOption.value) {
    case "priceLow":
      return result.sort((a, b) => a.price - b.price);
    case "priceHigh":
      return result.sort((a, b) => b.price - a.price);
    case "ratingHigh":
      return result.sort((a, b) => {
        const ratingA = a.rating ? a.rating.average : 0;
        const ratingB = b.rating ? b.rating.average : 0;
        return ratingB - ratingA;
      });
    default:
      return result;
  }
});

const formatCurrency = (amount) =>
  new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);

const formatDate = (dateString) => {
  const options = {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  };
  return new Date(dateString).toLocaleDateString("vi-VN", options);
};

const getInitials = (name) => {
  if (!name) return "?";
  return name
    .split(" ")
    .map((part) => part.charAt(0))
    .join("")
    .toUpperCase()
    .substring(0, 2);
};

const getRatingColor = (rating) => {
  if (rating >= 4.5) return "success";
  if (rating >= 3.5) return "primary";
  if (rating >= 2.5) return "warning";
  return "error";
};

const resetFilters = () => {
  selectedAppliance.value = null;
  loadAllServices();
};

const loadAppliances = async () => {
  try {
    const res = await axios.get("http://localhost:9090/api/appliances");
    appliances.value = res.data.map((appl) => ({
      id: appl.applianceId,
      name: appl.applianceName,
    }));
  } catch (e) {
    console.error("Lỗi khi tải danh sách thiết bị:", e);
  }
};

const loadAllServices = async () => {
  loading.value = true;
  try {
    const url = selectedAppliance.value
      ? `http://localhost:9090/api/services?applianceId=${selectedAppliance.value}`
      : `http://localhost:9090/api/services`;
    const res = await axios.get(url);
    const fetchedServices = res.data;

    for (const service of fetchedServices) {
      try {
        const reviewRes = await axios.get(
          `http://localhost:9090/api/reviews/service/${service.serviceId}`
        );
        const reviewList = reviewRes.data;

        if (reviewList.length > 0) {
          const average =
            reviewList.reduce((sum, r) => sum + r.rating, 0) /
            reviewList.length;
          service.rating = {
            average,
            count: reviewList.length,
          };
        } else {
          service.rating = null;
        }

        // Set ảnh mặc định nếu không có URL ảnh
        service.imageUrl =
          service.imageUrl ||
          "https://cdn-icons-png.flaticon.com/512/2550/2550261.png";
      } catch (e) {
        console.log(e);
        service.rating = null;
      }
    }

    services.value = fetchedServices;
  } catch (e) {
    console.error("Lỗi khi tải dịch vụ:", e);
  } finally {
    // Thêm độ trễ nhỏ để hiển thị skeleton đẹp hơn
    setTimeout(() => {
      loading.value = false;
    }, 500);
  }
};

const openReviewDialog = async (service) => {
  selectedService.value = service;
  dialog.value = true;

  try {
    const res = await axios.get(
      `http://localhost:9090/api/reviews/service/${service.serviceId}`
    );
    reviews.value = res.data;
  } catch (err) {
    console.log(err);
    reviews.value = [];
  }
};

onMounted(() => {
  loadAppliances();
  loadAllServices();
});
</script>

<style scoped>
.service-container {
  background-color: #f9f9f9;
}

.service-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.service-card:hover {
  transform: translateY(-5px);
}

.service-title {
  height: 60px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.service-info {
  height: 200px;
}

.text-truncate-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.text-truncate-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sort-select :deep(.v-field__input) {
  padding-top: 8px;
  padding-bottom: 8px;
}

.service-filter :deep(.v-field__input) {
  padding-top: 12px;
  padding-bottom: 12px;
}
</style>
