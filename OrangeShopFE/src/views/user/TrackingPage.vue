<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <el-card v-if="isCancel === 2" class="box-card">
      <div slot="header" class="clearfix">
        <span> Mã order:<strong> {{ order.id }}</strong></span>
        <el-button v-if="active === 1 && order.orderStatus.id !== 2" class="float-right" type="danger" @click="undoCancelOrder">Hoàn tác huỷ</el-button>
      </div>
      <el-steps :active="activeCancel">
        <el-step title="Chờ huỷ" icon="el-icon-warning" :description="order.waitForCancelTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Huỷ thành công" icon="el-icon-close" :description="order.cancelTime | formatDateTime('DD-MM-YYYY')" />
      </el-steps>
    </el-card>
    <h1 v-if="isCancel === 1">Không tìm thấy đơn hàng</h1>
    <el-card v-if="isCancel === 0" class="box-card">
      <div slot="header" class="clearfix">
        <span> Mã order:<strong> {{ order.id }}</strong></span>
        <el-button v-if="active === 1 || active === 2 || active === 3" class="float-right" type="danger" @click="cancelOrderData = order; cancelOrderVisible = true">Hủy đơn</el-button>
      </div>
      <el-steps :active="active">
        <el-step title="Đặt hàng" icon="ti-package" :description="order.createDate | formatDateTime('DD-MM-YYYY')" />
        <el-step v-show="order.paymentType.id === 2" title="Thanh toán" icon="el-icon-s-finance" :description="order.confirmedTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Xác nhận" icon="el-icon-check" :description="order.confirmedTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Xử lý" icon="ti-reload" :description="order.deliveryInProgressTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Vận chuyển" icon="el-icon-truck" :description="order.deliveryInProgressTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Đang giao hàng" icon="ti-layout-media-right" :description="order.deliveryInProgressTime | formatDateTime('DD-MM-YYYY')" />
        <el-step title="Hoàn thành" icon="el-icon-sold-out" :description="order.completedTime | formatDateTime('DD-MM-YYYY')" />
      </el-steps>
      <el-divider />
    </el-card>
    <el-dialog
      title="Yêu cầu hủy đơn"
      :visible.sync="cancelOrderVisible"
      :before-close="handleClose"
      width="85%"
      top="5vh"
      :destroy-on-close="true"
    >
      <el-form label-position="top">
        <el-form-item label="Hãy cho chúng tôi biết lý do hủy đơn của bạn:">
          <el-input v-model="reason" type="textarea" autocomplete="off" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelOrderVisible = false; reason=''; cancelOrderData = ''">Thoát</el-button>
        <el-button type="danger" @click="cancelOrder">Hủy đơn</el-button>
      </span>
    </el-dialog>
  </el-main>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-2-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import PageHeader from '@/views/user/component/PageHeader'
import { cancelOrder, getOrderDetail, undoCancelOrder } from '@/api/order'
import { parseTime } from '@/utils'
import { currency, formatDateTime } from '@/filters'

export default {
  name: 'Shop',
  directives: {
    horizontalScroll
  },
  filters: { parseTime, formatDateTime, currency },
  components: { PageHeader },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      active: 1,
      activeCancel: 1,
      order: {
        paymentType: {
          id: 1,
          value: ''
        }
      },
      isCancel: 0,
      reason: '',
      cancelOrderVisible: false,
      cancelOrderData: ''
    }
  },
  created() {
    this.getOrder()
  },
  methods: {
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
        text: 'Đơn hàng không thể giao đến bạn nếu bị hủy!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Hủy đơn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          cancelOrder({ orderId: this.cancelOrderData.id, reason: this.reason }).then(res => {
            // this.getListOrder()
            this.getOrder()
            this.$swal(
              'Đã yêu cầu hủy đơn hàng!',
              'Bạn đã yêu hủy đơn hàng này. Vui lòng chờ xác nhận.',
              'success'
            )
            this.isCancel = 2
            this.cancelOrderData = ''
            this.reason = ''
            this.cancelOrderVisible = false
          }).finally(() => { this.hideLoading() })
        }
      })
    },
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
    getOrder() {
      this.showLoading()
      getOrderDetail(this.$route.params).then(res => {
        console.log(1, res)
        this.order = res.data
        const orderStatus = res.data.orderStatus.id
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
        } else if (orderStatus === 6 || orderStatus === 10) {
          this.active = 4
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
