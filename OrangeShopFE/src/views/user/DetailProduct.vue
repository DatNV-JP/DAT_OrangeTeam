<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <!-- Shop Detail Start -->
    <div class="container-fluid py-5">
      <div class="row px-xl-5">
        <div class="col-lg-5 pb-5 border">
          <el-carousel>
            <el-carousel-item v-for="(img, index) in slideImg" :key="index">
              <img class="w-100 h-100" :src="urlViewImg + '/productImage/' + img" alt="Image">
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="col-lg-7 pb-5">
          <h3 class="font-weight-semi-bold">{{ productAddToCard.productName }}</h3>
          <div class="d-flex mb-3">
            <div class="text-primary mr-2">
              <small class="fas fa-star" />
              <small class="fas fa-star" />
              <small class="fas fa-star" />
              <small class="fas fa-star-half-alt" />
              <small class="far fa-star" />
            </div>
            <small class="pt-1">(50 Reviews)</small>
          </div>
          <h3 class="font-weight-semi-bold d-inline">
            {{ productAddToCard.price | currency('VND', 0, 'đ', '.', ',') }}
            <h6 v-if="productAddToCard.price !== productAddToCard.priceDefault" class="d-inline">
              <del class="text-muted mx-2">{{ productAddToCard.priceDefault | currency('VND', 0, 'đ', '.', ',') }}</del>
              <b-badge variant="danger">-{{ Math.floor(100-(productAddToCard.price/productAddToCard.priceDefault*100)) }}%</b-badge>
            </h6>
          </h3>
          <p class="mb-4 text-justify mt-4">{{ product.description }}</p>
          <div class="d-flex mb-3">
            <p class="text-dark font-weight-medium mb-0 mr-3">Số lượng kho:</p>
            <span class="text-muted">{{ quantityOfProductDetail }}</span>
          </div>
          <div class="d-flex mb-3">
            <p class="text-dark font-weight-medium mb-0 mr-3">{{ sizes.name }}:</p>
            <el-radio-group v-model="selectedSize" style="padding-top: .4rem">
              <el-radio v-for="(s, index) in sizes.value" :key="index" :label="s" class="mb-0 font-weight-bold" />
            </el-radio-group>
          </div>
          <div class="d-flex mb-4">
            <p class="text-dark font-weight-medium mb-0 mr-3">{{ colors.name }}:</p>
            <el-radio-group v-model="selectedColor" style="padding-top: .4rem">
              <el-radio v-for="(c, index) in colors.value" :key="index" :label="c" class="mb-0 font-weight-bold" />
            </el-radio-group>
          </div>
          <div class="d-flex align-items-center mb-4 pt-2">
            <div class="input-group quantity mr-3" style="width: 130px;">
              <div class="input-group-btn">
                <button class="btn btn-primary btn-minus h-100" @click="quantityReduce">
                  <i class="fa fa-minus" />
                </button>
              </div>
              <input v-model="quantity" type="text" class="form-control bg-secondary text-center" min="1">
              <div class="input-group-btn">
                <button class="btn btn-primary btn-plus h-100" @click="quantityIncrease">
                  <i class="fa fa-plus" />
                </button>
              </div>
            </div>
            <button class="btn btn-primary px-3" @click="addToCart(productAddToCard, selectedSize, selectedColor, quantity)"><i class="fa fa-shopping-cart mr-1" /> Add To Cart</button>
          </div>
          <div class="d-flex pt-2">
            <p class="text-dark font-weight-medium mb-0 mr-2">Share on:</p>
            <div class="d-inline-flex">
              <a class="text-dark px-2" href="">
                <i class="fab fa-facebook-f" />
              </a>
              <a class="text-dark px-2" href="">
                <i class="fab fa-twitter" />
              </a>
              <a class="text-dark px-2" href="">
                <i class="fab fa-linkedin-in" />
              </a>
              <a class="text-dark px-2" href="">
                <i class="fab fa-pinterest" />
              </a>
            </div>
          </div>
        </div>
      </div>
      <div class="mt-xl-5 px-xl-5">
        <el-tabs type="card" @tab-click="handleClick">
          <el-tab-pane label="Description">
            <h4 class="mb-3">Product Description</h4>
            <p>{{ product.description }}</p>
          </el-tab-pane>
          <el-tab-pane label="Information">
            <h4 class="mb-3">Additional Information</h4>
            <p>Eos no lorem eirmod diam diam, eos elitr et gubergren diam sea. Consetetur vero aliquyam invidunt duo dolores et duo sit. Vero diam ea vero et dolore rebum, dolor rebum eirmod consetetur invidunt sed sed et, lorem duo et eos elitr, sadipscing kasd ipsum rebum diam. Dolore diam stet rebum sed tempor kasd eirmod. Takimata kasd ipsum accusam sadipscing, eos dolores sit no ut diam consetetur duo justo est, sit sanctus diam tempor aliquyam eirmod nonumy rebum dolor accusam, ipsum kasd eos consetetur at sit rebum, diam kasd invidunt tempor lorem, ipsum lorem elitr sanctus eirmod takimata dolor ea invidunt.</p>
            <div class="row">
              <div class="col-md-6">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item px-0">
                    Sit erat duo lorem duo ea consetetur, et eirmod takimata.
                  </li>
                  <li class="list-group-item px-0">
                    Amet kasd gubergren sit sanctus et lorem eos sadipscing at.
                  </li>
                  <li class="list-group-item px-0">
                    Duo amet accusam eirmod nonumy stet et et stet eirmod.
                  </li>
                  <li class="list-group-item px-0">
                    Takimata ea clita labore amet ipsum erat justo voluptua. Nonumy.
                  </li>
                </ul>
              </div>
              <div class="col-md-6">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item px-0">
                    Sit erat duo lorem duo ea consetetur, et eirmod takimata.
                  </li>
                  <li class="list-group-item px-0">
                    Amet kasd gubergren sit sanctus et lorem eos sadipscing at.
                  </li>
                  <li class="list-group-item px-0">
                    Duo amet accusam eirmod nonumy stet et et stet eirmod.
                  </li>
                  <li class="list-group-item px-0">
                    Takimata ea clita labore amet ipsum erat justo voluptua. Nonumy.
                  </li>
                </ul>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <!-- Shop Detail End -->
    <!-- Products Start -->
    <div class="py-5">
      <div class="text-center mb-4">
        <h2 class="section-title px-5"><span class="px-2">You May Also Like</span></h2>
      </div>
      <div style="height: 28em">
        <el-carousel :interval="4000" type="card">
          <el-carousel-item v-for="(item, index) in prRecommend" :key="index">
            <product-card :product-card="item" :col="12" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
    <!-- Products End -->

  </el-main>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import userCommon from '@/views/user/mixin/user-mixin'
