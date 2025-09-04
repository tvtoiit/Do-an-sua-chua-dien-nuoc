<template>
  <v-container class="pa-4 pa-sm-6 pa-md-8">
    <title>Lịch làm việc</title>
    <v-card class="mb-6 rounded-lg border" elevation="2">
      <v-card-item class="bg-primary">
        <template #prepend>
          <v-icon icon="mdi-wrench" size="large" class="text-white" />
        </template>
        <v-card-title class="text-white text-h5 font-weight-bold">
          Công việc đang thực hiện
        </v-card-title>
        <v-card-subtitle class="text-white pt-2">
          Quản lý tiến độ và cập nhật trạng thái
        </v-card-subtitle>
      </v-card-item>
    </v-card>

    <v-slide-y-transition group>
      <v-card
        v-for="(job, index) in schedule"
        :key="index"
        class="mb-5 rounded-lg border"
        :class="job.status === 'Completed' ? 'border-success' : ''"
        elevation="3"
      >
        <v-card-item
          :class="
            job.status === 'Completed'
              ? 'bg-success-lighten-5'
              : 'bg-grey-lighten-4'
          "
        >
          <template #prepend>
            <v-avatar
              :color="getStatusColor(job.status)"
              size="42"
              class="mr-4"
            >
              <v-icon :icon="getStatusIcon(job.status)" color="white" />
            </v-avatar>
          </template>
          <v-card-title class="font-weight-bold">
            {{ job.title }}
          </v-card-title>
          <v-card-subtitle class="pt-1">
            <v-icon icon="mdi-account" size="small" class="mr-1" />
            {{ job.customer }}
          </v-card-subtitle>
        </v-card-item>

        <v-card-text class="pt-4">
          <v-row>
            <v-col cols="12" sm="6">
              <div class="d-flex align-center mb-2">
                <v-icon
                  icon="mdi-calendar-clock"
                  class="mr-2"
                  color="primary"
                />
                <span class="font-weight-medium">Thời gian hẹn:</span>
                <span class="ml-2">{{ formatAppointmentDate(job.time) }}</span>
              </div>

              <div
                v-if="
                  job.status === 'in-progress' ||
                  job.status === 'WaitingConfirm'
                "
                class="d-flex align-center mb-2"
              >
                <v-icon icon="mdi-clock-start" class="mr-2" color="info" />
                <span class="font-weight-medium">Bắt đầu lúc:</span>
                <span class="ml-2">{{ formatTime(job.startTime) }}</span>
              </div>

              <div
                v-if="
                  job.status === 'in-progress' ||
                  job.status === 'WaitingConfirm'
                "
                class="d-flex align-center mb-2"
              >
                <v-icon icon="mdi-clock-outline" class="mr-2" color="orange" />
                <span class="font-weight-medium"
                  >Dự kiến hoàn thành trong:</span
                >
                <span class="ml-2 font-weight-bold">
                  {{ job.durationMinutes + job.extensions }}
                  phút
                </span>
              </div>

              <div
                v-if="job.extensions && job.extensions > 0 && job.progress > 0"
                class="d-flex align-center mb-2"
              >
                <v-icon
                  icon="mdi-clock-plus-outline"
                  class="mr-2"
                  color="warning"
                />
                <span class="font-weight-medium">Đã gia hạn:</span>
                <span class="ml-2">{{ job.extensions }} phút</span>
              </div>
            </v-col>

            <v-col cols="12" sm="6">
              <v-sheet rounded class="pa-3 bg-grey-lighten-5 mb-3">
                <div class="d-flex justify-space-between align-center mb-2">
                  <span class="font-weight-bold">Tiến độ công việc:</span>
                  <span
                    :class="`font-weight-bold ${getProgressTextColor(
                      job.progress
                    )}`"
                  >
                    {{ Math.min(job.progress, 100).toFixed(0) }}%
                  </span>
                </div>

                <v-progress-linear
                  :model-value="Math.min(job.progress, 100)"
                  height="20"
                  :color="getProgressColor(job.progress)"
                  :class="
                    job.progress === 100
                      ? 'progress-pulse rounded-lg'
                      : 'rounded-lg'
                  "
                  striped
                  animated
                >
                  <template #default>
                    <span class="text-white font-weight-bold"
                      >{{ Math.min(job.progress, 100).toFixed(0) }}%</span
                    >
                  </template>
                </v-progress-linear>
              </v-sheet>
            </v-col>
          </v-row>

          <v-divider class="my-4" />

          <v-row align="center" justify="space-between">
            <v-col cols="12" sm="8">
              <div
                v-if="job.status === 'Completed'"
                class="d-flex align-center"
              >
                <v-icon
                  color="success"
                  icon="mdi-check-circle"
                  size="large"
                  class="mr-3"
                />
                <span class="text-success font-weight-bold"
                  >Công việc đã hoàn thành!</span
                >
              </div>
            </v-col>

            <v-col cols="12" sm="4" class="d-flex justify-end">
              <v-btn
                v-if="job.status === 'Processing'"
                color="primary"
                variant="elevated"
                prepend-icon="mdi-play"
                class="text-none"
                size="large"
                @click="openStartDialog(job)"
              >
                Bắt đầu công việc
              </v-btn>

              <div
                v-else-if="job.status === 'in-progress'"
                class="d-flex flex-column flex-sm-row gap-2"
              >
                <v-btn
                  color="warning"
                  variant="tonal"
                  prepend-icon="mdi-clock-plus"
                  class="text-none mb-2 mb-sm-0"
                  size="small"
                  @click="openExtendDialog(job)"
                >
                  Thêm thời gian
                </v-btn>

                <v-btn
                  color="success"
                  variant="elevated"
                  prepend-icon="mdi-check-circle"
                  class="text-none"
                  size="small"
                  @click="confirmCompletion(job)"
                >
                  Hoàn thành
                </v-btn>
              </div>

              <v-btn
                v-else-if="job.status === 'WaitingConfirm'"
                color="success"
                variant="elevated"
                prepend-icon="mdi-check-circle"
                class="text-none pulse-button"
                size="large"
                @click="confirmCompletion(job)"
              >
                Xác nhận hoàn thành
              </v-btn>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
    </v-slide-y-transition>

    <v-scale-transition>
      <v-card
        v-if="schedule.length === 0"
        class="text-center pa-6 rounded-lg border"
        elevation="1"
      >
        <v-icon icon="mdi-calendar-check" color="grey" size="64" />
        <div class="text-h6 mt-4 text-grey">
          Không có công việc nào đang thực hiện
        </div>
        <div class="text-body-1 text-grey-darken-1 mt-2">
          Hiện tại bạn không có công việc nào cần xử lý
        </div>
      </v-card>
    </v-scale-transition>

    <!-- Dialog bắt đầu công việc -->
    <v-dialog
      v-model="dialogStart"
      max-width="500"
      transition="dialog-bottom-transition"
    >
      <v-card class="rounded-lg">
        <v-toolbar color="primary" class="text-white">
          <v-icon icon="mdi-clock-start" class="mr-2" />
          <v-toolbar-title>Bắt đầu công việc</v-toolbar-title>
          <v-spacer />
          <v-btn icon @click="dialogStart = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-card-text class="pt-6">
          <div v-if="form.job" class="text-body-1 mb-4">
            Bạn sẽ bắt đầu công việc <strong>{{ form.job.title }}</strong> cho
            khách hàng <strong>{{ form.job.customer }}</strong
            >.
          </div>

          <v-text-field
            v-model.number="form.duration"
            label="Thời gian dự kiến (phút)"
            type="number"
            min="10"
            variant="outlined"
            required
            hide-details="auto"
            prepend-inner-icon="mdi-timer-outline"
          />

          <div class="text-caption text-grey mt-2">
            Hãy ước tính thời gian hoàn thành công việc này
          </div>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn
            variant="outlined"
            color="grey"
            prepend-icon="mdi-close"
            class="text-none"
            @click="dialogStart = false"
          >
            Hủy
          </v-btn>
          <v-btn
            color="primary"
            prepend-icon="mdi-play"
            variant="elevated"
            class="text-none"
            @click="confirmStart"
          >
            Bắt đầu ngay
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog cập nhật thêm thời gian -->
    <v-dialog
      v-model="dialogExtend"
      max-width="500"
      transition="dialog-bottom-transition"
    >
      <v-card class="rounded-lg">
        <v-toolbar color="warning" class="text-white">
          <v-icon icon="mdi-clock-plus" class="mr-2" />
          <v-toolbar-title>Gia hạn thời gian</v-toolbar-title>
          <v-spacer />
          <v-btn icon @click="dialogExtend = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-toolbar>

        <v-card-text class="pt-6">
          <div v-if="form.job" class="text-body-1 mb-4">
            <div>
              Công việc: <strong>{{ form.job.title }}</strong>
            </div>
            <div class="mt-1">
              Thời gian hiện tại:
              <strong
                >{{
                  form.job?.durationMinutes + form.job?.extensions
                }}
                phút</strong
              >
            </div>
          </div>

          <v-text-field
            v-model.number="form.extendMinutes"
            label="Thêm thời gian (phút)"
            type="number"
            min="1"
            variant="outlined"
            required
            hide-details="auto"
            prepend-inner-icon="mdi-clock-plus-outline"
          />

          <div class="text-caption text-grey mt-2">
            Nhập số phút cần thêm để hoàn thành công việc
          </div>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer />
          <v-btn
            variant="outlined"
            color="grey"
            prepend-icon="mdi-close"
            class="text-none"
            @click="dialogExtend = false"
          >
            Hủy
          </v-btn>
          <v-btn
            color="warning"
            prepend-icon="mdi-check"
            variant="elevated"
            class="text-none"
            @click="confirmExtend"
          >
            Xác nhận
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color || 'success'"
      location="top"
      timeout="3000"
      rounded="pill"
    >
      <div class="d-flex align-center">
        <v-icon
          v-if="snackbar.color === 'success'"
          icon="mdi-check-circle"
          class="mr-2"
        />
        <span>{{ snackbar.message }}</span>
      </div>

      <template #actions>
        <v-btn icon @click="snackbar.show = false">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import axios from "axios";
import { useCookie } from "#app";

const schedule = ref([]);
const dialogStart = ref(false);
const dialogExtend = ref(false);
const form = ref({
  job: null,
  duration: 120,
  extendMinutes: 30,
});
const snackbar = ref({
  show: false,
  message: "",
  color: "success",
});

let timer;

onMounted(async () => {
  await fetchSchedule();

  // 1. Khôi phục tiến độ từ localStorage
  schedule.value.forEach((job) => {
    const storedProgress = localStorage.getItem(`job-${job.id}-progress`);
    if (storedProgress) {
      job.progress = parseFloat(storedProgress);
    }
  });

  // 2. Kiểm tra trạng thái của từng công việc và tính toán lại tiến độ
  schedule.value.forEach((job) => {
    if (job.status === "in-progress") {
      const now = new Date();
      const start = new Date(job.startTime);
      const elapsed = (now - start) / 60000; // thời gian đã qua tính bằng phút
      const totalDuration = job.durationMinutes + job.extensions;
      const progress = Math.min((elapsed / totalDuration) * 100, 100); // tính tiến độ mới

      // Nếu tiến độ đã tính lại khác với tiến độ đã lưu trong localStorage, cập nhật lại tiến độ
      if (progress !== job.progress) {
        job.progress = progress;
      }
    }
  });

  // Cập nhật tiến độ mỗi giây
  timer = setInterval(updateProgressByTime, 1000);
});


onBeforeUnmount(() => {
  clearInterval(timer);
});

const fetchSchedule = async () => {
  try {
    const userId = useCookie("userId").value;
    snackbar.value = {
      show: true,
      message: "Đang tải danh sách công việc...",
      color: "info",
    };

    const res = await axios.get(
      `http://localhost:9090/api/repair-requests/technician/${userId}?status=Processing&status=in-progress&status=WaitingConfirm`
    );

    schedule.value = res.data.map((item) => ({
      id: item.requestId,
      title: item.appliance?.applianceName || "Không rõ thiết bị",
      customer: item.customer?.fullName || "Không rõ",
      time: item.appointmentDate,
      status: item.status,
      progress: item.progress || 0,  // Đảm bảo lấy đúng tiến độ từ server
      startTime: item.startTime,
      durationMinutes: item.durationMinutes || 0,
      extensions: typeof item.extensions === 'number' ? item.extensions : 0,
    }));

    snackbar.value = {
      show: true,
      message: `Đã tải ${schedule.value.length} công việc`,
      color: "success",
    };
  } catch (error) {
    console.error("Lỗi tải lịch làm việc:", error);
    snackbar.value = {
      show: true,
      message: "Lỗi tải danh sách công việc",
      color: "error",
    };
  }
};


