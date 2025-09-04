<template>
  <v-container fluid class="fill-height auth-background">
    <title>Đăng ký</title>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="10" md="8" lg="6">
        <v-card class="register-card elevation-12 rounded-xl">
          <v-row no-gutters>
            <!-- Banner bên trái -->
            <v-col cols="12" md="5" class="d-none d-md-flex">
              <v-sheet
                class="register-banner fill-height d-flex flex-column justify-center align-center text-white"
              >
                <v-img
                  src="/api/placeholder/600/800"
                  alt="Register"
                  class="register-image"
                />
                <div
                  class="register-overlay pa-6 d-flex flex-column justify-end"
                >
                  <h2 class="text-h4 font-weight-bold mb-2">Chào mừng bạn!</h2>
                  <p class="text-subtitle-1">
                    Tham gia cùng chúng tôi để trải nghiệm dịch vụ tốt nhất.
                  </p>
                </div>
              </v-sheet>
            </v-col>

            <!-- Form bên phải -->
            <v-col cols="12" md="7">
              <v-card-item>
                <v-card-title
                  class="text-h5 font-weight-bold text-center primary--text pt-6"
                >
                  {{
                    currentStep === "register"
                      ? "Đăng ký tài khoản"
                      : "Xác thực OTP"
                  }}
                </v-card-title>
                <v-card-subtitle class="text-center text-medium-emphasis pt-2">
                  {{
                    currentStep === "register"
                      ? "Điền thông tin cá nhân để tạo tài khoản mới"
                      : "Nhập mã xác thực đã gửi đến email của bạn"
                  }}
                </v-card-subtitle>
              </v-card-item>

              <v-card-text>
                <!-- Form đăng ký -->
                <v-form
                  v-if="currentStep === 'register'"
                  ref="form"
                  v-model="isFormValid"
                  @submit.prevent="register"
                >
                  <v-row dense>
                    <v-col cols="12">
                      <v-text-field
                        v-model="fullName"
                        :rules="nameRules"
                        label="Họ và tên"
                        prepend-inner-icon="mdi-account-outline"
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="email"
                        :rules="emailRules"
                        label="Email"
                        type="email"
                        prepend-inner-icon="mdi-email-outline"
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="phoneNumber"
                        :rules="phoneRules"
                        label="Số điện thoại"
                        prepend-inner-icon="mdi-phone-outline"
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                      />
                    </v-col>

                    <v-col cols="12">
                      <v-text-field
                        v-model="address"
                        :rules="addressRules"
                        label="Địa chỉ"
                        prepend-inner-icon="mdi-map-marker-outline"
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                      />
                    </v-col>

                    <v-col cols="12">
                      <v-select
                        v-model="selectedRole"
                        :items="roleOptions"
                        item-title="label"
                        item-value="value"
                        :rules="roleRules"
                        label="Chọn vai trò"
                        prepend-inner-icon="mdi-account-group-outline"
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="password"
                        :rules="passwordRules"
                        label="Mật khẩu"
                        :type="showPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-lock-outline"
                        :append-inner-icon="
                          showPassword ? 'mdi-eye-off' : 'mdi-eye'
                        "
                        variant="outlined"
                        density="comfortable"
                        required
                        hide-details="auto"
                        class="mb-3"
                        @click:append-inner="showPassword = !showPassword"
                        @input="checkPasswordStrength"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="confirmPassword"
                        :rules="confirmPasswordRules"
                        label="Xác nhận mật khẩu"
                        :type="showConfirmPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-lock-check-outline"
                        :append-inner-icon="
                          showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'
                        "
                        class="mb-3"
                        hide-details="auto"
                        variant="outlined"
                        density="comfortable"
                        required
                        @click:append-inner="
                          showConfirmPassword = !showConfirmPassword
                        "
                      />
                    </v-col>

                    <!-- Thêm đo độ mạnh mật khẩu -->
                    <v-col v-if="password" cols="12">
                      <div class="mb-3">
                        <div
                          class="d-flex justify-space-between align-center mb-1"
                        >
                          <span class="text-caption">Độ mạnh mật khẩu:</span>
                          <span
                            class="text-caption font-weight-medium"
                            :class="strengthColor.text"
                          >
                            {{ passwordStrengthText }}
                          </span>
                        </div>
                        <v-progress-linear
                          :model-value="passwordStrength"
                          height="8"
                          rounded
                          :color="strengthColor.bar"
                          class="rounded-lg"
                        />

                        <div class="d-flex flex-wrap gap-2 mt-2">
                          <v-chip
                            size="small"
                            :color="hasMinLength ? 'success' : 'grey'"
                            :variant="hasMinLength ? 'flat' : 'outlined'"
                            class="text-caption"
                          >
                            <v-icon size="x-small" :start="true">
                              {{ hasMinLength ? "mdi-check" : "mdi-close" }}
                            </v-icon>
                            8+ ký tự
                          </v-chip>

                          <v-chip
                            size="small"
                            :color="hasUpperCase ? 'success' : 'grey'"
                            :variant="hasUpperCase ? 'flat' : 'outlined'"
                            class="text-caption"
                          >
                            <v-icon size="x-small" :start="true">
                              {{ hasUpperCase ? "mdi-check" : "mdi-close" }}
                            </v-icon>
                            Chữ hoa
                          </v-chip>

                          <v-chip
                            size="small"
                            :color="hasNumber ? 'success' : 'grey'"
                            :variant="hasNumber ? 'flat' : 'outlined'"
                            class="text-caption"
                          >
                            <v-icon size="x-small" :start="true">
                              {{ hasNumber ? "mdi-check" : "mdi-close" }}
                            </v-icon>
                            Chữ số
                          </v-chip>

                          <v-chip
                            size="small"
                            :color="hasSpecialChar ? 'success' : 'grey'"
                            :variant="hasSpecialChar ? 'flat' : 'outlined'"
                            class="text-caption"
                          >
                            <v-icon size="x-small" :start="true">
                              {{ hasSpecialChar ? "mdi-check" : "mdi-close" }}
                            </v-icon>
                            Ký tự đặc biệt
                          </v-chip>
                        </div>
                      </div>
                    </v-col>
                  </v-row>

                  <v-card-actions class="px-0 pt-4">
                    <v-btn
                      type="submit"
                      color="primary"
                      variant="elevated"
                      size="large"
                      block
                      :loading="isLoading"
                      :disabled="!isFormValid || isLoading"
                    >
                      <v-icon start>mdi-account-plus</v-icon>
                      Đăng ký ngay
                    </v-btn>
                  </v-card-actions>

                  <div class="d-flex align-center justify-center mt-6">
                    <span class="text-medium-emphasis">Đã có tài khoản?</span>
                    <v-btn
                      to="/auth/login"
                      variant="text"
                      color="primary"
                      class="ml-2"
                    >
                      Đăng nhập
                    </v-btn>
                  </div>
                </v-form>

                <!-- Form nhập OTP -->
                <v-form
                  v-else
                  ref="otpForm"
                  v-model="isOtpFormValid"
                  @submit.prevent="verifyOtp"
                >
                  <div class="text-center mb-6">
                    <v-icon color="primary" size="48" class="mb-3"
                      >mdi-email-check</v-icon
                    >
                    <p class="text-body-1 mb-1">
                      Chúng tôi đã gửi mã OTP đến email
                    </p>
                    <p class="text-body-1 font-weight-bold">{{ email }}</p>
                    <p class="text-caption text-medium-emphasis mt-2">
                      Mã OTP có hiệu lực trong vòng 5 phút
                    </p>
                  </div>

                  <!-- OTP input -->
                  <v-row justify="center" class="mb-6">
                    <v-col cols="8">
                      <v-text-field
                        v-model="otpCode"
                        :rules="otpRules"
                        label="Nhập mã OTP"
                        variant="outlined"
                        density="comfortable"
                        class="text-center"
                        maxlength="6"
                        placeholder="123456"
                        prepend-inner-icon="mdi-lock-check"
                        hide-details="auto"
                        required
                      />
                    </v-col>
                  </v-row>

                  <v-card-actions class="flex-column">
                    <v-btn
                      type="submit"
                      color="primary"
                      variant="elevated"
                      size="large"
                      block
                      :loading="isVerifying"
                      :disabled="!isOtpFormValid || isVerifying"
                      class="mb-3"
                    >
                      <v-icon start>mdi-check-circle</v-icon>
                      Xác nhận
                    </v-btn>

                    <div class="d-flex align-center justify-center mb-3">
                      <span class="text-medium-emphasis me-2"
                        >Chưa nhận được mã?</span
                      >
                      <v-btn
                        variant="text"
                        color="primary"
                        :disabled="resendCooldown > 0 || isResending"
                        :loading="isResending"
                        @click="resendOtp"
                      >
                        Gửi lại
                        {{ resendCooldown > 0 ? `(${resendCooldown}s)` : "" }}
                      </v-btn>
                    </div>

                    <v-btn
                      variant="outlined"
                      color="grey"
                      block
                      :disabled="isVerifying || isResending"
                      @click="currentStep = 'register'"
                    >
                      <v-icon start>mdi-arrow-left</v-icon>
                      Quay lại
                    </v-btn>
                  </v-card-actions>
                </v-form>
              </v-card-text>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- Snackbar thông báo -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
      location="top"
    >
      {{ snackbar.text }}
      <template #actions>
        <v-btn color="white" variant="text" @click="snackbar.show = false">
          Đóng
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onUnmounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const form = ref(null);
const otpForm = ref(null);
const isFormValid = ref(false);
const isOtpFormValid = ref(false);
const isLoading = ref(false);
const isVerifying = ref(false);
const isResending = ref(false);
const showPassword = ref(false);
const showConfirmPassword = ref(false);

