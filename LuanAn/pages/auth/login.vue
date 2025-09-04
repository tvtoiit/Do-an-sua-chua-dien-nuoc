<template>
  <section class="login-section">
    <title>Đăng nhập</title>
    <v-container fluid class="fill-height pa-0">
      <v-row class="fill-height ma-0">
        <!-- Phần banner bên trái - hiển thị trên màn hình trung bình và lớn -->
        <v-col md="6" lg="7" class="d-none d-md-flex pa-0">
          <div class="login-banner-container d-flex flex-column justify-center">
            <div class="login-banner-content px-12">
              <div class="d-flex align-center mb-10">
                <v-avatar color="white" size="52" class="mr-4">
                  <v-icon size="34" color="primary">mdi-tools</v-icon>
                </v-avatar>
                <h2 class="text-h4 font-weight-bold text-white">
                  Repair System
                </h2>
              </div>

              <h1 class="text-h3 font-weight-bold text-white mb-4">
                Giải pháp sửa chữa chuyên nghiệp
              </h1>
              <p class="text-body-1 text-white mb-8 login-banner-text">
                Hệ thống quản lý dịch vụ sửa chữa hiện đại, giúp doanh nghiệp
                của bạn tối ưu quy trình, nâng cao chất lượng dịch vụ và tăng sự
                hài lòng của khách hàng.
              </p>
            </div>
          </div>
        </v-col>

        <!-- Phần đăng nhập bên phải -->
        <v-col
          cols="12"
          md="6"
          lg="5"
          class="login-form-container d-flex align-center justify-center"
        >
          <v-card
            width="100%"
            max-width="460"
            class="pa-8 rounded-xl login-card"
            elevation="0"
          >
            <div class="text-center d-md-none mb-8">
              <v-avatar color="primary" size="64" class="mb-4">
                <v-icon size="36" color="white">mdi-tools</v-icon>
              </v-avatar>
              <h2 class="text-h4 font-weight-bold">Repair System</h2>
            </div>

            <div class="d-flex align-center mb-8">
              <v-avatar color="primary" size="48" class="mr-4 login-avatar">
                <v-icon size="24" color="white">mdi-shield-account</v-icon>
              </v-avatar>
              <div>
                <h2 class="text-h5 font-weight-bold mb-1">Đăng nhập</h2>
              </div>
            </div>

            <v-form ref="form" class="login-form" @submit.prevent="login">
              <v-slide-y-transition>
                <v-alert
                  v-if="errorMessage"
                  density="compact"
                  type="error"
                  variant="tonal"
                  closable
                  class="mb-4"
                  @click:close="errorMessage = ''"
                >
                  <div class="d-flex align-center">
                    <v-icon class="mr-2">mdi-alert-circle</v-icon>
                    <span>{{ errorMessage }}</span>
                  </div>
                </v-alert>
              </v-slide-y-transition>

              <v-text-field
                v-model="email"
                label="Email"
                placeholder="email"
                type="email"
                variant="outlined"
                class="mb-4 rounded-lg login-input"
                :rules="[rules.required, rules.email]"
                prepend-inner-icon="mdi-email-outline"
                autocomplete="email"
                hide-details="auto"
                required
                bg-color="surface"
              />

              <v-text-field
                v-model="password"
                label="Mật khẩu"
                placeholder="Nhập mật khẩu"
                :type="showPassword ? 'text' : 'password'"
                variant="outlined"
                class="mb-2 rounded-lg login-input"
                :rules="[rules.required, rules.min]"
                prepend-inner-icon="mdi-lock-outline"
                :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                required
                autocomplete="current-password"
                hide-details="auto"
                bg-color="surface"
                @click:append-inner="showPassword = !showPassword"
              />

              <div class="d-flex justify-space-between align-center mb-6">
                <v-btn
                  to="/auth/forgot-password"
                  variant="text"
                  color="primary"
                  class="text-body-2 text-none px-2 font-weight-medium"
                  density="comfortable"
                >
                  Quên mật khẩu?
                </v-btn>
              </div>

              <v-btn
                type="submit"
                color="primary"
                variant="flat"
                class="mb-6 rounded-lg text-none login-button"
                height="48"
                block
                :loading="loading"
                :disabled="!isFormValid"
              >
                <v-icon start class="mr-2">mdi-login</v-icon>
                Đăng nhập
              </v-btn>

              <v-divider class="mb-6" />

              <div class="text-center">
                <span class="text-body-2 text-medium-emphasis"
                  >Chưa có tài khoản?
                </span>
                <v-btn
                  to="/auth/register"
                  variant="text"
                  color="primary"
                  class="text-none font-weight-medium px-2"
                >
                  Đăng ký ngay
                </v-btn>
              </div>
            </v-form>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const email = ref("");
