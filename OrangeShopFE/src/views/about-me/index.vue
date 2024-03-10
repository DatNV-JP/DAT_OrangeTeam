<template>
  <div>
    <div class="container-fluid">
      <Navbar />
      <div class="row px-3">
        <div class="col-sm-12 " :style="{ backgroundImage: `url(${urlViewImg}/productDetailImages/myBanner.png)`, backgroundSize: 'cover', width: '100%', height: '0', paddingBottom: 'calc(550px / 100 * 100)' }">
          <div class="row mt-banner-custom">
            <h1 class="col-sm-12 d-flex justify-content-center font-weight-bold" style="color: white">Câu Chuyện Về Sanvadio</h1>
            <span class="col-sm-12 d-flex justify-content-center" style="color: white">Đây là chuyên mục giúp các bạn biết được Sanvadio đã hình thành ra sao và chúng tôi muốn xây dựng một công ty như thế nào!</span>
          </div>
        </div>
      </div>
      <div class="row mt-5">
        <div class="col-sm-3">
          <h2>Sanvadio sinh ra để làm gì?</h2>
        </div>
        <div class="col-sm-9">
          <p>Shop thời trang nam Sanvadio là một trong những địa chỉ mua sắm hàng đầu cho các quý ông yêu thích phong cách thời trang hiện đại và sang trọng. Với một bộ sưu tập đa dạng các sản phẩm từ áo sơ mi, áo khoác, quần jean, Sanvadio mang đến cho khách hàng những sản phẩm chất lượng cao và độc đáo.</p>
          <p>Được thành lập từ năm 2020, Sanvadio đã trở thành một trong những thương hiệu thời trang nam hàng đầu tại Việt Nam. Với tiêu chí luôn cập nhật xu hướng mới nhất và chất lượng sản phẩm đảm bảo, Sanvadio không chỉ thu hút được khách hàng trong nước mà còn có nhiều khách hàng quốc tế.</p>
          <p>Cửa hàng Sanvadio được thiết kế với không gian rộng rãi, thoáng mát và trang trí đẹp mắt, tạo ra một không gian mua sắm thú vị và thoải mái cho khách hàng. Bên cạnh đó, đội ngũ nhân viên tận tình, nhiệt tình và giàu kinh nghiệm sẽ giúp quý khách tìm kiếm những sản phẩm phù hợp nhất với phong cách của mình.</p>
          <p>Với sự cam kết mang đến những sản phẩm chất lượng và dịch vụ tốt nhất cho khách hàng, Sanvadio đang trở thành một trong những địa chỉ mua sắm không thể bỏ qua đối với các quý ông yêu thích thời trang tại Việt Nam.</p>
        </div>
      </div>
      <div class="row mt-3 d-flex align-items-center" style="background-color: black; height: 190px;">
        <div>
          <h2 class="d-flex justify-content-center" style="color: #f9f86c;">"Sự tự tin của bạn bắt đầu từ việc lựa chọn thời trang tại chúng tôi"</h2>
          <span class="d-flex justify-content-center" style="color: white; display: block;">-VoCute-</span>
        </div>
      </div>
      <div class="row mt-3">
        <div class="col-sm-6">
          <img class="img-fluid w-100 rounded" :src="urlViewImg + '/productDetailImages/banner2.jpg'">
        </div>
        <div class="col-sm-6 d-flex justify-content-start">
          <div class="row">
            <div lass="col-sm-12">
              <h2 style="color: #0a76a4">Câu chuyện dịch vụ khách hàng của Sanvadio</h2>
              <p class="mt-5">Theo một khảo sát gần đây nhất của Sanvadio tự thực hiện thì có tới 61% khách hàng quyết định mua sắm ở Sanvadio vì ấn tượng với dịch vụ khách hàng. 94% khách hàng sẵn sàng giới thiệu Sanvadio với những người khác.</p>
              <p>Ở Sanvadio, chúng tôi tin rằng việc bán một gói hàng đó là bán cả một trải nghiệm mua sắm. Sanvadio có kỳ vọng trở thành một thương hiệu điển hình về việc hướng tới khách hàng một cách sâu sắc tại Việt Nam. Với mong muốn góp một phần nhỏ thay đổi nhận thức của các doanh nghiệp trong việc mang tới những trải nghiêm tốt hơn cho khách hàng, đặc biệt trong lĩnh vực Thương Mại Điện Tử.</p>
              <p>Việc đóng gói một hộp đồ với “Double Box” cũng được Sanvadio đi tiên phong từ cách đây 2 năm, và trở thành phổ biến nhiều hơn ở các thương hiệu khác.</p>
              <p>Nếu bạn thử hỏi CSKH Sanvadio một tiệm Pizza ngon quanh khu vực của bạn thì chắc các bạn ấy cũng sẽ cố gắng tìm và gửi cho bạn một địa chỉ phù hợp đó!</p>
            </div>
          </div>
        </div>
      </div>
      <div class="row mt-3 d-flex align-items-center" style="background-color: #4083ff; height: 200px;">
        <div>
          <h2 class="d-flex justify-content-center font-weight-bold" style="color: white;">"Trong cuộc sống có quá nhiều sự lựa chọn, cảm ơn bạn đã lựa chọn Sanvadio!"</h2>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script>
import { Navbar, Footer } from '@/userlayout/components'
export default {
  name: 'IndexAboutMe',
  components: { Footer, Navbar },
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL
    }
  }
}
</script>

<style scoped>
.mt-banner-custom {
  margin-top: 12rem;
}

</style>

