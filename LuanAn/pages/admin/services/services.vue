<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý dịch vụ</title>
    <v-row class="justify-space-between mb-4">
      <!-- Nút Quay về -->
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>

      <v-col cols="auto">
        <h1 class="text-h6">Quản lý dịch vụ</h1>
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
      label="Tìm kiếm dịch vụ"
      class="mb-4"
      clearable
    />

    <v-data-table
      :headers="headers"
      :items="filteredUsers"
      :search="search"
      item-value="id"
      class="elevation-1"
    >
      <template #item.imageUrl="{ item }">
        <v-img
          v-if="item.imageUrl"
          :src="item.imageUrl"
          max-width="100"
          max-height="100"
          cover
          class="rounded"
          style="cursor: pointer"
          @click="openImageDialog(item.imageUrl)"
        />
        <span v-else class="text-grey text-caption">Không có ảnh</span>
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

    <!-- Dialog thêm/sửa -->
    <v-dialog v-model="dialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="text-h6">{{
            editedIndex === -1 ? "Thêm dịch vụ" : "Chỉnh sửa dịch vụ"
          }}</span>
        </v-card-title>

        <v-text-field v-model="editedItem.serviceName" label="Tên dịch vụ" />
        <v-text-field v-model="editedItem.price" label="Giá" type="number" />
        <v-textarea v-model="editedItem.description" label="Mô tả" rows="2" />
        <v-text-field v-model="editedItem.imageUrl" label="Đường dẫn ảnh" />

        <v-select
          v-model="editedItem.applianceId"
          :items="appliances"
          item-title="applianceName"
          item-value="applianceId"
          label="Thiết bị"
        />

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" text @click="closeDialog">Hủy</v-btn>
          <v-btn color="blue darken-1" text @click="saveItem">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog
      v-model="showImageDialog"
      max-width="600"
      transition="scale-transition"
      eager
    >
      <v-card>
        <v-img :src="selectedApplianceImage" aspect-ratio="1.6" cover />
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="showImageDialog = false">Đóng</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

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
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const search = ref("");
const dialog = ref(false);
const editedIndex = ref(-1);
const selectedImageUrl = ref("");

const editedItem = ref({
  serviceId: null,
  serviceName: "",
  price: null,
  description: "",
  imageUrl: "",
  applianceId: null,
});
const services = ref([]);
const appliances = ref([]);
const router = useRouter();
const showImageDialog = ref(false);

const headers = [
  { text: "ID", title: "ID", value: "serviceId" },
  { text: "Tên dịch vụ", title: "Tên dịch vụ", value: "serviceName" },
  { text: "Giá", title: "Giá", value: "price" },
  { text: "Mô tả", title: "Mô tả", value: "description" },
  { text: "Hình ảnh", title: "Hình ảnh", value: "imageUrl" },
  { text: "Thiết bị", title: "Thiết bị", value: "applianceName" }, // hiển thị tên thiết bị
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];

const filteredUsers = computed(() => {
  return services.value.map((s) => ({
    ...s,
    applianceName: s.appliance?.applianceName || "",
  }));
});

const selectedApplianceImage = computed(() => {
  const appliance = appliances.value.find(
    (a) => a.applianceId === editedItem.value.applianceId
  );
  return appliance?.imageUrl || "";
});

function getServices() {
  axios.get("http://localhost:9090/api/services").then((res) => {
    services.value = res.data;
  });
}

function getAppliances() {
  axios.get("http://localhost:9090/api/appliances").then((res) => {
    appliances.value = res.data;
  });
}

function editItem(item) {
  editedIndex.value = services.value.findIndex(
    (s) => s.serviceId === item.serviceId
  );
  editedItem.value = {
    serviceId: item.serviceId,
    serviceName: item.serviceName,
    price: item.price,
    description: item.description,
    imageUrl: item.imageUrl,
    applianceId: item.appliance?.applianceId || item.applianceId || null,
  };
  dialog.value = true;
}

function deleteItem(item) {
  if (confirm("Bạn có chắc muốn xoá dịch vụ này?")) {
    axios
      .put(`http://localhost:9090/api/services/delete/${item.serviceId}`)
      .then(() => {
        getServices();
      });
  }
}

function saveItem() {
  if (editedIndex.value === -1) {
    axios
      .post("http://localhost:9090/api/services", editedItem.value)
      .then(() => {
        getServices();
        closeDialog();
      });
  } else {
    axios
      .put(
        `http://localhost:9090/api/services/${editedItem.value.serviceId}`,
        editedItem.value
      )
      .then(() => {
        getServices();
        closeDialog();
      });
  }
}

function closeDialog() {
  dialog.value = false;
  editedIndex.value = -1;
  editedItem.value = {
    serviceId: null,
    serviceName: "",
    price: null,
    description: "",
    applianceId: null,
  };
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

function goBack() {
  router.back();
}

function openImageDialog(url) {
  selectedImageUrl.value = url;
  showImageDialog.value = true;
}

onMounted(() => {
  getServices();
  getAppliances();
});
</script>