import PageHeader from '@/views/user/component/PageHeader'
import { getListProductByCategory, getProductDetail } from '@/api/product'
import ProductCard from '@/views/user/component/ProductCard/index.vue'
import { currency } from '@/filters'

export default {
  name: 'DetailProduct',
  directives: {
    horizontalScroll
  },
  filters: { currency },
  components: { PageHeader, ProductCard },
  mixins: [baseCommon, userCommon, BaseValidate],
  data() {
    return {
      quantity: 1,
      product: {},
      productAddToCard: {
        productDetailId: '',
        productName: '',
        image: '',
        color: '',
        size: '',
        priceDefault: 0,
        price: 0,
        quantity: 1
      },
      selectedSize: '',
      selectedColor: '',
      sizes: [],
      colors: [],
      variationOptions: [],
      quantityOfProductDetail: 10000000,
      prRecommend: [],
      slideImg: [],
      urlViewImg: process.env.VUE_APP_UPLOAD_URL
    }
  },
  computed: {
    productPriceSale() {
      // console.log(2, this.productAddToCard)
      // console.log(2, this.product)
      const product = this.product
      if (product.productDetails) {
        if (this.productAddToCard.price === 0 && this.product.productDetails[0].priceSale === 0) {
          return this.productAddToCard.priceDefault
        } else {
          return this.productAddToCard.price
        }
      }
      return 0
    },
    productPriceDefault() {
      // console.log(3, this.product)
      // console.log(3, this.productAddToCard)
      const product = this.product
      if (product.productDetails) {
        return this.productAddToCard.priceDefault === 0 ? this.product.productDetails[0].priceDefault : this.productAddToCard.priceDefault
      } else return 0
    }
  },
  watch: {
    quantity: function(newVal) {
      this.quantity = String(this.quantity).replace(/\D/g, '')
      if (newVal > this.quantityOfProductDetail) {
        this.quantity = this.quantityOfProductDetail
      }
      if (newVal <= 1) {
        this.quantity = 1
      }
    },
    selectedSize: function() {
      this.selectProductDetail()
    },
    selectedColor: function() {
      this.selectProductDetail()
    }
  },
  created() {
    this.getProductDetail()
  },
  methods: {
    handleClick(tab, event) {
      console.log(tab, event)
    },
    quantityReduce() {
      if (this.quantity <= 1) {
        this.quantity = 1
      } else {
        this.quantity--
      }
    },
    quantityIncrease() {
      if (this.quantity >= this.quantityOfProductDetail) {
        this.quantity = this.quantityOfProductDetail
      } else {
        this.quantity++
      }
    },
    getProductDetail() {
      getProductDetail(this.$route.params).then(res => {
        this.product = res.data
        this.productAddToCard.productName = this.product.name
        this.productAddToCard.price = this.product.productDetails[0].priceSale === 0 ? this.product.productDetails[0].priceDefault : this.product.productDetails[0].priceSale
        this.productAddToCard.priceDefault = this.product.productDetails[0].priceDefault
        this.productAddToCard.image = this.product.productDetails[0].images
        // eslint-disable-next-line no-return-assign
        this.quantityOfProductDetail = this.product.productDetails.map(p => p.quantity).reduce((total, quantity) => total += quantity, 0)
        this.slideImg = this.product.defaultImage.split(',')
        // this.slideImg.push(this.product.defaultImage)
        console.log(1, this.product)
        this.getProductVariations()
        this.getProRecommend()
      })
    },
    getProductVariations() {
      const options = {}
      let productVariations = []
      for (let i = 0; i < this.product.productDetails.length; i++) {
        const variationOptions = this.product.productDetails[i].variationOptions

        for (let j = 0; j < variationOptions.length; j++) {
          const variation = variationOptions[j].variation
          const value = variationOptions[j].value
          if (!options[variation.name]) {
            options[variation.name] = []
          }
          options[variation.name].push(value)
        }
      }
      productVariations = options

      for (const property in productVariations) {
        const obj = {}
        obj.name = property
        obj.value = productVariations[property].filter((item, index) => {
          return productVariations[property].indexOf(item) === index
        })
        this.variationOptions.push(obj)
      }
      this.sizes = this.variationOptions[0]
      this.colors = this.variationOptions[1]
      // console.log(this.variationOptions)
    },
    selectProductDetail() {
      if (this.selectedSize && this.selectedColor) {
        // Duyệt qua các phần tử trong mảng "productDetails"
        for (let i = 0; i < this.product.productDetails.length; i++) {
          const detail = this.product.productDetails[i]
          let hasSizeM = false
          let hasColorBlack = false

          // Kiểm tra xem phần tử có tuỳ chọn size 'M' hay không
          for (let j = 0; j < detail.variationOptions.length; j++) {
            if (detail.variationOptions[j].value === this.selectedSize && detail.variationOptions[j].variation.name === this.sizes.name) {
              hasSizeM = true
              break
            }
          }

          // Kiểm tra xem phần tử có tuỳ chọn color 'Black' hay không
          for (let j = 0; j < detail.variationOptions.length; j++) {
            if (detail.variationOptions[j].value === this.selectedColor && detail.variationOptions[j].variation.name === this.colors.name) {
              hasColorBlack = true
              break
            }
          }

          // Nếu phần tử có cả hai tuỳ chọn này, lấy thông tin chi tiết của phần tử đó
          if (hasSizeM && hasColorBlack) {
            // console.log(detail)
            // console.log(this.productAddToCard)
            this.productAddToCard.price = detail.priceSale === 0 ? detail.priceDefault : detail.priceSale
            this.productAddToCard.priceDefault = detail.priceDefault
            this.productAddToCard.productDetailId = detail.id
            this.productAddToCard.image = detail.images
            this.productAddToCard.color = this.selectedColor
            this.productAddToCard.size = this.selectedSize
            this.quantityOfProductDetail = detail.quantity
            break
          }
        }
      }
    },
    getProRecommend() {
      const params = {
        cid: this.product.categoryId,
        page: 0,
        size: 9
      }
      getListProductByCategory(params).then(res => {
        this.prRecommend = res.data.result
      })
    }
  }
}
</script>

