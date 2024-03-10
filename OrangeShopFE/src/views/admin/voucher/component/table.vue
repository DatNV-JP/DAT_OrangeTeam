<template>
  <div>
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        label="Ngày tạo"
        width="110"
        prop="createDate"
        sortable
      >
        <template v-slot="{ row }">
          <div class="text-wrap">
            <p class="font-weight-bold">{{ row.createDate | formatDateTime('DD-MM-YYYY') }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="Trạng thái | Hạn sử dụng"
        width="185"
      >
        <template v-slot="{ row }">
          <div style="display: block">
            <div
              style=""
              :class="'badge badge-pill badge-' + checkVoucherTime(row.startDate, row.endDate, row.status).color + ' text-wrap pt-1 pb-1 pe-2 ps-2'"
            >
              {{ checkVoucherTime(row.startDate, row.endDate, row.status).value }}
            </div>
            <el-timeline>
              <el-timeline-item
                :color="'#67C23A'"
                :timestamp="row.startDate | formatDateTime('DD-MM-YYYY HH:mm')"
              >
                Bắt đầu
              </el-timeline-item>
              <el-timeline-item
                :color="'#F56C6C'"
                :timestamp="row.endDate | formatDateTime('DD-MM-YYYY HH:mm')"
              >
                Kết thúc
              </el-timeline-item>
            </el-timeline>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="Tên | Mã voucher"
        width="160"
        prop="name"
        sortable
      >
        <template v-slot="{ row }">
          <div class="text-wrap">
            <p class="font-weight-bold">{{ row.name }}</p>
            <p>{{ row.code }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="Mục tiêu hiển thị"
        width="140"
      >
        <template v-slot="{ row }">{{ row.isGlobal ? "Tất cả" : "Nhóm khách hàng" }}</template>
      </el-table-column>
      <el-table-column
        label="Giảm giá"
        align="center"
        width="120"
      >
        <template v-slot="{ row }">
          <span v-if="row.isPercent"> {{ row.discountAmount + '%' }}</span>
          <span v-else> {{ row.discountAmount | currency('VND', 0, 'đ', '.', ',') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="Tổng số mã giảm giá"
        align="center"
        width="100"
      >
        <template v-slot="{ row }">{{ row.usageLimit }}</template>
      </el-table-column>
      <el-table-column
        label="Đã dùng"
        align="center"
        width="100"
      >
        <template v-slot="{ row }">{{ row.used }}</template>
      </el-table-column>
      <el-table-column
        label="Mô tả"
      >
        <template v-slot="{ row }">
          <div class="text-ellipsis">{{ row.description }}</div>
        </template>
      </el-table-column>
      <el-table-column
        label="Thao tác"
        width="80"
        align="center"
      >
        <template v-slot="{ row }">
          <div>
            <el-tooltip class="item" content="Xem chi tiết" placement="top">
              <el-button type="primary" icon="el-icon-view" @click="openViewVoucher(row)" />
            </el-tooltip>
          </div>
          <div v-if="(row.status === 1) && (checkVoucherTime(row.startDate, row.endDate, row.status).value !== 'Đã kết thúc')" class="mt-2">
            <el-tooltip class="item" content="Ngưng hoạt động?" placement="bottom">
              <el-button type="danger" icon="el-icon-remove" @click="deleteVoucher(row)" />
            </el-tooltip>
          </div>
          <div v-if="(row.status === 2) && (checkVoucherTime(row.startDate, row.endDate, row.status).value !== 'Đã kết thúc')" class="mt-2">
            <el-tooltip class="item" content="Kích hoạt lại?" placement="bottom">
              <el-button type="success" icon="el-icon-refresh-left" @click="reactivateVoucher(row)" />
            </el-tooltip>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div class="col-12 pb-1">
      <el-pagination
        :current-page.sync="pagination.pageNumber"
        :page-size="pagination.pageSize"
        :page-sizes="pageSizes"
        layout="sizes,prev, pager, next, total"
        :total="pagination.totalItems"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <el-dialog
      title="Chi tiết voucher"
      :visible.sync="dialogViewVoucherVisible"
      :before-close="handleClose"
      width="70%"
    >
      <ViewVoucher :check-voucher-time="viewVoucherTimeLine" :view-data="viewVoucherData" />
    </el-dialog>
  </div>
</template>

<script>
import { deleteVoucher, reactivateVoucher, searchVoucher } from '@/api/voucher'
import baseCommon from '@/utils/base-common'
import { currency, formatDateTime } from '@/filters'
import moment from 'moment-timezone'
import ViewVoucher from '@/views/admin/voucher/component/ViewVoucher.vue'

export default {
  components: { ViewVoucher },
  filters: {
    currency, formatDateTime
  },
  mixins: [baseCommon],
  props: {
    isDataChange: {
      type: Boolean,
      require: true,
      default: false
    },
    statusFilter: {
      type: Number,
      require: true,
      default: 1
    },
    searchVoucher: {
      type: Number,
      require: true,
      default: 1
    }
  },
  data() {
    return {
      viewVoucherData: {},
      viewVoucherTimeLine: {},
      dialogViewVoucherVisible: false,
      tableData: [],
      multipleSelection: []
    }
  },
  watch: {
    statusFilter: function() {
      this.pagination.pageNumber = 1
      this.init()
    },
    isDataChange: function() {
      if (this.isDataChange) {
        this.init()
        this.isDataChange = false
      }
    },
    searchVoucher: function() {
      clearTimeout(this.searchProTimer)
      this.searchProTimer = setTimeout(() => {
        this.showLoading()
        const params = {
          page: 0,
          size: this.pagination.pageSize,
          keyWord: this.searchVoucher || '',
          status: this.statusFilter,
          sort: 'createDate,desc'
        }
        searchVoucher(params).then(res => {
          this.tableData = res.data.result
          // console.log(this.tableData)
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      }, 2000)
    }
  },
  created() {
    this.init()
  },
  methods: {
    handleClose(done) {
      done()
    },
    checkVoucherTime(startDate, endDate, status) {
      const _startDate = moment(startDate)
      const _endDate = moment(endDate)
      const now = moment()

      if (now.isBetween(_startDate, _endDate)) {
        return {
          value: status === 1 ? 'Đang diễn ra' : 'Ngừng hoạt động',
          color: status === 1 ? 'warning' : 'secondary'
        }
      } else if (_startDate.isAfter(now)) {
        return {
          value: status === 1 ? 'Sắp diễn ra' : 'Ngừng hoạt động',
          color: status === 1 ? 'success' : 'secondary'
        }
      } else {
        return {
          value: 'Đã kết thúc',
          color: 'danger'
        }
      }
    },
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.init()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.init()
    },
    deleteVoucher(data) {
      this.$swal({
        title: 'Bạn có chắc chắn muốn ngừng hoạt động voucher này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          deleteVoucher(data.id).then(res => {
            this.$swal({
              title: 'Đã ngừng voucher này!',
              message: res.message,
              icon: 'success',
              showConfirmButton: false,
              timer: 1500,
              timerProgressBar: true
            })
            this.init()
          })
            .catch(error => {
              console.log(error)
            })
        }
      })
    },
    reactivateVoucher(data) {
      this.showLoading()
      reactivateVoucher(data.id).then(res => {
        this.$swal({
          title: 'Đã kích hoạt lại!',
          message: res.message,
          icon: 'success',
          showConfirmButton: false,
          timer: 1500,
          timerProgressBar: true
        })
        this.init()
      })
        .catch(error => {
          console.log(error)
          this.hideLoading()
        })
        .finally(() => this.hideLoading())
    },
    openViewVoucher(data) {
      console.log(data)
      this.viewVoucherTimeLine = this.checkVoucherTime(data.startDate, data.endDate, data.status)
      this.viewVoucherData = data
      this.dialogViewVoucherVisible = true
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      // console.log(this.multipleSelection)
      this.$emit('select-list', val)
    },
    init() {
      this.showLoading()
      const params = {
        page: this.pagination.pageNumber - 1,
        size: this.pagination.pageSize,
        status: this.statusFilter
      }
      searchVoucher(params).then(res => {
        this.tableData = res.data.result
        // console.log(this.tableData)
        this.pagination.totalItems = res.data.totalItems
      }).finally(() => {
        this.hideLoading()
      })
    }
  }
}
</script>

<style>
/* .el-table .success-row {
    background: #0493f9;
  } */
.text-ellipsis {
  white-space: pre-wrap;
  overflow-wrap: break-word;
  overflow: auto;
  text-overflow: ellipsis;
  max-width: 100%;
  max-height: 150px;
}

.item .el-badge__content {
  height: 20px;
  margin-top: 6px;
}
</style>
