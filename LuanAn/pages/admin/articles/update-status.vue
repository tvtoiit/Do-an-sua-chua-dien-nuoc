<!-- eslint-disable vue/no-multiple-template-root -->
<template>
  <v-btn color="success" icon @click="updateStatus(1, 'Completed')">
    <v-icon>mdi-check-circle</v-icon>
    Hoàn thành
  </v-btn>
  <v-btn icon color="error" @click="updateStatus(1, 'Cancelled')">
    <v-icon>mdi-cancel-circle</v-icon>
    Huỷ
  </v-btn>
</template>

<script setup>
import axios from 'axios'

const updateStatus = async (requestId, newStatus) => {
  try {
    await axios.put(`/api/repair-requests/${requestId}/status`, null, {
      params: { status: newStatus }
    })
    alert('Trạng thái đã được cập nhật!')
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (e) {
    alert('Không thể cập nhật trạng thái')
  }
}
</script>
