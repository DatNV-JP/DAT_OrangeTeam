<template>
  <div :class="'col-lg-' + col + ' col-md-6 col-sm-12 pb-1'">
    <div class="card product-item border-0 mb-4 d-flex align-items-stretch">
      <div class="card-header product-img position-relative overflow-hidden bg-transparent border p-0" style="height: 10em">
        <img class="img-fluid w-100" :src="urlViewImg + '/productDetailImages/' + productAddToCard.image" alt="">
      </div>
      <b-badge variant="danger" :hidden="badge.hidden">{{ badge.name }}</b-badge>
      <div class="card-body border-left border-right text-center p-0 pt-2 pb-1">
        <div class="d-flex justify-content-center px-1" style="height: 4em">
          <router-link :to="'/product/view-detail/' + productCard.id" class="linkCard text-black"><h6 class="text-truncate mb-1">{{ productCard.name }}</h6></router-link>
        </div>
        <div class="d-flex justify-content-center mb-1">
          <h6>{{ minMaxPrice.minPrice | currency('VND', 0, 'đ', '.', ',') }} - {{ minMaxPrice.maxPrice | currency('VND', 0, 'đ', '.', ',') }}</h6>
        </div>
        <div class="d-flex justify-content-center mb-1">
          <h6>{{ productPriceSale | currency('VND', 0, 'đ', '.', ',') }}</h6>
          <h6 v-if="productPriceSale !== productPriceDefault">
            <del class="text-muted mx-2">{{ productPriceDefault | currency('VND', 0, 'đ', '.', ',') }}</del>
            <b-badge variant="danger">{{ Math.floor(100-((productPriceSale/productPriceDefault)*100)) }}%</b-badge>
          </h6>
        </div>
        <div class="d-flex justify-content-center mb-1">
          <el-radio-group v-model="selectedSize" size="mini" fill="#D19C97">
            <el-radio-button v-for="(s) in sizes.value" :key="s" :label="s" />
          </el-radio-group>
        </div>
        <div class="d-flex justify-content-center align-items-end w-100">
          <el-radio-group v-model="selectedColor" size="mini" fill="#D19C97">
            <el-radio-button v-for="(c) in colors.value" :key="c" :label="c" />
          </el-radio-group>
        </div>
      </div>
      <div class="card-footer d-flex justify-content-between bg-light border">
        <el-button class="linkCard text-dark p-0 border-0 bg-light" @click="viewDetail('/product/view-detail/' + productCard.id)">
          <i class="fas fa-eye mr-1" style="color: #D19C97" />Xem chi tiết
        </el-button>
        <el-button class="linkCard text-dark p-0 border-0 bg-light" @click="addToCart(productAddToCard, selectedSize, selectedColor)">
          <i class="fas fa-shopping-cart mr-1" style="color: #D19C97" />Thêm vào giỏ
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import baseCommon from '@/utils/base-common'
import userCommon from '@/views/user/mixin/user-mixin'
import { currency, numberFormatter } from '@/filters'

export default {
  filters: { currency, numberFormatter },
  mixins: [baseCommon, userCommon],
  props: {
    productCard: {
      type: Object,
      require: true,
      default(rawProps) {
        return { message: 'Truyền vào product' }
      }
    },
    col: {
      type: Number,
      require: true,
      default(rawProps) {
        return 3
      }
    },
    badge: {
      type: Object,
      default(rawProps) {
        return { name: 'NEW', hidden: true }
      }
    }
  },
  data() {
    return {
      selectedSize: '',
      selectedColor: '',
      sizes: [],
      colors: [],
      variationOptions: [],
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
      urlViewImg: process.env.VUE_APP_UPLOAD_URL
    }
  },
  computed: {
    ...mapGetters([
      'avatar',
      'token'
    ]),
    productPriceSale() {
      if (this.productAddToCard.price === 0 && this.productCard.productDetails[0].priceSale === 0) {
        return this.productAddToCard.priceDefault
      } else {
        return this.productAddToCard.price
      }
    },
    productPriceDefault() {
      return this.productAddToCard.priceDefault === 0 ? this.productCard.productDetails[0].priceDefault : this.productAddToCard.priceDefault
    },
    minMaxPrice() {
      return this.getProductPriceRange(this.productCard.productDetails)
    }
  },
  watch: {
    selectedSize: function() {
      this.selectProductDetail()
    },
    selectedColor: function() {
      this.selectProductDetail()
    },
    productCard: {
      handler(newVal, oldVal) {
        this.productAddToCard.productName = this.productCard.name
        this.productAddToCard.price = this.productCard.productDetails[0].priceSale === 0 ? this.productCard.productDetails[0].priceDefault : this.productCard.productDetails[0].priceSale
        this.productAddToCard.priceDefault = this.productCard.productDetails[0].priceDefault
        // const arrImg = this.productCard.defaultImage.split(',')
        this.productAddToCard.image = this.productCard.productDetails[0].images
      },
      deep: true
    }
  },
  created() {
    this.getProductVariations()
  },
  mounted() {
    this.productAddToCard.productName = this.productCard.name
    this.productAddToCard.price = this.productCard.productDetails[0].priceSale === 0 ? this.productCard.productDetails[0].priceDefault : this.productCard.productDetails[0].priceSale
    this.productAddToCard.priceDefault = this.productCard.productDetails[0].priceDefault
    // const arrImg = this.productCard.defaultImage.split(',')
    this.productAddToCard.image = this.productCard.productDetails[0].images
  },
  methods: {
    getProductPriceRange(products) {
      const priceArr = products.map(product => product.priceSale)
      const priceArrMax = products.map(product => product.priceDefault)
      const maxPrice = priceArrMax.reduce((prev, current) => prev > current ? prev : current, priceArrMax[0])
      let minPrice = priceArr.reduce((prev, current) => prev < current ? prev : current, priceArr[0])
      if (minPrice === 0) {
        minPrice = maxPrice
      }
      return { maxPrice, minPrice }
    },
    viewDetail(data) {
      this.$router.push(data)
    },
    getProductVariations() {
      const options = {}
      let productVariations = []
      for (let i = 0; i < this.productCard.productDetails.length; i++) {
        const variationOptions = this.productCard.productDetails[i].variationOptions

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
        for (let i = 0; i < this.productCard.productDetails.length; i++) {
          const detail = this.productCard.productDetails[i]
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
            // console.log(1, detail.priceDefault)
            this.productAddToCard.price = detail.priceSale === 0 ? detail.priceDefault : detail.priceSale
            this.productAddToCard.priceDefault = detail.priceDefault
            this.productAddToCard.productDetailId = detail.id
            this.productAddToCard.image = detail.images
            this.productAddToCard.color = this.selectedColor
            this.productAddToCard.size = this.selectedSize
            // console.log(detail)
            // console.log(2, this.productAddToCard.priceDefault)
            break
          }
        }
      }
    }
  }
}
</script>

<style>
.el-radio-button--mini .el-radio-button__inner {
  padding: 5px 12px;
}
.product-img img {
  transition: .5s;
}

.product-img img:hover {
  transform: scale(1.2);
}

.product-item .linkCard:hover {
  color: #D19C97 !important;
  text-decoration: none;
  background: none !important;
}

.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  text-align: center;
}
</style>
