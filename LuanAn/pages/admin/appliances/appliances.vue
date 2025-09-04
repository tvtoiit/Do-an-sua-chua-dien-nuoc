<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý thiết bị</title>
    <v-row class="justify-space-between mb-4">
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>

      <v-col cols="auto">
        <h1 class="text-h6">Quản lý thiết bị</h1>
      </v-col>

      <v-col cols="auto">
        <v-btn color="primary" @click="openAddDialog">
          <v-icon left>mdi-plus</v-icon> Thêm thiết bị
        </v-btn>
      </v-col>
    </v-row>

    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Tìm kiếm thiết bị"
      class="mb-4"
      clearable
    />

    <v-data-table
      :headers="headers"
      :items="filteredAppliances"
      :search="search"
      item-value="applianceId"
      class="elevation-1"
    >
      <template #item.imageUrl="{ item }">
        <v-img
          :src="item.imageUrl"
          max-width="100"
          max-height="100"
          cover
          class="rounded"
          style="cursor: pointer"
          @click="openImageDialog(item.imageUrl)"
        />
      </template>

      <template #item.actions="{ item }">
        <v-btn icon @click="editItem(item)">
          <v-icon color="primary">mdi-pencil</v-icon>
        </v-btn>
        <v-btn icon @click="deleteItem(item)">
          <v-icon color="red">mdi-delete</v-icon>
        </v-btn>
      </template>
    </v-data-table>

    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h6">{{
            editedIndex === -1 ? "Thêm thiết bị" : "Chỉnh sửa thiết bị"
          }}</span>
        </v-card-title>

        <v-card-text>
          <v-text-field
            v-model="editedItem.applianceName"
            label="Tên thiết bị"
          />
          <v-text-field v-model="editedItem.brand" label="Thương hiệu" />
          <v-text-field v-model="editedItem.model" label="Model" />
          <v-text-field v-model="editedItem.imageUrl" label="URL ảnh" />

          <!-- Dropdown cho thợ sửa chữa -->
          <v-select
            v-model="editedItem.technician.technicianId"
            :items="technicianText"
            item-value="value"
            item-title="title"
            label="Chọn thợ sửa chữa"
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" text @click="closeDialog">Hủy</v-btn>
          <v-btn color="blue darken-1" text @click="saveItem">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Dialog hiển thị ảnh -->
    <v-dialog
      v-model="showImageDialog"
      max-width="600"
      transition="scale-transition"
      eager
    >
      <v-card>
        <v-img :src="selectedImageUrl" aspect-ratio="1.6" cover />
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="showImageDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const search = ref("");
const dialog = ref(false);
const appliances = ref([]);
const editedIndex = ref(-1);

const editedItem = ref({
  applianceId: null,
  applianceName: "",
  brand: "",
  model: "",
  imageUrl: "",
  technician: {
    technicianId: null,
  },
});

const showImageDialog = ref(false);
const selectedImageUrl = ref("");

function openImageDialog(url) {
  selectedImageUrl.value = url;
  showImageDialog.value = true;
}

const headers = [
  { text: "ID", title: "ID", value: "applianceId" },
  { text: "Tên thiết bị", title: "Tên thiết bị", value: "applianceName" },
  { text: "Thương hiệu", title: "Thương hiệu", value: "brand" },
  { text: "Model", title: "Số hiệu", value: "model" },
  { text: "Ảnh", title: "Hình ảnh", value: "imageUrl" },
  { text: "ID thợ", title: "ID thợ", value: "technician.technicianId" },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];
// Mảng thợ sửa chữa
const technicians = ref([]); 

// Computed property để tạo danh sách thợ sửa chữa với định dạng 'ID - Tên'
const technicianText = computed(() =>
  technicians.value.map((t) => ({
    title: `${t.technicianId} - ${t.user.fullName}`,
    value: t.technicianId,
  }))
);

onMounted(() => {
  fetchTechnicians(); // Lấy danh sách thợ sửa chữa khi component được gắn vào
  fetchAppliances(); // Lấy danh sách thiết bị
});

function fetchTechnicians() {
  axios
    .get("http://localhost:9090/api/technicians") // Endpoint lấy danh sách thợ sửa chữa
    .then((res) => {
      technicians.value = res.data; // Lưu danh sách thợ sửa chữa vào technicians
    })
    .catch((err) => console.error(err));
}

function fetchAppliances() {
  axios
    .get("http://localhost:9090/api/appliances")
    .then((res) => (appliances.value = res.data))
    .catch((err) => console.error(err));
}

function editItem(item) {
  editedIndex.value = appliances.value.findIndex(
    (a) => a.applianceId === item.applianceId
  );
  editedItem.value = JSON.parse(JSON.stringify(item)); // clone sâu để tách reference

  // Trường hợp không có technician
  if (!editedItem.value.technician) {
    editedItem.value.technician = { technicianId: null };
  }

  dialog.value = true;
}

function deleteItem(item) {
  if (confirm("Bạn có chắc chắn muốn xóa thiết bị này không?")) {
    axios
      .put(`http://localhost:9090/api/appliances/delete/${item.applianceId}`)
      .then(() => fetchAppliances())
      .catch((err) => console.error(err));
  }
}

function saveItem() {
  if (editedIndex.value > -1) {
    // Chỉnh sửa thiết bị
    axios
      .put(
        `http://localhost:9090/api/appliances/${editedItem.value.applianceId}`,
        {
          ...editedItem.value,
          technician: {
            technicianId: editedItem.value.technician.technicianId,
          }, // Chỉ gửi technicianId
        }
      )
      .then(() => fetchAppliances())
      .catch((err) => console.error(err));
  } else {
    // Thêm mới thiết bị
    axios
      .post("http://localhost:9090/api/appliances", {
        ...editedItem.value,
        technician: { technicianId: editedItem.value.technician.technicianId }, // Chỉ gửi technicianId
      })
      .then(() => fetchAppliances())
      .catch((err) => console.error(err));
  }
  closeDialog();
}

function openAddDialog() {
  editedIndex.value = -1;
  editedItem.value = {
    applianceName: "",
    brand: "",
    model: "",
    imageUrl: "",
    technician: {
      technicianId: null,
    },
  };
  dialog.value = true;
}

function closeDialog() {
  dialog.value = false;
  editedIndex.value = -1;
  editedItem.value = {
    applianceId: null,
    applianceName: "",
    brand: "",
    model: "",
    imageUrl: "",
    technician: {
      technicianId: null,
    },
  };
}

const filteredAppliances = computed(() => {
  if (!search.value) return appliances.value;
  return appliances.value.filter((item) =>
    item.applianceName?.toLowerCase().includes(search.value.toLowerCase())
  );
});

function goBack() {
  router.back();
}
</script>
