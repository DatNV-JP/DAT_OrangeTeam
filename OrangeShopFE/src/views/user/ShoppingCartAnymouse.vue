<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <!-- Cart Start -->
    <div class="container-fluid pt-5">
      <div class="row px-xl-1">
        <div class="col-lg-6 table-responsive mb-5">
          <div class="card border-0 mb-3 mt-3">
            <div class="card-header bg-secondary border-0">
              <h4 class="font-weight-semi-bold m-0">Chi tiết đơn hàng</h4>
            </div>
            <div class="card-body">
              <cart-row
                v-for="(product, index) in cart"
                :key="index"
                :cart-row="product"
              />
              <hr class="mt-0">
              <div class="d-flex justify-content-between pt-1">
                <h6 class="font-weight-medium">Tổng tiền hàng</h6>
                <h6 class="font-weight-medium">{{ subTotal | currency('VND', 0, 'đ', '.', ',') }}</h6>
              </div>
              <div class="d-flex justify-content-between">
                <h6 class="font-weight-medium">Giảm giá</h6>
                <h6 class="font-weight-medium">{{ discount | currency('VND', 0, 'đ', '.', ',') }}</h6>
              </div>
              <div class="d-flex justify-content-between">
                <h6 class="font-weight-medium">Phí ship</h6>
                <h6 class="font-weight-medium">{{ shipping | currency('VND', 0, 'đ', '.', ',') }}</h6>
              </div>
              <div class="d-flex justify-content-between">
                <h6 class="font-weight-medium">Giao hàng dự kiến:</h6>
                <h6 class="font-weight-medium">{{ leadTime | parseTime('{d}-{m}-{y} {h}:{i}') }}</h6>
              </div>
            </div>
            <div class="card-footer border-secondary bg-transparent">
              <div class="d-flex justify-content-between mt-2">
                <h5 class="font-weight-bold">Tổng</h5>
                <h5 class="font-weight-bold">{{ subTotal - discount + shipping | currency('VND', 0, 'đ', '.', ',') }}</h5>
              </div>
            </div>
          </div>
          <div class="card border-0 mb-5">
            <div class="card-body">
              <form action="">
                <div class="input-group">
                  <el-select v-model="voucherSelect" filterable placeholder="Chọn voucher" value-key="item.id" class="w-100 mb-3" clearable @change="handleSelectVoucher">
                    <el-option
                      v-for="item in listVoucher"
                      :key="item.id"
                      :label="item.code"
                      :value="item"
                      :disabled="item.minimumOrderValue > subTotal"
                    >
                      <div class="link">
                        <span v-if="item.isPercent" class="value">Mã voucher: {{ item.code }}, Giảm {{ item.discountAmount }}% tối đa: {{ item.maxDiscountAmount | currency('VND', 0, 'đ', '.', ',') }} cho đơn hàng từ {{ item.minimumOrderValue | currency('VND', 0, 'đ', '.', ',') }}</span>
                        <span v-else class="value">Mã voucher: {{ item.code }}, Giảm {{ item.discountAmount | currency('VND', 0, 'đ', '.', ',') }} cho đơn hàng từ {{ item.minimumOrderValue | currency('VND', 0, 'đ', '.', ',') }}</span>
                      </div>
                    </el-option>
                  </el-select>
                </div>
              </form>
            </div>
          </div>
        </div>
        <div class="col-lg-6">
          <ValidationObserver v-slot="{ invalid, handleSubmit }">
            <el-form label-position="top">
              <el-row type="flex" class="row-bg">
                <el-col :span="12">
                  <h4 class="font-weight-semi-bold mb-4">Địa chỉ nhận hàng</h4>
                </el-col>
                <el-col :span="12">
                  <h4 class="font-weight-semi-bold mb-4 d-flex justify-content-end" />
                </el-col>
              </el-row>
              <div class="row">
                <div class="col-md-6 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required">
                    <el-form-item :error="messageError('Họ tên', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Họ tên<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input v-model="billingAddress.name" />
                    </el-form-item>
                  </ValidationProvider>
                </div>
                <div class="col-md-6 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required|phone">
                    <el-form-item :error="messageError('Số điện thoại', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Số điện thoại<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input v-model="billingAddress.phone" />
                    </el-form-item>
                  </ValidationProvider>
                </div>
                <div class="col-md-12 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required|email">
                    <el-form-item :error="messageError('Email', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Email<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input v-model="billingAddress.email" />
                    </el-form-item>
                  </ValidationProvider>
                </div>
                <div class="col-md-12 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required">
                    <el-form-item :error="messageError('Địa chỉ', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Địa chỉ<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input
                        v-model="billingAddress.addressLine1"
                        type="text"
                      />
                    </el-form-item>
                  </ValidationProvider>
                </div>
                <div class="row">
                  <div class="col-md-4">
                    <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                      <el-form-item :error="messageError('Thành phố', errors[0])">
                        <template v-slot:label><label><span class="svg-container">Thành phố<span
                          class="text-danger"
                        > *</span></span></label></template>
                        <el-select
                          v-model="city"
                          class="w-100"
                          placeholder="Chọn thành phố"
                          value-key="id"
                          filterable
                          remote
                          reserve-keyword
                          :remote-method="remoteCity"
                          @change="changeCity"
                        >
                          <el-option
                            v-for="(item, index) in cities"
                            :key="index"
                            :label="item.name"
                            :value="item"
                          />
                        </el-select>
                      </el-form-item>
                    </ValidationProvider>
                  </div>
                  <div class="col-md-4">
                    <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                      <el-form-item :error="messageError('Quận/Huyện', errors[0])">
                        <template v-slot:label><label><span class="svg-container">Quận/Huyện<span
                          class="text-danger"
                        > *</span></span></label></template>
                        <el-select
                          v-model="district"
                          class="w-100"
                          placeholder="Chọn quận/huyện"
                          value-key="id"
                          filterable
                          remote
                          reserve-keyword
                          :remote-method="remoteDistrict"
                          :disabled="!city"
                        >
                          <el-option
                            v-for="(item, index) in listDistrict"
                            :key="index"
                            :label="item.name"
                            :value="item"
                          />
                        </el-select>
                      </el-form-item>
                    </ValidationProvider>
                  </div>
                  <div class="col-md-4">
                    <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                      <el-form-item :error="messageError('Phường/xã', errors[0])">
                        <template v-slot:label><label><span class="svg-container">Phường/xã<span
                          class="text-danger"
                        > *</span></span></label></template>
                        <el-select
                          v-model="village"
                          class="w-100"
                          placeholder="Chọn phường/xã"
                          value-key="id"
                          filterable
                          remote
                          reserve-keyword
                          :remote-method="remoteVillage"
                          :disabled="!district"
                        >
                          <el-option
                            v-for="(item, index) in listVillage"
                            :key="index"
                            :label="item.name"
                            :value="item"
                          />
                        </el-select>
                      </el-form-item>
                    </ValidationProvider>
                  </div>
                </div>
                <div class="col-md-12 form-group">
                  <label>Ghi chú:</label>
                  <el-input
                    v-model="description"
                    type="textarea"
                    :rows="2"
                    placeholder="Please input"
                  />
                </div>
                <div class="col-md-12 form-group">
                  <div class="card border-0 mb-5">
                    <div class="card-header bg-secondary border-0">
                      <h4 class="font-weight-semi-bold m-0">Payment</h4>
                    </div>
                    <div class="card-body">
                      <el-radio-group v-model="paymentType">
                        <div v-for="(pm, index) in paymentMethod" :key="index" class="pb-3">
                          <el-radio :label="pm">{{ pm.value }}</el-radio>
                        </div>
                      </el-radio-group>
                    </div>
                    <div class="card-footer border-secondary bg-transparent">
                      <button class="btn btn-lg btn-block btn-primary font-weight-bold my-3 py-3" :disabled="invalid||cart.length<1" @click.prevent="handleSubmit(placeOrder)">Đặt hàng</button>
                    </div>
                  </div>
                </div>
              </div>
            </el-form>
          </ValidationObserver>
        </div>
      </div>
    </div>
    <!-- Cart End -->
  </el-main>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import userCommon from '@/views/user/mixin/user-mixin'
