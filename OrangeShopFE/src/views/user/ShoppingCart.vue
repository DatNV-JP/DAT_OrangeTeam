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
                  <!--                  <input type="text" class="form-control p-4" placeholder="Coupon Code">-->
                  <!--                  <div class="input-group-append">-->
                  <!--                    <button class="btn btn-primary" :disabled="cart">Apply Coupon</button>-->
                  <!--                  </div>-->
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
          <el-row type="flex" class="row-bg">
            <el-col :span="12">
              <h4 class="font-weight-semi-bold mb-4">Địa chỉ nhận hàng</h4>
            </el-col>
            <el-col :span="12">
              <h4 class="font-weight-semi-bold mb-4 d-flex justify-content-end">
                <el-button type="primary" icon="el-icon-edit" round @click="dialogTableVisible = true">Thay đổi</el-button></h4>
            </el-col>
          </el-row>
          <div class="row">
            <div class="col-md-6 form-group">
              <label>Họ tên:</label>
              <input class="form-control" type="text" :value="billingAddress.name" readonly>
            </div>
            <div class="col-md-6 form-group">
              <label>Số điện thoại:</label>
              <input class="form-control" type="text" :value="billingAddress.phone" readonly>
            </div>
            <!--            <div class="col-md-12 form-group">-->
            <!--              <label>E-mail:</label>-->
            <!--              <input class="form-control" type="text" readonly>-->
            <!--            </div>-->
            <div class="col-md-12 form-group">
              <label>Địa chỉ:</label>
              <input
                class="form-control"
                type="text"
                :value="billingAddress.addressLine1 + ', ' + billingAddress.village.name + ', ' + billingAddress.village.district.name + ', ' + billingAddress.village.district.city.name"
                readonly
              >
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
                  <button class="btn btn-lg btn-block btn-primary font-weight-bold my-3 py-3" :disabled="cart.length < 1 || !billingAddress.id" @click="placeOrder">Đặt hàng</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Cart End -->
    <el-dialog v-el-drag-dialog :visible.sync="dialogTableVisible" title="Shipping address" :close-on-click-modal="false" width="80%">
      <el-tabs v-model="activeName" @tab-click="handleTabClick">
        <el-tab-pane name="addressList" label="Danh sách">
          <el-table
            :data="listAddress"
            style="width: 100%"
          >
            <el-table-column
              prop="name"
              label="Họ và tên"
              width="130"
              fixed
            />
            <el-table-column label="Địa chỉ giao hàng">
              <el-table-column
                prop="phone"
                label="Số điện thoại"
                width="130"
              />
              <el-table-column label="Thông tin địa chỉ">
                <el-table-column
                  prop="addressLine1"
                  label="Địa chỉ"
                  width="200"
                />
                <el-table-column
                  prop="village.name"
                  label="Xã/phường"
                  width="200"
                />
                <el-table-column
                  prop="village.district.name"
                  label="Quận/huyện"
                  width="150"
                />
                <el-table-column
                  prop="village.district.city.name"
                  label="Thành phố"
                  width="100"
                />
              </el-table-column>
            </el-table-column>
            <el-table-column label="Lựa chọn" width="auto" fixed="right">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  @click="handleSelected(scope.$index, scope.row)"
                >Chọn</el-button>
                <el-button
                  size="mini"
                  type="primary"
                  @click="handleChange(scope.$index, listAddress)"
                >Sửa</el-button>
                <el-button
                  size="mini"
                  type="danger"
                  @click="handleDelete(scope.$index, listAddress)"
                >Xoá</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane name="addAddress" label="Thêm địa chỉ" @click="console.log('Thông tin cá nhân được chọn')">
          <ValidationObserver ref="observer" v-slot="{ invalid, handleSubmit }">
            <el-form ref="addressForm" :model="addressForm" label-position="top">
              <div class="row">
                <div class="col-md-6 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required">
                    <el-form-item :error="messageError('Họ tên', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Họ tên<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input v-model="addressForm.name" />
                    </el-form-item>
                  </ValidationProvider>
                </div>
                <div class="col-md-6 form-group">
                  <ValidationProvider v-slot="{ errors }" rules="required|phone">
                    <el-form-item :error="messageError('Số điện thoại', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Số điện thoại<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-input v-model="addressForm.phone" />
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
                        v-model="addressForm.address.addressLine1"
                        type="text"
                      />
                    </el-form-item>
                  </ValidationProvider>
                </div>

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
                        :loading="loading"
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
                        :loading="loading"
                        :disabled="city.id===undefined"
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
                        v-model="addressForm.address.village"
                        class="w-100"
                        placeholder="Chọn phường/xã"
                        value-key="id"
                        filterable
                        remote
                        reserve-keyword
                        :remote-method="remoteVillage"
                        :loading="loading"
                        :disabled="district.id===undefined"
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
                <div class="col-md-4"><el-checkbox v-model="addressForm.isDefault">Đặt làm mặc định</el-checkbox></div>
              </div>
            </el-form>
            <el-button
              icon="el-icon-position"
              type="primary"
              plain
              style="text-transform: uppercase;"
              :disabled="invalid"
              @click="handleSubmit(addAddress)"
            >Xác nhận
            </el-button>
          </ValidationObserver>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <el-dialog
      title="Sửa thông tin"
      :visible.sync="dialogVisibleUpdateAddress"
      width="65%"
      :before-close="handleClose"
    >
      <ValidationObserver ref="observer" v-slot="{ invalid, handleSubmit }">
        <el-form ref="addressForm" :model="addressForm" label-position="top">
          <div class="row">
            <div class="col-md-6 form-group">
              <ValidationProvider v-slot="{ errors }" rules="required">
                <el-form-item :error="messageError('Họ tên', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Họ tên<span
                    class="text-danger"
                  > *</span></span></label></template>
                  <el-input v-model="addressForm.name" />
                </el-form-item>
              </ValidationProvider>
            </div>
            <div class="col-md-6 form-group">
              <ValidationProvider v-slot="{ errors }" rules="required|phone">
                <el-form-item :error="messageError('Số điện thoại', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Số điện thoại<span
                    class="text-danger"
                  > *</span></span></label></template>
                  <el-input v-model="addressForm.phone" />
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
                    v-model="addressForm.address.addressLine1"
                    type="text"
                  />
                </el-form-item>
              </ValidationProvider>
            </div>

            <div class="col-md-4">
              <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                <el-form-item :error="messageError('Thành phố', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Thành phố<span
                    class="text-danger"
                  > *</span></span></label>
                  </template>
                  <el-select
                    v-model="city"
                    class="w-100"
                    placeholder="Chọn thành phố"
                    value-key="id"
                    filterable
                    remote
                    reserve-keyword
                    :remote-method="remoteCity"
                    :loading="loading"
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
                    :loading="loading"
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
                    v-model="addressForm.address.village"
                    class="w-100"
                    placeholder="Chọn phường/xã"
                    value-key="id"
                    filterable
                    remote
                    reserve-keyword
                    :remote-method="remoteVillage"
                    :loading="loading"
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
            <div class="col-md-4"><el-checkbox v-model="addressForm.isDefault">Đặt làm mặc định</el-checkbox></div>
          </div>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <div class="mt-5">
            <el-button @click="handleClose">Cancel</el-button>
            <el-button type="primary" :disabled="invalid" @click="handleSubmit(updateAddress)">Confirm</el-button>
          </div>
        </span>
      </ValidationObserver>
    </el-dialog>
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
import { addAddress, updateAddress, getListAddress, removeAddress } from '@/api/address'
import { getCalculateShip, getListPaymentMethod, redirectPayment } from '@/api/payment'
import { addOrder } from '@/api/order'
import { parseTime } from '@/utils'
import { currency } from '@/filters'
import { checkVoucher, getListVoucherUser } from '@/api/voucher'
import { Message } from 'element-ui'
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
      isUpdate: true,
      isActiveChangeUpdate: 0,
      indexUpdateSelected: -1,
      dialogVisibleUpdateAddress: false,
      voucherSelect: '',
      listVoucher: [],
      paymentType: {},
      size: '',
      color: '',
      quantity: 1,
      addressForm: {
        name: '',
        phone: '',
        isDefault: false,
        address: {
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
        }
      },
      district: '',
      city: '',
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
      'email'
    ])
  },
  watch: {
    district: {
      handler(newVal, oldVal) {
        if (newVal.id !== oldVal.id && this.isUpdate === false) {
          this.addressForm.address.village = []
          this.listVillage = []
        }
        if (newVal.id !== oldVal.id && this.isUpdate === true && oldVal.id !== undefined) {
          this.addressForm.address.village = []
          this.listVillage = []
        }
      }
    },
    city: {
      handler(newVal, oldVal) {
        if (newVal.id !== oldVal.id && this.isUpdate === false) {
          this.district = []
          this.addressForm.address.village = []
          this.listDistrict = []
          this.listVillage = []
        }
        if (newVal.id !== oldVal.id && this.isUpdate === true && oldVal.id !== undefined) {
          this.district = []
          this.addressForm.address.village = []
          this.listDistrict = []
          this.listVillage = []
        }
      }
    },
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
    }
  },
  created() {
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
    handleTabClick(tab, event) {
      if (tab.name === 'addressList') {
        this.isUpdate = true
        return
      }
      this.resetForm()
      this.city = []
      this.district = []
      this.isUpdate = false
    },
    handleClose() {
      this.$swal({
        title: 'Đóng',
        text: 'Bạn có chắc chắn muốn đóng ???',
        icon: 'warning',
        buttons: true,
        showCancelButton: true,
        dangerMode: true
      }).then((result) => {
        if (result.isConfirmed) {
          this.dialogVisibleUpdateAddress = false
          // this.resetForm()
          // this.city = []
          // this.district = []
        } else {
          this.dialogVisibleUpdateAddress = true
        }
      })
    },
    handleSelectVoucher(item) {
      if (item.id) {
        checkVoucher({ voucherId: item.id, orderValue: this.subTotal }).then(res => {
          console.log(res)
          console.log(item)
          if (item.isPercent) {
            this.discount = (this.subTotal * item.discountAmount) > item.maxDiscountAmount ? item.maxDiscountAmount : (this.subTotal * item.discountAmount)
          } else {
            this.discount = item.discountAmount
          }
          Message({
            message: 'Đã áp dụng voucher thành công',
            type: 'success',
            duration: 5000
          })
          // this.discount = item.
        }).catch(() => {
          Message({
            message: 'Có lỗi khi áp dụng voucher hãy thử lại sau',
            type: 'error',
            duration: 5000
          })
          this.discount = 0
          this.voucherSelect = ''
        })
      } else {
        this.discount = 0
        this.voucherSelect = ''
      }
    },
    addAddress() {
      this.$confirm(
        'Bạn có chắc chắn muốn thêm địa chỉ này không?',
        'Cảnh báo',
        {
          confirmButtonText: 'Đồng ý',
          cancelButtonText: 'Huỷ',
          type: 'warning',
          center: true
        }
      ).then(() => {
        this.showLoading()
        this.addressForm.address.village.district = this.district
        this.addressForm.address.village.district.city = this.city
        addAddress(this.addressForm).then(res => {
          this.notifySuccess('Thành công', 'Thêm địa chỉ thành công!')
          this.listAddress.push(res.data)
          this.addressForm = {
            name: '',
            phone: '',
            address: {
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
            isDefault: false
          }
          this.activeName = 'addressList'
        }).catch(err => {
          console.log(err)
          this.notifyError('Thất bại', 'Thêm địa chỉ không thành công!')
        }).finally(() => {
          this.hideLoading()
        })
      })
    },
    updateAddress() {
      this.$confirm(
        'Bạn có chắc chắn muốn sửa địa chỉ này không?',
        'Cảnh báo',
        {
          confirmButtonText: 'Đồng ý',
          cancelButtonText: 'Huỷ',
          type: 'warning',
          center: true
        }
      ).then(() => {
        this.showLoading()
        this.addressForm.address.village.district = this.district
        this.addressForm.address.village.district.city = this.city
        updateAddress(this.addressForm).then(res => {
          this.notifySuccess('Thành công', 'Sửa địa chỉ thành công!')
          if (res.code === 200) {
            this.dialogVisibleUpdateAddress = false
            this.listAddress.splice(this.indexUpdateSelected, 1, res.data)
            // this.resetForm()
            // this.city = []
            // this.district = []
            this.activeName = 'addressList'
            this.getListAddress()
          }
        }).catch(err => {
          console.log(err)
          this.notifyError('Thất bại', 'Sửa địa chỉ không thành công!')
        }).finally(() => {
          this.hideLoading()
        })
      })
    },
    resetForm() {
      this.addressForm = {
        name: '',
        phone: '',
        address: {
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
        isDefault: false
      }
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
        this.getListAddress()
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
      })
    },
    getListAddress() {
      getListAddress().then(res => {
        this.listAddress = res.data
        if (this.listAddress) {
          const defaultAddress = this.listAddress.find(address => address.isDefault === true)
          if (defaultAddress) {
            this.billingAddress = defaultAddress
          } else {
            this.billingAddress = this.listAddress[0]
          }
          this.calculateShip()
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
          order.consigneeEmail = this.email
          order.orderTotal = this.subTotal - this.discount + this.shipping
          order.paymentType = this.paymentType
          order.address = {
            id: this.billingAddress.id,
            villageId: this.billingAddress.village.id,
            addressLine1: this.billingAddress.addressLine1
          }
          order.orderDetails = this.cart
          order.description = this.description
          order.estimatedDeliveryTime = this.leadTime
          order.voucherDiscount = this.discount
          order.shippingFee = this.shipping
          order.voucherId = this.voucherSelect.id
          if (order.paymentType.id === 1) {
            addOrder(order).then(res => {
              console.log(res)
              this.hideLoading()
              this.$swal(
                'Đã tạo đơn!',
                'Bạn đã tạo đơn hàng thành công!.',
                'success'
              ).then(() => {
                this.$store.dispatch('cart/getCart')
                this.$router.push(`/user/tracking-page/${res.data.id}`)
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
    async handleChange(index, row) {
      // mmm
      await this.resetDataList()
      await this.insertDataUpdate(index, row)
    },
    resetDataList() {
      this.resetForm()
      this.city = []
      this.district = []
    },
    insertDataUpdate(index, row) {
      this.indexUpdateSelected = index
      this.dialogVisibleUpdateAddress = true
      this.addressForm.name = row[index].name
      this.addressForm.phone = row[index].phone
      this.addressForm.address.addressLine1 = row[index].addressLine1
      this.addressForm.address.village = row[index].village
      this.addressForm.isDefault = row[index].isDefault
      this.addressForm.address.id = row[index].id
      this.city = row[index].village.district.city
      this.district = row[index].village.district
      this.remoteCity()
      this.remoteDistrict(this.district.name)
      this.remoteVillage(this.addressForm.address.village.name)
    },
    handleDelete(index, data) {
      // removeAddress(data)
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn xoá địa chỉ này!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Xoá'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          removeAddress(data[index].id).then(res => {
            console.log(data[index].id, this.billingAddress.id)
            if (data[index].id === this.billingAddress.id) {
              this.billingAddress = {
                name: '',
                phone: '',
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
              }
            }
            data.splice(index, 1)
            Message({
              message: 'Đã xoá địa chỉ thành công',
              type: 'success',
              duration: 5000
            })
          }).finally(() => {
            this.getListPaymentMethod()
            this.hideLoading()
          })
        }
      })
    },
    calculateShip() {
      const data = {
        to_district_id: this.billingAddress.village.district.id,
        to_ward_code: this.billingAddress.village.id,
        insurance_value: this.subTotal - this.discount
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
