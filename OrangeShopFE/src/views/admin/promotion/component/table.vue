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
        label="Thời gian hoạt động"
        width="185"
      >
        <template v-slot="{ row }">
          <div style="display: block">
            <div
              style=""
              :class="'badge badge-pill badge-' + checkPromotinTime(row.startDate, row.endDate, row.status).color + ' text-wrap pt-1 pb-1 pe-2 ps-2'"
            >
              {{ checkPromotinTime(row.startDate, row.endDate, row.status).value }}
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
        label="Tên chương trình"
        width="150"
        prop="name"
        sortable
      >
        <template v-slot="{ row }">
          <div class="text-wrap">
            <p class="font-weight-bold">{{ row.name }}</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="Kiểu khuyến mãi"
        width="140"
      >
        <template v-slot="{ row }">{{ row.isDate ? "Theo ngày" : "Theo giờ" }}</template>
      </el-table-column>
      <el-table-column
        label="Giảm giá"
        align="center"
        width="120"
      >
        <template v-slot="{ row }">
          <span v-if="row.isPercent"> {{ row.discount + '%' }}</span>
          <span v-else> {{ row.discount | currency('VND', 0, 'đ', '.', ',') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="Giảm tối đa"
        align="center"
        width="100"
      >
        <!--        <template v-slot="{ row }">{{ row.maxSale }}</template>-->
      </el-table-column>
      <el-table-column
        label="Số lượng sản phẩm đang áp dụng"
        align="center"
        width="107"
      >
        <template v-slot="{ row }">{{ row.productDetailDTOs.length }}</template>
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
              <router-link :to="`/management/promotion-management/promotion-view/${row.id}`">
                <el-button type="primary" icon="el-icon-view" />
              </router-link>
            </el-tooltip>
          </div>
          <div v-if="row.status" class="mt-2">
            <el-tooltip class="item" content="Ngưng hoạt động?" placement="bottom">
              <el-button type="danger" icon="el-icon-remove" @click="deactivatePromotion(row)" />
            </el-tooltip>
          </div>
          <!--          <div v-if="checkPromotinTime(row.startDate, row.endDate, row.status).value === 'Ngừng hoạt động'" class="mt-2">-->
          <!--            <el-tooltip class="item" content="Kích hoạt lại?" placement="bottom">-->
          <!--              <el-button type="success" icon="el-icon-refresh-left" @click="reactivationPromotion(row)" />-->
          <!--            </el-tooltip>-->
          <!--          </div>-->
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
  </div>
</template>

<script>
import baseCommon from '@/utils/base-common'
import { currency, formatDateTime } from '@/filters'
import moment from 'moment-timezone'
import { searchPromotion, deletePromotion, reactivationPromotion } from '@/api/promotion'

export default {
  filters: {
    currency, formatDateTime
  },
  mixins: [baseCommon],
  props: {
    isDataChange: {
      type: Boolean,
      require: false,
      default: false
    },
    statusFilter: {
      type: Number,
      require: true,
      default: 1
    },
    searchPro: {
      type: String,
      require: true,
      default: ''
    }
  },
  data() {
    return {
      searchProTimer: null,
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
    searchPro: function() {
      clearTimeout(this.searchProTimer)
      this.searchProTimer = setTimeout(() => {
        this.showLoading()
        const params = {
          page: 0,
          size: this.pagination.pageSize,
          keyWord: this.searchPro || '',
          statusFilter: this.statusFilter,
          sort: 'createDate,desc'
        }
        searchPromotion(params).then(res => {
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
    checkPromotinTime(startDate, endDate, status) {
      const _startDate = moment(startDate)
      const _endDate = moment(endDate)
      const now = moment()

      if (now.isBetween(_startDate, _endDate)) {
        return {
          value: status ? 'Đang diễn ra' : 'Ngừng hoạt động',
          color: status ? 'warning' : 'secondary'
        }
      } else if (_startDate.isAfter(now)) {
        return {
          value: status ? 'Sắp diễn ra' : 'Ngừng hoạt động',
          color: status ? 'success' : 'secondary'
        }
      } else {
        return {
          value: 'Đã hết hạn',
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
    deactivatePromotion(data) {
      this.$swal({
        title: 'Bạn có chắc chắn muốn ngừng hoạt động khuyến mãi này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          deletePromotion({ id: data.id }).then(res => {
            this.$swal({
              title: 'Đã ngừng khuyến mãi!',
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
    reactivationPromotion(data) {
      reactivationPromotion(data).then(res => {
        this.$swal({
          title: 'Đã kích hoạt lại khuyến mãi!',
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
    },
    viewVoucher(data) {
      console.log(data)
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
        statusFilter: this.statusFilter,
        keyWord: this.searchPro
      }
      searchPromotion(params).then(res => {
        this.tableData = res.data.result
        console.log(1, this.tableData, res)
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