const password = ref("");
const showPassword = ref(false);
const loading = ref(false);
const errorMessage = ref("");
const router = useRouter();
const auth = useAuthStore();
const form = ref(null);

// Validation rules
const rules = {
  required: (v) => !!v || "Thông tin này là bắt buộc",
  email: (v) => /.+@.+\..+/.test(v) || "Email không hợp lệ",
  min: (v) => v.length >= 8 || "Mật khẩu phải có ít nhất 8 ký tự",
};

// Tính toán trạng thái hợp lệ của form
const isFormValid = computed(() => {
  return (
    !!email.value && /.+@.+\..+/.test(email.value) && password.value.length >= 8
  );
});

// Kiểm tra nếu đã đăng nhập, thì chuyển hướng khỏi trang login
onMounted(() => {
  if (auth.token) {
    router.push("/"); // hoặc chuyển đến dashboard tùy vai trò
  }
});

const login = async () => {
  try {
    if (!form.value.validate()) {
      return;
    }

    loading.value = true;
    errorMessage.value = "";

    const { data, error } = await useFetch(
      "http://localhost:9090/api/auth/login",
      {
        method: "POST",
        body: {
          email: email.value,
          password: password.value,
        },
      }
    );

    if (error.value) {
      throw new Error(
        error.value?.data?.message ||
          "Vui lòng kiểm tra lại email hoặc mật khẩu!"
      );
    }

    const token = data.value;

    // Tách phần payload
    const base64Payload = token.split(".")[1];
    const payload = JSON.parse(atob(base64Payload));

    auth.login({
      token,
      email: payload.sub,
      role: payload.role,
      userId: payload.userId,
    });

    // Thông báo đăng nhập thành công
    // Bạn cần thêm snackbar vào App.vue hoặc layout

    // Hiệu ứng delay trước khi chuyển trang
    setTimeout(() => {
      if (payload.role === "Admin") {
        router.push("/admin/admin-dashboard");
      } else {
        router.push("/");
      }
    }, 800);
  } catch (e) {
    errorMessage.value = e.message;
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-section {
  min-height: 100vh;
  background-color: #f7f9fc;
}

.login-banner-container {
  background: linear-gradient(135deg, #4f46e5 0%, #2563eb 100%);
  height: 100%;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.login-banner-container::before {
  content: "";
  position: absolute;
  top: -50px;
  right: -50px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.login-banner-container::after {
  content: "";
  position: absolute;
  bottom: -80px;
  left: -80px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.login-banner-content {
  position: relative;
  z-index: 2;
}

.login-banner-text {
  max-width: 500px;
  line-height: 1.7;
}

.login-banner-dots {
  display: flex;
}

.banner-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  margin-right: 8px;
}

.banner-dot:first-child {
  background-color: white;
}

.login-form-container {
  min-height: 100vh;
}

.login-card {
  border: 1px solid rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04) !important;
}

.login-avatar {
  box-shadow: 0 4px 8px rgba(79, 70, 229, 0.25);
}

.login-input :deep(.v-field__outline) {
  opacity: 0.8;
}

.login-input:focus-within :deep(.v-field__outline) {
  opacity: 1;
}

.login-button {
  transition: transform 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.25) !important;
}

/* Animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-form {
  animation: fadeIn 0.5s ease-out forwards;
}

/* Responsive adjustments */
@media (max-width: 959px) {
  .login-form-container {
    padding: 16px;
  }

  .login-card {
    padding: 24px !important;
  }
}
</style>
