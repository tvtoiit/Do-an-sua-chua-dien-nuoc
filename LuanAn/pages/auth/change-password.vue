<template>
  <v-container class="fill-height" fluid>
    <title>Đổi mật khẩu</title>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="pa-6 elevation-12 rounded-xl" :loading="loading">
          <div class="text-center mb-6">
            <v-avatar color="primary" size="56" class="mb-3">
              <v-icon size="32" color="white">mdi-shield-key</v-icon>
            </v-avatar>
            <v-card-title class="text-h5 font-weight-bold justify-center">
              Đổi mật khẩu
            </v-card-title>
            <v-card-subtitle>
              Vui lòng điền thông tin dưới đây để cập nhật mật khẩu của bạn
            </v-card-subtitle>
          </div>

          <v-card-text>
            <v-form
              ref="form"
              v-model="isFormValid"
              @submit.prevent="handleSubmit"
            >
              <v-slide-y-transition>
                <v-alert
                  v-if="alert.show"
                  :type="alert.type"
                  variant="tonal"
                  class="mb-4"
                  closable
                  @click:close="alert.show = false"
                >
                  {{ alert.message }}
                </v-alert>
              </v-slide-y-transition>

              <v-text-field
                v-model="currentPassword"
                label="Mật khẩu hiện tại"
                :type="showCurrentPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock"
                :append-inner-icon="
                  showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye'
                "
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="current-password"
                class="mb-2"
                @click:append-inner="showCurrentPassword = !showCurrentPassword"
              />

              <v-text-field
                v-model="newPassword"
                label="Mật khẩu mới"
                :type="showNewPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock-reset"
                :append-inner-icon="showNewPassword ? 'mdi-eye-off' : 'mdi-eye'"
                variant="outlined"
                :rules="[rules.required, rules.min]"
                autocomplete="new-password"
                class="mb-2"
                @click:append-inner="showNewPassword = !showNewPassword"
              />

              <v-text-field
                v-model="confirmNewPassword"
                label="Xác nhận mật khẩu mới"
                :type="showConfirmPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock-check"
                :append-inner-icon="
                  showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye'
                "
                class="mb-2"
                :rules="[rules.required, passwordMatch]"
                variant="outlined"
                autocomplete="new-password"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
              />

              <v-card-text class="pt-0 px-0">
                <v-progress-linear
                  v-model="passwordStrength.score"
                  height="8"
                  rounded
                  :color="passwordStrength.color"
                  class="mt-3"
                />
                <div class="caption text-right mt-1 text-caption">
                  {{ passwordStrength.text }}
                </div>
              </v-card-text>

              <div class="d-flex flex-column gap-3 mt-4">
                <v-btn
                  type="submit"
                  color="primary"
                  size="large"
                  block
                  :loading="loading"
                  :disabled="!isFormValid || loading"
                >
                  <v-icon start>mdi-check</v-icon>
                  Đổi mật khẩu
                </v-btn>
                <v-btn
                  variant="tonal"
                  color="secondary"
                  size="large"
                  block
                  :disabled="loading"
                  @click="goBack"
                >
                  <v-icon start>mdi-arrow-left</v-icon>
                  Quay lại
                </v-btn>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";

const router = useRouter();
const auth = useAuthStore();
const form = ref(null);
const isFormValid = ref(false);
const loading = ref(false);

// Password fields
const currentPassword = ref("");
const newPassword = ref("");
const confirmNewPassword = ref("");
const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

// Alert state
const alert = ref({
  show: false,
  type: "info",
  message: "",
});

// Form validation rules
const rules = {
  required: (v) => !!v || "Trường này không được để trống",
  min: (v) => (v && v.length >= 8) || "Mật khẩu phải có ít nhất 8 ký tự",
};

const passwordMatch = (v) =>
  v === newPassword.value || "Mật khẩu xác nhận không khớp";

// Password strength calculator
const passwordStrength = computed(() => {
  if (!newPassword.value) {
    return { score: 0, color: "grey", text: "Chưa nhập mật khẩu" };
  }

  const password = newPassword.value;
  let score = 0;

  // Kiểm tra độ dài
  if (password.length >= 8) score += 20;
  if (password.length >= 12) score += 10;

  // Kiểm tra chữ thường
  if (/[a-z]/.test(password)) score += 15;

  // Kiểm tra chữ hoa
  if (/[A-Z]/.test(password)) score += 15;

  // Kiểm tra số
  if (/[0-9]/.test(password)) score += 15;

  // Kiểm tra ký tự đặc biệt
  if (/[^A-Za-z0-9]/.test(password)) score += 25;

  // Xác định màu và thông báo dựa trên điểm
  let color = "error";
  let text = "Yếu";

  if (score >= 90) {
    color = "success";
    text = "Rất mạnh";
  } else if (score >= 70) {
    color = "light-green";
    text = "Mạnh";
  } else if (score >= 50) {
    color = "warning";
    text = "Trung bình";
  } else if (score >= 30) {
    color = "orange";
    text = "Yếu";
  }

  return { score, color, text };
});

// Reset alert after 5 seconds
watch(
  () => alert.value.show,
  (newVal) => {
    if (newVal) {
      setTimeout(() => {
        alert.value.show = false;
      }, 5000);
    }
  }
);

const showAlert = (type, message) => {
  alert.value = {
    show: true,
    type,
    message,
  };
};

const handleSubmit = async () => {
  if (!isFormValid.value) {
    form.value?.validate();
    return;
  }

  if (newPassword.value === currentPassword.value) {
    showAlert("warning", "Mật khẩu mới không được trùng với mật khẩu hiện tại");
    return;
  }

  loading.value = true;

  try {
    const res = await fetch("http://localhost:9090/api/auth/change-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${auth.token}`,
      },
      body: JSON.stringify({
        email: auth.email,
        currentPassword: currentPassword.value,
        newPassword: newPassword.value,
        confirmPassword: confirmNewPassword.value,
      }),
    });

    const responseText = await res.text();

    if (!res.ok) {
      showAlert("error", responseText || "Đã xảy ra lỗi khi đổi mật khẩu");
      return;
    }

    showAlert("success", "Đổi mật khẩu thành công!");

    // Reset form
    currentPassword.value = "";
    newPassword.value = "";
    confirmNewPassword.value = "";

    // Redirect after successful password change
    setTimeout(() => {
      router.push("/");
    }, 2000);
  } catch (e) {
    console.error(e);
    showAlert("error", "Lỗi kết nối đến máy chủ!");
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>
