<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <el-card v-if="isCancel === 2" class="box-card">
      <div slot="header" class="clearfix">
        <span> Mã order:<strong> {{ order.id }}</strong></span>
        <el-button-group class="float-right">
          <el-button v-if="order.orderStatus.id === 7" type="danger" icon="el-icon-delete" @click="confirmOrder(2, 'Bạn có chắc chắn muốn hủy đơn hàng này?', false)">Đồng ý hủy đơn hàng</el-button>
          <el-button v-if="active === 1 && order.orderStatus.id !== 2" type="primary" @click="undoCancelOrder">Hoàn tác huỷ</el-button>
        </el-button-group>
      </div>
      <el-steps :active="activeCancel">
        <el-step title="Chờ huỷ" icon="el-icon-warning" :description="order.waitForCancelTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Huỷ thành công" icon="el-icon-close" :description="order.cancelTime | formatDateTime('DD-MM-YYYY')" />
      </el-steps>
    </el-card>
    <h1 v-if="isCancel === 1">Không tìm thấy đơn hàng</h1>
    <template v-if="isCancel === 0">
      <el-card class="box-card mb-3">
        <div slot="header" class="clearfix">
          <span> Mã order:<strong> {{ order.id }}</strong></span>
        </div>
        <el-steps :active="active">
          <el-step title="Đặt hàng" icon="ti-package" :description="order.createDate | formatDateTime('DD-MM-YYYY')" />
          <el-step v-show="order.paymentType.id === 2" title="Thanh toán" icon="el-icon-s-finance" :description="order.waitForPayTime | formatDateTime('DD-MM-YYYY')" />
          <el-step title="Xác nhận" icon="el-icon-check" :description="order.confirmedTime | formatDateTime('DD-MM-YYYY')" />
          <el-step title="Đóng gói" icon="ti-reload" :description="order.packagingTime | formatDateTime('DD-MM-YYYY')" />
          <el-step title="Vận chuyển" icon="el-icon-truck" :description="order.deliveryInProgressTime | formatDateTime('DD-MM-YYYY')" />
          <el-step title="Đang giao hàng" icon="ti-layout-media-right" :description="order.deliveryInProgressTime | formatDateTime('DD-MM-YYYY')" />
          <el-step v-if="order.shippingMethod && order.shippingMethod.id !== 1" title="Hoàn thành" icon="el-icon-sold-out" :description="order.completedTime | formatDateTime('DD-MM-YYYY')" />
          <el-step v-if="order.shippingMethod && order.shippingMethod.id === 1" title="Hoàn thành" icon="el-icon-sold-out" :description="order.createDate | formatDateTime('DD-MM-YYYY')" />
        </el-steps>
        <el-divider />
        <div class="float-right mb-3">
          <el-button-group>
            <!--            <el-button type="info" icon="el-icon-edit">Sửa đơn hàng</el-button>-->
            <el-button v-if="order.orderStatus && order.orderStatus.id === 5" type="primary" icon="el-icon-check" @click="confirmOrder(6, 'Bạn có chắc chắn xác nhận đơn hàng này?', true)">Duyệt đơn hàng</el-button>
            <el-button v-if="order.orderStatus && (order.orderStatus.id === 5 || order.orderStatus.id === 6 || order.orderStatus.id === 4 || order.orderStatus.id === 3)" type="danger" icon="el-icon-delete" @click="cancelOrderVisible = true">Hủy đơn hàng</el-button>
            <el-button v-if="order.orderStatus && order.orderStatus.id === 7" type="danger" icon="el-icon-delete" @click="confirmOrder(2, 'Bạn có chắc chắn muốn hủy đơn hàng này?', false)">Đồng ý hủy đơn hàng</el-button>
          </el-button-group>
        </div>
      </el-card>
      <el-card v-if="order.userId" class="box-card mb-3">
        <div slot="header" class="clearfix">
          <span><strong>Thông tin khách hàng</strong></span>
        </div>
        <div v-if="user">
          <div> Họ tên:<strong> {{ `${user.firstName} ${user.lastName}` }}</strong></div>
          <div> Số điện thoại:<strong> {{ user.phone }}</strong></div>
          <div> Email:<strong> {{ user.email? order.consigneeEmail: 'Chưa có' }}</strong></div>
        </div>
        <div v-else>
          <div> Họ tên:<strong> {{ order.consigneeName }}</strong></div>
          <div> Số điện thoại:<strong> {{ order.consigneePhone }}</strong></div>
          <div> Email:<strong> {{ order.consigneeEmail? order.consigneeEmail: 'Chưa có' }}</strong></div>
        </div>
      </el-card>
      <el-card class="box-card mb-3">
        <div slot="header" class="clearfix">
          <span><strong>Thông tin đơn hàng</strong></span>
        </div>
        <el-row :gutter="20">
          <el-col :span="12">
            <div> Họ tên người nhận:<strong> {{ order.consigneeName }}</strong></div>
            <div> Số điện thoại người nhận:<strong> {{ order.consigneePhone }}</strong></div>
            <div> Email người nhận:<strong> {{ order.consigneeEmail? order.consigneeEmail: 'Chưa có' }}</strong></div>
            <div> Mã giảm giá:<strong> {{ voucherCode ? voucherCode : 'Chưa có' }}</strong></div>
            <div> Trạng thái đơn hàng:
              <strong v-if="order.orderStatus && order.orderStatus.id === 1"> Thành công</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 2"> Đã hủy</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 3"> Chờ thanh toán</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 4"> Đang giao hàng</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 5"> Chờ xác nhận</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 6"> Đã xác nhận</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 7"> Chờ xác nhận hủy</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 8"> Hoàn trả</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 9"> Chờ hoàn trả</strong>
              <strong v-if="order.orderStatus && order.orderStatus.id === 10"> Đóng gói</strong>
            </div>
          </el-col>
          <el-col :span="12">
            <div> Tổng giá trị sản phẩm:<strong> {{ order.orderTotal - order.shippingFee + order.voucherDiscount | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
            <div> Giảm giá:<strong> {{ order.voucherDiscount | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
            <div> Phí ship hàng:<strong> {{ order.shippingFee | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
            <div> Tổng đơn hàng:<strong> {{ order.orderTotal | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
            <div> Ngày tạo:<strong> {{ order.createDate | formatDateTime('DD-MM-YYYY') }}</strong></div>
          </el-col>
        </el-row>
      </el-card>
      <el-card class="box-card mb-3">
        <div slot="header" class="clearfix">
          <span><strong>Thông tin giao hàng</strong></span>
          <el-button v-if="order.orderStatus && order.orderStatus.id === 6" style="float: right;" type="success" @click="updateStatusOrder(10, 'Bạn có chắc chắn muốn đóng gói đơn hàng này?')">Đóng gói</el-button>
          <el-button v-if="order.orderStatus && order.orderStatus.id === 10" style="float: right;" type="success" @click="updateStatusOrder(4, 'Bạn có chắc chắn muốn xuất kho đơn hàng này?')">Xuất kho</el-button>
          <el-button v-if="order.orderStatus && order.orderStatus.id === 4" style="float: right;" type="success" @click="updateStatusOrder(1, 'Bạn có chắc chắn đơn hàng đã thành công?')">Thành công</el-button>
        </div>
        <div>
          <div> Mã vận đơn:<strong> {{ order.shippingCode }}</strong></div>
          <div> Trạng thái giao hàng:<strong> {{ orderGHN.status === 'ready_to_pick'? 'Chờ nhận hàng' : '' }}</strong></div>
          <div> Hình thức giao hàng:<strong> {{ order.shippingMethod?order.shippingMethod.name:'' }}</strong></div>
          <div> Dự kiến nhận hàng:<strong> {{ orderGHN.pickup_time | formatDateTime('DD-MM-YYYY') }}</strong></div>
          <div> Địa chỉ giao hàng:<strong>
            <span v-if="order.shippingMethod && order.shippingMethod.id === 2 && order.address"> {{ order.address.addressLine1 + ', ' + order.address.village.name + ', ' + order.address.village.district.name + ', ' + order.address.village.district.city.name }}</span>
            <span v-if="order.shippingMethod && order.shippingMethod.id === 2 && !order.address"> {{ order.toAddress + ', ' + order.toWard + ', ' + order.toDistrict + ', ' + order.toProvince }}</span>
            <span v-else> Nhận tại cửa hàng</span>
          </strong></div>
        </div>
      </el-card>
      <el-card class="box-card mb-3">
        <div slot="header" class="clearfix">
          <span><strong>Thông tin chi tiết đơn hàng</strong></span>
        </div>
        <el-table
          :data="order.orderDetailViews"
          style="width: 100%"
          show-summary
        >
          <el-table-column
            label="STT"
            width="80"
            type="index"
          />
          <el-table-column
            prop="productName"
            label="Tên sản phẩm"
            width="280"
          />
          <el-table-column width="180" align="left" label="Thuộc tính">
            <template v-slot="scope">
              <span v-for="(va, index) in scope.row.productDetail.variationOptions" :key="index" class="d-block">{{ va.variation.name + ': ' }} <strong>{{ va.value }}</strong> </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="quantity"
            label="Số lượng"
            width="85"
            align="center"
          />
          <el-table-column
            label="Giá tiền"
            width="100"
            align="center"
            prop="price"
          >
            <template v-slot="scope">
              <span>{{ scope.row.price | currency('VND', 0, 'đ', '.', ',') }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </template>
    <el-dialog
      title="Yêu cầu hủy đơn"
      :visible.sync="cancelOrderVisible"
      :before-close="handleClose"
      width="85%"
      top="5vh"
      :destroy-on-close="true"
    >
      <el-form label-position="top">
        <el-form-item label="Hãy cho biết lý do hủy đơn của bạn:">
          <el-input v-model="reason" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelOrderVisible = false; reason='';">Thoát</el-button>
        <el-button type="danger" @click="cancelOrder">Hủy đơn</el-button>
      </span>
    </el-dialog>
  </el-main>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-2-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import PageHeader from '@/views/user/component/PageHeader/index.vue'
import { cancelOrder, confirmOrder, getOrderDetailAdmin, undoCancelOrder, updateOrder } from '@/api/order'
import { findUserById } from '@/api/user'
import { parseTime } from '@/utils'
import { currency, formatDateTime } from '@/filters'
import { getDetailOrderGHN } from '@/api/goog-remote-search'
import { getVoucherById } from '@/api/voucher'

export default {
  name: 'Shop',
  filters: { parseTime, formatDateTime, currency },
  directives: {
    horizontalScroll
  },
  components: { PageHeader },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      reason: '',
      cancelOrderVisible: false,
      active: 1,
      order: {
        paymentType: {
          id: 1,
          value: ''
        }
      },
      user: '',
      isCancel: 0,
      orderGHN: '',
      activeCancel: 1,
      voucherCode: ''
    }
  },
  created() {
  },
  mounted() {
    this.getOrder()
  },
  methods: {
    handleClose(done) {
      this.$confirm('Bạn muốn thoát ra?')
        .then(_ => {
          this.cancelOrderData = ''
          this.reason = ''
          done()
        })
        .catch(_ => {
        })
    },
    undoCancelOrder() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn hoàn tác hủy!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Hoàn tác'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          undoCancelOrder(this.order.id).then(res => {
            // this.getListOrder()
            this.getOrder()
            this.$swal(
              'Đã hoàn tác yêu cầu hủy đơn hàng!',
              'success'
            )
            this.isCancel = 0
          }).finally(() => { this.hideLoading() })
        }
      })
    },
    cancelOrder() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Đơn hàng không thể giao nếu bị hủy!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Hủy đơn'
      }).then((result) => {
        if (result.isConfirmed) {
          cancelOrder({ orderId: this.order.id, reason: this.reason }).then(res => {
            confirmOrder(this.order.id, this.order, false).then(res => {
              this.order = res.data
              this.changeStep()
              this.$swal(
                'Đã thay đổi trạng thái đơn hàng!',
                'Thay đổi trạng thái đơn hàng thành công!.',
                'success'
              )
            })
          }).finally(() => {
            this.cancelOrderData = ''
            this.reason = ''
            this.cancelOrderVisible = false
            this.hideLoading()
          })
        }
      })
    },
    updateStatusOrder(status, mess) {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: mess,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Chắc chắn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          const data = {
            orderId: this.order.id,
            orderStatusId: status
          }
          updateOrder(data).then(res => {
            this.$swal(
              'Đã thay đổi trạng thái đơn hàng!',
              'Thay đổi trạng thái đơn hàng thành công!.',
              'success'
            )
            this.order = res.data
            this.changeStep()
          }).finally(() => {
            this.hideLoading()
          })
        }
      })
    },
    confirmOrder(status, mess, choose) {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: mess,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Chắc chắn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          confirmOrder(this.order.id, this.order, choose).then(res => {
            this.$swal(
              'Đã thay đổi trạng thái đơn hàng!',
              'Thay đổi trạng thái đơn hàng thành công!.',
              'success'
            )
            this.order = res.data
            this.changeStep()
            if (choose) {
              this.getOrderGHN()
            }
          }).finally(() => {
            this.hideLoading()
          })
        }
      })
    },
    changeStep() {
      const orderStatus = this.order.orderStatus.id
      if (orderStatus === 3) {
        this.active = 1
      } else if (orderStatus === 5) {
        this.active = 2
      } else if (orderStatus === 1) {
        this.active = 7
      } else if (orderStatus === 2) {
        this.activeCancel = 2
        this.isCancel = 2
      } else if (orderStatus === 7) {
        this.activeCancel = 1
        this.isCancel = 2
      } else if (orderStatus === 4) {
        this.active = 5
      } else if (orderStatus === 6) {
        this.active = 4
      } else if (orderStatus === 10) {
        this.active = 4
      }
    },
    getOrderGHN() {
      getDetailOrderGHN({ order_code: this.order.shippingCode }).then(res => {
        this.orderGHN = res.data.data
        console.log(this.orderGHN)
      })
    },
    getOrder() {
      this.showLoading()
      getOrderDetailAdmin(this.$route.params).then(res => {
        this.order = res.data
        this.changeStep()
        if (this.order.userId) {
          findUserById(this.order.userId).then(res => {
            console.log(res)
            this.user = res.data
          })
        }
        if (this.order.shippingCode) {
          this.getOrderGHN()
        }
        if (this.order.voucherId) {
          getVoucherById({ id: this.order.voucherId }).then(res => {
            this.voucherCode = res.data.code
            console.log(res)
          })
        }
      }).catch((err) => {
        console.log(err.response)
        this.isCancel = 1
      }).finally(() => {
        this.hideLoading()
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
.el-checkbox__label {
  font-size: 1.5em;
}
label {
   margin-bottom: 0;
}
.el-form--label-top .el-form-item__label {
  padding-bottom:0;
}
</style>
