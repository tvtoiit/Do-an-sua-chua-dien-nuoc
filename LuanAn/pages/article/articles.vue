<!-- eslint-disable vue/no-v-html -->
<template>
  <v-container class="px-6">
    <title>Bài viết chia sẻ kinh nghiệm</title>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 font-weight-bold primary--text mb-2">Sửa chữa thiết bị gia dụng</h1>
        <v-divider class="mb-6"/>
        
        <div class="d-flex align-center justify-space-between mb-8">
          <h2 class="text-h5 font-weight-bold">Bài viết chia sẻ kinh nghiệm</h2>
          <v-btn
            v-if="auth.isAuthenticated"
            color="primary"
            variant="elevated"
            prepend-icon="mdi-pencil"
            rounded
            elevation="2"
            @click="showForm = !showForm"
          >
            Viết bài chia sẻ
          </v-btn>
        </div>

        <!-- Form đăng bài -->
        <v-expand-transition>
          <v-card v-if="showForm" class="mb-8 pa-4" variant="elevated" elevation="3">
            <v-card-title class="px-0 pt-0 text-h6">Đăng bài chia sẻ kinh nghiệm mới</v-card-title>
            <v-form ref="articleForm" @submit.prevent="submitArticle">
              <v-text-field
                v-model="title"
                label="Tiêu đề bài viết"
                placeholder="Nhập tiêu đề bài viết của bạn"
                :rules="[v => !!v || 'Tiêu đề không được để trống']"
                required
                variant="outlined"
                class="mb-3"
              />
              <v-text-field
                v-model="description"
                label="Mô tả ngắn gọn"
                placeholder="Tóm tắt nội dung bài viết trong vài dòng"
                :rules="[v => !!v || 'Mô tả không được để trống']"
                required
                variant="outlined"
                class="mb-3"
              />
              <v-textarea
                v-model="content"
                label="Nội dung chi tiết"
                placeholder="Chia sẻ kiến thức và kinh nghiệm của bạn tại đây..."
                :rules="[v => !!v || 'Nội dung không được để trống']"
                rows="8"
                required
                variant="outlined"
                class="mb-4"
              />
              <div class="d-flex gap-3">
                <v-btn type="submit" color="success" size="large" variant="elevated">
                  <v-icon left class="mr-1">mdi-send</v-icon>
                  Gửi bài viết
                </v-btn>
                <v-btn color="error" variant="plain" @click="cancelForm">
                  Hủy
                </v-btn>
              </div>
            </v-form>
          </v-card>
        </v-expand-transition>

        <!-- Thông báo thành công -->
        <v-scale-transition>
          <v-alert
            v-if="submitted"
            color="success"
            icon="mdi-check-circle"
            variant="tonal"
            closable
            class="mb-6"
            @click:close="submitted = false"
          >
            Bài viết đã được gửi và đang chờ phê duyệt. Cảm ơn bạn đã đóng góp!
          </v-alert>
        </v-scale-transition>

        <!-- Danh sách bài viết -->
        <v-row v-if="articles.length > 0">
          <v-col v-for="article in articles" :key="article.articleId" cols="12" md="6">
            <v-hover v-slot="{ isHovering, props }">
              <v-card
                v-bind="props"
                :elevation="isHovering ? 8 : 2"
                class="h-100 transition-swing"
                :class="{ 'on-hover': isHovering }"
                @click="viewArticle(article.articleId)"
              >
                <v-card-item>
                  <template #prepend>
                    <v-avatar color="primary" class="mr-3">
                      <span class="text-subtitle-1 text-uppercase font-weight-bold">
                        {{ article.author.fullName.charAt(0) }}
                      </span>
                    </v-avatar>
                  </template>
                  <v-card-title class="text-h6 font-weight-bold text-primary">
                    {{ article.title }}
                  </v-card-title>
                  <v-card-subtitle>
                    <div class="d-flex align-center">
                      <v-icon size="small" class="mr-1">mdi-account</v-icon>
                      {{ article.author.fullName }}
                      <v-icon size="small" class="mx-1">mdi-circle-small</v-icon>
                      <v-icon size="small" class="mr-1">mdi-calendar</v-icon>
                      {{ formatDate(article.createdAt) }}
                    </div>
                  </v-card-subtitle>
                </v-card-item>
                <v-card-text class="text-body-1">
                  {{ truncateText(article.description, 150) }}
                </v-card-text>
                <v-card-actions>
                  <v-spacer/>
                  <v-btn
                    color="primary"
                    variant="text"
                    prepend-icon="mdi-eye"
                    @click.stop="viewArticle(article.articleId)"
                  >
                    Đọc thêm
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-hover>
          </v-col>
        </v-row>

        <!-- Hiển thị khi không có bài viết -->
        <v-card
          v-else
          class="pa-8 text-center"
          variant="outlined"
        >
          <v-icon size="64" color="grey">mdi-file-document-outline</v-icon>
          <div class="text-h6 mt-4 text-grey-darken-1">Chưa có bài viết nào</div>
          <div class="text-body-1 text-grey">Hãy là người đầu tiên chia sẻ kinh nghiệm của bạn!</div>
        </v-card>

        <!-- Dialog chi tiết bài viết -->
        <v-dialog v-model="dialog" max-width="900px" scrollable>
          <v-card>
            <v-toolbar color="primary" flat>
              <v-toolbar-title class="text-white font-weight-bold">
                Chi tiết bài viết
              </v-toolbar-title>
              <v-spacer/>
              <v-btn icon @click="dialog = false">
                <v-icon color="white">mdi-close</v-icon>
              </v-btn>
            </v-toolbar>
            <v-card-item v-if="selectedArticle">
              <v-card-title class="text-h5 font-weight-bold">
                {{ selectedArticle.title }}
              </v-card-title>
              <v-card-subtitle class="pt-2">
                <div class="d-flex align-center flex-wrap">
                  <v-avatar color="primary" size="32" class="mr-2">
                    <span class="text-caption text-uppercase font-weight-bold">
                      {{ selectedArticle.author?.fullName.charAt(0) }}
                    </span>
                  </v-avatar>
                  <span class="font-weight-medium">{{ selectedArticle.author?.fullName }}</span>
                  <v-icon size="small" class="mx-1">mdi-circle-small</v-icon>
                  <span>{{ formatDate(selectedArticle.createdAt, true) }}</span>
                </div>
              </v-card-subtitle>
            </v-card-item>
            <v-divider/>
            <v-card-text class="py-5">
              <div class="text-body-1 mb-4">Mô tả: {{ selectedArticle?.description }}</div>
              <v-divider class="mb-4"/>
              Nội dung:
              <div class="article-content" v-html="selectedArticle?.content"/>
            </v-card-text>
            <v-divider/>
            <v-card-actions>
              <v-spacer/>
              <v-btn color="primary" variant="tonal" @click="dialog = false">
                <v-icon left class="mr-1">mdi-close</v-icon>
                Đóng
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>

      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";

