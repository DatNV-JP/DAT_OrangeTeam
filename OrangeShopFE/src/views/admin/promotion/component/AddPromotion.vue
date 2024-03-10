<template>
  <div>
    <ValidationObserver v-slot="{ invalid, handleSubmit }">
      <el-form ref="ruleForm" :model="promotionForm" label-width="36%" label-position="left">
        <div class="shadow-sm">
          <el-row :gutter="20" class=" p-3">
            <el-col :span="12">
              <ValidationProvider v-slot="{ errors }" rules="required|min:3">
                <el-form-item :error="messageError('Tên chương trình khuyến mại', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Tên chương trình<span
                    class="text-danger"
                  > *</span></span></label></template>
                  <el-input v-model="promotionForm.name" />
                </el-form-item>
              </ValidationProvider>
              <ValidationProvider v-slot="{ errors }" rules="required|min:1">
                <el-form-item :error="messageError('Giảm giá', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Giảm giá<span
                    class="text-danger"
                  > *</span></span></label></template>
                  <el-row type="flex">
                    <el-col :span="16">
                      <el-input-number
                        v-model="promotionForm.discount"
                        :step="promotionForm.isPercent ? 5 : 1000"
                        :min="1"
                        :max="promotionForm.isPercent ? 100 : 9999999999999"
                      />
                    </el-col>
                    <el-col :span="8" justify="end">
                      <el-radio-group
                        v-model="promotionForm.isPercent"
                        @change="() => {promotionForm.isPercent ? promotionForm.discount = 30 : promotionForm.discount = 1000}"
                      >
                        <el-radio v-model="promotionForm.isPercent" :label="true">%</el-radio>
                        <el-radio v-model="promotionForm.isPercent" :label="false">VNĐ</el-radio>
                      </el-radio-group>
                    </el-col>
                  </el-row>
                </el-form-item>
              </ValidationProvider>
              <ValidationProvider v-slot="{ errors }" rules="min:20|max:3000">
                <el-form-item :error="messageError('Mô tả', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Mô tả</span></label></template>
                  <el-input
                    v-model="promotionForm.description"
                    type="textarea"
                    :rows="5"
                    maxlength="3000"
                    show-word-limit
                  />
                </el-form-item>
              </ValidationProvider>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Khuyến mãi theo" prop="isDate">
                <el-radio-group v-model="promotionForm.isDate">
                  <el-radio v-model="promotionForm.isDate" :label="true">Ngày</el-radio>
                  <el-radio v-model="promotionForm.isDate" :label="false">Giờ</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <template v-slot:label><label><span class="svg-container">Thời gian hoạt động<span
                  class="text-danger"
                > *</span></span></label></template>
                <ValidationProvider v-if="promotionForm.isDate" v-slot="{ errors }" rules="required">
                  <el-form-item :error="messageError('Thời gian hoạt động', errors[0])">
                    <el-date-picker
                      v-model="dateRange"
                      style="width: 100%"
                      type="daterange"
                      align="right"
                      start-placeholder="Bắt đầu"
                      end-placeholder="Kết thúc"
                      :picker-options="{ disabledDate: (time) => disabledEndDateRange(time) }"
                      @change="handleChangeDateRange"
                    />
                  </el-form-item>
                </ValidationProvider>
                <div v-if="!promotionForm.isDate">
                  <ValidationProvider v-slot="{ errors }" rules="required">
                    <el-form-item :error="messageError('Ngày', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Chọn ngày<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-date-picker
                        v-model="date"
                        type="date"
                        style="width: 65%"
                        placeholder="Chọn ngày"
                        :picker-options="{ disabledDate: (time) => disabledEndDate(time) }"
                        @change="handleChangeDate"
                      />
                    </el-form-item>
                  </ValidationProvider>
                  <div class="m-3" />
                  <ValidationProvider v-if="date" v-slot="{ errors }" rules="required|timeRangeRule|nowHoursRule">
                    <el-form-item :error="messageError('Giờ', errors[0])">
                      <template v-slot:label><label><span class="svg-container">Chọn giờ<span
                        class="text-danger"
                      > *</span></span></label></template>
                      <el-time-picker
                        v-model="timeRange"
                        style="width: 65%"
                        is-range
                        range-separator="-"
                        start-placeholder="Giờ bắt đầu"
                        end-placeholder="Giờ kết thúc"
                        value-format="HH"
                        :picker-options="{ step: '01:00', format: 'HH' }"
                      />
                    </el-form-item>
                  </ValidationProvider>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class=" p-3 mb-3">
            <div class="float-right">
              <el-button
                type="primary"
                :disabled="productDTOS.length < 1 || invalid || maxSaleInvalid"
                @click.prevent="handleSubmit(addPromotion)"
              >Tạo khuyến mại
              </el-button>
              <el-button @click="resetForm('ruleForm')">Nhập lại</el-button>
            </div>
          </el-row>
        </div>
      </el-form>
      <!-- Add product for promotion-->
      <el-row>
        <el-row>
          <el-col :span="8">
            <el-row type="flex" style="margin-bottom: 5px">
              <el-button type="success" @click="dialogVisible = true">Thêm sản phẩm khuyến mãi<i
                class="el-icon-plus el-icon--right"
              /></el-button>
              <el-button
                :hidden="multipleSelection.length < 1"
                type="danger"
                @click="removeSelectedPro"
              >Xóa tất cả<i
                class="el-icon-delete el-icon--right"
              /></el-button>
            </el-row>
          </el-col>
          <el-col :span="16">
            <el-row align="middle" :gutter="20">
              <el-col :span="10">
                <ValidationProvider v-slot="{ errors }" rules="required|integer">
                  <el-row align="middle">
                    <el-row align="middle" type="flex">
                      <label style="margin-right: 0.5rem">Giảm giá tối đa: </label>
                      <el-input-number v-model="maxSale" :min="1" :step="1000" />
                    </el-row>
                    <span class="text-danger">{{ messageError('Giá nhập', errors[0]) }}</span>
                  </el-row>
                </ValidationProvider>
              </el-col>
              <el-col :span="6">
                <el-row align="middle" type="flex">
                  <label style="margin-right: 0.5rem; margin-top: 3px">Giảm giá thông minh: </label>
                  <el-switch v-model="isSmartSale">
                    hi
                  </el-switch>
                </el-row>
              </el-col>
              <el-col :span="4">
                <el-button type="primary" :disabled="checkSettingMaxSale || productDTOS.length <= 0" @click="setMaxSaleAll">
                  Áp dụng cho tất cả
                </el-button>
              </el-col>
            </el-row>
            <el-row v-if="isSmartSale" align="middle" class="mt-2 mb-2">
              <el-col :span="6">
                <el-select v-model="smartSaleType" placeholder="Chọn kiểu">
                  <el-option
                    v-for="item in optionSmartSale"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-col>
              <el-col v-if="smartSaleType !== 3" :span="6">
                <ValidationProvider v-slot="{ errors }" rules="required|integer|min:1">
                  <el-row align="middle">
                    <el-input-number v-model="smartSalePrice" :min="1" :step="1000" />
                    <div class="text-danger">{{ messageError('Giá', errors[0]) }}</div>
                  </el-row>
                </ValidationProvider>
              </el-col>
              <el-col v-if="smartSaleType === 3" :span="16">
                <el-row :gutter="60" type="flex">
                  <el-col :span="10">
                    <ValidationProvider v-slot="{ errors }" rules="required|integer|min:1">
                      <el-row align="middle">
                        <el-row align="middle" type="flex">
                          <label style="margin-right: 0.5rem">Từ: </label>
                          <el-input-number v-model="smartSalePriceFrom" :min="1" :step="1000" />
                        </el-row>
                        <span class="text-danger">{{ messageError('Giá từ', errors[0]) }}</span>
                      </el-row>
                    </ValidationProvider>
                  </el-col>
                  <el-col :span="10">
                    <ValidationProvider v-slot="{ errors }" rules="required|integer|min:1">
                      <el-row align="middle">
                        <el-row align="middle" type="flex">
                          <label style="margin-right: 0.5rem">Đến: </label>
                          <el-input-number v-model="smartSalePriceTo" :min="1" :step="1000" />
                        </el-row>
                        <span class="text-danger">{{ messageError('Giá đến', errors[0]) }}</span>
                      </el-row>
                    </ValidationProvider>
                  </el-col>
                </el-row>
                <span class="text-danger"> {{ errorPriceSmartSaleRange }} </span>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
        <div>
          <el-table
            ref="multipleTable"
            :data="paginatedData"
            border
            style="width: 100%"
            default-expand-all
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
                      <span
                        v-for="(va, index) in scope.row.variationOptions"
                        :key="index"
                        class="d-block"
                      >
                        {{ va.variation.name + ': ' }} <strong>{{ va.value }}</strong> </span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="quantity"
                    label="Số lượng"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    label="Giá gốc"
                  >
                    <template v-slot="scope">
                      {{ scope.row.priceDefault | currency('VND', 0, 'đ', '.', ',') }}
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="Giá sau khi giảm"
                  >
                    <template v-slot="scope">
                      <div
                        :class="getPriceAfterSale(scope.row.priceDefault, scope.row.maxSale) <= 0 ? 'text-danger' : ''"
                      >
                        {{
                          getPriceAfterSale(scope.row.priceDefault, scope.row.maxSale) | currency('VND', 0, 'đ', '.', ',')
                        }}
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="Giảm giá tối đa"
                  >
                    <template v-slot="{row}">
                      <ValidationProvider v-slot="{ errors }" rules="required|integer|min:1">
                        <el-input
                          v-model="row.maxSale"
                          type="number"
                          :min="1"
                          class="w-100"
                        />
                        <span class="text-danger">{{ messageError('Giảm tối đa', errors[0]) }}</span>
                      </ValidationProvider>
                    </template>
                  </el-table-column>
                  <el-table-column
                    label="Thao tác"
                    width="90"
                  >
                    <template v-slot="scope">
                      <el-button type="danger" @click="removeDetailRow(scope.row)">Xóa</el-button>
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
                {{
                  categoriesNoLevel.find(cate => cate.id === row.categoryId).name
                }}
              </template>
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
                <el-tooltip class="item" content="Xoá" placement="top">
                  <el-button type="danger" icon="el-icon-delete" @click="removeRow(row)" />
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>
          <div class="col-12 pb-1">
            <el-pagination
              :current-page.sync="currentPage"
              :page-size="pageSize"
              layout="sizes,prev, pager, next, total"
              :total="productDTOS.length"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </el-row>
    </ValidationObserver>
    <el-dialog
      title="Chọn sản phẩm thêm khuyến mãi"
      :visible.sync="dialogVisible"
      :before-close="handleClose"
      width="85%"
      top="5vh"
      :destroy-on-close="true"
    >
      <AddListPromotionProduct
        :list-promotion-product="productDTOS"
        @close-dialog="closeDialog"
        @add-list="addSelectListPro"
      />
    </el-dialog>
  </div>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import { extend, ValidationObserver, ValidationProvider } from 'vee-validate'
