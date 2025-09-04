<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Thống kê bài viết</title>
    <h1 class="text-h5 mb-6">Thống kê bài viết</h1>
    <v-col cols="auto">
      <v-btn color="secondary" @click="goBack">
        <v-icon left>mdi-arrow-left</v-icon> Quay về
      </v-btn>
    </v-col>
    <!-- Bộ lọc thời gian -->
    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="4">
        <v-text-field v-model="filter.fromDate" label="Từ ngày" type="date" />
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <v-text-field v-model="filter.toDate" label="Đến ngày" type="date" />
      </v-col>
      <v-col cols="12" sm="12" md="4" class="d-flex align-end">
        <v-btn color="primary" @click="exportToExcel">
          <v-icon left>mdi-download</v-icon> Xuất Excel
        </v-btn>
      </v-col>
    </v-row>

    <!-- Biểu đồ -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card class="pa-4">
          <h2 class="text-subtitle-1 mb-2">Biểu đồ trạng thái bài viết</h2>
          <Bar :data="chartData" :options="chartOptions" />
        </v-card>
      </v-col>
    </v-row>

    <!-- Bảng dữ liệu
    <v-card class="mt-6 pa-4">
      <h2 class="text-subtitle-1 mb-4">Danh sách bài viết</h2>
      <v-data-table
        :headers="headers"
        :items="filteredArticles"
        item-value="id"
        class="elevation-1"
      >
        <template #item.status="{ item }">
          <v-chip :color="getStatusColor(item.status)" dark>
            {{ item.status }}
          </v-chip>
        </template>
      </v-data-table>
    </v-card> -->
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios"; // 👈 Import axios
import { useRouter } from "vue-router";
import { Bar } from "vue-chartjs";
import ExcelJS from "exceljs";
import { saveAs } from "file-saver";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
);

const router = useRouter();
const articles = ref([]); // 👈 ban đầu để mảng rỗng

const statusMap = {
  Pending: "Chờ duyệt",
  Approved: "Đã duyệt",
  Rejected: "Bị từ chối",
};

const mapStatus = (status) => statusMap[status] || "Không rõ";

const filter = ref({
  fromDate: "",
  toDate: "",
});

// Gọi API khi mounted
onMounted(async () => {
  try {
    const response = await axios.get("http://localhost:9090/api/articles");

    articles.value = response.data.map((article) => ({
      ...article,
      status: mapStatus(article.status), // 👈 chuyển trạng thái về tiếng Việt
    }));
  } catch (error) {
    console.error("Lỗi khi tải bài viết:", error);
  }
});

// Bộ lọc bài viết theo ngày
const filteredArticles = computed(() => {
  if (!filter.value.fromDate && !filter.value.toDate) return articles.value;

  return articles.value.filter((article) => {
    const createdAt = new Date(article.createdAt);
    const from = filter.value.fromDate ? new Date(filter.value.fromDate) : null;
    const to = filter.value.toDate ? new Date(filter.value.toDate) : null;

    return (!from || createdAt >= from) && (!to || createdAt <= to);
  });
});

/* Bảng headers
const headers = [
  { text: "ID", value: "id" },
  { text: "Tiêu đề", value: "title" },
  { text: "Tác giả", value: "author" },
  { text: "Trạng thái", value: "status" },
  { text: "Ngày tạo", value: "createdAt" },
];

const getStatusColor = (status) => {
  switch (status) {
    case "Đã duyệt":
      return "green";
    case "Chờ duyệt":
      return "orange";
    case "Bị từ chối":
      return "red";
    default:
      return "grey";
  }
};
*/

const chartData = computed(() => {
  const statusCount = { "Đã duyệt": 0, "Chờ duyệt": 0, "Bị từ chối": 0 };

  for (const article of filteredArticles.value) {
    statusCount[article.status] = (statusCount[article.status] || 0) + 1;
  }

  return {
    labels: Object.keys(statusCount),
    datasets: [
      {
        label: "Số lượng",
        data: Object.values(statusCount),
        backgroundColor: ["#4caf50", "#ff9800", "#f44336"],
      },
    ],
  };
});

const chartOptions = {
  responsive: true,
  plugins: {
    legend: { position: "top" },
    title: { display: true, text: "Thống kê trạng thái bài viết" },
  },
};

// Xuất Excel
async function exportToExcel() {
  const workbook = new ExcelJS.Workbook();
  const worksheet = workbook.addWorksheet("BaiViet");

  worksheet.columns = [
    { header: "ID", key: "id", width: 10 },
    { header: "Tiêu đề", key: "title", width: 30 },
    { header: "Tác giả", key: "author", width: 20 },
    { header: "Trạng thái", key: "status", width: 15 },
    { header: "Ngày tạo", key: "createdAt", width: 15 },
  ];

  filteredArticles.value.forEach((article) => {
    worksheet.addRow(article);
  });

  const buffer = await workbook.xlsx.writeBuffer();
  const blob = new Blob([buffer], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });
  saveAs(blob, "Thong_Ke_Bai_Viet.xlsx");
}

function goBack() {
  router.back();
}
</script>