// Các bước đăng ký
const currentStep = ref("register"); // 'register' hoặc 'verify'

// Form data
const fullName = ref("");
const email = ref("");
const phoneNumber = ref("");
const address = ref("");
const selectedRole = ref("");
const password = ref("");
const confirmPassword = ref("");

// OTP data
const otpCode = ref("");
const resendCooldown = ref(0);
let resendTimer = null;

// Password strength variables
const passwordStrength = ref(0);
const hasMinLength = ref(false);
const hasUpperCase = ref(false);
const hasNumber = ref(false);
const hasSpecialChar = ref(false);

// Password strength text and color
const passwordStrengthText = computed(() => {
  if (passwordStrength.value === 0) return "Chưa nhập";
  if (passwordStrength.value < 30) return "Rất yếu";
  if (passwordStrength.value < 50) return "Yếu";
  if (passwordStrength.value < 70) return "Trung bình";
  if (passwordStrength.value < 90) return "Mạnh";
  return "Rất mạnh";
});

const strengthColor = computed(() => {
  if (passwordStrength.value === 0) return { bar: "grey", text: "text-grey" };
  if (passwordStrength.value < 30) return { bar: "red", text: "text-red" };
  if (passwordStrength.value < 50)
    return { bar: "orange", text: "text-orange" };
  if (passwordStrength.value < 70)
    return { bar: "amber", text: "text-amber-darken-2" };
  if (passwordStrength.value < 90)
    return { bar: "light-green", text: "text-light-green" };
  return { bar: "success", text: "text-success" };
});

