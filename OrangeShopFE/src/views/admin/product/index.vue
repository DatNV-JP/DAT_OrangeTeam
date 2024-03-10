<template>
  <el-main>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="title">
            <strong class="float-left">Danh mục</strong>
            <div class="float-right mb-3" @click="dialogCategory = true">
              <el-button size="mini" type="success" icon="el-icon-plus" circle />
            </div>
          </div>
          <div id="listCheckbox" class="checkbox mt-2">
            <el-input
              v-model="searchCate"
              placeholder="Tìm kiếm danh mục"
              prefix-icon="el-icon-search"
              style="border-top-style: hidden;"
            />
            <div class="ml-2 mt-2 menu">
              <el-button size="medium" type="text" class="text-muted pl-2" @click="fillByCate=-1"><strong>Tất cả</strong></el-button>
              <el-tree
                ref="treeCate"
                :data="categories"
                :filter-node-method="filterCate"
                accordion
              >
                <span slot-scope="{ node, data }" class="custom-tree-node">
                  <span @click="fillByCate=node.data.id">{{ node.data.name }}</span>
                  <span>
                    <el-button size="mini" type="text" @click="fillCategory(node, data)">Sửa</el-button>
                    <el-button size="mini" type="text" @click="removeCategory(node, data)">Xóa</el-button>
                  </span>
                </span>
              </el-tree>

            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-row class="pb-3">
          <el-col :span="8">
            <el-input
              v-model="searchPro"
              placeholder="Tìm kiếm sản phẩm"
              prefix-icon="el-icon-search"
              style="border-top-style: hidden;"
            >
              <el-button slot="append" icon="el-icon-search" @click="searchPro1 = searchPro" />
            </el-input>
          </el-col>
          <el-col :span="16">
            <div style="float: right;">
              <el-dropdown trigger="click">
                <el-button type="success">
                  Thêm mới<i class="el-icon-arrow-down el-icon--right" />
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item>
                    <router-link to="/product-admin/add-product" type="text" class="w-100">
                      Thêm sản phẩm
                    </router-link>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <el-button type="danger" class="ml-2" @click="deletePro"><i class="el-icon-delete" /> Xóa</el-button>
            </div>
          </el-col>
        </el-row>
        <Table :categories="categoriesNoLevel" :fill-pro="fillByCate" :search-pro="searchPro1" :reload="reload" @select-list="selectListPro" />
      </el-col>
    </el-row>
    <!-- dialog -->
    <el-dialog
      :visible.sync="dialogCategory"
      width="39%"
      :before-close="handleClose"
    >
      <span slot="title"><strong>Thêm mới danh mục</strong> </span>
      <el-form label-position="left" label-width="30%" :model="formCate">
        <el-form-item label="Tên danh mục:">
          <el-input v-model="formCate.name" />
        </el-form-item>
        <el-form-item label="Danh mục cha:">
          <el-select v-model="formCate.parentCategoryId" placeholder="Chọn danh mục cha" class="w-100">
            <el-option v-for="cate in categoriesNoLevel" :key="cate.id" :label="cate.name" :value="cate.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button style="background-color: #898c8d; color: white;" @click="dialogCategory = false"><i class="el-icon-close" /> Bỏ qua</el-button>
        <el-button v-if="addCate" type="primary" style="background-color: #0dac50;" @click="addCategory"><i class="fas fa-save" /> Thêm</el-button>
        <el-button v-if="!addCate" type="primary" style="background-color: #0dac50;" @click="editCategory"><i class="fas fa-save" /> Sửa</el-button>
      </span>
    </el-dialog>
  </el-main>
</template>