import PageHeader from '@/views/user/component/PageHeader'
import CartRow from '@/views/user/component/CartRow'
import { mapGetters } from 'vuex'
import { getCalculateLeadTime, getCities, getDistricts, getVillages } from '@/api/goog-remote-search'
import elDragDialog from '@/directive/el-drag-dialog'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import { getCalculateShip, getListPaymentMethod, redirectPayment } from '@/api/payment'
import { addOrder } from '@/api/order'
import { parseTime } from '@/utils'
import { currency } from '@/filters'
import { getListVoucherUser } from '@/api/voucher'
// import { checkVoucher, getListVoucherUser } from '@/api/voucher'
// import { Message } from 'element-ui'
export default {
  name: 'DetailProduct',
  filters: {
    parseTime, currency
  },
  directives: {
    horizontalScroll, elDragDialog
  },
  components: { PageHeader, CartRow, ValidationObserver, ValidationProvider },
  mixins: [baseCommon, userCommon, BaseValidate],
  data() {
    return {
      voucherSelect: '',
      listVoucher: [],
      paymentType: {},
      size: '',
      color: '',
      quantity: 1,
      district: '',
      city: '',
      village: '',
      listAddress: [],
      listDistrict: [],
      listVillage: [],
      state: '',
      loading: false,
      discount: 0,
      shipping: 0,
      description: '',
      activeName: 'addressList',
      dialogTableVisible: false,
      cities: [],
      billingAddress: {
        name: '',
        phone: '',
        email: '',
        addressLine1: '',
        village: {
          id: '',
          name: '',
          district: {
            id: '',
            name: '',
            city: {
              id: '',
              name: ''
            }
          }
        }
      },
      paymentMethod: [],
      leadTime: ''
    }
  },
  computed: {
    ...mapGetters([
      'cart',
      'email',
      'token'
    ])
  },
  watch: {
    subTotal: {
      handler(newVal, oldVal) {
        if (this.voucherSelect) {
          if (newVal < this.voucherSelect.minimumOrderValue) {
            console.log(newVal)
            this.voucherSelect = ''
            this.discount = 0
          }
        }
      }
    },
    cart: {
      handler(newVal, oldVal) {
        if (this.cart.length < 1) {
          this.$swal({
            imageUrl: 'https://unsplash.it/400/200',
            imageWidth: 400,
            imageHeight: 200,
            title: 'Oops...',
            text: 'Giỏ hàng đang trống hãy thêm sản phẩm để mua hàng!',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Thêm sản phẩm ngay'
          }).then((result) => {
            if (result.isConfirmed) this.$router.push('/product/our-product')
          })
        }
      }
    },
    village: {
      handler(newVal, oldVal) {
        this.billingAddress.village = newVal
        this.billingAddress.village.district = this.district
        this.billingAddress.village.district.city = this.city
        this.calculateShip()
      },
      deep: true
    }
  },
  created() {
    if (this.token) {
      this.$router.push('/user/shopping-cart')
    }
    this.getListPaymentMethod()
  },
  mounted() {
    const param = {
      status: 4,
      page: 0,
      size: 10000 }
    getListVoucherUser(param).then(res => {
      this.listVoucher = res.data.result
      console.log(this.listVoucher)
    })
  },
  methods: {
    changeCity() {
      this.district = {}
      this.village = {}
      this.listDistrict = []
      this.listVillage = []
    },
    handleSelectVoucher(item) {
      // if (item.id) {
      //   checkVoucher({ voucherId: item.id, orderValue: this.subTotal }).then(res => {
      //     console.log(res)
      //     console.log(item)
      //     if (item.isPercent) {
      //       this.discount = (this.subTotal * item.discountAmount) > item.maxDiscountAmount ? item.maxDiscountAmount : (this.subTotal * item.discountAmount)
      //     } else {
      //       this.discount = item.discountAmount
      //     }
      //     Message({
      //       message: 'Đã áp dụng voucher thành công',
      //       type: 'success',
      //       duration: 5000
      //     })
      //     // this.discount = item.
      //   }).catch(() => {
      //     Message({
      //       message: 'Có lỗi khi áp dụng voucher hãy thử lại sau',
      //       type: 'error',
      //       duration: 5000
      //     })
      //     this.discount = 0
      //     this.voucherSelect = ''
      //   })
      // } else {
      //   this.discount = 0
      //   this.voucherSelect = ''
      // }
    },
    remoteCity(queryString) {
      if (queryString !== '') {
        this.loading = true
        getCities().then(res => {
          this.cities = res.data.data.map(city => {
            return {
              id: city.ProvinceID,
              name: city.ProvinceName
            }
          })
          this.loading = false
          this.cities = this.cities.filter(item => {
            const searchNormalized = queryString.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '')
            return item.name.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '').indexOf(searchNormalized) !== -1
          })
        })
      } else {
        this.cities = []
      }
    },
    remoteDistrict(queryString) {
      if (queryString !== '') {
        this.loading = true
        getDistricts(this.city.id).then(res => {
          this.loading = false
          const districts = res.data.data
          this.listDistrict = districts.filter(item => {
            const searchNormalized = queryString.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '')
            return item.DistrictName.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '').indexOf(searchNormalized) !== -1
          }).map(city => {
            return {
              id: city.DistrictID,
              name: city.DistrictName
            }
          })
        })
      } else {
        this.listDistrict = []
      }
    },
    remoteVillage(queryString) {
      if (queryString !== '') {
        this.loading = true
        getVillages(this.district.id).then(res => {
          this.loading = false
          const villages = res.data.data
          this.listVillage = villages.filter(item => {
            const searchNormalized = queryString.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '')
            return item.WardName.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
              .toLowerCase().replace(/[^a-z0-9]/g, '').indexOf(searchNormalized) !== -1
          }).map(city => {
            return {
              id: city.WardCode,
              name: city.WardName
            }
          })
        })
      } else {
        this.listVillage = []
      }
    },
    handleDrag() {
      this.$refs.select.blur()
    },
    getListPaymentMethod() {
      this.showLoading()
      getListPaymentMethod().then(res => {
        this.paymentMethod = res.data.result
        this.paymentType = this.paymentMethod[0]
        if (this.cart.length < 1) {
          this.$swal({
            imageUrl: 'https://unsplash.it/400/200',
            imageWidth: 400,
            imageHeight: 200,
            title: 'Oops...',
            text: 'Giỏ hàng đang trống hãy thêm sản phẩm để mua hàng!',
            confirmButtonColor: '#3085d6',
            confirmButtonText: 'Thêm sản phẩm ngay'
          }).then((result) => {
            if (result.isConfirmed) this.$router.push('/product/our-product')
          })
        }
      }).finally(() => {
        this.hideLoading()
      })
    },
    placeOrder() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn tạo đơn hàng này!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Mua thêm sản phẩm',
        confirmButtonText: 'Tạo đơn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          const order = {}
          order.consigneeName = this.billingAddress.name
          order.consigneePhone = this.billingAddress.phone
          order.consigneeEmail = this.billingAddress.email
          order.orderTotal = this.subTotal - this.discount + this.shipping
          order.paymentType = this.paymentType
          order.toAddress = this.billingAddress.addressLine1
          order.toWard = this.billingAddress.village.name
          order.toDistrict = this.billingAddress.village.district.name
          order.toProvince = this.billingAddress.village.district.city.name
          order.orderDetails = this.cart
          order.description = this.description
          order.estimatedDeliveryTime = this.leadTime
          order.voucherDiscount = this.discount
          order.shippingFee = this.shipping
          order.voucherId = this.voucherSelect.id
          console.log('order', order)
          if (order.paymentType.id === 1) {
            addOrder(order).then(res => {
              console.log(res)
              this.hideLoading()
              this.$swal(
                'Đã tạo đơn!',
                'Bạn đã tạo đơn hàng thành công!.',
                'success'
              ).then(() => {
                localStorage.setItem('cart', JSON.stringify([]))
                this.$store.dispatch('cart/getCart')
                this.$router.push('/home')
              })
            }).finally(() => {
              this.hideLoading()
            })
          } else {
            redirectPayment(order).then(res => {
              this.hideLoading()
              this.$swal(
                'Đã tạo đơn!',
                'Bạn đã tạo đơn hàng thành công!.',
                'success'
              ).then(() => {
                localStorage.setItem('cart', JSON.stringify([]))
                window.location.href = res.data
              })
            }).finally(() => {
              this.hideLoading()
            })
          }
        }
      })
    },
    handleSelected(index, row) {
      console.log(index, row)
      this.billingAddress = row
      this.dialogTableVisible = false
      this.calculateShip()
    },
    calculateShip() {
      this.showLoading()
      const data = {
        to_district_id: this.billingAddress.village.district.id,
        to_ward_code: this.billingAddress.village.id,
        insurance_value: this.subTotal
      }
      getCalculateShip(data).then(res => {
        this.shipping = res.data.total
        getCalculateLeadTime(data).then(res => {
          this.leadTime = res.data.data.leadtime
        })
      }).finally(() => {
        this.hideLoading()
      })
    }
  }
}
</script>

<style>
.swal2-container {
  z-index: 20000 !important;
}
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
.el-radio__label {
  font-size: 1rem;
}
.el-radio__inner {
  width: 1rem;
  height: 1rem;
}
.el-dialog__body {
  padding-top: 0;
}
</style>
