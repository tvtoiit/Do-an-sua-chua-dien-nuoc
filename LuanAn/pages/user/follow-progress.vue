<template>
  <v-container>
    <title>Theo dõi tiến độ</title>
    <h2 class="text-h5 mb-4">Theo dõi tiến độ sửa chữa</h2>

    <v-card v-for="job in customerJobs" :key="job.id" class="mb-4">
      <v-card-title>{{ job.title }}</v-card-title>
      <v-card-text>
        <div>Thời gian hẹn: {{ formatAppointmentDate(job.time) }}</div>
        <div>Tiến độ: {{ Math.min(job.progress, 100).toFixed(0) }}%</div>

        <v-progress-linear
          :model-value="Math.min(job.progress, 100)"
          height="20"
          :color="getProgressColor(job.progress)"
          striped
          animated
          class="mt-2"
        >
          <template #default>
            <strong>{{ Math.min(job.progress, 100).toFixed(0) }}%</strong>
          </template>
        </v-progress-linear>

        <div v-if="job.status === 'completed'" class="text-green mt-2">
          ✅ Tiến trình công việc đã xong, vui lòng đợi xác nhận hoàn thành!
        </div>
        <div
          v-else-if="
            job.status === 'in-progress' ||
            (job.progress > 0 && job.progress < 100)
          "
          class="mt-2"
        >
          Bắt đầu lúc: {{ formatTime(job.startTime) }}<br />
          Dự kiến hoàn thành trong:
          {{ job.durationMinutes + job.extensions }} phút
        </div>
        <div v-else class="text-grey mt-2">🕒 Chưa bắt đầu</div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
import { useCookie } from "#app";

const customerJobs = ref([]);
let timer;

const getUserJobs = async () => {
  const userId = useCookie("userId");
  const customerId = userId.value;
  if (!customerId) return;

  try {
    const response = await axios.get(
      `http://localhost:9090/api/repair-requests/${customerId}`
    );

    // Lọc các job đang trong trạng thái đang xử lý
    customerJobs.value = response.data
      .filter(
        (job) => job.status === "Processing" || job.status === "in-progress"
      )
      .map((job) => ({
        id: job.requestId,
        title:
          job.appliance?.applianceName ||
          job.service?.serviceName ||
          "Không rõ",
        time: job.appointmentDate,
        status: job.status.toLowerCase(), // chuẩn hóa thành lowercase cho Vue xử lý
        startTime: job.startTime,
        progress: job.progress || 0,
        durationMinutes: job.durationMinutes || 0,
        extensions: job.extensions || [],
      }));
  } catch (error) {
    console.error("Lỗi khi tải danh sách công việc của khách hàng:", error);
  }
};

onMounted(async () => {
  await getUserJobs();
  timer = setInterval(updateProgress, 1000);
});

onBeforeUnmount(() => {
  clearInterval(timer);
});

function updateProgress() {
  const now = new Date();
  customerJobs.value.forEach((job) => {
    if (job.status === "in-progress" && job.startTime) {
      const start = new Date(job.startTime);
      const elapsed = (now - start) / 60000; // phút
      const total = job.durationMinutes + job.extensions;
      const newProgress = Math.min((elapsed / total) * 100, 100);

      // Cập nhật trực tiếp nếu backend chưa cập nhật theo thời gian thực
      if (newProgress > job.progress) {
        job.progress = newProgress;
      }

      if (job.progress >= 100 && job.status !== "completed") {
        job.progress = 100;
        job.status = "completed";
      }
    }
  });
}

function formatTime(iso) {
  const d = new Date(iso);
  return d.toLocaleTimeString();
}

function getProgressColor(progress) {
  if (progress < 30) return "red";
  if (progress < 70) return "orange";
  return "green";
}

function formatAppointmentDate(isoString) {
  if (!isoString) return "";
  const d = new Date(isoString);
  return d.toLocaleString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}
</script>