// Check password strength
const checkPasswordStrength = () => {
  const pwd = password.value;
  if (!pwd) {
    passwordStrength.value = 0;
    hasMinLength.value = false;
    hasUpperCase.value = false;
    hasNumber.value = false;
    hasSpecialChar.value = false;
    return;
  }

  // Check conditions
  hasMinLength.value = pwd.length >= 8;
  hasUpperCase.value = /[A-Z]/.test(pwd);
  hasNumber.value = /[0-9]/.test(pwd);
  hasSpecialChar.value = /[!@#$%^&*(),.?":{}|<>_\-\\[\]/+=~]/.test(pwd);

  // Calculate strength
  let strength = 0;

  // Basic length contribution (up to 25 points)
  strength += Math.min(pwd.length * 3, 25);

  // Complexity contributions
  if (hasUpperCase.value) strength += 15;
  if (hasNumber.value) strength += 15;
  if (hasSpecialChar.value) strength += 20;

  // Mix of character types
  const charTypes = [
    /[a-z]/.test(pwd),
    hasUpperCase.value,
    hasNumber.value,
    hasSpecialChar.value,
  ].filter(Boolean).length;

  strength += (charTypes - 1) * 10;

  // Penalize repeated characters and patterns
  const repeats = (pwd.match(/(.)\1+/g) || []).length;
  const sequences = (
    pwd.match(/(abc|bcd|cde|def|efg|123|234|345|456|567|678|789)/gi) || []
  ).length;

  strength -= repeats * 5;
  strength -= sequences * 10;

  // Ensure strength is between 0 and 100
  passwordStrength.value = Math.max(0, Math.min(100, strength));
};

// Validation rules
const nameRules = [
  (v) => !!v || "Họ và tên không được để trống",
  (v) => (v && v.length >= 3) || "Họ và tên phải có ít nhất 3 ký tự",
];

const emailRules = [
  (v) => !!v || "Email không được để trống",
  (v) => /.+@.+\..+/.test(v) || "Email không hợp lệ",
];

const phoneRules = [
  (v) => !!v || "Số điện thoại không được để trống",
  (v) => /^[0-9]{10}$/.test(v) || "Số điện thoại phải có 10 chữ số",
];

const addressRules = [
  (v) => !!v || "Địa chỉ không được để trống",
  (v) => (v && v.length >= 5) || "Địa chỉ phải có ít nhất 5 ký tự",
];

const roleRules = [(v) => !!v || "Vui lòng chọn vai trò"];

const passwordRules = [
  (v) => !!v || "Mật khẩu không được để trống",
  (v) => (v && v.length >= 8) || "Mật khẩu phải có ít nhất 8 ký tự",
  (v) => /[A-Z]/.test(v) || "Mật khẩu phải có ít nhất 1 chữ cái viết hoa",
  (v) => /[0-9]/.test(v) || "Mật khẩu phải có ít nhất 1 chữ số",
  (v) =>
    /[!@#$%^&*(),.?":{}|<>_\-\\[\]/+=~]/.test(v) ||
    "Mật khẩu phải có ít nhất 1 ký tự đặc biệt",
];

const confirmPasswordRules = [
  (v) => !!v || "Xác nhận mật khẩu không được để trống",
  (v) => v === password.value || "Mật khẩu không khớp",
];

const otpRules = [
  (v) => !!v || "Mã OTP không được để trống",
  (v) => /^\d{6}$/.test(v) || "Mã OTP phải có 6 chữ số",
];

// Role options
const roleOptions = [
  { label: "Khách hàng", value: "Customer" },
  { label: "Thợ sửa chữa", value: "Technician" },
];

// Snackbar state
const snackbar = ref({
  show: false,
  text: "",
  color: "success",
});

// Hiển thị thông báo
const showSnackbar = (text, color = "success") => {
  snackbar.value = {
    show: true,
    text,
    color,
  };
};

// Register function
const register = async () => {
  if (!form.value.validate()) return;

  try {
    isLoading.value = true;

    const { error, data } = await useFetch(
      "http://localhost:9090/api/auth/register",
      {
        method: "POST",
        body: {
          fullName: fullName.value,
          email: email.value,
          phoneNumber: phoneNumber.value,
          address: address.value,
          password: password.value,
          role: selectedRole.value,
        },
      }
    );

    if (error.value) {
      showSnackbar("Đăng ký thất bại: " + error.value, "error");
      return;
    }

    showSnackbar("Đã gửi mã OTP đến email của bạn!", "info");
    currentStep.value = "verify"; // Chuyển sang bước nhập OTP
    startResendCooldown();
  } catch (err) {
    showSnackbar("Có lỗi xảy ra, vui lòng thử lại sau", "error");
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

// Verify OTP function
const verifyOtp = async () => {
  if (!otpForm.value.validate()) return;

  try {
    isVerifying.value = true;

    const { error, data } = await useFetch(
      "http://localhost:9090/api/auth/verify-otp",
      {
        method: "POST",
        params: {
          email: email.value,
          otp: otpCode.value,
        },
      }
    );

    if (error.value) {
      showSnackbar("Mã xác thực hết hạn hoặc không chính xác", "error");
      return;
    }

    showSnackbar(
      "Đăng ký thành công! Chuyển hướng đến trang đăng nhập...",
      "success"
    );

    // Đợi 1.5 giây trước khi chuyển hướng
    setTimeout(() => {
      router.push("/auth/login");
    }, 1500);
  } catch (err) {
    showSnackbar("Có lỗi xảy ra, vui lòng thử lại sau", "error");
    console.error(err);
  } finally {
    isVerifying.value = false;
  }
};

// Resend OTP function
const resendOtp = async () => {
  if (resendCooldown.value > 0) return;

  try {
    isResending.value = true;

    const { error, data } = await useFetch(
      "http://localhost:9090/api/auth/resend-otp",
      {
        method: "POST",
        params: {
          email: email.value,
        },
      }
    );

    if (error.value) {
      showSnackbar("Gửi lại mã thất bại: " + error.value, "error");
      return;
    }

    showSnackbar("Đã gửi lại mã OTP mới!", "success");
    startResendCooldown();
    otpCode.value = ""; // Xóa mã cũ
  } catch (err) {
    showSnackbar("Có lỗi xảy ra, vui lòng thử lại sau", "error");
    console.error(err);
  } finally {
    isResending.value = false;
  }
};

// Start resend cooldown timer
const startResendCooldown = () => {
  resendCooldown.value = 60; // 60 seconds cooldown
  clearInterval(resendTimer);
  resendTimer = setInterval(() => {
    if (resendCooldown.value > 0) {
      resendCooldown.value--;
    } else {
      clearInterval(resendTimer);
    }
  }, 1000);
};

// Cleanup on component unmount
onUnmounted(() => {
  clearInterval(resendTimer);
});
</script>

<style scoped>
.auth-background {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f2 100%);
}

.register-card {
  overflow: hidden;
  border-radius: 16px;
}

.register-banner {
  position: relative;
  background-color: var(--v-primary-base);
  height: 100%;
  overflow: hidden;
}

.register-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.6;
}

.register-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, transparent 100%);
  z-index: 1;
}
</style>
