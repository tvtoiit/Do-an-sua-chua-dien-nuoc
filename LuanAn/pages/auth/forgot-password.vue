<template>
  <v-container class="fill-height" fluid>
    <title>Quên mật khẩu</title>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="pa-8 elevation-8 rounded-xl" theme="light">
          <!-- Logo và tiêu đề -->
          <div class="text-center mb-6">
            <v-avatar size="70" class="mb-4 bg-primary-lighten-5">
              <v-icon size="40" color="primary">mdi-lock-reset</v-icon>
            </v-avatar>
            <h1 class="text-h4 font-weight-bold primary--text mb-1">
              Quên mật khẩu
            </h1>
            <p class="text-body-1 text-medium-emphasis">
              Nhập email của bạn để nhận mã xác nhận
            </p>
          </div>

          <v-divider class="mb-6" />

          <!-- Form đăng nhập -->
          <v-card-text class="px-0 pt-0">
            <v-form v-model="isFormValid" @submit.prevent="sendCode">
              <v-text-field
                v-model="email"
                label="Email"
                type="email"
                prepend-inner-icon="mdi-email-outline"
                variant="outlined"
                density="comfortable"
                :rules="emailRules"
                class="mb-1"
                bg-color="grey-lighten-5"
                placeholder="example@domain.com"
                hide-details="auto"
              />

              <p class="text-body-2 text-medium-emphasis mb-6">
                Chúng tôi sẽ gửi mã xác nhận đến email của bạn để đặt lại mật
                khẩu
              </p>

              <!-- Loading state -->
              <v-btn
                type="submit"
                :loading="isLoading"
                :disabled="!isFormValid || isLoading"
                color="primary"
                class="mt-2 py-6"
                block
                elevation="2"
                rounded="lg"
              >
                <v-icon start class="mr-1">mdi-send</v-icon>
                Gửi mã xác nhận
              </v-btn>
            </v-form>

            <!-- Notify area -->
            <v-alert
              v-if="notification.show"
              :type="notification.type"
              variant="tonal"
              class="mt-6"
              closable
              @click:close="notification.show = false"
            >
              {{ notification.message }}
            </v-alert>

            <div class="d-flex justify-center mt-6 pt-4">
              <v-btn
                to="/auth/login"
                class="text-primary"
                variant="text"
                prepend-icon="mdi-arrow-left"
                size="small"
              >
                Quay lại trang đăng nhập
              </v-btn>
            </div>
          </v-card-text>
        </v-card>

        <!-- Footer -->
        <div class="mt-6 text-center text-body-2 text-medium-emphasis">
          <p>
            Cần trợ giúp?
            <a href="#" class="text-decoration-none text-primary"
              >Liên hệ hỗ trợ</a
            >
          </p>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const email = ref("");
const isFormValid = ref(false);
const isLoading = ref(false);

const notification = reactive({
  show: false,
  type: "success",
  message: "",
});

// Validate email
const emailRules = [
  (v) => !!v || "Email không được để trống",
  (v) => /.+@.+\..+/.test(v) || "Email không hợp lệ",
];

const sendCode = async () => {
  // Reset notify
  notification.show = false;

  if (!isFormValid.value) return;

  isLoading.value = true;

  try {
    const res = await fetch(
      `http://localhost:9090/api/auth/forgot-password?email=${email.value}`,
      {
        method: "POST",
      }
    );

    const responseText = await res.text(); // lấy text trả về

    if (!res.ok) {
      notification.type = "error";
      notification.message = `Không gửi được mã: ${responseText}`;
      notification.show = true;
      return;
    }

    notification.type = "success";
    notification.message = "Đã gửi mã xác nhận đến email của bạn";
    notification.show = true;

    // Delay chuyển trang để người dùng kịp đọc thông báo
    setTimeout(() => {
      router.push("/auth/reset-password");
    }, 1500);
  } catch (err) {
    notification.type = "error";
    notification.message = `Đã xảy ra lỗi: ${
      err.message || "Không thể kết nối tới máy chủ"
    }`;
    notification.show = true;
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.v-card {
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
}

.v-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1) !important;
}
</style>
