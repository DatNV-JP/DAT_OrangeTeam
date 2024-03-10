<template>
  <el-main style="height: 100%">
    <el-row :gutter="20">
      <el-col :span="14" class="h-100">
        <el-autocomplete v-model="searchPro" class="w-100" prefix-icon="el-icon-search" popper-class="my-autocomplete" :fetch-suggestions="querySearch" placeholder="Tìm sản phẩm" @select="handleSelect">
          <template v-slot="{ item }">
            <el-row :gutter="20" class="pb-2">
              <el-col :span="6" style="height: 5em">
                <img :src="urlViewImg + '/productDetailImages/' + item.cartRow.image" class="h-100" alt="anh san pham">
              </el-col>
              <el-col :span="18" style="height: 5em">
                <div class="value">{{ item.value }}</div>
                <span class="link">{{ item.property1 }}: {{ item.property1Value }},</span>
                <span class="link"> {{ item.property2 }}: {{ item.property2Value }}, </span>
                <span class="link"> Giá: {{ item.cartRow.price | currency('VND', 0, 'đ', '.', ',') }}</span>
              </el-col>
            </el-row>
          </template>
        </el-autocomplete>
        <el-tabs v-model="editableTabsValue" type="card" editable class="mt-3" style="min-height: 15em" @edit="handleTabsEdit">
          <el-tab-pane
            v-for="item in editableTabs"
            :key="item.name"
            :label="item.title"
            :name="item.name"
          >
            <order-row v-for="(row, index) in item.orderRow" :key="index" :cart-row="row" @remove-order-row="removeOrderRow" />
          </el-tab-pane>
        </el-tabs>
        <el-card class="box-card mt-3">
          <el-select v-model="voucherSelect" filterable placeholder="Select" value-key="item.id" class="w-100 mb-3" clearable @change="handleSelectVoucher">
            <el-option
              v-for="item in listVoucher"
              :key="item.id"
              :label="item.code"
              :value="item"
              :disabled="item.minimumOrderValue > totalOrder"
            >
              <div class="link">
                <span v-if="item.isPercent" class="value">Mã voucher: {{ item.code }}, Giảm {{ item.discountAmount }}% tối đa: {{ item.maxDiscountAmount | currency('VND', 0, 'đ', '.', ',') }} cho đơn hàng từ {{ item.minimumOrderValue | currency('VND', 0, 'đ', '.', ',') }}</span>
                <span v-else class="value">Mã voucher: {{ item.code }}, Giảm {{ item.discountAmount | currency('VND', 0, 'đ', '.', ',') }} cho đơn hàng từ {{ item.minimumOrderValue | currency('VND', 0, 'đ', '.', ',') }}</span>
              </div>
            </el-option>
          </el-select>
          <el-row :gutter="20">
            <el-col :span="13">
              <el-input
                v-model="orderNote"
                type="textarea"
                rows="6"
                resize="none"
                maxlength="3000"
                show-word-limit
                placeholder="Ghi chú đơn hàng"
              />
            </el-col>
            <el-col :span="11">
              <div class="text item mb-1">
                <span>Tổng tiền hàng: {{ totalOrder | currency('VND', 0, 'đ', '.', ',') }}</span>
              </div>
              <div class="text item mb-1">
                <span>Giảm giá: {{ voucherValue | currency('VND', 0, 'đ', '.', ',') }}</span>
              </div>
              <transition-group name="slide-fade">
                <template v-if="visibleAddress">
                  <div :key="1" class="text item mb-1">
                    <span>Phí ship: {{ feeShip | currency('VND', 0, 'đ', '.', ',') }}</span>
                  </div>
                  <div :key="2" class="text item mb-1">
                    <span>Dự kiến giao hàng: {{ estimateTime | parseTime('{d}/{m}/{y} {h}:{i}') }}</span>
                  </div>
                </template>
              </transition-group>
              <el-divider />
              <div class="text item">
                <span><strong>Khách phải trả:</strong> {{ (totalOrder + feeShip - voucherValue) | currency('VND', 0, 'đ', '.', ',') }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{ firstName }} {{ lastName }}</span>
            <span style="float: right; padding: 3px 0">{{ now() | parseTime('{d}/{m}/{y} {h}:{i}') }}</span>
          </div>
          <div class="text item">
            <el-autocomplete v-model="searchUser" :disabled="disableUser" prefix-icon="el-icon-search" class="w-100" popper-class="my-autocomplete" :fetch-suggestions="querySearchUser" placeholder="Tìm khách hàng" @select="handleSelectUser">
              <template v-slot="{ item }">
                <el-row :gutter="20" class="pb-2">
                  <el-col :span="6" style="height: 5em">
                    <img :src="urlViewImg + '/avatar/' + item.user.avatar" class="h-100" alt="avatar">
                  </el-col>
                  <el-col :span="18" style="height: 5em">
                    <div class="value">Name: {{ item.value }}</div>
                    <span class="link">Username: {{ item.user.username }}</span>
                  </el-col>
                </el-row>
              </template>
              <el-tooltip v-if="disableUser" slot="append" content="Xóa khách hàng" placement="bottom" effect="light">
                <el-button icon="el-icon-delete" @click="removeUser" />
              </el-tooltip>
            </el-autocomplete>
            <el-divider />
          </div>
          <ValidationObserver ref="observer" v-slot="{ invalid, handleSubmit}">
            <el-form class="mt-3" label-position="top">
              <ValidationProvider v-slot="{ errors }" rules="required">
                <el-form-item :error="messageError('Tên người nhận', errors[0])">
                  <el-input v-model="addressForm.name" placeholder="Tên người nhận" />
                </el-form-item>
              </ValidationProvider>
              <ValidationProvider v-slot="{ errors }" rules="required|phone">
                <el-form-item :error="messageError('SĐT người nhận', errors[0])">
                  <el-input v-model="addressForm.phone" placeholder="SĐT người nhận" />
                </el-form-item>
              </ValidationProvider>
              <ValidationProvider v-slot="{ errors }" rules="email">
                <el-form-item :error="messageError('Email người nhận', errors[0])">
                  <el-input v-model="addressForm.email" placeholder="Email người nhận" />
                </el-form-item>
              </ValidationProvider>
              <transition-group name="slide-fade">
                <template v-if="visibleAddress">
                  <ValidationProvider v-slot="{ errors }" :key="1" rules="required|min:10">
                    <el-form-item :error="messageError('Địa chỉ ', errors[0])">
                      <el-input v-model="addressForm.addressLine1" placeholder="Địa chỉ cụ thể" type="textarea" maxlength="3000" show-word-limit />
                    </el-form-item>
                  </ValidationProvider>
                  <el-row :key="2">
                    <el-col :span="11">
                      <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                        <el-form-item :error="messageError('Thành phố', errors[0])">
                          <el-select
                            v-model="city"
                            class="w-100"
                            placeholder="Thành phố"
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
                    </el-col>
                    <el-col class="line" :span="2"><span><p>&nbsp;</p></span></el-col>
                    <el-col :span="11">
                      <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
                        <el-form-item :error="messageError('Quận/huyện', errors[0])">
                          <el-select
                            v-model="district"
                            class="w-100"
                            placeholder="Quận/huyện"
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
                    </el-col>
                  </el-row>
                  <ValidationProvider v-slot="{ errors }" :key="3" rules="requiredSelect">
                    <el-form-item :error="messageError('Phường/xã', errors[0])">
                      <el-select
                        v-model="village"
                        class="w-100"
                        placeholder="Phường/xã"
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
                </template>
              </transition-group>
              <el-form-item label="Vận chuyển">
                <el-radio-group v-model="shippingMethod">
                  <el-radio v-for="item in listShippingMethod" :key="item.id" :label="item.id">{{ item.name }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :disabled="invalid || dataTabs.orderRow.length < 1 || isFeeShipErr" @click="handleSubmit(createOrder)">Tạo đơn</el-button>
              </el-form-item>
            </el-form>
          </ValidationObserver>
        </el-card>
      </el-col>
    </el-row>
  </el-main>
</template>

<script>
import { searchProduct } from '@/api/product'
import { currency, parseTime } from '@/filters'
import OrderRow from '@/views/admin/order/component/OrderRow/index.vue'
import { mapGetters } from 'vuex'
import { now } from 'moment'
import { getCalculateLeadTime, getCities, getDistricts, getVillages } from '@/api/goog-remote-search'
import { findUserByUsername } from '@/api/user'
import { getListAddress } from '@/api/address'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import { getCalculateShip, getListShippingMethod } from '@/api/payment'
import baseValidate from '@/utils/BaseValidate'
import { addOrderAdmin } from '@/api/order'
import baseCommon from '@/utils/base-common'
import { checkVoucher, getListVoucherStatus } from '@/api/voucher'
import { Message } from 'element-ui'

export default {
  name: 'CreateOrder',
  filters: { currency, parseTime },
  components: { OrderRow, ValidationObserver, ValidationProvider },
  mixins: [baseValidate, baseCommon],
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL,
      loading: false,
      isFeeShipErr: false,
      disableUser: false,
      feeShip: 0,
      estimateTime: '',
      shippingMethod: '',
      visibleAddress: false,
      listShippingMethod: [],
      addressForm: {
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
      district: '',
      village: '',
      city: '',
      listAddress: [],
      listDistrict: [],
      listVillage: [],
      cities: [],
      voucherValue: 0,
      orderNote: '',
      searchPro: '',
      searchUser: '',
      searchVoucher: '',
      editableTabsValue: '1',
      editableTabs: [{
        title: 'Hóa đơn 1',
        name: '1',
        orderRow: [],
        user: {},
        orderInfo: {
          orderNote: '',
          orderTotal: 0,
          leadTime: '',
          shippingMethod: {
            id: 1,
            createBy: null,
            createDate: null,
            modifiedDate: null,
            modifiedBy: null,
            name: 'Nhận tại cửa hàng',
            status: true
          },
          orderStatus: {},
          paymentType: {
            id: 1,
            value: 'Ship code'
          }
        }
      }, {
        title: 'Hóa đơn 2',
        name: '2',
        orderRow: [],
        user: {},
        orderInfo: {
          orderNote: '',
          leadTime: '',
          orderTotal: 0,
          shippingMethod: {
            id: 1,
            createBy: null,
            createDate: null,
            modifiedDate: null,
            modifiedBy: null,
            name: 'Nhận tại cửa hàng',
            status: true
          },
          orderStatus: {},
          paymentType: {
            id: 1,
            value: 'Ship code'
          }
        }
      }],
      tabIndex: 2,
      voucherSelect: '',
      listVoucher: []
    }
  },
  computed: {
    ...mapGetters(['firstName', 'lastName', 'username']),
    totalOrder() {
      const dataTab = this.editableTabs.find(tab => tab.name === this.editableTabsValue)
      // eslint-disable-next-line no-return-assign
      return dataTab.orderRow.map(item => item.quantity * item.price).reduce((total, qty) => total += qty, 0)
    },
    dataTabs() {
      return this.editableTabs.find(item => item.name === this.editableTabsValue)
    }
  },
  watch: {
    shippingMethod: {
      handler(newVal, oldVal) {
        this.visibleAddress = newVal === 2
        if (newVal === 2) {
          if (this.dataTabs.user.id) {
            getListAddress({ id: this.dataTabs.user.id }).then(res => {
              let addressArr = this.addressForm
              if (res.data) {
                addressArr = res.data.find(address => address.isDefault === true)
                if (!addressArr) {
                  addressArr = res.data[0]
                }
                this.cities = [addressArr.village.district.city]
                this.listDistrict = [addressArr.village.district]
                this.listVillage = [addressArr.village]
                this.city = addressArr.village.district.city
                this.district = addressArr.village.district
                this.village = addressArr.village
                this.addressForm = addressArr
              }
              this.dataTabs.user.addressForm = addressArr
            })
          }
        } else {
          this.feeShip = 0
        }
        // console.log(this.listShippingMethod.find(s => s.id === newVal))
        this.dataTabs.orderInfo.shippingMethod = this.listShippingMethod.find(s => s.id === newVal)
        // console.log(1, this.dataTabs.orderInfo.shippingMethod)
      },
      deep: true
    },
    addressForm: {
      handler(newVal, oldVal) {
        if (this.village) {
          if (this.visibleAddress) {
            this.calculateShipAndTime(newVal.village, this.district)
          }
          newVal.village = this.village
        }
        this.dataTabs.user.addressForm = newVal
      },
      deep: true
    },
    district: {
      handler(newVal, oldVal) {
        if (this.city) {
          newVal.city = this.city
        }
      },
      deep: true
    },
    village: {
      handler(newVal, oldVal) {
        if (this.district) {
          newVal.district = this.district
          this.dataTabs.user.addressForm.village = newVal.district
        }
      },
      deep: true
    },
    orderNote: {
      handler(newVal, oldVal) {
        this.dataTabs.orderInfo.orderNote = newVal
      },
      deep: true
    },
    editableTabsValue: {
      handler(newVal, oldVal) {
        this.setDataForm()
      }
    },
    totalOrder: {
      handler(newVal, oldVal) {
        if (this.voucherSelect.id) {
          console.log(this.voucherSelect)
          if (newVal < this.voucherSelect.minimumOrderValue) {
            this.voucherSelect = ''
            this.voucherValue = 0
          }
        }
      }
    }
  },
  beforeRouteLeave(to, from, next) {
    localStorage.setItem('waitingOrder', JSON.stringify(this.editableTabs))
    window.removeEventListener('beforeunload', this.confirmExit)
    next()
  },
  created() {
    const param = {
      status: 4,
      page: 0,
      size: 10000 }
    getListVoucherStatus(param).then(res => {
      this.listVoucher = res.data.result
    })
    getListShippingMethod().then(res => {
      this.listShippingMethod = res.data
      const data = JSON.parse(localStorage.getItem('waitingOrder'))
      if (data.length > 0) {
        this.editableTabs = data
        this.tabIndex = data.length
        this.editableTabsValue = data[0].name
        this.setDataForm()
      }
    })
  },
  mounted() {
    window.addEventListener('beforeunload', this.confirmExit)
    // this.shippingMethod = this.dataTabs.orderInfo.shippingMethod
  },
  beforeUnmount() {
    window.removeEventListener('beforeunload', this.confirmExit)
  },
  methods: {
    now,
    changeCity() {
      this.district = {}
      this.village = {}
      this.listDistrict = []
      this.listVillage = []
    },
    confirmExit(event) {
      // Hiển thị thông báo xác nhận
      localStorage.setItem('waitingOrder', JSON.stringify(this.editableTabs))
      // event.preventDefault()
      // event.returnValue = ''
    },
    setDataForm() {
      if (this.dataTabs.user.addressForm) {
        this.addressForm = this.dataTabs.user.addressForm
        this.cities = [this.dataTabs.user.addressForm.village.district.city]
        this.listDistrict = [this.dataTabs.user.addressForm.village.district]
        this.listVillage = [this.dataTabs.user.addressForm.village]
        this.city = this.dataTabs.user.addressForm.village.district.city
        this.district = this.dataTabs.user.addressForm.village.district
        this.village = this.dataTabs.user.addressForm.village
      } else {
        this.addressForm = {
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
        }
        this.searchUser = ''
        this.disableUser = false
        this.city = ''
        this.district = ''
        this.village = ''
        this.cities = []
        this.listDistrict = []
        this.listVillage = []
        this.feeShip = 0
        this.estimateTime = ''
        this.shippingMethod = this.listShippingMethod[0].id
      }
      if (this.dataTabs.orderInfo.shippingMethod.id) {
        this.shippingMethod = this.dataTabs.orderInfo.shippingMethod.id
      }
      if (this.dataTabs.orderInfo.voucherId) {
        this.voucherSelect = this.listVoucher.find(v => v.id === this.dataTabs.orderInfo.voucherId)
        this.handleSelectVoucher(this.voucherSelect)
      } else {
        this.voucherSelect = ''
        this.voucherValue = 0
      }
      this.orderNote = this.dataTabs.orderInfo.orderNote
    },
    removeUser() {
      this.addressForm = {
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
      }
      this.dataTabs.user = {}
      this.searchUser = ''
      this.disableUser = false
      this.feeShip = 0
      this.voucherValue = 0
      this.voucherSelect = ''
    },
    createOrder() {
      // console.log(this.dataTabs)
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
          const { user, orderRow, orderInfo } = this.dataTabs
          const orderStatus = orderInfo.shippingMethod.id === 1 ? { id: 1, status: 'Completed' } : { id: 6, status: 'Confirmed' }
          let address = {
            id: user.addressForm.id,
            villageId: user.addressForm.village.id,
            addressLine1: user.addressForm.addressLine1
          }
          if (!user.addressForm.id) {
            address = null
          }
          const orderData = {
            userId: user.id,
            consigneeName: user.addressForm.name,
            consigneePhone: user.addressForm.phone,
            consigneeEmail: user.addressForm.email,
            toAddress: this.addressForm.addressLine1,
            toWard: this.addressForm.village ? this.addressForm.village.name : '',
            toDistrict: this.district ? this.district.name : '',
            toProvince: this.city ? this.city.name : '',
            description: orderInfo.orderNote,
            estimatedDeliveryTime: this.estimateTime ? this.estimateTime : '',
            // eslint-disable-next-line no-return-assign
            orderTotal: this.totalOrder + this.feeShip - this.voucherValue, // this.dataTabs.orderRow.map(item => item.quantity * item.price).reduce((total, qty) => total += qty, 0),
            voucherId: this.dataTabs.orderInfo.voucherId,
            voucherDiscount: this.voucherValue,
            shippingFee: this.feeShip,
            address: address,
            orderStatus: orderStatus,
            paymentType: orderInfo.paymentType,
            shippingMethod: orderInfo.shippingMethod,
            orderDetails: orderRow.map(o => ({ productDetailId: o.productDetailId, quantity: o.quantity, price: o.price }))
          }
          addOrderAdmin(orderData).then(res => {
            console.log(res)
            this.hideLoading()
            this.$swal(
              'Đã tạo đơn!',
              'Bạn đã tạo đơn hàng thành công!.',
              'success'
            )
            this.removeUser()
            this.handleTabsEdit(this.dataTabs.name, 'remove')
          }).finally(() => {
            this.hideLoading()
          })
        }
      })
      // console.log(orderData)
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
    removeOrderRow(data) {
      for (const editableTabsKey of this.editableTabs) {
        if (editableTabsKey.name === this.editableTabsValue) {
          editableTabsKey.orderRow = editableTabsKey.orderRow.filter(item => JSON.stringify(item) !== JSON.stringify(data))
        }
      }
    },
    querySearch(queryString, cb) {
      const products = []
      const params = {
        page: 0,
        size: 50,
        keyWord: queryString || '',
        sort: 'createDate,desc'
      }
      searchProduct(params).then(res => {
        for (let i = 0; i < res.data.result.length; i++) {
          const product = res.data.result[i]
          for (let j = 0; j < product.productDetails.length; j++) {
            const productDetail = product.productDetails[j]
            if (productDetail.variationOptions.length > 0) {
              const property1 = productDetail.variationOptions[0].variation.name
              const property2 = productDetail.variationOptions[1].variation.name
              products.push({
                value: product.name,
                property1: property1,
                property2: property2,
                property1Value: productDetail.variationOptions[0].value,
                property2Value: productDetail.variationOptions[1].value,
                cartRow:
                  {
                    productDetailId: productDetail.id,
                    productName: product.name,
                    image: productDetail.images,
                    quantity: 1,
                    price: productDetail.priceSale === 0 ? productDetail.priceDefault : productDetail.priceSale,
                    [property1]: productDetail.variationOptions[0].value,
                    [property2]: productDetail.variationOptions[1].value
                  }
              })
            }
          }
        }
        // queryString ? products.filter(this.createFilter(queryString)) : products
        // call callback function to return suggestions
        cb(products)
      })
    },
    querySearchUser(queryString, cb) {
      const users = []
      const params = {
        keyWord: queryString || ''
      }
      findUserByUsername(params).then(res => {
        const userNotMe = res.data.filter(u => u.username !== this.username)
        for (let i = 0; i < userNotMe.length; i++) {
          const user = userNotMe[i]
          users.push({ value: user.firstName + user.lastName, user: user })
        }
        cb(users)
      })
    },
    handleSelect(item) {
      console.log(item)
      for (const editableTabsKey of this.editableTabs) {
        if (editableTabsKey.name === this.editableTabsValue) {
          editableTabsKey.orderRow.push(item.cartRow)
        }
      }
    },
    handleSelectUser(item) {
      for (const editableTabsKey of this.editableTabs) {
        if (editableTabsKey.name === this.editableTabsValue) {
          const { avatar, id, username, firstName, lastName, email, phone } = item.user
          editableTabsKey.user = { avatar, id, username, firstName, lastName, email, phone }
          this.addressForm.name = firstName + ' ' + lastName
          this.addressForm.email = email
          this.addressForm.phone = phone
          this.disableUser = true
        }
      }
    },
    handleSelectVoucher(item) {
      if (item.id) {
        checkVoucher({ voucherId: item.id, orderValue: this.totalOrder }).then(res => {
          console.log(res, item)
          if (res.code === 200) {
            this.voucherValue = item.maxDiscountAmount
            if (item.isPercent) {
              this.voucherValue = (this.totalOrder * item.discountAmount) > item.maxDiscountAmount ? item.maxDiscountAmount : (this.totalOrder * item.discountAmount)
            } else {
              this.voucherValue = item.discountAmount
            }
            this.dataTabs.orderInfo.voucherId = item.id
            this.voucherSelect = item.code
          } else {
            this.voucherValue = 0
            this.dataTabs.orderInfo.voucherId = null
            this.voucherSelect = null
          }
        }).catch(() => {
          this.voucherValue = 0
          this.dataTabs.orderInfo.voucherId = null
          this.voucherSelect = null
          Message({
            message: 'Có lỗi khi áp dụng voucher hãy thử lại sau',
            type: 'error',
            duration: 5000
          })
        })
      } else {
        this.voucherValue = 0
        this.voucherSelect = null
        this.dataTabs.orderInfo.voucherId = null
      }
    },
    handleTabsEdit(targetName, action) {
      if (action === 'add') {
        if (this.editableTabs.length < 20) {
          const newTabName = ++this.tabIndex + Math.ceil(Math.random() * now()) + ''
          this.editableTabs.push({
            title: `Hóa đơn chờ ${Math.ceil(Math.random() * 999)}`,
            name: newTabName,
            orderRow: [],
            user: {},
            orderInfo: {
              orderNote: '',
              leadTime: '',
              orderTotal: 0,
              shippingMethod: this.listShippingMethod[0],
              orderStatus: {},
              paymentType: {
                id: 1,
                value: 'Ship code'
              }
            }
          })
          this.editableTabsValue = newTabName
        } else {
          this.$swal({
            title: 'Không thể tạo nhiều hơn 20 hóa đơn chờ!',
            icon: 'warning',
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true
          })
        }
      }
      if (action === 'remove') {
        const tabs = this.editableTabs
        let activeName = this.editableTabsValue
        if (activeName === targetName) {
          tabs.forEach((tab, index) => {
            if (tab.name === targetName) {
              const nextTab = tabs[index + 1] || tabs[index - 1]
              if (nextTab) {
                activeName = nextTab.name
              }
            }
          })
        }

        this.editableTabsValue = activeName
        this.editableTabs = tabs.filter(tab => tab.name !== targetName)
        if (this.editableTabs.length < 1) {
          this.handleTabsEdit('', 'add')
        }
      }
    },
    calculateShipAndTime(village, district) {
      if (district.id && village.id) {
        const data = {
          to_district_id: district.id,
          to_ward_code: village.id,
          insurance_value: this.totalOrder
        }
        getCalculateShip(data).then(res => {
          this.feeShip = res.data.total
          getCalculateLeadTime(data).then(res => {
            this.estimateTime = res.data.data.leadtime
            this.dataTabs.orderInfo.leadTime = this.estimateTime
          })
        }).catch(() => {
          this.$swal({
            title: 'Địa chỉ của bạn không đúng hãy thao tác lại!',
            icon: 'warning',
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true
          })
          this.isFeeShipErr = true
        })
      }
    }
  }
}
</script>

<style>
  .my-autocomplete > li {
    line-height: normal;
    padding: 7px;
  }
  .my-autocomplete > .value {
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .my-autocomplete > .link {
    font-size: 12px;
    color: #b4b4b4;
  }
  .slide-fade-enter-active {
    transition: all .3s ease;
  }
  .slide-fade-leave-active {
    transition: all .8s cubic-bezier(1.0, 0.5, 0.8, 1.0);
  }
  .slide-fade-enter, .slide-fade-leave-to
    /* .slide-fade-leave-active below version 2.1.8 */ {
    transform: translateX(10px);
    opacity: 0;
  }
</style>
