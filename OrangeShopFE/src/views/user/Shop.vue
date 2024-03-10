<template>
  <el-main>
    <!-- Page Header Start -->
    <page-header />
    <!-- Page Header End -->
    <!-- Shop Start -->
    <div class="container-fluid pt-5">
      <div class="row">
        <!-- Shop Sidebar Start -->
        <div class="col-lg-3 col-md-12">
          <!-- Price Start -->
          <div class="border-bottom mb-4 pb-4">
            <h5 class="font-weight-semi-bold mb-4">Lọc theo giá</h5>
            <template>
              <div class="block row mb-3">
                <div class="col-sm-12">
                  <el-slider
                    v-model="valueFilterPrice"
                    range
                    :min="0"
                    :max="2000000"
                    :marks="{0:'0', 500000:'500000',1000000:'1000000',1500000:'1500000',2000000:'2000000'}"
                    @change="onFilter"
                  />
                </div>
              </div>
            </template>
          </div>
          <!-- Price End -->

          <!-- Color Start -->
          <div class="border-bottom mb-4 pb-4">
            <h5 class="font-weight-semi-bold mb-4">Filter by Category</h5>
            <form>
              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                <input id="cate-all" v-model="selectedOption" type="radio" class="custom-control-input" name="filterCategory" :value="0" checked @change="onFilter">
                <label class="custom-control-label" for="cate-all">Tất Cả</label>
                <span class="badge border font-weight-normal text-black-50" />
              </div>
              <div v-for="(category,index2) in categorys" :key="index2">
                <div class="custom-checkbox d-flex justify-content-start mb-3">
                  <!--                    <input :id="category.id" type="radio" class="custom-control-input" name="filterCategory">-->
                  <label class="" :for="category.id">{{ category.name }}</label>
                  <span class="badge border font-weight-normal text-black-50" />
                </div>
                <div v-for="(cateChild,index) in category.children" :key="index" class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                  <input :id="cateChild.id" v-model="selectedOption" :value="cateChild.id" type="radio" class="custom-control-input" name="filterCategory" @change="onFilter">
                  <label class="custom-control-label" :for="cateChild.id">{{ cateChild.name }}</label>
                  <span class="badge border font-weight-normal text-black-50" />
                </div>
              </div>
              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                <!--                <span class="btn btn-light" @click="addListCateGory">Xem Thêm</span>-->
              </div>
            </form>
          </div>
          <!-- Color End -->

          <!-- Size Start -->
          <!--          <div class="mb-5">-->
          <!--            <h5 class="font-weight-semi-bold mb-4">Filter by size</h5>-->
          <!--            <form>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">-->
          <!--                <input id="size-all" type="checkbox" class="custom-control-input" checked>-->
          <!--                <label class="custom-control-label" for="size-all">All Size</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">1000</span>-->
          <!--              </div>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">-->
          <!--                <input id="size-1" type="checkbox" class="custom-control-input">-->
          <!--                <label class="custom-control-label" for="size-1">XS</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">150</span>-->
          <!--              </div>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">-->
          <!--                <input id="size-2" type="checkbox" class="custom-control-input">-->
          <!--                <label class="custom-control-label" for="size-2">S</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">295</span>-->
          <!--              </div>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">-->
          <!--                <input id="size-3" type="checkbox" class="custom-control-input">-->
          <!--                <label class="custom-control-label" for="size-3">M</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">246</span>-->
          <!--              </div>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">-->
          <!--                <input id="size-4" type="checkbox" class="custom-control-input">-->
          <!--                <label class="custom-control-label" for="size-4">L</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">145</span>-->
          <!--              </div>-->
          <!--              <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between">-->
          <!--                <input id="size-5" type="checkbox" class="custom-control-input">-->
          <!--                <label class="custom-control-label" for="size-5">XL</label>-->
          <!--                <span class="badge border font-weight-normal text-black-50">168</span>-->
          <!--              </div>-->
          <!--            </form>-->
          <!--          </div>-->
          <!-- Size End -->
        </div>
        <!-- Shop Sidebar End -->

        <!-- Shop Product Start -->
        <div class="col-lg-9 col-md-12">
          <div class="row pb-3">
            <div class="col-12 pb-1">
              <div class="d-flex align-items-center justify-content-between mb-4">
                <form action="">
                  <div class="input-group">
                    <input v-model="nameProduct" type="text" class="form-control" placeholder="Tìm theo tên sản phẩm" @change="onFilter">
                    <div class="input-group-append">
                      <span class="input-group-text bg-transparent text-primary">
                        <i class="fa fa-search" />
                      </span>
                    </div>
                  </div>
                </form>
                <div class="row">
                  <div class="col-sm-12 d-flex justify-content-start mb-1"><span>Sắp xếp</span></div>
                  <div class="dropdown col-sm-12">
                    <el-dropdown trigger="click">
                      <span class="el-dropdown-link">
                        <el-button> {{ nameSort }} <i class="el-icon-arrow-down el-icon--right" /></el-button>
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item><el-button type="text" class="w-100 p-3" @click="onChangeFilter('date')">Ngày đăng</el-button></el-dropdown-item>
                        <el-dropdown-item><el-button type="text" class="w-100 p-3" @click="onChangeFilter('name')">Tên</el-button></el-dropdown-item>
                        <el-dropdown-item><el-button type="text" class="w-100 p-3" @click="onChangeFilter('price')">Giá</el-button></el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </div>
              </div>
            </div>
            <product-card v-for="(product, index) in products" :key="index" :product-card="product" :col="4" />
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
        </div>
        <!-- Shop Product End -->
      </div>
    </div>
    <!-- Shop End -->
  </el-main>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-2-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import userCommon from '@/views/user/mixin/user-mixin'
