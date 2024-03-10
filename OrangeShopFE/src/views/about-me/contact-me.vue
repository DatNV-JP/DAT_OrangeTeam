<template>
  <div>
    <Navbar />
    <div class="row">
      <div class="col-sm-12 " :style="{ backgroundImage: `url(${urlViewImg}/productDetailImages/banner3.jpg)`, backgroundSize: 'cover', width: '100%', height: '0', paddingBottom: 'calc(550px / 100 * 100)' }">
        <div class="mt-5 d-flex justify-content-center"><h1 class="font-weight-bold" style="color: white">Trang Liên Hệ</h1></div>
      </div>
      <div class="col-sm-12 mt-5 mb-5">
        <div class="row">
          <div class="col-sm-6 pl-5">
            <h1>Thời Trang Sanvadio</h1>
            <span>Đội ngũ CSKH của Sanvadio luôn sẵn sàng hỗ trợ từ 8:30 sáng cho tới 22:00 tối tất cả các ngày trong tuần bao gồm cả thứ 7 và Chủ Nhật. Mục tiêu của chúng tôi là tới quý 2 năm 2023, đội ngũ CSKH sẽ hoạt động 24 giờ/1 ngày và 365 ngày trong 1 năm.</span>
          </div>
          <div class="col-sm-6 pr-5">
            <h1>Liên Hệ Chúng Tôi</h1>
            <p>Email: sanvadio.buzz@gmail.com</p>
            <p>CSKH: 0988888888</p>
            <p>Thời gian làm việc: 8:30 sáng cho tới 22:00 tối tất cả các ngày trong tuần</p></div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script>
import { Footer, Navbar } from '@/userlayout/components'
export default {
  name: 'ContactMe',
  components: { Footer, Navbar },
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL
    }
  }
}
</script>

<style scoped>

</style>
