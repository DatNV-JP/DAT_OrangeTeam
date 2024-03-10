<template>
  <el-main>
    <ValidationObserver v-slot="{ invalid, handleSubmit }">
      <el-form ref="ruleForm" :model="voucherForm" label-width="35%" label-position="left">
        <el-row :gutter="20">
          <el-col :span="12">
            <ValidationProvider v-slot="{ errors }" rules="required|min:3">
              <el-form-item :error="messageError('Tên Voucher', errors[0])">
                <template v-slot:label><label><span class="svg-container">Tên Voucher<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-input v-model="voucherForm.name" />
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|min:3|max:6">
              <el-form-item :error="messageError('Mã Voucher', errors[0])">
                <template v-slot:label><label><span class="svg-container">Mã Voucher<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-input v-model="voucherForm.code" />
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|min:1">
              <el-form-item :error="messageError('Mã Voucher', errors[0])">
                <template v-slot:label><label><span class="svg-container">Số lượt sử dụng<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-input-number v-model="voucherForm.usageLimit" :min="1" :step="2" />
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|min:1">
              <el-form-item :error="messageError('Giá trị đơn hàng tối thiểu', errors[0])">
                <template v-slot:label><label><span class="svg-container">Giá trị đơn hàng tối thiểu<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-input-number v-model="voucherForm.minimumOrderValue" :min="1" :step="1000" />
                <strong>VNĐ</strong>
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|min:1">
              <el-form-item :error="messageError('Giảm giá', errors[0])">
                <template v-slot:label><label><span class="svg-container">Giảm giá<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-row type="flex">
                  <el-col :span="16">
                    <el-input-number
                      v-model="voucherForm.discountAmount"
                      :step="voucherForm.isPercent ? 5 : 1000"
                      :min="1"
                      :max="voucherForm.isPercent ? 100 : 9999999999999"
                    />
                  </el-col>
                  <el-col :span="8" justify="end">
                    <el-radio-group
                      v-model="voucherForm.isPercent"
                      @change="() => {voucherForm.isPercent ? voucherForm.discountAmount = 30 : voucherForm.discountAmount = 1000}"
                    >
                      <el-radio v-model="voucherForm.isPercent" :label="true">%</el-radio>
                      <el-radio v-model="voucherForm.isPercent" :label="false">VNĐ</el-radio>
                    </el-radio-group>
                  </el-col>
                </el-row>
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|min:1">
              <el-form-item :error="messageError('Giảm tối đa', errors[0])">
                <template v-slot:label><label><span class="svg-container">Giảm tối đa<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-input-number v-model="voucherForm.maxDiscountAmount" :min="1" :step="1000" />
                <strong>VNĐ</strong>
              </el-form-item>
            </ValidationProvider>
          </el-col>
          <el-col :span="12">
            <ValidationProvider v-slot="{ errors }" rules="required">
              <el-form-item :error="messageError('Thời gian sử dụng', errors[0])">
                <template v-slot:label><label><span class="svg-container">Thời gian sử dụng<span
                  class="text-danger"
                > *</span></span></label></template>
                <el-date-picker
                  v-model="voucherForm.date"
                  type="datetimerange"
                  range-separator="-"
                  start-placeholder="Bắt đầu"
                  end-placeholder="Kết thúc"
                  :picker-options="{ disabledDate: (time) => disabledEndDate(time) }"
                  @change="handleChangeDateRange"
                />
              </el-form-item>
            </ValidationProvider>
            <el-form-item label="Mục tiêu hiển thị" prop="isGlobal">
              <el-radio-group v-model="voucherForm.isGlobal">
                <el-radio v-model="voucherForm.isGlobal" :label="true">Tất cả</el-radio>
                <el-radio v-model="voucherForm.isGlobal" :label="false">Nhóm khách hàng</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :hidden="voucherForm.isGlobal">
              <template v-slot:label><label><span class="svg-container">Chọn nhóm khách hàng<span
                class="text-danger"
              > *</span></span></label></template>
              <el-select
                v-model="voucherForm.groupId"
                placeholder="chọn nhóm khách hàng"
                @change="handleChangeGroupCustomer"
              >
                <el-option label="Khách hàng mới" :value="0" />
                <el-option v-for="item in groupCustomers" :key="item.id" :label="item.groupName" :value="item.id" />
              </el-select>
              <p>có {{ pageCustomer.totalItems }} Khách hàng</p>
            </el-form-item>
            <ValidationProvider v-slot="{ errors }" rules="min:20|max:3000">
              <el-form-item :error="messageError('Mô tả', errors[0])">
                <template v-slot:label><label><span class="svg-container">Mô tả</span></label></template>
                <el-input v-model="voucherForm.description" type="textarea" :rows="6" maxlength="3000" show-word-limit />
              </el-form-item>
            </ValidationProvider>
          </el-col>
        </el-row>
        <div class="float-right">
          <el-button type="primary" :disabled="invalid" @click="handleSubmit(addVoucher)">Create</el-button>
          <el-button @click="resetForm()">Reset</el-button>
        </div>
      </el-form>
    </ValidationObserver>
  </el-main>
