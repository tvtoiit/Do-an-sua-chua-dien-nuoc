<template>
  <div class="carousel-container">
    <v-carousel
      ref="carousel"
      v-model="currentSlide"
      :show-arrows="false"
      :cycle="true"
      :interval="4000"
      hide-delimiters
      height="500"
      class="rounded-lg overflow-hidden custom-carousel"
    >
      <v-carousel-item
        v-for="(slide, index) in slides"
        :key="index"
        transition="fade-transition"
      >
        <v-sheet height="100%" tile class="position-relative">
          <v-img :src="slide.src" height="500" cover class="carousel-image">
            <div
              class="fill-height d-flex align-center"
              :class="slide.contentAlignment"
            >
              <v-card
                class="py-6 px-8 mx-6 rounded-xl slide-card"
                elevation="8"
                :color="slide.overlayColor"
                :style="{
                  opacity: 0.92,
                  maxWidth: '550px',
                  borderLeft: slide.accentBorder
                    ? `6px solid ${slide.accentColor}`
                    : 'none',
                }"
              >
                <div class="slide-content">
                  <v-chip
                    v-if="slide.tag"
                    size="small"
                    :color="slide.accentColor"
                    class="mb-2 font-weight-bold text-uppercase"
                  >
                    {{ slide.tag }}
                  </v-chip>

                  <h1
                    class="text-h3 font-weight-bold mb-2"
                    :class="slide.textColor"
                  >
                    {{ slide.title }}
                  </h1>

                  <h4 class="my-4 text-subtitle-1" :class="slide.textColor">
                    {{ slide.subtitle }}
                  </h4>

                  <v-btn
                    :color="slide.buttonColor || 'primary'"
                    size="large"
                    class="mt-3 text-none px-6 py-2"
                    :to="slide.buttonLink"
                    rounded="pill"
                    elevation="2"
                  >
                    <v-icon
                      v-if="slide.buttonIcon"
                      :icon="slide.buttonIcon"
                      start
                      class="mr-2"
                    />
                    {{ slide.buttonText }}
                    <v-icon end class="ml-1">mdi-chevron-right</v-icon>
                  </v-btn>
                </div>
              </v-card>
            </div>
          </v-img>

          <!-- Custom navigation dots -->
          <div class="custom-nav-dots">
            <div
              v-for="(_, dotIndex) in slides"
              :key="dotIndex"
              class="custom-dot"
              :class="{ active: dotIndex === index }"
              @click="goToSlide(dotIndex)"
            />
          </div>

          <!-- Custom arrows -->
          <div class="custom-arrows">
            <v-btn
              icon="mdi-chevron-left"
              variant="text"
              class="custom-arrow custom-arrow-prev"
              @click="prevSlide"
            />
            <v-btn
              icon="mdi-chevron-right"
              variant="text"
              class="custom-arrow custom-arrow-next"
              @click="nextSlide"
            />
          </div>
        </v-sheet>
      </v-carousel-item>
    </v-carousel>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentSlide: 0,
      slides: [
        {
          src: "https://suadodien.com/wp-content/uploads/2019/12/Trung-Tam-Sua-Chua-Dien-Tu-Dien-Lanh-Ha-Noi-3.jpg",
          title: "Dịch vụ sửa chữa gia dụng",
          subtitle:
            "Đội ngũ kỹ thuật chuyên nghiệp với hơn 10 năm kinh nghiệm, phục vụ tận tâm 24/7",
          buttonText: "Đặt lịch ngay",
          buttonLink: "/services",
          overlayColor: "white",
          textColor: "text-red-darken-2",
          accentBorder: true,
          accentColor: "red-darken-2",
          buttonColor: "red-darken-2",
          buttonIcon: "mdi-calendar-check",
          contentAlignment: "justify-start",
          tag: "Dịch vụ hot",
        },
        {
          src: "https://img.freepik.com/free-photo/technician-repairing-refrigerator_23-2149141434.jpg",
          title: "Bảo dưỡng thiết bị điện tử",
          subtitle:
            "Kéo dài tuổi thọ thiết bị với dịch vụ bảo dưỡng định kỳ chuyên nghiệp",
          buttonText: "Tìm hiểu thêm",
          buttonLink: "/services",
          overlayColor: "grey-darken-3",
          textColor: "text-white",
          accentBorder: false,
          buttonColor: "light-blue",
          buttonIcon: "mdi-information-outline",
          contentAlignment: "justify-center",
          tag: "Tiết kiệm chi phí",
        },
        {
          src: "https://img.freepik.com/free-photo/technician-service-removing-air-filter-air-conditioner-cleaning_35076-3617.jpg",
          title: "Chia sẻ kinh nghiệm",
          subtitle:
            "Gia nhập cộng đồng chia sẻ kinh nghiệm sửa chữa và bảo trì thiết bị điện tử",
          buttonText: "Tham gia",
          buttonLink: "/article/articles",
          overlayColor: "red-lighten-1",
          textColor: "text-white",
          accentBorder: false,
          buttonColor: "white",
          buttonIcon: "mdi-tag-outline",
          contentAlignment: "justify-end",
          tag: "Chia sẻ kinh nghiệm",
        },
      ],
    };
  },
  methods: {
    goToSlide(index) {
      this.currentSlide = index;
    },
    nextSlide() {
      const newIndex = (this.currentSlide + 1) % this.slides.length;
      this.currentSlide = newIndex;
    },
    prevSlide() {
      const newIndex =
        (this.currentSlide - 1 + this.slides.length) % this.slides.length;
      this.currentSlide = newIndex;
    },
  },
  // eslint-disable-next-line vue/order-in-components
  mounted() {
    // Add animation effects when component is mounted
    const animateElements = () => {
      const cards = document.querySelectorAll(".slide-card");
      cards.forEach((card) => {
        card.classList.add("animated");
      });
    };

    setTimeout(animateElements, 100);
  },
};
</script>

<style scoped>
.carousel-container {
  position: relative;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.custom-carousel {
  border-radius: 12px;
}

.carousel-image {
  position: relative;
}

.carousel-image::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.3));
  pointer-events: none;
}

.slide-card {
  transition: all 0.5s ease;
  transform: translateY(30px);
  opacity: 0;
}

.slide-card.animated {
  transform: translateY(0);
  opacity: 1;
}

.custom-nav-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 10;
}

.custom-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.custom-dot:hover {
  background-color: rgba(255, 255, 255, 0.8);
}

.custom-dot.active {
  width: 24px;
  border-radius: 10px;
  background-color: white;
}

.custom-arrows {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  pointer-events: none;
}

.custom-arrow {
  width: 48px;
  height: 48px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
  pointer-events: auto;
}

.custom-arrow:hover {
  background-color: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
}

@media (max-width: 600px) {
  .custom-arrow {
    width: 36px;
    height: 36px;
  }

  .slide-card {
    max-width: 90% !important;
  }
}
</style>