import { currency, formatDateTime } from '@/filters'
import baseCommon from '@/utils/base-common'
import AddListPromotionProduct from './searchProductTable.vue'
import { addAndOverwritePromotion, addPromotion, checkPromotionExit } from '@/api/promotion'
import moment from 'moment-timezone'
import { mapGetters } from 'vuex'

export default {
  name: 'AddPromotion',
  filters: { currency, formatDateTime },
  components: { ValidationObserver, ValidationProvider, AddListPromotionProduct },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      smartSalePrice: 1000,
      smartSalePriceFrom: 1000,
      smartSalePriceTo: 2000,
      smartSaleType: 1,
      optionSmartSale: [{
        value: 1,
        label: 'Giá lớn hơn'
      }, {
        value: 2,
        label: 'Giá nhỏ hơn'
      }, {
        value: 3,
        label: 'Giá từ -> đến'
      }],
      isSmartSale: false,
      maxSaleInvalid: true,
      categoriesNoLevel: [],
      exitedPromotion: [],
      isAcceptOverwrite: false,
      dialogVisible: false,
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      multipleSelection: [],
      currentPage: 1,
      pageSize: 10,
      dateRange: '',
      date: '',
      timeRange: ['', ''],
      productDTOS: [],
      maxSale: 1000,
      promotionForm: {
        name: '',
        description: '',
        startDate: null,
        endDate: null,
        discount: 10,
        status: true,
        isPercent: true,
        isDate: true,
        productDetailDTOs: []
      }
    }
  },
  computed: {
    ...mapGetters(['categories']),
    paginatedData() {
      const startIndex = (this.currentPage - 1) * this.pageSize
      const endIndex = startIndex + this.pageSize
      return this.productDTOS.slice(startIndex, endIndex)
    },
    checkSettingMaxSale() {
      let disabled = false
      if (!this.maxSale || this.maxSale < 0) {
        disabled = true
      }
      if (this.isSmartSale) {
        if (this.smartSaleType === 3) {
          if (!this.smartSalePriceFrom || !this.smartSalePriceTo) {
            disabled = true
          }
          if ((this.smartSalePriceTo - this.smartSalePriceFrom) < 1000) {
            disabled = true
          }
        } else {
          if (!this.smartSalePrice || this.smartSalePrice < 0) {
            return true
          }
        }
      }
      return disabled
    },
    errorPriceSmartSaleRange() {
      return this.smartSalePriceTo - this.smartSalePriceFrom >= 1000 ? '' : 'Giá từ phải lớn hơn giá đến ít nhất 1.000đ'
    }
  },
  created() {
    this.getAllCategoriesNoLevel(this.categories)
  },
  mounted() {
    // Mở rộng quy tắc kiểm tra hợp lệ
    extend('timeRangeRule', {
      validate: (value) => {
        // Tùy chỉnh quy tắc kiểm tra hợp lệ
        const startHours = Number(value[0])
        const endHours = Number(value[1])
        return startHours < endHours // Trả về kết quả kiểm tra
      },
      message: ' bắt đầu phải trước giờ kết thúc ít nhất 1h' // Chuỗi thông báo lỗi
    })
    extend('nowHoursRule', {
      validate: (value) => {
        // Tùy chỉnh quy tắc kiểm tra hợp lệ
        const choiceDate = moment(this.date).format('YYYY-MM-DD')
        const startHour = Number(value[0])
        const startDate = moment(moment(`${choiceDate} ${startHour}:00:00`).format('YYYY-MM-DD HH:mm:ss'))
        const now = moment(moment(new Date()).format('YYYY-MM-DD HH:mm:ss'))
        return startDate.isAfter(now) // Trả về kết quả kiểm tra
      },
      message: ' bắt đầu phải sau giờ hiện tại' // Chuỗi thông báo lỗi
    })
  },
  methods: {
    setMaxSaleAll() {
      this.showLoading()
      let maxSaleInvalid = false
      for (let i = 0; i < this.productDTOS.length; i++) {
        const productDetails = this.productDTOS[i].productDetails
        for (let j = 0; j < productDetails.length; j++) {
          if (this.isSmartSale) {
            if (this.smartSaleType === 3) {
              if (productDetails[j].priceDefault > this.smartSalePriceFrom && productDetails[j].priceDefault < this.smartSalePriceTo) {
                if (!this.promotionForm.isPercent && productDetails[j].priceDefault < this.maxSale) {
                  maxSaleInvalid = true
                }
                productDetails[j].maxSale = this.maxSale
              }
            } else if (this.smartSaleType === 2) {
              if (productDetails[j].priceDefault <= this.smartSalePrice) {
                if (!this.promotionForm.isPercent && productDetails[j].priceDefault < this.maxSale) {
                  maxSaleInvalid = true
                }
                productDetails[j].maxSale = this.maxSale
              }
            } else if (this.smartSaleType === 1) {
              if (productDetails[j].priceDefault >= this.smartSalePrice) {
                if (!this.promotionForm.isPercent && productDetails[j].priceDefault < this.maxSale) {
                  maxSaleInvalid = true
                }
                productDetails[j].maxSale = this.maxSale
              }
            }
          } else {
            if (!this.promotionForm.isPercent && productDetails[j].priceDefault < this.maxSale) {
              maxSaleInvalid = true
            }
            productDetails[j].maxSale = this.maxSale
          }
        }
      }
      if (maxSaleInvalid) {
        this.$swal({
          title: 'Giảm giá tối đa không hợp lệ!',
          text: 'Một số sản phẩm đã giảm giá vượt quá giá bán!',
          icon: 'error'
        })
        this.hideLoading()
        return
      } else {
        this.$swal({
          title: 'Áp dụng thành công!',
          icon: 'success',
          timer: 1500,
          timerProgressBar: true
        })
      }
      this.maxSaleInvalid = maxSaleInvalid
      this.hideLoading()
    },
    getPriceAfterSale(price, maxSale) {
      var discount = 10
      var priceAfterSale = price
      if (this.promotionForm.isPercent) {
        discount = price * (this.promotionForm.discount / 100)
      } else {
        discount = this.promotionForm.discount
      }
      if (discount > maxSale) {
        priceAfterSale = price - maxSale
      } else {
        priceAfterSale = price - discount
      }
      return priceAfterSale
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
    closeDialog(visible) {
      this.dialogVisible = visible
    },
    addSelectListPro(data) {
      const mergedArray = [...this.productDTOS, ...data]
      // Loại bỏ các phần tử trùng lặp dựa trên trường "id"
      this.productDTOS = mergedArray.reduce((acc, item) => {
        if (!acc.some(obj => obj.id === item.id)) {
          acc.push(item)
        }
        return acc
      }, [])
    },
    handlePageChange(newPage) {
      this.currentPage = newPage
    },
    formatTimePromotion() {
      if (this.promotionForm.isDate) {
        this.promotionForm.startDate = moment(this.dateRange[0]).format('YYYY-MM-DD HH')
        this.promotionForm.endDate = moment(this.dateRange[1]).format('YYYY-MM-DD HH')
      } else {
        const choiceDate = moment(this.date).format('YYYY-MM-DD')
        const startHour = this.timeRange[0]
        const endHour = this.timeRange[1]
        const startDate = moment(`${choiceDate} ${startHour}`).format('YYYY-MM-DD HH')
        const endDate = moment(`${choiceDate} ${endHour}`).format('YYYY-MM-DD HH')
        this.promotionForm.startDate = startDate
        this.promotionForm.endDate = endDate
      }
    },
    resetDataAfterCreated() {
      this.currentPage = 1
      this.pageSize = 10
      this.dateRange = ''
      this.date = ''
      this.timeRange = ['09', '10']
      this.promotionForm.name = ''
      this.promotionForm.description = ''
      this.promotionForm.startDate = null
      this.promotionForm.endDate = null
      this.promotionForm.discount = 10
      this.promotionForm.maxSale = 1000
      this.promotionForm.status = true
      this.promotionForm.isPercent = true
      this.promotionForm.isDate = true
      this.promotionForm.productDetailDTOs = []
      this.productDTOS = []
    },
    addPromotion() {
      console.log('qư3')
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn thêm chương trình khuyễn mãi này!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Chưa',
        confirmButtonText: 'Chắc chắn'
      }).then((result) => {
        /* Begin If comfirm to create promotion */
        if (result.isConfirmed) {
          this.showLoading()
          this.formatTimePromotion()
          // this.promotionForm.productDetailDTOs = this.productDTOS.reduce((acc, cur) => {
          //   return [...acc, ...cur.items]
          // }, [])
          const arrProductDetail = []
          for (let i = 0; i < this.productDTOS.length; i++) {
            const productDetails = this.productDTOS[i].productDetails
            for (let j = 0; j < productDetails.length; j++) {
              arrProductDetail.push(productDetails[j])
            }
          }
          this.promotionForm.productDetailDTOs = arrProductDetail
          console.log(this.promotionForm)
          /* Begin check promotion */
          checkPromotionExit(this.promotionForm).then(res => {
            /* Begin if promotion is valid */
            if (res.code === 200) {
              addPromotion(this.promotionForm).then(res => {
                this.$swal({
                  title: 'Đã tạo khuyến mãi!',
                  message: res.message,
                  icon: 'success',
                  showConfirmButton: false,
                  timer: 1500,
                  timerProgressBar: true
                })
                this.resetDataAfterCreated()
              })
                .catch(error => {
                  console.log(error)
                })
                .finally(() => {
                  this.hideLoading()
                })
            } else if (res.code === 208) {
              this.$swal({
                title: 'Bạn có chắc chắn?',
                text: 'Đã có trương trình khuyến mãi được áp dụng trong thời gian này, bạn có muốn ghi đè không?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                cancelButtonText: 'Hủy',
                confirmButtonText: 'Ghi đè'
              }).then((result) => {
                if (result.isConfirmed) {
                  this.showLoading()
                  addAndOverwritePromotion(this.promotionForm).then(res => {
                    this.$swal({
                      title: 'Đã tạo khuyến mãi!',
                      message: res.message,
                      icon: 'success',
                      showConfirmButton: false,
                      timer: 1500,
                      timerProgressBar: true
                    })
                    this.resetDataAfterCreated()
                  })
                    .catch(error => {
                      console.log(error)
                    })
                    .finally(() => {
                      this.hideLoading()
                    })
                }
              })
            }
            this.hideLoading()
            /* End if promotion is valid */
          })
            .catch(error => {
              console.log(error)
            })
            .finally(() => {
              this.hideLoading()
            })
        }
        /* End If comfirm to create promotion */
      })
    },
    handleChangeDateRange() {
      console.log(this.dateRange)
    },
    handleChangeDate() {
      this.timeRange = [this.timeRange[0], this.timeRange[1]]
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
      this.promotionForm.name = ''
      this.promotionForm.isDate = true
      this.promotionForm.isPercent = true
      this.dateRange = ''
      this.promotionForm.startDate = ''
      this.promotionForm.endDate = ''
      this.productDTOS = []
      this.promotionForm.productDetailDTOs = []
    },
    disabledEndDateRange(date) {
      // If !departureDate then return valid dates after today
      // var now = new Date()
      // var previousDate = now.setDate(now.getDate() - 1)
      return date.getTime() < new Date()
    },
    disabledEndDate(date) {
      // If !departureDate then return valid dates after today
      var now = new Date()
      var previousDate = now.setDate(now.getDate() - 1)
      return date.getTime() < previousDate
    },
    handleClose(done) {
      this.$confirm('Bạn muốn thoát ra?')
        .then(_ => {
          done()
        })
        .catch(_ => {
        })
    },
    removeRow(row) {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Xóa nhóm sản phẩm này khỏi khuyến mãi?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Xóa'
      }).then((result) => {
        if (result.isConfirmed) {
          this.productDTOS = this.productDTOS.filter(aItem => aItem.id !== row.id)
        }
      })
    },
    removeSelectedPro() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Xóa tất cả các sản phẩm đã chọn ra khỏi khuyến mãi này?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Xóa'
      }).then((result) => {
        if (result.isConfirmed) {
          this.productDTOS = this.productDTOS.filter(aItem => !this.multipleSelection.some(bItem => aItem.id === bItem.id))
        }
      })
    },
    removeDetailRow(data) {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Xóa sản phẩm này khỏi khuyến mãi?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Xóa'
      }).then((result) => {
        if (result.isConfirmed) {
          this.productDTOS = this.productDTOS
            .map((product) => {
              return ({
                ...product,
                productDetails: product.productDetails.filter((detail) => detail.id !== data.id)
              })
            })
            .filter(product => product.productDetails.length > 0)
        }
      })
    }
  }
}
</script>

<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