</template>

<script>
import { addVoucher, getListGroup, getListCustomerByGroup } from '@/api/voucher'
import BaseValidate from '@/utils/BaseValidate'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import { currency } from '@/filters'
import baseCommon from '@/utils/base-common'

export default {
  name: 'AddVoucher',
  filters: { currency },
  components: { ValidationObserver, ValidationProvider },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      groupCustomers: [],
      pageCustomer: {},
      voucherForm: {
        name: '',
        code: '',
        usageLimit: 1,
        discountAmount: 10,
        maxDiscountAmount: 1000,
        date: '',
        startDate: null,
        endDate: null,
        isGlobal: true,
        isPercent: true,
        userIds: [],
        minimumOrderValue: 10000,
        groupId: 0,
        description: ''
      }
    }
  },
  created() {
    getListGroup().then(res => {
      this.groupCustomers = res.data
    })
      .catch(error => {
        console.log(error)
      })
    console.log(this.voucherForm.groupId)
    getListCustomerByGroup({ groupId: this.voucherForm.groupId }).then(res => {
      this.pageCustomer = res.data
      console.log(res, this.pageCustomer)
    })
      .catch(error => {
        console.log(error)
      })
  },
  methods: {
    addVoucher() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn thêm voucher này!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Chưa',
        confirmButtonText: 'Chắc chắn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          // console.log(this.voucherForm)
          if (this.voucherForm.isGlobal) {
            this.voucherForm.groupId = null
            this.voucherForm.userIds = null
          } else {
            if (this.voucherForm.groupId === 0) {
              this.voucherForm.groupId = null
            }
            this.voucherForm.userIds = this.pageCustomer.result.map(customer => customer.userId)
          }
          addVoucher(this.voucherForm).then(res => {
            this.resetForm()
            this.$swal({
              title: 'Đã tạo voucher!',
              message: res.message,
              icon: 'success',
              showConfirmButton: false,
              timer: 1500,
              timerProgressBar: true
            })
            console.log(res)
          }).catch(err => {
            console.log(err.response)
          }).finally(() => {
            this.hideLoading()
          })
        }
      })
    },
    handleChangeDateRange() {
      this.voucherForm.startDate = new Date(this.voucherForm.date[0])
      this.voucherForm.endDate = new Date(this.voucherForm.date[1])
    },
    handleChangeGroupCustomer() {
      console.log(this.voucherForm.groupId)
      getListCustomerByGroup({ groupId: this.voucherForm.groupId }).then(res => {
        this.pageCustomer = res.data
        console.log('here', this.pageCustomer, res.data)
      })
        .catch(error => {
          console.log(error)
        })
    },
    resetForm() {
      this.voucherForm.name = ''
      this.voucherForm.code = ''
      this.voucherForm.usageLimit = 1
      this.voucherForm.discountAmount = 10
      this.voucherForm.maxDiscountAmount = 1000
      this.voucherForm.date = ''
      this.voucherForm.startDate = null
      this.voucherForm.endDate = null
      this.voucherForm.isGlobal = true
      this.voucherForm.isPercent = true
      this.voucherForm.userIds = []
      this.voucherForm.minimumOrderValue = 100000
      this.voucherForm.groupId = 0
      this.voucherForm.description = ''
    },
    disabledEndDate(time) {
      // If !departureDate then return valid dates after today
      var now = new Date()
      var previousDate = now.setDate(now.getDate() - 1)
      return time.getTime() < previousDate
    }
  }
}
</script>

<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