const articles = ref([]);
const title = ref("");
const description = ref("");
const content = ref("");
const showForm = ref(false);
const submitted = ref(false);
const dialog = ref(false);
const selectedArticle = ref(null);
const auth = useAuthStore();
const articleForm = ref(null);

const fetchArticles = async () => {
  try {
    const { data, error } = await useFetch(
      "http://localhost:9090/api/articles/approved"
    );
    if (!error.value && data.value) {
      articles.value = data.value;
    } else {
      console.error("Lỗi khi tải bài viết:", error.value);
    }
  } catch (err) {
    console.error("Có lỗi xảy ra:", err);
  }
};

const submitArticle = async () => {
  const { valid } = await articleForm.value.validate();
  
  if (!valid) return;
  
  try {
    const userId = useCookie("userId");
    const payload = {
      author: { userId: userId.value }, 
      title: title.value,
      description: description.value,
      content: content.value,
      createdAt: new Date().toISOString(),
    };

    const { error } = await useFetch("http://localhost:9090/api/articles", {
      method: "POST",
      body: payload,
    });
    
    if (!error.value) {
      submitted.value = true;
      resetForm();
    } else {
      console.error("Lỗi khi gửi bài viết:", error.value);
    }
  } catch (err) {
    console.error("Có lỗi xảy ra:", err);
  }
};

const resetForm = () => {
  title.value = "";
  description.value = "";
  content.value = "";
  showForm.value = false;
};

const cancelForm = () => {
  resetForm();
};

const viewArticle = async (articleId) => {
  try {
    const { data, error } = await useFetch(
      `http://localhost:9090/api/articles/${articleId}`
    );
    if (!error.value && data.value) {
      selectedArticle.value = data.value;
      dialog.value = true;
    } else {
      console.error("Lỗi khi tải chi tiết bài viết:", error.value);
    }
  } catch (err) {
    console.error("Có lỗi xảy ra:", err);
  }
};

const formatDate = (dateString, includeTime = false) => {
  if (!dateString) return "";
  
  const date = new Date(dateString);
  const options = { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric'
  };
  
  if (includeTime) {
    options.hour = '2-digit';
    options.minute = '2-digit';
  }
  
  return date.toLocaleDateString('vi-VN', options);
};

const truncateText = (text, maxLength) => {
  if (!text) return "";
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + "...";
};

onMounted(() => {
  fetchArticles();
});
</script>

<style scoped>
.on-hover {
  transform: translateY(-5px);
}

.article-content {
  line-height: 1.8;
  font-size: 1.05rem;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin-top: 1.5rem;
  margin-bottom: 1rem;
  font-weight: bold;
}

.article-content :deep(p) {
  margin-bottom: 1rem;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  margin-bottom: 1rem;
  padding-left: 1.5rem;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 1rem 0;
}
</style>