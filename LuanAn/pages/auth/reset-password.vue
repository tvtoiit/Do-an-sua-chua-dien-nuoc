<template>
  <v-container class="fill-height" fluid>
    <title>Đặt lại mật khẩu</title>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="5" lg="4">
        <v-card class="pa-6 elevation-12 rounded-xl border" style="border-color: #e0e0e0;">
          <div class="d-flex justify-center mb-4">
            <v-avatar color="primary" size="64" class="elevation-4">
              <v-icon size="36" color="white">mdi-lock-reset</v-icon>
            </v-avatar>
          </div>
          
          <v-card-title class="text-h5 font-weight-bold text-center pb-2">
            Đặt lại mật khẩu
          </v-card-title>
          
          <v-card-subtitle class="text-center pb-4">
            Nhập email và mã xác nhận để đặt lại mật khẩu của bạn
          </v-card-subtitle>

          <v-card-text>
            <v-form ref="form" v-model="isFormValid" lazy-validation @submit.prevent="resetPassword">
              <v-text-field
                v-model="email"
                :rules="emailRules"
                label="Email"
                type="email"
                prepend-inner-icon="mdi-email-outline"
                variant="outlined"
                density="comfortable"
                class="mb-3"
                placeholder="example@domain.com"
                required
                autocomplete="email"
                hide-details="auto"
              />

              <v-text-field
                v-model="code"
                :rules="codeRules"
                label="Mã xác nhận"
                prepend-inner-icon="mdi-shield-key-outline"
                variant="outlined"
                density="comfortable"
                class="mb-6"
                placeholder="Nhập mã 6 số"
                required
                maxlength="6"
                hide-details="auto"
                autocomplete="one-time-code"
              />

              <div class="d-flex flex-column gap-4">
                <v-btn 
                  type="submit" 
                  color="primary" 
                  size="large"
                  :loading="isLoading"
                  :disabled="!isFormValid || isLoading"
                  block
                  elevation="2"
                  class="text-none rounded-lg"
                >
                  <v-icon start>mdi-lock-reset</v-icon>
                  Đặt lại mật khẩu
                </v-btn>
                
                <v-btn 
                  variant="text" 
                  color="secondary" 
                  class="text-none"
                  :disabled="isLoading"
                  @click="goToLogin"
                >
                  <v-icon start>mdi-arrow-left</v-icon>
                  Quay lại đăng nhập
                </v-btn>
              </div>
            </v-form>
          </v-card-text>
        </v-card>
        
        <v-snackbar
          v-model="snackbar.show"
          :color="snackbar.color"
          :timeout="3000"
          location="top"
          rounded="pill"
        >
          {{ snackbar.text }}
          <template #actions>
            <v-btn
              variant="text"
              @click="snackbar.show = false"
            >
              Đóng
            </v-btn>
          </template>
        </v-snackbar>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const form = ref(null);
const email = ref("");
const code = ref("");
const isFormValid = ref(false);
const isLoading = ref(false);

const snackbar = reactive({
  show: false,
  text: "",
  color: "success",
});

const emailRules = [
  v => !!v || "Email là bắt buộc",
  v => /.+@.+\..+/.test(v) || "Email không hợp lệ"
];

const codeRules = [
  v => !!v || "Mã xác nhận là bắt buộc",
  v => /^\d{6}$/.test(v) || "Mã xác nhận phải là 6 chữ số"
];

const resetPassword = async () => {
  const isValid = await form.value?.validate();
  
  if (!isValid?.valid) return;
  
  isLoading.value = true;
  
  try {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const { error, data } = await useFetch(
      "http://localhost:9090/api/auth/reset-password",
      {
        method: "POST",
        body: {
          email: email.value,
          otp: code.value,
        },
      }
    );

    if (error.value) {
      showSnackbar("Mã xác nhận không hợp lệ hoặc đã hết hạn", "error");
      return;
    }

    showSnackbar("Đặt lại mật khẩu thành công", "success");
    
    // Chuyển đến trang đăng nhập sau 1.5 giây
    setTimeout(() => {
      router.push("/auth/login");
    }, 1500);
    
  } catch (err) {
    showSnackbar("Đã xảy ra lỗi, vui lòng thử lại sau", "error");
    console.error(err);
  } finally {
    isLoading.value = false;
  }
};

const goToLogin = () => {
  router.push("/auth/login");
};

const showSnackbar = (text, color = "success") => {
  snackbar.text = text;
  snackbar.color = color;
  snackbar.show = true;
};
</script>