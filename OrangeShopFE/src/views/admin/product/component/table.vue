<template>
  <div>
    <el-table
      ref="multipleTable"
      :data="tableData"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="39"
      />
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-table
            :data="props.row.productDetails"
            style="width: 100%"
            :row-class-name="tableRowClassName"
          >
            <el-table-column
              label="Hình ảnh"
              width="90"
            >
              <template slot-scope="scope">
                <img :src="uploadUrl +'/productDetailImages/' + scope.row.images" alt="" style="width: 4em">
              </template>
            </el-table-column>
            <el-table-column width="180" align="left" label="Thuộc tính">
              <template v-slot="scope">
                <span v-for="(va, index) in scope.row.variationOptions" :key="index" class="d-block">{{ va.variation.name + ': ' }} <strong>{{ va.value }}</strong> </span>
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
              <template v-slot="scope">
                {{ scope.row.priceInput | currency('VND', 0, 'đ', '.', ',') }}
              </template>
            </el-table-column>
            <el-table-column
              label="Giá mặc định"
            >
              <template v-slot="scope">
                {{ scope.row.priceDefault | currency('VND', 0, 'đ', '.', ',') }}
              </template>
            </el-table-column>
            <el-table-column
              label="Giá sale"
            >
              <template v-slot="scope">
                {{ scope.row.priceSale | currency('VND', 0, 'đ', '.', ',') }}
              </template>
            </el-table-column>
            <el-table-column
              label="Thao tác"
              width="90"
            >
              <template v-slot="scope">
                <el-button type="danger" @click="deleteDetail(scope.row)">Xóa</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column
        label="Hình ảnh"
        width="90"
      >
        <template v-slot="{ row }">
          <img :src="uploadUrl +'/productImage/' + row.defaultImage.split(',')[0]" alt="" style="width: 4em">
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        label="Name"
        width="200"
      />
      <el-table-column
        label="Danh mục"
        width="120"
      >
        <template v-slot="{ row }">{{ categories.find(cate => cate.id === row.categoryId).name }}</template>
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
        width="140"
      >
        <template v-slot="{ row }">
          <el-button-group>
            <el-tooltip class="item" content="Cập nhật" placement="top">
              <el-button type="primary" icon="el-icon-edit" @click="editProduct(row)" />
            </el-tooltip>
            <el-tooltip class="item" content="Xoá" placement="top">
              <el-button type="danger" icon="el-icon-delete" @click="deleteProduct(row)" />
            </el-tooltip>
          </el-button-group>
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
import {
  deleteProduct, deleteProductDetail, getListProduct,
  getListProductByCategory, searchProduct
} from '@/api/product'
import baseCommon from '@/utils/base-common'
import { currency } from '@/filters'

export default {
  filters: {
    currency
  },
  mixins: [baseCommon],
  props: {
    categories: {
      type: Array,
      require: true,
      default: () => { return [] }
    },
    fillPro: {
      type: Number,
      require: true,
      default: () => { return [-1] }
    },
    searchPro: {
      type: String,
      require: true,
      default: ''
    },
    reload: {
      type: Boolean,
      require: false,
      default: false
    }
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      tableData: [],
      multipleSelection: []
    }
  },
  watch: {
    fillPro: function() {
      this.pagination.pageNumber = 1
      this.init()
    },
    reload: function() {
      if (this.reload) {
        this.pagination.pageNumber = 1
        this.init()
      }
    },
    searchPro: function() {
      this.showLoading()
      const params = {
        page: 0,
        size: this.pagination.pageSize,
        keyWord: this.searchPro || '',
        sort: 'createDate,desc'
      }
      searchProduct(params).then(res => {
        this.tableData = res.data.result
        // console.log(this.tableData)
        this.pagination.totalItems = res.data.totalItems
      }).finally(() => {
        this.hideLoading()
      })
    }
  },
  created() {
    this.init()
  },
  methods: {
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.init()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.init()
    },
    deleteDetail(data) {
      console.log(data)
      this.$swal({
        title: 'Bạn có chắc chắn muốn xóa sản phẩm này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          deleteProductDetail(data).then(res => {
            this.messSuccess(res.message)
          }).finally(() => {
            this.init()
          })
        }
      })
    },
    deleteProduct(data) {
      this.$swal({
        title: 'Bạn có chắc chắn muốn xóa sản phẩm này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          deleteProduct([data.id]).then(res => {
            this.messSuccess(res.message)
          }).finally(() => {
            this.init()
          })
        }
      })
    },
    editProduct(data) {
      console.log(data)
      this.$router.push(`/product-admin/update-product/${data.id}`)
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
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
        cid: this.fillPro
      }
      if (this.fillPro !== -1) {
        getListProductByCategory(params).then(res => {
          this.tableData = res.data.result
          // console.log(this.tableData)
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      } else {
        getListProduct(params).then(res => {
          this.tableData = res.data.result
          // console.log(this.tableData)
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      }
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.quantity <= 50) {
        return 'error-row'
      } else if (row.quantity <= 100) {
        return 'warning-row'
      }
      // if (rowIndex === 1) {
      //   return 'warning-row'
      // } else if (rowIndex === 3) {
      //   return 'success-row'
      // }
      return ''
    }
  }
}
</script>

<style>
/* .el-table .success-row {
    background: #0493f9;
  } */
.text-ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
.el-table .error-row {
  background: red;
}
</style>
