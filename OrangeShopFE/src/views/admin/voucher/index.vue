<template>
  <el-main>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-row class="pb-3">
          <el-col :span="8">
            <el-input
              v-model="searchVoucher"
              placeholder="Tìm kiếm"
              prefix-icon="el-icon-search"
              suffix-icon="el-icon-search"
              style="border-top-style: hidden;"
            />
          </el-col>
          <el-col :span="8" style="padding-left: 1rem">
            <span>Lọc: </span>
            <el-select v-model="value" placeholder="Select" @change="handleStatusChange">
              <el-option
                v-for="(item, index) in voucherStatus"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-col>
          <el-col :span="8">
            <div style="float: right;">
              <el-button class="btn btn-success" type="text" @click="dialogVisible = true">Thêm mới<i class="el-icon-plus el-icon--right" /></el-button>
            </div>
          </el-col>
        </el-row>
        <Table :search-voucher="parseInt(searchVoucher)" :is-data-change="isDataChange" :status-filter="fillByStatus" @select-list="selectListPro" />
      </el-col>
    </el-row>

    <el-dialog
      title="Thêm mới voucher"
      :visible.sync="dialogVisible"
      :before-close="handleClose"
      width="85%"
      top="5vh"
      :destroy-on-close="true"
    >
      <VoucherForm />
    </el-dialog>
  </el-main>
</template>

<script>

import Table from './component/table'
import VoucherForm from './component/AddVoucher.vue'
import baseCommon from '@/utils/base-2-common'

export default {
  name: 'Voucher',
  components: { Table, VoucherForm },
  mixins: [baseCommon],
  data() {
    return {
      isDataChange: false,
      fillByStatus: 1,
      dialogVisible: false,
      checked: '',
      textarea: '',
      searchVoucher: '',
      selected: '',
      selectPro: [],
      voucherStatus: [{
        value: 1,
        label: 'Tất cả'
      }, {
        value: 2,
        label: 'Đã ngưng hoạt động'
      }, {
        value: 3,
        label: 'Sắp diễn ra'
      }, {
        value: 4,
        label: 'Đang diễn ra'
      }, {
        value: 5,
        label: 'Đã kết thúc'
      }],
      value: 1
    }
  },
  watch: {
    searchCate(val) {
      this.$refs.treeCate.filter(val)
    }
  },
  created() {
  },
  methods: {
    selectListPro(data) {
      this.selectPro = data
    },
    handleClose(done) {
      this.$confirm('Bạn muốn thoát ra?')
        .then(_ => {
          done()
          this.isDataChange = true
        })
        .catch(_ => {
        })
    },
    handleSizeChange(val) {
      console.log(`${val} items per page`)
    },
    handleCurrentChange(val) {
      console.log(`current page: ${val}`)
    },
    handleStatusChange() {
      this.fillByStatus = Number(this.value)
    }
  }
}
</script>

<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