function openStartDialog(job) {
  form.value.job = job;
  form.value.duration = 120;
  dialogStart.value = true;
}

async function confirmStart() {
  const job = form.value.job;
  const startTime = new Date().toISOString();
  const duration = form.value.duration;

  try {
    await axios.put(
      `http://localhost:9090/api/repair-requests/${job.id}/start`,
      {
        startTime,
        durationMinutes: duration,
      }
    );

    job.status = "in-progress";
    job.startTime = startTime;
    job.durationMinutes = duration;
    job.progress = 0;

    snackbar.value = {
      show: true,
      message: `Đã bắt đầu công việc: ${job.title}`,
      color: "info",
    };
  } catch (error) {
    console.error("Lỗi khi bắt đầu công việc:", error);
    snackbar.value = {
      show: true,
      message: "Không thể bắt đầu công việc",
      color: "error",
    };
  }

  dialogStart.value = false;
}

function openExtendDialog(job) {
  form.value.job = job;
  form.value.extendMinutes = 30;
  dialogExtend.value = true;
}

async function confirmExtend() {
  const job = form.value.job;
  const extendMinutes = form.value.extendMinutes;

  try {
    await axios.put(
      `http://localhost:9090/api/repair-requests/${job.id}/extend`,
      {
        extensions: extendMinutes,
      }
    );

    job.extensions = (job.extensions || 0) + extendMinutes;

    console.log("alo" + extendMinutes);
    snackbar.value = {
      show: true,
      message: `Đã thêm ${extendMinutes} phút cho công việc`,
      color: "warning",
    };
  } catch (error) {
    console.error("Lỗi khi kéo dài thời gian:", error);
    snackbar.value = {
      show: true,
      message: "Không thể kéo dài công việc",
      color: "error",
    };
  }

  dialogExtend.value = false;
}