import PageHeader from '@/views/user/component/PageHeader'
import { mapGetters } from 'vuex'
import ProductCard from '@/views/user/component/ProductCard/index.vue'
import { getListProduct, getListProductByCategory, getProductByFilter } from '@/api/product'
import { getAllCategoryUseParam } from '@/api/category'

export default {
  name: 'Shop',
  directives: {
    horizontalScroll
  },
  components: { PageHeader, ProductCard },
  mixins: [baseCommon, userCommon, BaseValidate],
  data() {
    return {
      valueFilterPrice: [0, 20000000],
      nameProduct: '',
      products: {},
      categorys: {},
      paramSize: 15,
      selectedOption: 0,
      isAsc: false,
      sort: 'name',
      nameSort: 'Tên'
    }
  },
  computed: {
    ...mapGetters([
      'cart'
    ])
  },
  watch: {},
  created() {
    this.getData()
    this.getListCategory({ page: 0, size: this.paramSize })
  },
  methods: {
    onFilter() {
      this.pagination.pageNumber = 1
      this.onChangeFilter(this.sort)
    },
    onChangeFilter(sort) {
      this.sort = (sort === undefined) ? 'name' : sort
      this.nameSort = (this.sort === 'name') ? 'Tên' : (this.sort === 'price') ? 'Giá' : 'Ngày đăng'
      this.showLoading()
      this.isAsc = !this.isAsc
      getProductByFilter({ idCategory: this.selectedOption,
        fromPrice: this.valueFilterPrice.at(0),
        toPrice: this.valueFilterPrice.at(1),
        namePr: this.nameProduct,
        isAsc: this.isAsc,
        sortBy: this.sort
      })
        .then(res => {
          this.products = res.data.result
          this.pagination.totalItems = res.data.totalItems
          this.hideLoading()
          if (this.products.length === 0) {
            this.$swal({
              title: 'Thông Báo',
              text: 'Không có sản phẩm với thông tin bạn muốn tìm',
              icon: 'error',
              showConfirmButton: false,
              timer: 1500 })
          }
        })
    },
    addListCateGory() {
      this.paramSize = this.paramSize + 5
      this.getListCategory({ page: 0, size: this.paramSize })
    },
    getListCategory(param) {
      getAllCategoryUseParam(param).then(res => {
        this.categorys = res.data
      })
    },
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      if (this.selectedOption === 0 &&
        this.valueFilterPrice.at(0) === 0 &&
        this.valueFilterPrice.at(1) === 2000000 &&
        this.nameProduct.trim().length === 0
      ) {
        this.getData()
      } else {
        this.onChangeFilter()
      }
    },
    handleSizeChange(val) {
      this.pagination.pageNumber = val
      if (this.selectedOption === 0 &&
        this.valueFilterPrice.at(0) === 0 &&
        this.valueFilterPrice.at(1) === 2000000 &&
        this.nameProduct.trim().length === 0
      ) {
        this.getData()
      }
      this.onChangeFilter()
    },
    getData() {
      this.showLoading()
      if (this.$route.name === 'Shop by Category') {
        const params = {
          cid: this.$route.params.cid,
          page: this.pagination.pageNumber - 1,
          size: this.pagination.pageSize
        }
        getListProductByCategory(params).then(res => {
          if (res.code === 200) {
            // eslint-disable-next-line no-undef
            if (_.isEmpty(res.data.result[0]) || res.data.result.length === 1) {
              this.products = []
            } else {
              this.products = res.data.result
            }
            this.pagination.totalItems = res.data.totalItems
          }
        }).finally(() => {
          this.hideLoading()
        })
      } else {
        const params = {
          page: this.pagination.pageNumber - 1,
          size: this.pagination.pageSize
        }
        getListProduct(params).then(res => {
          if (res.code === 200) {
            // eslint-disable-next-line no-undef
            if (_.isEmpty(res.data.result[0]) || res.data.result.length === 1) {
              this.products = []
            } else {
              this.products = res.data.result
            }
            this.pagination.totalItems = res.data.totalItems
            this.hideLoading()
          }
        })
      }
    }
  }
}
</script>

