<template>
  <v-container fluid class="pa-0">
    <title>Giới thiệu</title>
    <!-- Hero section -->
    <v-parallax
      src="https://cdn-kvweb.kiotviet.vn/kiotviet-website/wp-content/uploads/2015/09/businessman_questions.png"
      height="400"
    >
      <v-row class="fill-height" align="center" justify="center">
        <v-col class="text-center text-red">
          <h1 class="display-2 font-weight-bold">Giới thiệu</h1>
          <h4 class="mt-2">Dịch vụ sửa chữa thiết bị gia dụng đáng tin cậy</h4>
        </v-col>
      </v-row>
    </v-parallax>

    <!-- Nội dung giới thiệu -->
    <v-container class="py-12">
      <v-row>
        <v-col cols="12" md="6">
          <v-img
            src="https://cdn.thuvienphapluat.vn/uploads/Hoidapphapluat/2023/NTCK/thang-7/dich-vu-sua-chua.jpg"
            height="100%"
            class="rounded-xl"
            cover
          />
        </v-col>
        <v-col cols="12" md="6">
          <h2 class="font-weight-bold mb-4">Chúng tôi là ai?</h2>
          <p class="text-body-1 mb-4">
            Chúng tôi là một đội ngũ kỹ thuật viên chuyên nghiệp, tận tâm và
            giàu kinh nghiệm trong lĩnh vực sửa chữa các thiết bị gia dụng như
            máy giặt, tủ lạnh, điều hòa, máy nước nóng,...
          </p>
          <p class="text-body-1 mb-4">
            Với phương châm <strong>“Uy tín tạo nên thương hiệu”</strong>, chúng
            tôi luôn đặt khách hàng làm trung tâm trong mọi hoạt động, đảm bảo
            sự hài lòng tối đa với từng dịch vụ được cung cấp.
          </p>
          <v-btn color="primary" to="/services" size="large"
            >Đặt lịch sửa chữa</v-btn
          >
        </v-col>
      </v-row>
    </v-container>

    <!-- Sứ mệnh & tầm nhìn -->
    <v-container class="py-12 grey lighten-4">
      <v-row>
        <v-col cols="12" md="6">
          <h3 class="font-weight-bold mb-3">🎯 Sứ mệnh</h3>
          <p class="text-body-1">
            Mang đến giải pháp sửa chữa nhanh chóng, hiệu quả và tiết kiệm nhất
            cho mọi gia đình, giúp khách hàng yên tâm sử dụng các thiết bị trong
            cuộc sống hàng ngày.
          </p>
        </v-col>
        <v-col cols="12" md="6">
          <h3 class="font-weight-bold mb-3">🚀 Tầm nhìn</h3>
          <p class="text-body-1">
            Trở thành đơn vị hàng đầu tại Việt Nam trong lĩnh vực sửa chữa thiết
            bị gia dụng, với hệ thống dịch vụ rộng khắp và đội ngũ kỹ thuật viên
            chuyên nghiệp.
          </p>
        </v-col>
      </v-row>
    </v-container>

    <!-- Đội ngũ kỹ thuật viên -->
    <v-container class="py-12">
      <h2 class="text-center font-weight-bold mb-8">Đội ngũ kỹ thuật viên</h2>
      <v-row>
        <v-col
          v-for="tech in technicians"
          :key="tech.name"
          cols="12"
          sm="6"
          md="3"
        >
          <v-card class="text-center rounded-xl">
            <v-img :src="tech.image" height="180px" cover />
            <v-card-title class="font-weight-bold">{{
              tech.name
            }}</v-card-title>
            <v-card-subtitle class="text-grey">{{
              tech.position
            }}</v-card-subtitle>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-container>
</template>

<script setup>
const technicians = [
  {
    name: "Nguyễn Văn A",
    position: "Kỹ thuật viên máy giặt",
    image: "https://png.pngtree.com/background/20230323/original/pngtree-the-repairman-working-with-power-drill-in-workshop-repairman-working-with-picture-image_2161260.jpg",
  },
  {
    name: "Trần Nam B",
    position: "Chuyên viên tủ lạnh",
    image: "https://png.pngtree.com/thumb_back/fw800/background/20230324/pngtree-engineer-repairing-machine-repair-maintenance-photo-image_2109779.jpg",
  },
  {
    name: "Lê Văn C",
    position: "Thợ điều hòa",
    image: "https://th.bing.com/th/id/OIP.ieVTpjE2XpXQPr_o-Fpw_gAAAA?w=390&h=593&rs=1&pid=ImgDetMain",
  },
  {
    name: "Phạm Thị D",
    position: "Hỗ trợ kỹ thuật",
    image: "https://th.bing.com/th/id/OIP.0K1GMCSI2_alWKTNKsIFJwHaHa?w=800&h=800&rs=1&pid=ImgDetMain",
  },
];
</script>