<script>
import Table from './component/table'
import { addCategory, removeCategory, updateCategory } from '@/api/category'
import baseCommon from '@/utils/base-2-common'
import { mapGetters } from 'vuex'
import { deleteProduct } from '@/api/product'
export default {
  name: 'Products',
  components: { Table },
  mixins: [baseCommon],
  data() {
    return {
      searchPro1: '',
      reload: false,
      fillByCate: -1,
      addCate: true,
      checked: '',
      textarea: '',
      searchCate: '',
      selected: '',
      dialogCategory: false,
      dialogProduct: false,
      categoriesNoLevel: [],
      formCate: {
        id: null,
        name: '',
        parentCategoryId: null
      },
      selectPro: [],
      searchPro: ''
    }
  },
  computed: {
    ...mapGetters(['categories'])
  },
  watch: {
    searchCate(val) {
      this.$refs.treeCate.filter(val)
    }
  },
  created() {
    this.getAllCategoriesNoLevel(this.categories)
  },
  methods: {
    addPromotion() {
      if (!this.selectPro || this.selectPro.length < 1) {
        this.$swal({
          title: 'Chưa có sản phẩm nào được chọn?',
          icon: 'warning'
        })
      }
    },
    deletePro() {
      if (!this.selectPro || this.selectPro.length < 1) {
        this.$swal({
          title: 'Chưa có sản phẩm nào được chọn?',
          icon: 'warning'
        })
      } else {
        this.$swal({
          title: 'Bạn có chắc chắn muốn xóa các sản phẩm này?',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33',
          cancelButtonText: 'Không',
          confirmButtonText: 'Có!'
        }).then((result) => {
          if (result.isConfirmed) {
            this.showLoading()
            const proArr = this.selectPro.map(pr => pr.id)
            console.log(proArr)
            deleteProduct(proArr).then(res => {
              this.messSuccess(res.message)
              this.reload = true
            }).finally(() => {
              this.hideLoading()
              this.reload = false
            })
          }
        })
      }
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
    selectListPro(data) {
      this.selectPro = data
    },
    addCategory() {
      this.dialogCategory = false
      this.$swal({
        title: 'Bạn có chắc chắn tạo danh mục này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          addCategory(this.formCate).then(res => {
            this.messSuccess(res.message)
            this.categoriesNoLevel = []
            this.formCate.id = null
            this.formCate.name = ''
            this.formCate.parentCategoryId = null
            this.$store.dispatch('categories/getAllCategory').then(() => {
              this.getAllCategoriesNoLevel(this.categories)
            })
          }).finally(() => {
            this.hideLoading()
          })
        } else {
          this.dialogCategory = true
        }
      })
    },
    filterCate(value, data) {
      if (!value) return true
      return data.name.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')
        .toLowerCase().replace(/[^a-z0-9]/g, '').indexOf(value.toLowerCase()) !== -1
    },
    fillCategory(node, data) {
      this.formCate.id = data.id
      this.formCate.name = data.name
      this.formCate.parentCategoryId = node.parent.data.id
      this.addCate = false
      this.dialogCategory = true
    },
    editCategory() {
      this.dialogCategory = false
      this.$swal({
        title: 'Bạn có chắc chắn muốn sửa danh mục này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          updateCategory(this.formCate).then(res => {
            this.messSuccess(res.message)
            this.categoriesNoLevel = []
            this.formCate.id = null
            this.formCate.name = ''
            this.formCate.parentCategoryId = null
            this.$store.dispatch('categories/getAllCategory').then(() => {
              this.getAllCategoriesNoLevel(this.categories)
            })
          }).finally(() => {
            this.hideLoading()
          })
        } else {
          this.dialogCategory = true
        }
      })
    },
    removeCategory(node, data) {
      this.$swal({
        title: 'Bạn có chắc chắn xoá danh mục này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          if (node.childNodes.length > 0) {
            this.notifyError('Cảnh báo!', 'Không được xoá danh mục đang sử dụng')
          } else {
            this.showLoading()
            removeCategory(data.id).then(res => {
              this.messSuccess(res.message)
              this.$store.dispatch('categories/getAllCategory').then(() => {
                this.getAllCategoriesNoLevel(this.categories)
              })
            }).finally(() => {
              this.hideLoading()
            })
          }
        }
      })
    },
    handleClose(done) {
      this.$confirm('Bạn muốn thoát ra?')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    handleSizeChange(val) {
      console.log(`${val} items per page`)
    },
    handleCurrentChange(val) {
      console.log(`current page: ${val}`)
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
