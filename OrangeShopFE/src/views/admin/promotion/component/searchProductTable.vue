<template>
  <div>
    <el-row class="pb-3">
      <el-col :span="7">
        <el-input
          v-model="searchPro"
          placeholder="Tìm kiếm"
          prefix-icon="el-icon-search"
          style="border-top-style: hidden;"
        />
      </el-col>
      <el-col :span="17">
        <el-row :gutter="10" type="flex" class="row-bg" justify="end">
          <el-col :span="6">
            <el-badge :value="listPromotionProduct.length" class="item">
              <el-button :disabled="multipleSelection.length < 1" type="danger" @click="handleAddListPro">Thêm vào
                khuyến mãi
              </el-button>
            </el-badge>
          </el-col>
          <el-col :span="4">
            <el-button :disabled="listPromotionProduct.length < 1" type="success" @click="handleClose">
              Hoàn tất
            </el-button>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-row :gutter="10">
      <el-col :span="5">
        <el-card class="box-card">
          <div class="title">
            <strong class="float-left">Danh mục</strong>
          </div>
          <div id="listCheckbox" class="checkbox mt-2">
            <el-input
              v-model="searchCate"
              placeholder="Tìm kiếm danh mục"
              prefix-icon="el-icon-search"
              style="border-top-style: hidden;"
            />
            <div class="ml-2 mt-2 menu">
              <el-button size="medium" type="text" class="text-muted pl-2" @click="fillByCate=-1"><strong>Tất
                cả</strong></el-button>
              <el-tree
                ref="treeCate"
                :data="categories"
                :filter-node-method="filterCate"
                accordion
              >
                <span slot-scope="{ node }" class="custom-tree-node">
                  <span @click="fillByCate=node.data.id">{{ node.data.name }}</span>
                </span>
              </el-tree>

            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="19">
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
            <template v-slot="{ row }">
              {{ categoriesNoLevel.find(cate => cate.id === row.categoryId).name }}
            </template>
          </el-table-column>
          <el-table-column
            label="Mô tả"
          >
            <template v-slot="{ row }">
              <div class="text-ellipsis">{{ row.description }}</div>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
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
import { currency } from '@/filters'
import baseCommon from '@/utils/base-common'
import {
  getListProduct,
  searchProduct
} from '@/api/product'
import { mapGetters } from 'vuex'
import { Message } from 'element-ui'

export default {
  name: 'SearchProductTable',
  filters: {
    currency
  },
  mixins: [baseCommon],
  props: {
    listPromotionProduct: {
      type: Array,
      require: true,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      searchProTimer: null,
      fillByCate: -1,
      searchCate: '',
      categoriesNoLevel: [],
      fillPro: '1',
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      tableData: [],
      multipleSelection: [],
      searchPro: ''
    }
  },
  computed: {
    ...mapGetters(['categories'])
  },
  watch: {
    searchCate(val) {
      this.$refs.treeCate.filter(val)
    },
    fillByCate: function() {
      this.pagination.pageNumber = 1
      this.init()
    },
    searchPro: function() {
      clearTimeout(this.searchProTimer)

      this.searchProTimer = setTimeout(() => {
        this.showLoading()
        const params = {
          page: 0,
          size: this.pagination.pageSize,
          keyWord: this.searchPro || '',
          sort: 'createDate,desc'
        }
        searchProduct(params).then(res => {
          this.tableData = res.data.result
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      }, 2000)
    }
  },
  created() {
    this.init()
    this.getAllCategoriesNoLevel(this.categories)
  },
  methods: {
    handleClose() {
      this.$emit('close-dialog', false)
    },
    filterCate(value, data) {
      if (!value) return true
      return data.name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
        .toLowerCase().replace(/[^a-z0-9]/g, '').indexOf(value.toLowerCase()) !== -1
    },
    getAllCategoriesNoLevel(categories) {
      categories.forEach((category) => {
        this.categoriesNoLevel.push({ id: category.id, name: category.name })

        if (category.children.length > 0) {
          this.getAllCategoriesNoLevel(category.children)
        }
      })

      return this.categoriesNoLevel
    },
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.init()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.init()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      // this.$emit('select-list', val)
    },
    handleAddListPro() {
      const allMatched = this.multipleSelection.every(aItem => this.listPromotionProduct.find(bItem => bItem.id === aItem.id))
      if (allMatched) {
        Message({
          message: 'Bạn đã thêm các sản phẩm này vào khuyến mãi rồi!',
          type: 'error',
          duration: 5 * 1000
        })
      } else {
        this.$emit('add-list', this.multipleSelection)
        Message({
          message: 'Đã thêm sản phẩm vào khuyến mãi!',
          type: 'success',
          duration: 5 * 1000
        })
      }
    },
    init() {
      this.showLoading()
      const params = {
        page: this.pagination.pageNumber - 1,
        size: this.pagination.pageSize,
        categoryId: this.fillByCate < 0 ? null : this.fillByCate
      }
      if (this.fillPro !== -1) {
        searchProduct(params).then(res => {
          this.tableData = res.data.result
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      } else {
        getListProduct(params).then(res => {
          this.tableData = res.data.result
          this.pagination.totalItems = res.data.totalItems
        }).finally(() => {
          this.hideLoading()
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
