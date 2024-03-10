<template>
  <div>
    <el-table :data="listOrder" row-key="id" :default-sort="{prop: 'id', order: 'descending'}" border fit highlight-current-row style="width: 100%">
      <el-table-column type="expand" width="70" label="Chi tiết" fixed>
        <template slot-scope="props">
          <el-table
            :data="props.row.orderDetailViews"
            style="width: 100%"
          >
            <el-table-column
              width="50"
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
            >
              <template v-slot="scope">
                <span>{{ scope.row.price | currency('VND', 0, 'đ', '.', ',') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>

      <el-table-column
        v-loading="loading"
        align="center"
        prop="id"
        label="Mã đơn hàng"
        sortable
        width="135"
        element-loading-text="Đang tải！"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column width="130" align="center" label="Ngày tạo" prop="createDate" sortable>
        <template slot-scope="scope">
          <span>{{ scope.row.createDate | parseTime('{d}-{m}-{y} {h}:{i}') }}</span>
        </template>
      </el-table-column>

      <el-table-column width="150" label="Người nhận">
        <template slot-scope="scope">
          <span>{{ scope.row.consigneeName }}</span>
        </template>
      </el-table-column>

      <el-table-column width="110" label="Số điện thoại">
        <template v-slot="scope">
          <span>{{ scope.row.consigneePhone }}</span>
        </template>
      </el-table-column>

      <el-table-column width="100" align="center" label="Tổng tiền">
        <template slot-scope="scope">
          <span>{{ scope.row.orderTotal | currency('VND', 0, 'đ', '.', ',') }}</span>
        </template>
      </el-table-column>

      <el-table-column width="250" align="center" label="Địa chỉ nhận">
        <template slot-scope="scope">
          <span v-if="scope.row.shippingMethod.id === 2 && scope.row.address">{{ scope.row.address.addressLine1 + ', ' + scope.row.address.village.name + ', ' + scope.row.address.village.district.name + ', ' + scope.row.address.village.district.city.name }}</span>
          <span v-if="scope.row.shippingMethod.id === 2 && !scope.row.address">{{ scope.row.toAddress + ', ' + scope.row.toWard + ', ' + scope.row.toDistrict + ', ' + scope.row.toProvince }}</span>
          <span v-if="scope.row.shippingMethod.id === 1">Nhận tại cửa hàng</span>
        </template>
      </el-table-column>

      <el-table-column class-name="status-col" label="Status" width="160">
        <template slot-scope="scope">
          <el-tag :type="scope.row.orderStatus.id | statusFilter">
            {{ scope.row.orderStatus.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" class-name="status-col" label="Thao tác" width="100">
        <template slot-scope="scope">
          <el-tooltip content="Top center" placement="top">
            <div slot="content">
              <el-button-group>
                <el-button type="primary" size="small" @click="detailOrder(scope.row)">Chi tiết</el-button>
                <!--                <el-button type="danger" size="small" :disabled="!(scope.row.orderStatus.id === 1)" @click="returnOrder(scope.row)">Hoàn trả</el-button>-->
                <el-button type="warning" size="small" :disabled="!(scope.row.orderStatus.id === 3 || scope.row.orderStatus.id === 5 || scope.row.orderStatus.id === 6)" @click="cancelOrderData = scope.row; cancelOrderVisible = true">Hủy đơn</el-button>
              </el-button-group>
            </div>
            <el-button type="primary">...</el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page.sync="pagination.pageNumber"
      :page-size="pagination.pageSize"
      :page-sizes="pageSizes"
      layout="sizes,prev, pager, next, total"
      :total="pagination.totalItems"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
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
        <el-button @click="cancelOrderVisible = false; reason=''; cancelOrderData = ''">Thoát</el-button>
        <el-button type="danger" @click="cancelOrder">Hủy đơn</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { cancelOrder, confirmOrder, getListOrderAdmin } from '@/api/order'
import baseCommon from '@/utils/base-common'
import { parseTime } from '@/utils'
import { currency } from '@/filters'
export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        1: 'success',
        2: 'danger',
        7: 'warning',
        6: 'primary'
      }
      return statusMap[status]
    },
    parseTime, currency
  },
  mixins: [baseCommon],
  props: {
    type: {
      type: String,
      default: '0'
    }
  },
  data() {
    return {
      listOrder: [],
      reason: '',
      cancelOrderVisible: false,
      cancelOrderData: ''
    }
  },
  created() {
    this.getListOrder()
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
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.getListOrder()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.getListOrder()
    },
    getListOrder() {
      this.showLoading()
      const params = {
        page: this.pagination.pageNumber - 1,
        size: this.pagination.pageSize,
        sort: 'createDate,desc',
        statusId: this.type === '0' ? null : this.type
      }
      getListOrderAdmin(params).then(res => {
        this.pagination.totalItems = res.data.totalItems
        this.listOrder = res.data.result
        console.log(this.listOrder)
      }).finally(() => {
        this.hideLoading()
      })
    },
    returnOrder(data) {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Đơn hàng sẽ được hoàn trả và hoàn tiền!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Hoàn trả'
      }).then((result) => {
        if (result.isConfirmed) {
          this.$swal(
            'Đã yêu hoàn đơn!',
            'Bạn đã yêu hoàn đơn hàng này. Vui lòng chờ xác nhận',
            'success'
          )
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
          cancelOrder({ orderId: this.cancelOrderData.id, reason: this.reason }).then(res => {
            this.confirmOrder(2, false)
          }).finally(() => { this.hideLoading() })
        }
      })
    },
    confirmOrder(status, choose) {
      confirmOrder(this.cancelOrderData.id, this.cancelOrderData, choose).then(res => {
        this.getListOrder()
        this.$swal(
          'Đã thay đổi trạng thái đơn hàng!',
          'Thay đổi trạng thái đơn hàng thành công!.',
          'success'
        )
        this.cancelOrderData = ''
        this.reason = ''
        this.cancelOrderVisible = false
      }).finally(() => { this.hideLoading() })
    },
    detailOrder(data) {
      this.$router.push('/order-management/tracking-page/' + data.id)
    }
  }
}
</script>
<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
