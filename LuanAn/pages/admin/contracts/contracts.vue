<!-- eslint-disable vue/valid-v-slot -->
<template>
  <v-container>
    <h1 class="text-h5 mb-4">Quản lý hợp đồng</h1>
    <v-row>
      <!-- Nút Quay về -->
      <v-col cols="auto">
        <v-btn color="secondary" @click="goBack">
          <v-icon left>mdi-arrow-left</v-icon> Quay về
        </v-btn>
      </v-col>

      <!-- Nút tạo hợp đồng -->
      <v-col cols="auto">
        <v-btn color="success" class="mb-4" @click="createNewContract">
          <v-icon left>mdi-plus</v-icon> Tạo hợp đồng mới
        </v-btn>
      </v-col>
    </v-row>
    <!-- Bảng hợp đồng -->
    <v-data-table
      :headers="headers"
      :items="contracts"
      class="elevation-1"
      item-value="id"
    >
      <template #item.actions="{ item }">
        <v-btn color="primary" size="small" @click="viewContract(item)"
          >Xem</v-btn
        >
        <v-btn
          color="warning"
          size="small"
          class="ml-2"
          @click="editContract(item)"
          >Sửa</v-btn
        >
        <v-btn
          color="error"
          size="small"
          class="ml-2"
          @click="deleteContract(item)"
          >Xoá</v-btn
        >
      </template>
    </v-data-table>

    <!-- Dialog xem PDF -->
    <v-dialog v-model="viewDialog" max-width="800px">
      <v-card>
        <v-toolbar flat color="primary" dark>
          <v-toolbar-title>Hợp đồng PDF</v-toolbar-title>
          <v-spacer />
          <v-btn icon @click="viewDialog = false"
            ><v-icon>mdi-close</v-icon></v-btn
          >
        </v-toolbar>
        <v-card-text style="height: 600px">
          <iframe
            v-if="selectedContract?.pdfUrl"
            :src="selectedContract.pdfUrl"
            width="100%"
            height="100%"
            frameborder="0"
          />
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- Dialog tạo/sửa hợp đồng -->
    <v-dialog v-model="editDialog" max-width="600px">
      <v-card>
        <v-card-title>
          {{ selectedContract?.id ? "Sửa hợp đồng" : "Tạo hợp đồng mới" }}
        </v-card-title>
        <v-card-text>
          <v-form ref="editForm">
            <v-text-field
              v-model="selectedContract.name"
              label="Tên hợp đồng"
              required
            />
            <v-text-field
              v-model="selectedContract.startDate"
              label="Ngày bắt đầu"
              type="date"
            />
            <v-text-field
              v-model="selectedContract.endDate"
              label="Ngày kết thúc"
              type="date"
            />
            <v-text-field
              v-model="selectedContract.pdfUrl"
              label="Đường dẫn PDF"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" text @click="editDialog = false"
            >Hủy</v-btn
          >
          <v-btn color="green darken-1" text @click="saveContract">Lưu</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const headers = [
  { text: "ID", value: "id" },
  { text: "Tên hợp đồng", value: "name" },
  { text: "Ngày bắt đầu", value: "startDate" },
  { text: "Ngày kết thúc", value: "endDate" },
  { text: "Thao tác", value: "actions", sortable: false },
];

const contracts = ref([
  {
    id: 1,
    name: "Hợp đồng bảo trì làm việc",
    startDate: "2025-01-01",
    endDate: "2025-12-31",
    pdfUrl: "/contracts/HopDong_ThoSuaChua.pdf",
  },
]);

const selectedContract = ref(null);
const viewDialog = ref(false);
const editDialog = ref(false);

// Xem PDF
function viewContract(contract) {
  selectedContract.value = { ...contract };
  viewDialog.value = true;
}

// Mở form sửa
function editContract(contract) {
  selectedContract.value = { ...contract };
  editDialog.value = true;
}

// Tạo hợp đồng mới
function createNewContract() {
  selectedContract.value = {
    id: null,
    name: "",
    startDate: "",
    endDate: "",
    pdfUrl: "",
  };
  editDialog.value = true;
}

// Lưu hợp đồng (sửa hoặc tạo mới)
function saveContract() {
  if (!selectedContract.value.name) return;

  if (selectedContract.value.id) {
    // Cập nhật hợp đồng
    const index = contracts.value.findIndex(
      (c) => c.id === selectedContract.value.id
    );
    if (index !== -1) {
      contracts.value[index] = { ...selectedContract.value };
    }
  } else {
    // Tạo mới
    const newId = Math.max(...contracts.value.map((c) => c.id)) + 1;
    contracts.value.push({ ...selectedContract.value, id: newId });
  }

  editDialog.value = false;
}

// Xoá hợp đồng
function deleteContract(contract) {
  const confirmed = confirm(`Bạn có chắc chắn muốn xoá "${contract.name}"?`);
  if (confirmed) {
    contracts.value = contracts.value.filter((c) => c.id !== contract.id);
  }
}

// Hàm xử lý quay về
function goBack() {
  router.back(); // Quay lại trang trước đó (nếu có router)
}
</script>
