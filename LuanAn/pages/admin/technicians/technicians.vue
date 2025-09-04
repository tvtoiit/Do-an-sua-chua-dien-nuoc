<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý thợ sửa chữa</title>
    <v-row class="justify-space-between mb-4">
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>

      <v-col cols="auto">
        <h1 class="text-h6">Quản lý thợ sửa chữa</h1>
      </v-col>

      <v-col cols="auto">
        <v-btn color="primary" @click="openAddDialog">
          <v-icon left>mdi-account-plus</v-icon> Thêm mới
        </v-btn>
      </v-col>
    </v-row>

    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Tìm kiếm thợ sửa chữa"
      class="mb-4"
      clearable
    />

    <v-data-table
      :headers="headers"
      :items="filteredTechnicians"
      :search="search"
      item-value="technicianId"
      class="elevation-1"
    >
      <!-- Nút Xem giấy chứng nhận -->
      <template #item.viewHealthCertificate="{ item }">
        <v-btn
          v-if="item.healthCertificate"
          color="info"
          small
          @click="openPdfDialog(item.healthCertificate)"
        >
          <v-icon left>mdi-file-pdf-box</v-icon> Xem
        </v-btn>
      </template>

      <!-- Nút sửa / xoá -->
      <template #item.actions="{ item }">
        <v-btn icon @click="editItem(item)">
          <v-icon color="primary">mdi-pencil</v-icon>
        </v-btn>
        <v-btn icon @click="deleteItem(item)">
          <v-icon color="red">mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <!-- Dialog thêm/sửa -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h6">
            {{
              editedIndex === -1
                ? "Thêm thợ sửa chữa"
                : "Chỉnh sửa thợ sửa chữa"
            }}
          </span>
        </v-card-title>

        <v-card-text>
          <v-autocomplete
            v-model="editedItem.user"
            :items="users"
            item-title="fullName"
            item-value="userId"
            label="Chọn người dùng"
            return-object
            clearable
          />
          <v-text-field
            v-model="editedItem.experienceYears"
            label="Số năm kinh nghiệm"
            type="number"
          />
          <v-text-field v-model="editedItem.qualification" label="Trình độ" />
          <v-text-field
            v-model="editedItem.healthCertificate"
            label="Giấy chứng nhận sức khỏe (URL PDF)"
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" text @click="closeDialog">Hủy</v-btn>
          <v-btn color="blue darken-1" text @click="saveItem">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog xem PDF -->
    <v-dialog v-model="pdfDialog" max-width="800px">
      <v-card>
        <v-card-title>Xem giấy chứng nhận sức khỏe</v-card-title>
        <v-card-text>
          <iframe
            v-if="pdfUrl"
            :src="pdfUrl"
            width="100%"
            height="600px"
            style="border: none"
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text color="primary" @click="pdfDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>
<script setup>
import axios from "axios";
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const search = ref("");
const dialog = ref(false);
const pdfDialog = ref(false);
const pdfUrl = ref("");

const editedIndex = ref(-1);
const technicians = ref([]);
const users = ref([]);
const router = useRouter();

const editedItem = ref({
  technicianId: null,
  user: null,
  experienceYears: null,
  qualification: "",
  healthCertificate: "",
});

const headers = [
  { text: "ID", title: "ID", value: "technicianId" },
  { text: "Tên", title: "Tên", value: "user.fullName" },
  { text: "Email", title: "Email", value: "user.email" },
  { text: "Trình độ", title: "Trình độ", value: "qualification" },
  { text: "Kinh nghiệm", title: "Kinh nghiệm", value: "experienceYears" },
  { text: "Giấy chứng nhận", title: "Giấy chứng nhận", value: "viewHealthCertificate", sortable: false },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];

onMounted(() => {
  fetchTechnicians();
  fetchUsers();
});

function fetchTechnicians() {
  axios
    .get("http://localhost:9090/api/technicians")
    .then((res) => (technicians.value = res.data))
    .catch((err) => console.error(err));
}

function fetchUsers() {
  axios
    .get("http://localhost:9090/api/users")
    .then((res) => (users.value = res.data))
    .catch((err) => console.error(err));
}

function editItem(item) {
  editedIndex.value = technicians.value.findIndex(
    (t) => t.technicianId === item.technicianId
  );
  editedItem.value = JSON.parse(JSON.stringify(item));
  dialog.value = true;
}

function deleteItem(item) {
  const confirmed = window.confirm(`Bạn có chắc muốn xoá kỹ thuật viên có ID: ${item.technicianId} không?`);
  
  if (confirmed) {
    axios
      .put(`http://localhost:9090/api/technicians/delete/${item.technicianId}`)
      .then(() => fetchTechnicians())
      .catch((err) => console.error(err));
  } else {
    console.log('User cancelled delete');
  }
}


function saveItem() {
  if (editedIndex.value > -1) {
    axios
      .put(
        `http://localhost:9090/api/technicians/${editedItem.value.technicianId}`,
        editedItem.value
      )
      .then(() => fetchTechnicians())
      .catch((err) => console.error(err));
  } else {
    axios
      .post("http://localhost:9090/api/technicians", editedItem.value)
      .then(() => fetchTechnicians())
      .catch((err) => console.error(err));
  }
  closeDialog();
}

const openAddDialog = () => {
  editedIndex.value = -1; // Không chỉnh sửa
  editedItem.value = {
    serviceId: null,
    serviceName: "",
    price: null,
    description: "",
    applianceId: null,
  };
  dialog.value = true;
};

function closeDialog() {
  dialog.value = false;
  editedIndex.value = -1;
  editedItem.value = {
    technicianId: null,
    user: null,
    experienceYears: null,
    qualification: "",
    healthCertificate: "",
  };
}

function openPdfDialog(url) {
  pdfUrl.value = url;
  pdfDialog.value = true;
}

const filteredTechnicians = computed(() => {
  if (!search.value) return technicians.value;
  return technicians.value.filter((t) =>
    t.user?.fullName?.toLowerCase().includes(search.value.toLowerCase())
  );
});

function goBack() {
  router.back();
}
</script>