<style>
.el-page-header__title {
  margin: auto !important;
}
.breadcrumb-container {
  padding-top: .5rem;
}
.el-carousel, .el-carousel__container {
  width: 100% !important;
  height: 100% !important;
}
.font-weight-medium {
  font-weight: 500;
}
.font-weight-semi-bold {
  font-weight: 600;
}
.text-primary {
  color: #D19C97 !important;
}
.btn-primary {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:hover {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
}

.btn-primary:focus, .btn-primary.focus {
  color: #fff;
  background-color: #c5837c;
  border-color: #c17a74;
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}

.btn-primary.disabled, .btn-primary:disabled {
  color: #212529;
  background-color: #D19C97;
  border-color: #D19C97;
}

.btn-primary:not(:disabled):not(.disabled):active, .btn-primary:not(:disabled):not(.disabled).active,
.show > .btn-primary.dropdown-toggle {
  color: #fff;
  background-color: #c17a74;
  border-color: #bd726b;
}

.btn-primary:not(:disabled):not(.disabled):active:focus, .btn-primary:not(:disabled):not(.disabled).active:focus,
.show > .btn-primary.dropdown-toggle:focus {
  box-shadow: 0 0 0 0.2rem rgba(183, 138, 135, 0.5);
}
.bg-secondary {
  background-color: #EDF1FF !important;
}

a.bg-secondary:hover, a.bg-secondary:focus,
button.bg-secondary:hover,
button.bg-secondary:focus {
  background-color: #bac9ff !important;
}

@media (prefers-reduced-motion: reduce) {
  .form-control {
    transition: none;
  }
}

.form-control::-ms-expand {
  background-color: transparent;
  border: 0;
}

.form-control:-moz-focusring {
  color: transparent;
  text-shadow: 0 0 0 #495057;
}

.form-control:focus {
  color: #495057;
  background-color: #fff;
  border-color: #f8f0ef;
  outline: 0;
  box-shadow: none;
}

.form-control::placeholder {
  color: #999999;
  opacity: 1;
}

.form-control:disabled, .form-control[readonly] {
  background-color: #e9ecef;
  opacity: 1;
}

input[type="date"].form-control,
input[type="time"].form-control,
input[type="datetime-local"].form-control,
input[type="month"].form-control {
  appearance: none;
}
.el-radio__input.is-checked + .el-radio__label, .el-tabs__item.is-active {
  color: #bd726b;
}
.el-radio__input.is-checked .el-radio__inner {
  border-color: #bd726b;
  background: #bd726b;
}
.el-checkbox__label {
  font-size: 1.5em;
}
label {
   margin-bottom: 0;
}
.el-form--label-top .el-form-item__label {
  padding-bottom:0;
}
</style>
