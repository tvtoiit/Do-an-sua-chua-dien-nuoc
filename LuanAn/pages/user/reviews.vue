<template>
    <v-container>
      <v-card class="pa-4" max-width="500" outlined>
        <v-card-title class="text-h6 font-weight-bold">Đánh giá dịch vụ</v-card-title>
        <v-form @submit.prevent="submitReview">
          <v-select
            v-model="serviceId"
            :items="services"
            item-value="id"
            item-text="name"
            label="Chọn dịch vụ"
            required
            density="compact"
            variant="outlined"
          />
  
          <v-rating
            v-model="rating"
            color="amber"
            length="5"
            density="compact"
            class="mt-4"
            required
          />
  
          <v-textarea
            v-model="comment"
            label="Nhận xét (tuỳ chọn)"
            rows="3"
            auto-grow
            variant="outlined"
            density="compact"
            class="mt-4"
          />
  
          <v-btn type="submit" color="primary" class="mt-4" block>
            Gửi đánh giá
          </v-btn>
        </v-form>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { useAuthStore } from "@/stores/auth";
  
  const customerId = useAuthStore().userId; // lấy từ auth store
  const serviceId = ref(null);
  const rating = ref(0);
  const comment = ref("");
  
  const services = ref([]); // danh sách dịch vụ
  
  onMounted(async () => {
    const { data } = await useFetch("http://localhost:9090/api/services");
    services.value = data.value || [];
  });
  
  const submitReview = async () => {
    try {
      const { error } = await useFetch("http://localhost:9090/api/reviews", {
        method: "POST",
        params: {
          customerId,
          serviceId: serviceId.value,
          rating: rating.value,
          comment: comment.value || "",
        },
      });
  
      if (error.value) throw new Error("Không thể gửi đánh giá!");
  
      alert("Cảm ơn bạn đã đánh giá!");
      // reset form
      serviceId.value = null;
      rating.value = 0;
      comment.value = "";
    } catch (err) {
      alert(err.message);
    }
  };
  </script>
  