<style>
.el-page-header__title {
  margin: auto !important;
}
.breadcrumb-container {
  padding-top: .5rem;
}
.el-carousel, .el-carousel__container {
  width: 100% !important;
  height: 100% !important;
}
.font-weight-medium {
  font-weight: 500;
}
.font-weight-semi-bold {
  font-weight: 600;
}
.text-primary {
  color: #D19C97 !important;
}
.btn-primary {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:hover {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
}

.btn-primary:focus, .btn-primary.focus {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}

.btn-primary.disabled, .btn-primary:disabled {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:not(:disabled):not(.disabled):active, .btn-primary:not(:disabled):not(.disabled).active,
.show > .btn-primary.dropdown-toggle {
  color: #fff;
  background-color: #c17a74;
  border-color: #bd726b;
}

.btn-primary:not(:disabled):not(.disabled):active:focus, .btn-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}
.bg-secondary {
  background-color: #EDF1FF !important;
}

a.bg-secondary:hover, a.bg-secondary:focus,
button.bg-secondary:hover,
button.bg-secondary:focus {
  background-color: #bac9ff !important;
}

@media (prefers-reduced-motion: reduce) {
  .form-control {
    transition: none;
  }
}

.form-control::-ms-expand {
  background-color: transparent;
  border: 0;
}

.form-control:-moz-focusring {
  color: transparent;
  text-shadow: 0 0 0 #495057;
}

.form-control:focus {
  color: #495057;
  background-color: #fff;
  border-color: #f8f0ef;
  outline: 0;
  box-shadow: none;
}

.form-control::placeholder {
  color: #999999;
  opacity: 1;
}

.form-control:disabled, .form-control[readonly] {
  background-color: #e9ecef;
  opacity: 1;
}

input[type="date"].form-control,
input[type="time"].form-control,
input[type="datetime-local"].form-control,
input[type="month"].form-control {
  appearance: none;
}
.el-radio__input.is-checked + .el-radio__label, .el-tabs__item.is-active {
  color: #bd726b;
}
.el-radio__input.is-checked .el-radio__inner {
  border-color: #bd726b;
  background: #bd726b;
}
.el-carousel, .el-carousel__container {
  overflow: hidden;
}
</style>
