<template>
  <v-container>
    <title>Liên hệ</title>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold mb-6">Liên hệ với chúng tôi</h1>
        <v-form ref="formRef" v-model="valid" lazy-validation>
          <!-- Họ và tên -->
          <v-text-field
            v-model="contactForm.name"
            label="Họ và tên"
            :rules="nameRules"
            outlined
            required
            :error-messages="nameErrors"
            @blur="validateName"
          />

          <!-- Email -->
          <v-text-field
            v-model="contactForm.email"
            label="Email"
            :rules="emailRules"
            outlined
            required
            :error-messages="emailErrors"
            @blur="validateEmail"
          />

          <!-- Số điện thoại -->
          <v-text-field
            v-model="contactForm.phone"
            label="Số điện thoại"
            :rules="phoneRules"
            outlined
            required
            :error-messages="phoneErrors"
            @blur="validatePhone"
          />

          <!-- Nội dung yêu cầu -->
          <v-textarea
            v-model="contactForm.message"
            label="Nội dung yêu cầu"
            :rules="messageRules"
            outlined
            required
            :error-messages="messageErrors"
            @blur="validateMessage"
          />

          <!-- Nút gửi -->
          <v-btn :disabled="!valid" color="primary" @click="submitForm">
            Gửi yêu cầu
          </v-btn>
        </v-form>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from "vue";

const valid = ref(false);

const contactForm = ref({
  name: "",
  email: "",
  phone: "",
  message: "",
});

// Các errors messages
const nameErrors = ref([]);
const emailErrors = ref([]);
const phoneErrors = ref([]);
const messageErrors = ref([]);

// Quy tắc xác thực
const nameRules = [
  (v) => !!v || "Họ và tên không được để trống",
  (v) => (v && v.length <= 50) || "Tên không được quá 50 ký tự",
];

const emailRules = [
  (v) => !!v || "Email không được để trống",
  (v) => /.+@.+\..+/.test(v) || "Email không hợp lệ",
];

const phoneRules = [
  (v) => !!v || "Số điện thoại không được để trống",
  (v) => /^[0-9]{10}$/.test(v) || "Số điện thoại phải có 10 chữ số",
];

const messageRules = [(v) => !!v || "Nội dung yêu cầu không được để trống"];

// Các phương thức kiểm tra
const validateName = () => {
  if (!contactForm.value.name) {
    nameErrors.value = ["Họ và tên không được để trống"];
  } else if (contactForm.value.name.length > 50) {
    nameErrors.value = ["Tên không được quá 50 ký tự"];
  } else {
    nameErrors.value = [];
  }
};

const validateEmail = () => {
  if (!contactForm.value.email) {
    emailErrors.value = ["Email không được để trống"];
  } else if (!/.+@.+\..+/.test(contactForm.value.email)) {
    emailErrors.value = ["Email không hợp lệ"];
  } else {
    emailErrors.value = [];
  }
};

const validatePhone = () => {
  if (!contactForm.value.phone) {
    phoneErrors.value = ["Số điện thoại không được để trống"];
  } else if (!/^[0-9]{10}$/.test(contactForm.value.phone)) {
    phoneErrors.value = ["Số điện thoại phải có 10 chữ số"];
  } else {
    phoneErrors.value = [];
  }
};

const validateMessage = () => {
  if (!contactForm.value.message) {
    messageErrors.value = ["Nội dung yêu cầu không được để trống"];
  } else {
    messageErrors.value = [];
  }
};

const formRef = ref(null); // Khai báo tham chiếu tới v-form

const submitForm = async () => {
  if (valid.value) {
    try {
      await $fetch("http://localhost:9090/api/contacts", {
        method: "POST",
        body: {
          name: contactForm.value.name,
          email: contactForm.value.email,
          phone: contactForm.value.phone,
          content: contactForm.value.message,
        },
      });
      alert("Cảm ơn bạn đã gửi yêu cầu! Chúng tôi sẽ phản hồi sớm.");

      contactForm.value = { name: "", email: "", phone: "", message: "" };
      nameErrors.value = [];
      emailErrors.value = [];
      phoneErrors.value = [];
      messageErrors.value = [];
      formRef.value?.reset();
      formRef.value?.resetValidation();
      valid.value = false;
    } catch (error) {
      alert("Lỗi khi gửi liên hệ!");
      console.error(error);
    }
  }
};

</script>
