<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý bài viết</title>
    <h2 class="mb-4 text-h5 font-weight-bold">Quản lý bài viết</h2>
    <v-col cols="auto">
      <v-btn color="secondary" @click="goBack">
        <v-icon left>mdi-arrow-left</v-icon> Quay về
      </v-btn>
    </v-col>
    <v-data-table
      :headers="headers"
      :items="articles"
      item-value="articleId"
      class="elevation-1"
    >
      <template #item.createdAt="{ item }">
        {{ formatDateTime(item.createdAt) }}
      </template>

      <template #item.status="{ item }">
        <v-chip
          :color="
            item.status === 'Approved'
              ? 'green'
              : item.status === 'Rejected'
              ? 'red'
              : 'orange'
          "
          dark
        >
          {{ item.status }}
        </v-chip>
      </template>

      <template #item.actions="{ item }">
        <v-btn color="info" icon @click="viewDetails(item)">
          <v-icon>mdi-eye</v-icon>
        </v-btn>

        <v-btn
          v-if="item.status === 'Pending'"
          color="success"
          icon
          @click="approveArticle(item.articleId)"
        >
          <v-icon>mdi-check</v-icon>
        </v-btn>

        <v-btn
          v-if="item.status === 'Pending'"
          color="error"
          icon
          @click="rejectArticle(item.articleId)"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-data-table>
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title class="text-h6 font-weight-bold">
          {{ selectedArticle?.title }}
        </v-card-title>
        <v-card-subtitle>
          Tác giả: {{ selectedArticle?.author?.fullName }}<br />
          Ngày tạo: {{ formatDateTime(selectedArticle?.createdAt) }}
        </v-card-subtitle>

        <v-card-text>
          <div>{{ selectedArticle?.content }}</div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="primary" text @click="dialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useAsyncData } from "#app";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const router = useRouter();
const dialog = ref(false);
const selectedArticle = ref(null);

const formatDateTime = (dateStr) => {
  const date = new Date(dateStr);
  const day = date.getDate().toString().padStart(2, "0");
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const year = date.getFullYear();
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  return `${day}/${month}/${year} ${hours}:${minutes}`;
};

// Kiểm tra nếu đã đăng nhập, thì chuyển hướng khỏi trang login
onMounted(() => {
  if (auth.role !== "Admin") {
    alert("Bạn không có quyền truy cập!");
    router.push("/"); // hoặc chuyển đến dashboard tùy vai trò
  }
});

const { data: articlesData, error } = await useAsyncData("articles", () =>
  $fetch("http://localhost:9090/api/articles")
);

// Tạo biến reactive để bind với bảng
const articles = ref([]);
if (!error.value && Array.isArray(articlesData.value)) {
  articles.value = articlesData.value;
}

// Khai báo headers cho bảng
const headers = [
  { text: "ID", title: "ID", value: "articleId" },
  { text: "Tiêu đề", title: "Tiêu đề", value: "title" },
  { text: "Tác giả", title: "Tác giả", value: "author.fullName" },
  { text: "Ngày tạo", title: "Ngày tạo", value: "createdAt" },
  { text: "Trạng thái", title: "Trạng thái", value: "status" },
  { text: "Thông báo", title: "Thông báo", value: "reason" },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];

// Hàm duyệt bài viết
const approveArticle = async (id) => {
  try {
    await $fetch(`http://localhost:9090/api/articles/approve/${id}`, {
      method: "PUT",
    });
    alert("Bài viết đã được duyệt");
    // Tải lại danh sách bài viết sau khi duyệt
    const updated = await $fetch("http://localhost:9090/api/articles");
    articles.value = Array.isArray(updated) ? updated : [];
  } catch (err) {
    alert("Duyệt thất bại");
    if (err);
  }
};

const rejectArticle = async (id) => {
  try {
    await $fetch(`http://localhost:9090/api/articles/reject/${id}`, {
      method: "PUT",
    });
    alert("Bài viết đã bị từ chối");
    // Tải lại danh sách bài viết sau khi từ chối
    const updated = await $fetch("http://localhost:9090/api/articles");
    articles.value = Array.isArray(updated) ? updated : [];
  } catch (err) {
    alert("Từ chối thất bại");
    if (err);
  }
};

const viewDetails = (item) => {
  selectedArticle.value = item;
  dialog.value = true;
};

function goBack() {
  router.back();
}
</script>
