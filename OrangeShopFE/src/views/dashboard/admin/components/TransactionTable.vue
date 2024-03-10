<template>
  <div>
    <el-table :data="listOder" style="width: 100%;padding-top: 15px;">
      <el-table-column label="STT" min-width="25" type="index" />
      <el-table-column label="Mã Đơn Hàng" width="195" align="center">
        <template slot-scope="scope">
          {{ scope.row.id }}
        </template>
      </el-table-column>
      <el-table-column label="Thành Tiền" width="195" align="center">
        <template slot-scope="scope">
          {{ scope.row.orderTotal | currency('VND', 0, 'đ', '.', ',') }}
        </template>
      </el-table-column>
      <el-table-column label="Người Nhận" width="195" align="center">
        <template slot-scope="scope">
          {{ scope.row.consigneeName }}
        </template>
      </el-table-column>
      <el-table-column label="Địa Chỉ" width="425" align="left">
        <template slot-scope="scope">
          <span v-if="scope.row.shippingMethod.id === 2 && scope.row.address">{{ scope.row.address.addressLine1 + ', ' + scope.row.address.village.name + ', ' + scope.row.address.village.district.name + ', ' + scope.row.address.village.district.city.name }}</span>
          <span v-if="scope.row.shippingMethod.id === 2 && !scope.row.address">{{ scope.row.toAddress + ', ' + scope.row.toWard + ', ' + scope.row.toDistrict + ', ' + scope.row.toProvince }}</span>
          <span v-if="scope.row.shippingMethod.id === 1">Nhận tại cửa hàng</span>
        </template>
      </el-table-column>
      <el-table-column label="Trạng Thái" width="150" align="left">
        <template slot-scope="{row}">
          <el-tag :type="row.orderStatus.status | statusFilter">
            {{ row.orderStatus.status }}
          </el-tag>
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
  </div>
</template>

<script>
import { getListOrderAdmin } from '@/api/order'
import { order } from 'mockjs/src/mock/random/helper'
import baseCommon from '@/utils/base-2-common'
import { currency } from '@/filters'

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        Completed: 'success',
        Cancel: 'danger',
        'Wait for pay': 'warning',
        'Delivery in progress': 'regular',
        'Wait for confirmation': '#606266',
        'Confirmed': 'info'
      }
      return statusMap[status]
    },
    orderNoFilter(str) {
      return str.substring(0, 30)
    },
    currency
  },
  mixins: [baseCommon],
  data() {
    return {
      list: null,
      listOder: []
    }
  },
  created() {
    this.callGetTopTenOrder()
  },
  methods: {
    order,
    callGetTopTenOrder() {
      const params = {
        cid: this.$route.params.cid,
        page: this.pagination.pageNumber - 1,
        size: this.pagination.pageSize
      }
      getListOrderAdmin(params).then(res => {
        this.listOder = res.data.result
        this.pagination.totalItems = res.data.totalItems
      })
    },
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.callGetTopTenOrder()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.callGetTopTenOrder()
    }
  }
}
</script>
