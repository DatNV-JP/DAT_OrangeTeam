<template>
  <el-main>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-row class="pb-3">
          <el-col :span="8">
            <el-input
              v-model="searchPro"
              placeholder="Tìm kiếm danh mục"
              prefix-icon="el-icon-search"
              suffix-icon="el-icon-search"
              style="border-top-style: hidden;"
            />
          </el-col>
          <el-col :span="8" style="padding-left: 1rem">
            <span>Lọc: </span>
            <el-select v-model="value" placeholder="Select" @change="handleStatusChange">
              <el-option
                v-for="(item, index) in promotionStatusFilter"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-col>
          <el-col :span="8">
            <div style="float: right;">
              <router-link to="/management/promotion-management/promotion-add" class="btn btn-success">
                Thêm mới<i class="el-icon-plus el-icon--right" />
              </router-link>
            </div>
          </el-col>
        </el-row>
        <Table :status-filter="fillByStatus" :search-pro="searchPro" />
      </el-col>
    </el-row>
  </el-main>
</template>

<script>
import Table from './table.vue'

export default {
  name: 'Promotion',
  components: { Table },
  data() {
    return {
      searchPro: '',
      fillByStatus: 1,
      promotionStatusFilter: [{
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
  methods: {
    handleStatusChange() {
      this.fillByStatus = Number(this.value)
    }
  }
}
</script>

<style scoped>

</style>
