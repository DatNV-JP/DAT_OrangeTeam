<template>
  <el-row :key="cartRow.productDetailId" class="pt-3 pb-3">
    <el-col :span="4" class="d-flex justify-content-center align-items-center" style="height: 8em">
      <img :src="imgProduct" class="w-75" alt="product img">
    </el-col>
    <el-col :span="16">
      <el-col :span="24" class="align-middle">
        <p class="font-weight-semi-bold p-2">{{ cartRow.productName }}</p>
      </el-col>
      <el-col :span="12" class="align-middle d-flex justify-content-center align-items-center mb-2">
        <span><strong>Size:</strong> {{ cartRow.size }} </span>
      </el-col>
      <el-col :span="12" class="align-middle d-flex justify-content-center align-items-center mb-2">
        <span><strong>Color:</strong> {{ cartRow.color }}</span>
      </el-col>
      <el-col :span="12" class="align-middle d-flex justify-content-center align-items-center">
        <el-input-number v-model="cartRow.quantity" :min="1" :max="quantityOfProductDetail" size="mini" @change="handleChange" />
      </el-col>
      <el-col :span="12" class="align-middle pl-3 d-flex justify-content-center align-items-center">
        <p class="font-weight-semi-bold">Tổng: {{ cartRow.price * cartRow.quantity | currency('VND', 0, 'đ', '.', ',') }}</p>
      </el-col>
    </el-col>
    <el-col :span="4" class="d-flex justify-content-center align-items-center" style="height: 8em">
      <button class="btn btn-sm btn-primary" @click="removeCartRow"><i class="fa fa-times" /></button>
    </el-col>
  </el-row>
</template>

<script>
import userCommon from '@/views/user/mixin/user-mixin'
import { removeItem, updateQuantity } from '@/api/cart'
import { getProductDetail } from '@/api/productDetail'
import { currency } from '@/filters'
import { mapGetters } from 'vuex'
export default {
  filters: { currency },
  mixins: [userCommon],
  props: {
    cartRow: {
      type: Object,
      require: true,
      default(rawProps) {
        return { message: 'Truyền vào cart row' }
      }
    }
  },
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL,
      quantityOfProductDetail: 1000000
    }
  },
  computed: {
    ...mapGetters([
      'token',
      'cart'
    ]),
    imgProduct() {
      return `${this.urlViewImg}/productDetailImages/${this.cartRow.image}`
    }
  },
  created() {
    this.getProductDetail()
  },
  methods: {
    handleChange(value) {
      if (!this.token) {
        const item = this.cart.find(item => item.productDetailId === this.cartRow.productDetailId)
        if (item) {
          item.quantity = parseInt(this.cartRow.quantity)
          this.saveToLocalStorage()
          this.$store.dispatch('cart/getCart')
        }
      } else {
        const data = {
          id: this.cartRow.productDetailId,
          qty: this.cartRow.quantity
        }
        updateQuantity(data).then(() => {
          this.$store.dispatch('cart/getCart')
        })
      }
    },
    handleClick(tab, event) {
      console.log(tab, event)
    },
    removeCartRow() {
      if (this.token) {
        removeItem(this.cartRow.productDetailId).then(() => {
          this.$store.dispatch('cart/getCart')
        })
      } else {
        const index = this.cart.findIndex(c => c.productDetailId === this.cartRow.productDetailId)
        this.cart.splice(index, 1)
        this.saveToLocalStorage()
        this.$store.dispatch('cart/getCart')
      }
    },
    getProductDetail() {
      getProductDetail(this.cartRow.productDetailId).then(res => {
        const { quantity } = res.data
        this.quantityOfProductDetail = quantity
      })
    }
  }
}
</script>

<style scoped>

</style>
