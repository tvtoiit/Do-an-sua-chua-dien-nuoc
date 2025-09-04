<template>
  <v-container>
    <title>Bảng điều khiển quản trị viên</title>
    <h1 class="text-h5 mb-6">Bảng điều khiển quản trị viên</h1>

    <v-row>
      <!-- Người dùng -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-account-multiple</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý người dùng</h2>
          <v-btn color="primary" class="mt-2" to="/admin/users/users"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Thợ sửa chữa -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-wrench</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý thợ sửa chữa</h2>
          <v-btn
            color="primary"
            class="mt-2"
            to="/admin/technicians/technicians"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Quản lý yêu cầu sửa chữa -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-calendar</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý yêu cầu sửa chữa</h2>
          <v-btn
            color="primary"
            class="mt-2"
            to="/admin/repair-requests/repair-requests"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Bài viết -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-file-document</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý bài viết</h2>
          <v-btn
            color="primary"
            class="mt-2"
            to="/admin/articles/manage-articles"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Dịch vụ -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-hammer-wrench</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý dịch vụ</h2>
          <v-btn color="primary" class="mt-2" to="/admin/services/services"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Thiết bị -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-monitor</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý thiết bị</h2>
          <v-btn color="primary" class="mt-2" to="/admin/appliances/appliances">
            Xem
          </v-btn>
        </v-card>
      </v-col>

      <!-- Thống kê bài viết -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-chart-bar</v-icon>
          <h2 class="text-subtitle-1 mt-2">Thống kê bài viết</h2>
          <v-btn color="primary" class="mt-2" to="/admin/statistics/articles"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Khiếu nại -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-alert-circle</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý liên hệ</h2>
          <v-btn color="primary" class="mt-2" to="/admin/complaints/complaints"
            >Xem</v-btn
          >
        </v-card>
      </v-col>

      <!-- Hợp đồng -->
      <v-col cols="12" sm="6" md="4">
        <v-card class="pa-4">
          <v-icon size="36" color="primary">mdi-file-sign</v-icon>
          <h2 class="text-subtitle-1 mt-2">Quản lý hợp đồng</h2>
          <v-btn color="primary" class="mt-2" to="/admin/contracts/contracts"
            >Xem</v-btn
          >
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>
