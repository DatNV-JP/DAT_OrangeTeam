<template>
  <el-main>
    <el-card class="box-card mb-3">
      <div slot="header" class="clearfix">
        <span><strong>Thông tin voucher</strong></span>
      </div>
      <el-row :gutter="20">
        <el-col :span="12">
          <div> Tên voucher:<strong> {{ viewData.name }}</strong></div>
          <div> Mã voucher:<strong> {{ viewData.code }}</strong></div>
          <div> Ngày tạo:<strong> {{ viewData.createDate | formatDateTime('DD-MM-YYYY') }}</strong></div>
          <div> Số lượt sử dụng:<strong> {{ viewData.usageLimit }}</strong></div>
          <div> Đã dùng:<strong> {{ viewData.used }}</strong></div>
          <div> Giá trị đơn hàng tối thiểu:<strong> {{ viewData.minimumOrderValue | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
          <div class="d-flex mt-2"> Trạng thái:
            <div style="display: block;">
              <div
                style=""
                :class="'badge badge-pill badge-' + checkVoucherTime.color + ' text-wrap pt-1 pb-1 pe-2 ps-2 ms-1 mb-2'"
              >
                {{ checkVoucherTime.value }}
              </div>
              <el-timeline>
                <el-timeline-item
                  :color="'#67C23A'"
                  :timestamp="viewData.startDate | formatDateTime('DD-MM-YYYY')"
                >
                  Bắt đầu
                </el-timeline-item>
                <el-timeline-item
                  :color="'#F56C6C'"
                  :timestamp="viewData.endDate | formatDateTime('DD-MM-YYYY')"
                >
                  Kết thúc
                </el-timeline-item>
              </el-timeline>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div v-if="viewData.isPercent"> Giảm Giá:<strong> {{ viewData.discountAmount + '%' }}</strong></div>
          <div v-if="!viewData.isPercent"> Giảm giá:<strong> {{ viewData.discountAmount | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
          <div> Giảm giá tối đa:<strong> {{ viewData.maxDiscountAmount | currency('VND', 0, 'đ', '.', ',') }}</strong></div>
          <div> Mục tiêu hiển thị:
            <strong v-if="viewData.isGlobal"> Tất cả khách hàng</strong>
            <strong v-if="!viewData.isGlobal && (viewData.groupId !== null)"> Dành cho khách hàng
              {{
                groupCustomers.find(g => g.id === viewData.groupId).groupName
              }}
            </strong>
            <strong v-if="!viewData.isGlobal && (viewData.groupId === null)">
              Dành cho khách hàng mới
            </strong>
          </div>
          <div> Mô tả:
            <div class="ps-5">
              <strong> {{ viewData.description }}</strong>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </el-main>
</template>

<script>
import { getListGroup, getListCustomerByGroup } from '@/api/voucher'
import BaseValidate from '@/utils/BaseValidate'
import { currency, formatDateTime } from '@/filters'
import baseCommon from '@/utils/base-common'

export default {
  name: 'ViewVoucher',
  filters: { currency, formatDateTime },
  components: { },
  mixins: [baseCommon, BaseValidate],
  props: {
    checkVoucherTime: {
      type: Object,
      require: true,
      default: () => {
        return {
          value: 'Đang diễn ra',
          color: 'warning'
        }
      }
    },
    viewData: {
      type: Object,
      require: true,
      default: () => {
        return {
          code: 'N/A',
          createBy: '',
          createDate: '',
          description: 'None',
          discountAmount: 10,
          endDate: '2023-06-30 00',
          groupId: 0,
          id: null,
          isGlobal: false,
          isPercent: true,
          maxDiscountAmount: 1000,
          minimumOrderValue: 10000,
          modifiedBy: '',
          modifiedDate: '',
          name: '',
          startDate: '2023-06-28 00',
          status: 1,
          usageLimit: 1,
          used: 0,
          userIds: [],
          voucherTypeId: null
        }
      }
    }
  },
  data() {
    return {
      groupCustomers: [],
      pageCustomer: {}
    }
  },
  created() {
    getListGroup().then(res => {
      this.groupCustomers = res.data
      console.log(this.groupCustomers)
    })
      .catch(error => {
        console.log(error)
      })
    getListCustomerByGroup({ groupId: this.viewData.groupId }).then(res => {
      this.pageCustomer = res.data
    })
  },
  methods: {
    handleChangeGroupCustomer() {
      console.log(this.voucherForm.groupId)
      getListCustomerByGroup({ groupId: this.voucherForm.groupId }).then(res => {
        this.pageCustomer = res.data
        console.log('here', this.pageCustomer, res.data)
      })
        .catch(error => {
          console.log(error)
        })
    }
  }
}
</script>

<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
