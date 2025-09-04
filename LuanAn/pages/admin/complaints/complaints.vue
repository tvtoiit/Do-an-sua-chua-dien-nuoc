<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <title>Quản lý liên hệ</title>
    <v-row class="justify-space-between mb-4">
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>
      <v-col cols="auto">
        <h1 class="text-h6">Quản lý thông tin liên hệ</h1>
      </v-col>
      <v-col cols="auto">
        <v-btn color="primary" @click="dialog = true">
          <v-icon left>mdi-account-plus</v-icon> Thêm mới
        </v-btn>
      </v-col>
    </v-row>

    <!-- Lọc nâng cao -->
    <v-row class="mb-4" dense>
  <v-col cols="12" md="6">
    <v-text-field
      v-model="searchQuery"
      label="Tìm kiếm (tên, email hoặc SĐT)"
      prepend-inner-icon="mdi-magnify"
      clearable
    />
  </v-col>
</v-row>


    <v-data-table
      :headers="headers"
      :items="filteredUsers"
      item-value="contactId"
      class="elevation-1"
    >
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
    <v-dialog v-model="dialog" max-width="600px">
      <v-card>
        <v-card-title>
          <span class="text-h6">
            {{
              editedIndex === -1
                ? "Thêm thông tin liên hệ"
                : "Chỉnh sửa thông tin liên hệ"
            }}
          </span>
        </v-card-title>
        <v-card-text>
          <v-text-field v-model="editedItem.name" label="Tên" />
          <v-text-field v-model="editedItem.email" label="Email" />
          <v-text-field v-model="editedItem.phone" label="Số điện thoại" />
          <v-textarea
            v-model="editedItem.content"
            label="Nội dung liên hệ"
            rows="3"
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
import { useRouter } from "vue-router";
import axios from "axios";

const API_BASE = "http://localhost:9090/api/contacts";

const dialog = ref(false);
const editedIndex = ref(-1);
const editedItem = ref({
  contactId: "",
  name: "",
  email: "",
  phone: "",
  content: "",
});
const users = ref([]);
const searchQuery = ref("");
const router = useRouter();

const headers = [
  { text: "ID", title: "ID", value: "contactId" },
  { text: "Tên", title: "Tên", value: "name" },
  { text: "Email", title: "Email", value: "email" },
  { text: "SĐT", title: "SĐT", value: "phone" },
  { text: "Nội dung", title: "Nội dung", value: "content" },
  { text: "Hành động", title: "Hành động", value: "actions", sortable: false },
];

// Lấy danh sách từ API
const fetchContacts = async () => {
  try {
    const res = await axios.get(API_BASE);
    users.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tải dữ liệu:", err);
  }
};

// Lưu (thêm hoặc cập nhật)
const saveItem = async () => {
  try {
    if (editedIndex.value === -1) {
      const res = await axios.post(API_BASE, editedItem.value);
      users.value.push(res.data);
    } else {
      const contactId = editedItem.value.contactId;
      const res = await axios.put(`${API_BASE}/${contactId}`, editedItem.value);
      Object.assign(users.value[editedIndex.value], res.data);
    }
    closeDialog();
  } catch (err) {
    console.error("Lỗi khi lưu:", err);
  }
};

// Xóa liên hệ
const deleteItem = async (item) => {
  if (confirm("Bạn có chắc muốn xóa?")) {
    try {
      await axios.put(`${API_BASE}/delete/${item.contactId}`);
      users.value = users.value.filter((u) => u.contactId !== item.contactId);
    } catch (err) {
      console.error("Lỗi khi xóa:", err);
    }
  }
};

const editItem = (item) => {
  editedIndex.value = users.value.findIndex((u) => u.contactId === item.contactId);
  editedItem.value = { ...item };
  dialog.value = true;
};

const closeDialog = () => {
  dialog.value = false;
  editedIndex.value = -1;
  editedItem.value = {
    contactId: "",
    name: "",
    email: "",
    phone: "",
    content: "",
  };
};

const goBack = () => {
  router.back();
};

// Lọc nâng cao
const filteredUsers = computed(() => {
  const query = searchQuery.value.toLowerCase();
  return users.value.filter((u) =>
    [u.name, u.email, u.phone].some((field) =>
      field?.toLowerCase().includes(query)
    )
  );
});

onMounted(fetchContacts);
</script>
