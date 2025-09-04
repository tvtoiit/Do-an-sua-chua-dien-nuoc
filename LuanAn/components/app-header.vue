<template>
  <div>
    <!-- App Bar -->
    <v-app-bar
      app
      elevation="4"
      color="white"
      height="68"
      class="app-bar-shadow"
    >
      <div class="d-flex align-center app-bar-content">
        <!-- Logo & Company Name -->
        <div class="d-flex align-center">
          <div class="logo-container mr-2">
            <v-icon color="primary" size="32">mdi-tools</v-icon>
          </div>
          <h1 class="text-primary font-weight-bold text-h5 company-name">
            Repair Service
          </h1>
        </div>

        <v-spacer />

        <!-- Main Navigation - Desktop -->
        <div class="main-nav d-none d-md-flex">
          <div
            v-for="(item, i) in mainNavItems"
            :key="`main-${i}`"
            class="nav-item-wrapper"
          >
            <v-btn
              :to="item.to"
              rounded="lg"
              class="nav-btn font-weight-medium"
              :class="{ 'active-nav': isActiveRoute(item.to) }"
              variant="text"
              color="grey-darken-3"
            >
              <v-icon start size="20" class="mr-1">{{ item.icon }}</v-icon>
              {{ item.title }}
            </v-btn>
          </div>
        </div>

        <!-- Role-based Navigation - Desktop -->
        <div v-if="auth.isAuthenticated" class="role-nav d-none d-md-flex ml-2">
          <v-menu location="bottom" offset-y min-width="220" rounded="lg">
            <template #activator="{ props }">
              <v-btn
                color="primary"
                variant="tonal"
                rounded="lg"
                class="role-nav-trigger"
                v-bind="props"
              >
                <v-icon start size="20">{{ roleIcon }}</v-icon>
                {{ roleName }}
                <v-icon end>mdi-chevron-down</v-icon>
              </v-btn>
            </template>

            <v-card class="role-menu-card" elevation="5">
              <v-list density="compact" nav>
                <v-list-item
                  v-for="(item, i) in roleBasedNavItems"
                  :key="`role-${i}`"
                  :to="item.to"
                  rounded="lg"
                  class="role-menu-item"
                >
                  <template #prepend>
                    <v-icon color="primary" size="20">{{ item.icon }}</v-icon>
                  </template>
                  <v-list-item-title>{{ item.title }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card>
          </v-menu>
        </div>

        <!-- User Menu -->
        <div class="ml-4">
          <v-menu location="bottom" offset-y min-width="220" rounded="lg">
            <template #activator="{ props }">
              <v-btn icon v-bind="props" class="user-menu-btn" variant="text">
                <v-avatar color="primary" class="user-avatar" size="38">
                  <v-icon v-if="!auth.user?.profileImage" color="white">
                    mdi-account
                  </v-icon>
                  <img v-else :src="auth.user.profileImage" alt="Avatar" >
                </v-avatar>
              </v-btn>
            </template>

            <v-card class="user-menu-card" elevation="5">
              <v-card-text
                v-if="auth.isAuthenticated"
                class="user-info pt-3 pb-2"
              >
                <div class="d-flex align-center">
                  <v-avatar color="primary" size="40" class="mr-3">
                    <v-icon v-if="!auth.user?.profileImage" color="white"
                      >mdi-account</v-icon
                    >
                    <img v-else :src="auth.user.profileImage" alt="Avatar" >
                  </v-avatar>
                  <div>
                    <p class="text-body-1 font-weight-medium mb-0">
                      {{ "Người dùng" }}
                    </p>
                    <p class="text-caption text-grey-darken-1">
                      {{ auth.email }}
                    </p>
                  </div>
                </div>
              </v-card-text>

              <v-divider v-if="auth.isAuthenticated" />

              <v-list density="compact" nav>
                <template v-if="auth.isAuthenticated">
                  <v-list-item
                    class="user-menu-item"
                    rounded="lg"
                    @click="goToProfile"
                  >
                    <template #prepend>
                      <v-icon color="primary" size="20"
                        >mdi-account-edit</v-icon
                      >
                    </template>
                    <v-list-item-title>Thông tin cá nhân</v-list-item-title>
                  </v-list-item>
                  <v-list-item
                    class="user-menu-item"
                    rounded="lg"
                    @click="goToChangePassword"
                  >
                    <template #prepend>
                      <v-icon color="primary" size="20">mdi-lock-reset</v-icon>
                    </template>
                    <v-list-item-title>Đổi mật khẩu</v-list-item-title>
                  </v-list-item>
                  <v-divider />
                  <v-list-item
                    class="user-menu-item logout-item"
                    rounded="lg"
                    @click="logout"
                  >
                    <template #prepend>
                      <v-icon color="error" size="20"
                        >mdi-logout-variant</v-icon
                      >
                    </template>
                    <v-list-item-title class="text-error"
                      >Đăng xuất</v-list-item-title
                    >
                  </v-list-item>
                </template>
                <template v-else>
                  <v-list-item
                    class="user-menu-item"
                    rounded="lg"
                    @click="goToLogin"
                  >
                    <template #prepend>
                      <v-icon color="primary" size="20">mdi-login</v-icon>
                    </template>
                    <v-list-item-title>Đăng nhập</v-list-item-title>
                  </v-list-item>
                  <v-list-item
                    class="user-menu-item"
                    rounded="lg"
                    @click="goToRegister"
                  >
                    <template #prepend>
                      <v-icon color="primary" size="20"
                        >mdi-account-plus</v-icon
                      >
                    </template>
                    <v-list-item-title>Đăng ký</v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </v-card>
          </v-menu>
        </div>

        <!-- Mobile Menu Trigger -->
        <v-btn
          icon
          variant="text"
          class="d-flex d-md-none ml-2"
          @click="drawer = !drawer"
        >
          <v-icon>mdi-menu</v-icon>
        </v-btn>
      </div>
    </v-app-bar>

    <!-- Mobile Navigation Drawer -->
    <v-navigation-drawer
      v-model="drawer"
      app
      temporary
      width="280"
      elevation="16"
      location="left"
      class="mobile-drawer"
    >
      <div class="pa-4 d-flex align-center">
        <div class="logo-container mr-2">
          <v-icon color="primary" size="32">mdi-tools</v-icon>
        </div>
        <h1 class="text-primary font-weight-bold text-h5">FIXMASTER</h1>
      </div>

      <v-divider />

      <!-- User Info -->
      <div v-if="auth.isAuthenticated" class="pa-4">
        <div class="d-flex align-center">
          <v-avatar color="primary" size="42" class="mr-3">
            <v-icon v-if="!auth.user?.profileImage" color="white"
              >mdi-account</v-icon
            >
            <img v-else :src="auth.user.profileImage" alt="Avatar" >
          </v-avatar>
          <div>
            <p class="text-body-1 font-weight-medium mb-0">
              {{ auth.user?.fullName || "Người dùng" }}
            </p>
            <p class="text-caption text-grey-darken-1">
              {{ auth.user?.email }}
            </p>
          </div>
        </div>
      </div>

      <v-divider v-if="auth.isAuthenticated" />

      <!-- Combined Navigation for Mobile -->
      <v-list nav density="comfortable">
        <v-list-item
          v-for="(item, i) in mainNavItems"
          :key="`drawer-main-${i}`"
          :to="item.to"
          :active="isActiveRoute(item.to)"
          class="drawer-nav-item"
          rounded="lg"
          @click="drawer = false"
        >
          <template #prepend>
            <v-icon color="grey-darken-2" size="22">{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>

        <v-divider v-if="roleBasedNavItems.length > 0" class="my-2" />

        <v-list-subheader
          v-if="roleBasedNavItems.length > 0"
          class="text-uppercase font-weight-medium"
        >
          {{ roleName }}
        </v-list-subheader>

        <v-list-item
          v-for="(item, i) in roleBasedNavItems"
          :key="`drawer-role-${i}`"
          :to="item.to"
          :active="isActiveRoute(item.to)"
          class="drawer-nav-item"
          rounded="lg"
          @click="drawer = false"
        >
          <template #prepend>
            <v-icon color="primary" size="22">{{ item.icon }}</v-icon>
          </template>
          <v-list-item-title>{{ item.title }}</v-list-item-title>
        </v-list-item>

        <v-divider class="my-2" />

        <template v-if="auth.isAuthenticated">
          <v-list-item
            rounded="lg"
            class="drawer-nav-item"
            @click="goToProfile"
          >
            <template #prepend>
              <v-icon color="grey-darken-2" size="22">mdi-account-edit</v-icon>
            </template>
            <v-list-item-title>Thông tin cá nhân</v-list-item-title>
          </v-list-item>

          <v-list-item
            rounded="lg"
            class="drawer-nav-item"
            @click="goToChangePassword"
          >
            <template #prepend>
              <v-icon color="grey-darken-2" size="22">mdi-lock-reset</v-icon>
            </template>
            <v-list-item-title>Đổi mật khẩu</v-list-item-title>
          </v-list-item>

          <v-list-item
            class="drawer-nav-item mt-2"
            rounded="lg"
            @click="logout"
          >
            <template #prepend>
              <v-icon color="error" size="22">mdi-logout-variant</v-icon>
            </template>
            <v-list-item-title class="text-error">Đăng xuất</v-list-item-title>
          </v-list-item>
        </template>

        <template v-else>
          <v-list-item class="drawer-nav-item" rounded="lg" @click="goToLogin">
            <template #prepend>
              <v-icon color="primary" size="22">mdi-login</v-icon>
            </template>
            <v-list-item-title>Đăng nhập</v-list-item-title>
          </v-list-item>

          <v-list-item
            class="drawer-nav-item"
            rounded="lg"
            @click="goToRegister"
          >
            <template #prepend>
              <v-icon color="primary" size="22">mdi-account-plus</v-icon>
            </template>
            <v-list-item-title>Đăng ký</v-list-item-title>
          </v-list-item>
        </template>
      </v-list>
    </v-navigation-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const drawer = ref(false);
const router = useRouter();
const route = useRoute();
const auth = useAuthStore();

// Navigation functions
const goToProfile = () => {
  router.push("/user/profile");
  drawer.value = false;
};

const goToChangePassword = () => {
  router.push("/auth/change-password");
  drawer.value = false;
};

const goToLogin = () => {
  router.push("/auth/login");
  drawer.value = false;
};

const goToRegister = () => {
  router.push("/auth/register");
  drawer.value = false;
};

const logout = () => {
  auth.logout();
  router.push("/auth/login");
  drawer.value = false;
};

// Helper to check if the route is active
const isActiveRoute = (path) => {
  return route.path === path || route.path.startsWith(`${path}/`);
};

// Main navigation items
const mainNavItems = computed(() => {
  if (auth.role === "Admin") {
    return [];
  }

  if (auth.role === "Technician") {
    return [];
  }

  // Trường hợp role không phải Admin
  return [
    { title: "Trang chủ", to: "/", icon: "mdi-home" },
    { title: "Dịch vụ", to: "/services", icon: "mdi-wrench" },
    { title: "Bài viết", to: "/article/articles", icon: "mdi-file-document" },
    { title: "Giới thiệu", to: "/about", icon: "mdi-information" },
    { title: "Liên hệ", to: "/contact", icon: "mdi-phone" },
  ];
});

// Role-based navigation items
const roleBasedNavItems = computed(() => {
  if (!auth.isAuthenticated) return [];

  // User role always has tracking option
  const commonItems = [
    {
      title: "Theo dõi tiến độ",
      to: "/user/follow-progress",
      icon: "mdi-chart-timeline",
    },
  ];

  if (auth.role === "Admin") {
    return [
      {
        title: "Bảng điều khiển",
        to: "/admin/admin-dashboard",
        icon: "mdi-view-dashboard",
      },
    ];
  } else if (auth.role === "Technician") {
    return [
      {
        title: "Công việc hiện tại",
        to: "/technician/jobs",
        icon: "mdi-briefcase",
      },
      {
        title: "Lịch làm việc",
        to: "/technician/schedule",
        icon: "mdi-calendar",
      },
    ];
  }

  return commonItems;
});

// Role name and icon
const roleName = computed(() => {
  if (!auth.isAuthenticated) return "";
  if (auth.role === "Admin") return "Quản trị viên";
  if (auth.role === "Technician") return "Kỹ thuật viên";
  return "Người dùng";
});

const roleIcon = computed(() => {
  if (!auth.isAuthenticated) return "";
  if (auth.role === "Admin") return "mdi-shield-account";
  if (auth.role === "Technician") return "mdi-account-hard-hat";
  return "mdi-account";
});

onMounted(() => {
  auth.checkAuth();
});
</script>

<style scoped>
.app-bar-shadow {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05) !important;
}

.app-bar-content {
  width: 100%;
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 16px;
}

.company-name {
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.logo-container {
  background-color: rgba(var(--v-theme-primary), 0.1);
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.main-nav {
  gap: 8px;
}

.nav-btn {
  height: 40px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.nav-btn:hover {
  background-color: rgba(var(--v-theme-primary), 0.08);
  transform: translateY(-2px);
}

.active-nav {
  color: rgb(var(--v-theme-primary)) !important;
  background-color: rgba(var(--v-theme-primary), 0.08);
  font-weight: 600 !important;
}

.role-nav-trigger {
  height: 40px;
  transition: all 0.2s ease;
}

.role-nav-trigger:hover {
  transform: translateY(-2px);
}

.user-menu-btn {
  transition: all 0.2s ease;
}

.user-menu-btn:hover {
  transform: scale(1.05);
}

.user-avatar {
  border: 2px solid rgba(var(--v-theme-primary), 0.2);
}

.role-menu-card,
.user-menu-card {
  border-radius: 12px;
  overflow: hidden;
}

.user-info {
  background-color: rgba(var(--v-theme-primary), 0.03);
}

.drawer-nav-item {
  margin-bottom: 4px;
}

.role-menu-item:hover,
.user-menu-item:hover,
.drawer-nav-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.05);
}

.logout-item:hover {
  background-color: rgba(var(--v-theme-error), 0.05);
}

.mobile-drawer {
  border-radius: 0 16px 16px 0;
}
</style>