// Khi cập nhật tiến độ
async function updateProgressByTime() {
  const now = new Date();

  for (const job of schedule.value) {
    if (job.status === "in-progress" && job.startTime) {
      const start = new Date(job.startTime);
      const elapsed = (now - start) / 60000;
      const totalDuration = job.durationMinutes + job.extensions;
      const progress = Math.min((elapsed / totalDuration) * 100, 100);

      // Kiểm tra xem tiến độ có thay đổi không
      if (progress !== job.progress) {
        // Lưu tiến độ vào localStorage
        localStorage.setItem(`job-${job.id}-progress`, progress);

        try {
          await axios.put(`http://localhost:9090/api/repair-requests/${job.id}/progress`, {
            progress,
          });
          job.progress = progress;
        } catch (error) {
          console.error(`Lỗi cập nhật tiến độ cho job ${job.id}:`, error);
        }
      }

      // Nếu tiến độ đã đạt 100% và công việc chưa chuyển sang trạng thái "WaitingConfirm"
      if (progress >= 100 && job.status !== "WaitingConfirm") {
        job.status = "WaitingConfirm";
        try {
          await axios.put(`http://localhost:9090/api/repair-requests/${job.id}/status?status=WaitingConfirm`);
        } catch (error) {
          console.error("Lỗi cập nhật trạng thái chờ xác nhận:", error);
        }
      }
    }
  }
}


