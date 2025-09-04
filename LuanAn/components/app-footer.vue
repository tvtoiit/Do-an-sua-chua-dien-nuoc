<template>
  <v-footer color="grey-darken-4" class="footer-section">
    <v-container>
      <!-- Main Footer Content -->
      <div class="footer-content">
        <!-- Company Info & Description -->
        <div class="footer-brand-section">
          <div class="d-flex align-center mb-4">
            <div class="logo-container mr-3">
              <v-icon color="white" size="32">mdi-tools</v-icon>
            </div>
            <h2 class="text-h5 font-weight-bold text-white">Repair Service</h2>
          </div>
          
          <p class="text-body-2 text-grey-lighten-2 mb-6 company-desc">
            Chúng tôi cung cấp dịch vụ sửa chữa thiết bị gia dụng chuyên nghiệp, 
            uy tín và nhanh chóng với đội ngũ kỹ thuật viên được đào tạo bài bản.
          </p>
          
          <div class="mt-4 d-flex">
            <v-btn 
              variant="tonal" 
              color="primary" 
              rounded="lg" 
              prepend-icon="mdi-calendar-clock"
              to="/services"
              class="book-service-btn px-4"
            >
              Đặt lịch hẹn
            </v-btn>
          </div>
        </div>

        <!-- Quick Links -->
        <div class="footer-links-section">
          <h3 class="text-h6 font-weight-bold text-white mb-4">Liên kết nhanh</h3>
          
          <div class="d-flex flex-column">
            <a 
              v-for="(link, i) in quickLinks" 
              :key="`quick-${i}`" 
              :to="link.to"
              class="footer-link d-flex align-center mb-2"
            >
              <v-icon size="18" color="primary" class="mr-2">{{ link.icon }}</v-icon>
              <span>{{ link.title }}</span>
            </a>
          </div>
        </div>

        <!-- Services -->
        <div class="footer-services-section">
          <h3 class="text-h6 font-weight-bold text-white mb-4">Dịch vụ của chúng tôi</h3>
          
          <div class="d-flex flex-column">
            <a 
              v-for="(service, i) in services" 
              :key="`service-${i}`" 
              :to="service.to"
              class="footer-link d-flex align-center mb-2"
            >
              <v-icon size="18" color="primary" class="mr-2">{{ service.icon }}</v-icon>
              <span>{{ service.title }}</span>
            </a>
          </div>
        </div>

        <!-- Contact Information -->
        <div class="footer-contact-section">
          <h3 class="text-h6 font-weight-bold text-white mb-4">Thông tin liên hệ</h3>
          
          <div class="contact-list">
            <div class="contact-item d-flex mb-3">
              <div class="contact-icon-wrapper">
                <v-icon color="white" size="20">mdi-phone</v-icon>
              </div>
              <div class="ml-3">
                <p class="text-body-2 text-white mb-0">Hotline</p>
                <p class="text-white font-weight-medium">0989 898 989</p>
              </div>
            </div>
            
            <div class="contact-item d-flex mb-3">
              <div class="contact-icon-wrapper">
                <v-icon color="white" size="20">mdi-email</v-icon>
              </div>
              <div class="ml-3">
                <p class="text-body-2 text-white mb-0">Email</p>
                <p class="text-white font-weight-medium">support@repairservice.vn</p>
              </div>
            </div>
            
            <div class="contact-item d-flex">
              <div class="contact-icon-wrapper">
                <v-icon color="white" size="20">mdi-map-marker</v-icon>
              </div>
              <div class="ml-3">
                <p class="text-body-2 text-white mb-0">Địa chỉ</p>
                <p class="text-white font-weight-medium">123 Trần Hưng Đạo, Quy Nhơn, Bình Định</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <v-divider color="grey-darken-3" class="my-6" />
      
      <!-- Bottom Footer -->
      <div class="bottom-footer d-flex flex-wrap">
        <div class="copyright text-grey-lighten-1">
          © {{ new Date().getFullYear() }} <span class="text-primary font-weight-medium">Repair Service</span>
        </div>
        
        <v-spacer/>
      </div>
      
      <!-- Back to top button -->
      <v-btn
        icon
        fab
        color="primary"
        class="back-to-top-btn"
        @click="scrollToTop"
      >
        <v-icon>mdi-chevron-up</v-icon>
      </v-btn>
    </v-container>
  </v-footer>
</template>

<script setup>

// Quick links
const quickLinks = [
  { title: 'Trang chủ', to: '/', icon: 'mdi-home' },
  { title: 'Giới thiệu', to: '/about', icon: 'mdi-information' },
  { title: 'Dịch vụ', to: '/services', icon: 'mdi-wrench' },
  { title: 'Bài viết', to: '/article/articles', icon: 'mdi-file-document' },
  { title: 'Liên hệ', to: '/contact', icon: 'mdi-phone' },
];

// Services
const services = [
  { title: 'Sửa chữa tủ lạnh', to: '/services/refrigerator', icon: 'mdi-fridge' },
  { title: 'Sửa chữa máy giặt', to: '/services/washing-machine', icon: 'mdi-washing-machine' },
  { title: 'Sửa chữa điều hòa', to: '/services/air-conditioner', icon: 'mdi-air-conditioner' },
  { title: 'Sửa chữa lò vi sóng', to: '/services/microwave', icon: 'mdi-microwave' },
  { title: 'Sửa chữa TV', to: '/services/tv', icon: 'mdi-television' },
];

// Scroll to top function
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};
</script>

<style scoped>
.footer-section {
  padding-top: 60px;
  padding-bottom: 30px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 40px;
  margin-bottom: 40px;
}

@media (min-width: 600px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (min-width: 960px) {
  .footer-content {
    grid-template-columns: 2fr 1fr 1fr 1.5fr;
    gap: 30px;
  }
}

.footer-brand-section {
  max-width: 380px;
}

.company-desc {
  line-height: 1.6;
}

.logo-container {
  background-color: rgba(var(--v-theme-primary), 0.2);
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-link {
  color: #bdbdbd;
  text-decoration: none;
  transition: all 0.2s ease;
  padding: 4px 0;
}

.footer-link:hover {
  color: white;
  transform: translateX(4px);
}

.contact-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(var(--v-theme-primary), 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-newsletter-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 40px;
  margin-bottom: 20px;
}

@media (min-width: 760px) {
  .social-newsletter-section {
    grid-template-columns: 2fr 1fr;
    gap: 30px;
  }
}

.newsletter-container {
  max-width: 500px;
}

.newsletter-form {
  max-width: 450px;
}

.newsletter-input :deep(.v-field__outline) {
  --v-field-border-opacity: 0.2;
}

.newsletter-input {
  max-width: 320px;
}

.subscribe-btn {
  height: 44px;
  min-width: 100px;
}

.social-buttons {
  display: flex;
  flex-wrap: wrap;
}

.social-btn {
  margin-bottom: 8px;
  transition: all 0.2s ease;
}

.social-btn:hover {
  transform: translateY(-4px);
}

.bottom-footer {
  font-size: 14px;
}

.legal-link {
  color: #bdbdbd;
  text-decoration: none;
  transition: color 0.2s ease;
  font-size: 14px;
  margin-bottom: 8px;
}

.legal-link:hover {
  color: white;
}

.book-service-btn {
  transition: transform 0.2s ease;
}

.book-service-btn:hover {
  transform: translateY(-2px);
}

.back-to-top-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 99;
  opacity: 0.8;
  transition: all 0.3s ease;
}

.back-to-top-btn:hover {
  opacity: 1;
  transform: translateY(-4px);
}
</style>