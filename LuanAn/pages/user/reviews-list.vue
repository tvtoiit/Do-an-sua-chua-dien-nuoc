<template>
  <v-container>
    <v-card class="pa-4" max-width="800" outlined>
      <v-card-title class="text-h6 font-weight-bold"
        >Đánh giá về dịch vụ</v-card-title
      >

      <v-select
        v-model="selectedServiceId"
        :items="services"
        item-text="name"
        item-value="id"
        label="Chọn dịch vụ"
        variant="outlined"
        class="mb-4"
        density="compact"
        @update:model-value="fetchReviews"
      />

      <v-list v-if="reviews.length > 0">
        <v-list-item v-for="review in reviews" :key="review.id">
          <v-list-item-content>
            <v-list-item-title>
              <v-rating v-model="review.rating" readonly dense color="amber" />
            </v-list-item-title>
            <v-list-item-subtitle v-if="review.comment">
              {{ review.comment }}
            </v-list-item-subtitle>
          </v-list-item-content>
        </v-list-item>
      </v-list>

      <v-alert v-else type="info" class="mt-4">Chưa có đánh giá nào.</v-alert>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";

const services = ref([]);
const selectedServiceId = ref(null);
const reviews = ref([]);

onMounted(async () => {
  const { data } = await useFetch("http://localhost:9090/api/services");
  services.value = data.value || [];
});

const fetchReviews = async () => {
  if (!selectedServiceId.value) return;

  const { data } = await useFetch(
    `http://localhost:9090/api/reviews/service/${selectedServiceId.value}`
  );
  reviews.value = data.value || [];
};
</script>
