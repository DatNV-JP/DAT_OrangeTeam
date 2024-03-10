<template>
  <div>
    <el-row class="text-xl-center fs-5 mt-3 fw-bold">Chi tiết chương trình khuyến mãi</el-row>
    <el-table
      :data="tableData"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
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
      <!--      <el-table-column-->
      <!--        label="Giảm tối đa"-->
      <!--        align="center"-->
      <!--        width="100"-->
      <!--      >-->
      <!--        &lt;!&ndash;        <template v-slot="{ row }">{{ row.maxSale }}</template>&ndash;&gt;-->
      <!--      </el-table-column>-->
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
    <el-row class="text-xl-center fs-5 mt-3 fw-bold">Các sản Phẩm được áp dụng</el-row>
    <el-table
      :data="paginatedData"
      border
      style="width: 100%"
    >
      <el-table-column
        label="Hình ảnh"
        width="90"
      >
        <template v-slot="{ row }">
          <img :src="uploadUrl +'/productDetailImages/' + row.images" alt="" style="width: 4em">
        </template>
      </el-table-column>
      <el-table-column width="180" align="left" label="Tên Sản phẩm">
        <template v-slot="{ row }">
          <strong>{{ row.nameProduct }}</strong>
        </template>
      </el-table-column>
      <el-table-column width="180" align="left" label="Thuộc tính">
        <template v-slot="{ row }">
          <span v-for="(va, index) in row.variationOptions" :key="index" class="d-block">{{ va.variation.name + ': ' }} <strong>{{ va.value }}</strong> </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="quantity"
        label="Số lượng"
        show-overflow-tooltip
      />
      <el-table-column
        label="Giá nhập"
      >
        <template v-slot="{ row }">
          {{ row.priceInput | currency('VND', 0, 'đ', '.', ',') }}
        </template>
      </el-table-column>
      <el-table-column
        label="Giá mặc định"
        prop="priceDefault"
      >
        <template v-slot="{ row }">
          {{ row.priceDefault | currency('VND', 0, 'đ', '.', ',') }}
        </template>
      </el-table-column>
      <el-table-column
        v-if="checkPromotinTime(tableData[0].startDate, tableData[0].endDate, tableData[0].status).value === 'Đang diễn ra'"
        label="Giá sau khi giảm"
        prop="priceSale"
      >
        <template v-slot="{ row }">
          {{ row.priceSale | currency('VND', 0, 'đ', '.', ',') }}
        </template>
      </el-table-column>
      <!--      D-->
    </el-table>
    <div class="col-12 pb-1">
      <el-pagination
        :current-page.sync="pagination.pageNumber"
        :page-size="pagination.pageSize"
        :page-sizes="pageSizes"
        layout="sizes,prev, pager, next, total"
        :total="productDetails.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { currency, formatDateTime } from '@/filters'
import baseCommon from '@/utils/base-common'
import {
  deletePromotion, getPromotionDetail,
  reactivationPromotion
} from '@/api/promotion'
import moment from 'moment-timezone'

export default {
  name: 'ViewPromotion',
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
      require: false,
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
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      searchProTimer: null,
      tableData: [],
      productDetails: [],
      multipleSelection: []
    }
  },
  computed: {
    paginatedData() {
      const startIndex = (this.pagination.pageNumber - 1) * this.pagination.pageSize
      const endIndex = startIndex + this.pagination.pageSize
      return this.productDetails.slice(startIndex, endIndex)
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
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
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
      getPromotionDetail(this.$route.params).then(res => {
        this.showLoading()
        this.tableData = []
        this.productDetails = []
        this.tableData.push(res.data)
        this.productDetails = res.data.productDetailDTOs
        console.log(res.data)
        console.log(this.productDetails)
      }).finally(() => {
        this.hideLoading()
      })
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
        } else {
          sums[index] = ''
        }
      })
      sums[6] = currency((sums[5] - sums[6]), 'VND', 0, 'đ', '.', ',')
      sums[5] = 'Tổng tiền đã giảm:'
      sums[3] = ''
      return sums
    }
  }
}
</script>

<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
