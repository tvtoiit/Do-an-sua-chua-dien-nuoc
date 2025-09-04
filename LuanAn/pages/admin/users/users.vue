<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý người dùng</title>
    <v-row class="justify-space-between mb-4">
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>

      <v-col cols="auto">
        <h1 class="text-h6">Quản lý người dùng</h1>
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
      label="Tìm kiếm người dùng"
      class="mb-4"
      clearable
    />

    <v-data-table
      :headers="headers"
      :items="filteredUsers"
      :search="search"
      item-value="userId"
      class="elevation-1"
    >
      <template #item.createdAt="{ item }">
        <span>{{ formattedDate(item.createdAt) }}</span>
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
            editedIndex === -1 ? "Thêm người dùng" : "Chỉnh sửa người dùng"
          }}</span>
        </v-card-title>

        <v-card-text>
          <v-text-field v-model="editedItem.fullName" label="Họ tên" />
          <v-text-field v-model="editedItem.email" label="Email" />
          <v-text-field
            v-model="editedItem.phoneNumber"
            label="Số điện thoại"
          />
          <v-text-field v-model="editedItem.address" label="Địa chỉ" />
          <v-text-field
            v-model="editedItem.role"
            label="Vai trò (Customer/Admin/Technician)"
          />
          <v-text-field
            v-model="editedItem.password"
            label="Mật khẩu (mock)"
            type="password"
          />
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" text @click="closeDialog">Hủy</v-btn>
          <v-btn color="blue darken-1" text @click="saveItem">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const search = ref("");
const dialog = ref(false);
const users = ref([]);
const editedIndex = ref(-1);
const formattedDate = (dateString) => {
  return new Date(dateString).toISOString().replace("T", " ").substring(0, 19);
};

const editedItem = ref({
  userId: null,
  fullName: "",
  email: "",
  phoneNumber: "",
  address: "",
  password: "",
  passwordHash: "",
  role: "Customer",
  createdAt: new Date().toISOString(),
});

const headers = [
  { text: "ID", title: "ID", value: "userId" },
  { text: "Họ tên", title: "Họ tên", value: "fullName" },
  { text: "Email", title: "Email", value: "email" },
  { text: "SĐT", title: "Số điện thoại", value: "phoneNumber" },
  { text: "Địa chỉ", title: "Địa chỉ", value: "address" },
  { text: "Vai trò", title: "Vai trò", value: "role" },
  { text: "Ngày tạo", title: "Ngày tạo", value: "createdAt" },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];

// Gọi API khi load
onMounted(fetchUsers);

function fetchUsers() {
  axios
    .get("http://localhost:9090/api/users")
    .then((res) => (users.value = res.data))
    .catch((err) => console.error(err));
}

function editItem(item) {
  editedIndex.value = users.value.findIndex((u) => u.userId === item.userId);
  editedItem.value = { ...item };
  dialog.value = true;
}

function deleteItem(item) {
  // Hiển thị hộp thoại xác nhận
  const confirmed = window.confirm(`Bạn có chắc muốn xoá thông tin ${item.fullName} không?`);
  
  if (confirmed) {
    axios
      .put(`http://localhost:9090/api/users/delete/${item.userId}`)
      .then(() => fetchUsers())
      .catch((err) => console.error(err));
  } else {
    // Nếu không xác nhận thì không làm gì
    console.log('User cancelled delete');
  }
}


function saveItem() {
  if (editedIndex.value > -1) {
    axios
      .put(
        `http://localhost:9090/api/users/${editedItem.value.userId}`,
        editedItem.value
      )
      .then(() => fetchUsers())
      .catch((err) => console.error(err));
  } else {
    axios
      .post("http://localhost:9090/api/users", editedItem.value)
      .then(() => fetchUsers())
      .catch((err) => console.error(err));
    console.log(editedItem.value.password);
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
    userId: null,
    fullName: "",
    email: "",
    phoneNumber: "",
    address: "",
    password: "",
    passwordHash: "",
    role: "Customer",
    createdAt: new Date().toISOString(),
  };
}

const filteredUsers = computed(() => {
  if (!search.value) return users.value;
  return users.value.filter((u) =>
    u.fullName.toLowerCase().includes(search.value.toLowerCase())
  );
});

function goBack() {
  router.back();
}
</script>
