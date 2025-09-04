<template>
  <v-container fluid class="pa-0">
    <title>Sửa chữa thiết bị gia dụng</title>
    <!-- Thay thế v-parallax bằng BannerCarousel -->
    <BannerCarousel />

    <!-- Dịch vụ nổi bật -->
    <v-container class="py-12">
      <h2 class="text-center font-weight-bold mb-8">Dịch vụ nổi bật</h2>
      <v-row>
        <v-col
          v-for="service in services"
          :key="service.title"
          cols="12"
          md="4"
        >
          <v-card class="hover:shadow-xl rounded-xl">
            <v-img :src="service.image" height="200px" cover />
            <v-card-title class="text-primary font-weight-bold">{{
              service.title
            }}</v-card-title>
            <v-card-text>{{ service.description }}</v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Lý do chọn chúng tôi -->
    <v-container class="py-12 grey lighten-4">
      <h2 class="text-center font-weight-bold mb-8">Tại sao chọn chúng tôi?</h2>
      <v-row justify="center">
        <v-col
          v-for="reason in reasons"
          :key="reason.title"
          cols="12"
          sm="6"
          md="3"
        >
          <v-card class="pa-4 text-center rounded-xl">
            <v-icon size="48" color="primary">{{ reason.icon }}</v-icon>
            <h4 class="mt-3 font-weight-medium">{{ reason.title }}</h4>
            <p class="text--secondary">{{ reason.content }}</p>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- CTA -->
    <v-container class="py-16 text-center">
      <h2 class="font-weight-bold mb-4">Thiết bị hư hỏng? Đừng lo!</h2>
      <p class="text-lg mb-6">
        Chúng tôi sẽ đến tận nơi kiểm tra và sửa chữa nhanh chóng cho bạn.
      </p>
      <v-btn color="primary" size="x-large" to="/services">
        Đặt lịch sửa chữa
      </v-btn>
    </v-container>
  </v-container>
</template>

<script setup>
// Import component BannerCarousel
import BannerCarousel from '~/components/BannerCarousel.vue';

const services = [
  {
    title: "Sửa máy giặt",
    image:
      "https://dienlanhhaivan.com/upload/images/sua-may-giat-tai-di-an-2.jpg",
    description:
      "Chuyên sửa chữa máy giặt các hãng Panasonic, LG, Samsung,... tại nhà.",
  },
  {
    title: "Sửa tủ lạnh",
    image:
      "https://baohanh-hitachi.com/upload/product/sua-tu-lanh-hitachi-3-8014.jpeg",
    description:
      "Khắc phục nhanh các lỗi tủ lạnh không lạnh, rò nước, kêu to,...",
  },
  {
    title: "Sửa điều hòa",
    image:
      "https://suagap.com/upload_images/images/2021/11/19/sua-chua-dieu-hoa-tai-nha-2.jpg",
    description: "Bảo dưỡng, vệ sinh và sửa chữa điều hòa tận nơi nhanh chóng.",
  },
];

const reasons = [
  {
    icon: "mdi-clock-fast",
    title: "Phục vụ nhanh",
    content: "Có mặt trong 30 phút, hỗ trợ 24/7, kể cả ngày lễ.",
  },
  {
    icon: "mdi-cash-multiple",
    title: "Giá cả hợp lý",
    content: "Báo giá trước khi sửa, không phát sinh chi phí ẩn.",
  },
  {
    icon: "mdi-shield-check",
    title: "Bảo hành dài hạn",
    content: "Bảo hành rõ ràng từ 3 - 12 tháng tùy dịch vụ.",
  },
  {
    icon: "mdi-wrench-cog",
    title: "Thợ chuyên nghiệp",
    content: "Kỹ thuật viên có tay nghề cao, tận tâm và lịch sự.",
  },
];
</script>