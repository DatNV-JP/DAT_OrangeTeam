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
        <span><strong>{{ propertyName1 }}:</strong> {{ propertyValue1 }} </span>
      </el-col>
      <el-col :span="12" class="align-middle d-flex justify-content-center align-items-center mb-2">
        <span><strong>{{ propertyName2 }}:</strong> {{ propertyValue2 }} </span>
      </el-col>
      <el-col :span="12" class="align-middle d-flex justify-content-center align-items-center">
        <el-input-number v-model="cartRow.quantity" :min="1" :max="quantityOfProductDetail" size="mini" @change="handleChange" />
      </el-col>
      <el-col :span="12" class="align-middle pl-3 d-flex justify-content-center align-items-center">
        <p class="font-weight-semi-bold">Tong: {{ cartRow.price * cartRow.quantity | currency('VND', 0, 'đ', '.', ',') }}</p>
      </el-col>
    </el-col>
    <el-col :span="4" class="d-flex justify-content-center align-items-center" style="height: 8em">
      <button class="btn btn-sm btn-primary" @click="removeCartRow"><i class="fa fa-times" /></button>
    </el-col>
  </el-row>
</template>

<script>
import userCommon from '@/views/user/mixin/user-mixin'
import { getProductDetail } from '@/api/productDetail'
import { currency } from '@/filters'
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
      quantityOfProductDetail: 1000000,
      propertyName1: '',
      propertyValue1: '',
      propertyName2: '',
      propertyValue2: ''
    }
  },
  computed: {
    imgProduct() {
      return `${this.urlViewImg}/productDetailImages/${this.cartRow.image}`
    }
  },
  created() {
    this.getProductDetail()
  },
  methods: {
    handleChange(value) {
    },
    removeCartRow() {
      this.$emit('remove-order-row', this.cartRow)
    },
    getProductDetail() {
      getProductDetail(this.cartRow.productDetailId).then(res => {
        const { quantity, variationOptions } = res.data
        this.quantityOfProductDetail = quantity
        this.propertyName1 = variationOptions[0].variation.name
        this.propertyName2 = variationOptions[1].variation.name
        this.propertyValue1 = variationOptions[0].value
        this.propertyValue2 = variationOptions[1].value
      })
    }
  }
}
</script>

<style scoped>

</style>