async function confirmCompletion(job) {
  try {
    await axios.put(
      `http://localhost:9090/api/repair-requests/${job.id}/status?status=Completed`
    );
    job.status = "Completed";

    snackbar.value = {
      show: true,
      message: `🎉 Công việc "${job.title}" đã được hoàn thành thành công!`,
      color: "success",
    };
  } catch (error) {
    console.error("Lỗi hoàn thành công việc:", error);
    snackbar.value = {
      show: true,
      message: "Không thể cập nhật trạng thái hoàn thành",
      color: "error",
    };
  }
}

function formatTime(isoString) {
  if (!isoString) return "";
  const d = new Date(isoString);
  return d.toLocaleTimeString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
  });
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

function getProgressColor(progress) {
  if (progress < 30) return "error";
  if (progress < 70) return "warning";
  return "success";
}

function getProgressTextColor(progress) {
  if (progress < 30) return "text-error";
  if (progress < 70) return "text-warning";
  return "text-success";
}

function getStatusColor(status) {
  switch (status) {
    case "Completed":
    case "WaitingConfirm":
      return "success";
    case "in-progress":
      return "info";
    case "Pending":
    default:
      return "grey";
  }
}

function getStatusIcon(status) {
  switch (status) {
    case "Completed":
      return "mdi-check-circle";
    case "WaitingConfirm":
      return "mdi-clock-check";
    case "in-progress":
      return "mdi-progress-wrench";
    case "Pending":
      return "mdi-clock-outline";
    default:
      return "mdi-help-circle";
  }
}
</script>

<style scoped>
@keyframes pulse {
  0% {
    box-shadow: 0 0 0px rgba(76, 175, 80, 0.7);
  }
  50% {
    box-shadow: 0 0 10px rgba(76, 175, 80, 1);
  }
  100% {
    box-shadow: 0 0 0px rgba(76, 175, 80, 0.7);
  }
}

@keyframes button-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(76, 175, 80, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0);
  }
}

.progress-pulse {
  animation: pulse 2s infinite;
}

.pulse-button {
  animation: button-pulse 1.5s infinite;
}

.border-success {
  border: 2px solid rgba(76, 175, 80, 0.5) !important;
}

/* Responsive styles */
@media (max-width: 600px) {
  .v-card-title {
    font-size: 1.1rem !important;
  }

  .v-card-subtitle {
    font-size: 0.9rem !important;
  }
}
</style>
