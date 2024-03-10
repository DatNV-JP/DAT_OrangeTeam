<template>
  <div class="components-container">
    <ValidationObserver v-slot="{ invalid, handleSubmit }">
      <el-form ref="productForm" :model="productForm" label-position="left" label-width="15%">
        <div class="editor-container pb-3">
          <dropzone id="myVueDropzone" :remove-file-all="removeAllFile" :url="uploadUrl+'/upload-files'" :remove-file-name="removeFileName" @dropzone-removedFile="dropzoneR" @dropzone-successmultiple="dropzoneS" />
          <span>Số lượng ảnh đã tải lên {{ listDefaultImg.length }}/5</span>
        </div>
        <el-card class="mb-3">
          <div slot="header">
            <strong>Thông tin cơ bản</strong>
          </div>
          <ValidationProvider v-slot="{ errors }" rules="required|min:10">
            <el-form-item :error="messageError('Tên sản phẩm', errors[0])">
              <template v-slot:label><label><span class="svg-container">Tên sản phẩm<span
                class="text-danger"
              > *</span></span></label></template>
              <el-input v-model="productForm.name" />
            </el-form-item>
          </ValidationProvider>
          <ValidationProvider v-slot="{ errors }" rules="requiredSelect">
            <el-form-item :error="messageError('Danh mục', errors[0])">
              <template v-slot:label><label><span class="svg-container">Danh mục<span
                class="text-danger"
              > *</span></span></label></template>
              <el-select
                v-model="productForm.categoryId"
                class="w-100"
                placeholder="Chọn danh mục"
                @change="getVariation"
              >
                <el-option
                  v-for="(item, index) in categoriesNoLevel"
                  :key="index"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </ValidationProvider>
          <ValidationProvider v-slot="{ errors }" rules="min:20|max:3000">
            <el-form-item :error="messageError('Mô tả sản phẩm', errors[0])">
              <template v-slot:label><label><span class="svg-container">Mô tả sản phẩm</span></label></template>
              <el-input
                v-model="productForm.description"
                type="textarea"
                :rows="6"
                resize="none"
                maxlength="3000"
                show-word-limit
              />
            </el-form-item>
          </ValidationProvider>
        </el-card>
        <el-card class="mb-3">
          <div slot="header">
            <strong>Phân loại hàng</strong>
          </div>
          <template v-if="!productForm.categoryId">
            <h3>Bạn có thể chọn phân loại hàng sau khi hoàn thành thông tin cơ bản của sản phẩm</h3>
          </template>
          <template v-if="classifyVisible">
            <el-card shadow="hover">
              <ValidationProvider v-slot="{ errors }" :rules="'required|uniqueValues:'+[listVariation[0].name, listVariation[1].name]">
                <el-form-item :error="messageError('Nhóm phân loại', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Nhóm phân loại 1</span></label></template>
                  <el-input v-model="listVariation[0].name" :readonly="isAddVariation" placeholder="Color/ Size" />
                </el-form-item>
              </ValidationProvider>
              <div v-for="(input, index) in inputs" :key="index">
                <ValidationProvider v-slot="{ errors }" :rules="(index!==inputs.length-1 || index === 0)?'required|uniqueValues:'+inputs:'|uniqueValues:'+inputs">
                  <el-form-item :error="messageError('Phân loại hàng ', errors[0])">
                    <template v-slot:label>
                      <label><span class="svg-container">Tên phân loại {{ index + 1 }}</span></label>
                    </template>
                    <el-input v-model="inputs[index]" placeholder="Blue/ XL" @input="addInput(index)">
                      <template v-slot:append>
                        <el-button v-if="inputs.length > 1 && (inputs.length - 1) !== index" type="danger" icon="el-icon-delete" circle @click="deleteInput(index)" />
                      </template>
                    </el-input>
                  </el-form-item>
                </ValidationProvider>
              </div>
            </el-card>
            <el-card shadow="hover" class="mt-3">
              <ValidationProvider v-slot="{ errors }" :rules="'required|uniqueValues:'+[listVariation[0].name, listVariation[1].name]">
                <el-form-item :error="messageError('Nhóm phân loại', errors[0])">
                  <template v-slot:label><label><span class="svg-container">Nhóm phân loại 2</span></label></template>
                  <el-input v-model="listVariation[1].name" :readonly="isAddVariation" placeholder="Color/ Size" />
                </el-form-item>
              </ValidationProvider>
              <div v-for="(input, index) in inputs1" :key="index">
                <ValidationProvider v-slot="{ errors }" :rules="(index!==inputs1.length-1 || index === 0)?'required|uniqueValues:'+inputs1:'|uniqueValues:'+inputs1">
                  <el-form-item :error="messageError('Phân loại hàng ', errors[0])">
                    <template v-slot:label>
                      <label><span class="svg-container">Tên phân loại {{ index + 1 }}</span></label>
                    </template>
                    <el-input v-model="inputs1[index]" placeholder="Blue/ XL" @input="addInput1(index)">
                      <template v-slot:append>
                        <el-button v-if="inputs1.length > 1 && (inputs1.length - 1) !== index" type="danger" icon="el-icon-delete" circle @click="deleteInput1(index)" />
                      </template>
                    </el-input>
                  </el-form-item>
                </ValidationProvider>
              </div>
            </el-card>
          </template>
        </el-card>
        <template v-if="classifyVisible">
          <el-card class="mb-3">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="Số lượng:" label-width="42%">
                  <el-input-number
                    v-model="quantity"
                    :min="0"
                    placeholder="Số lượng"
                    class="w-100"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="Giá nhập:" label-width="42%">
                  <el-input-number
                    v-model="priceInput"
                    :min="0"
                    placeholder="Giá nhập"
                    class="w-100"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="Giá bán:" label-width="42%">
                  <el-input-number
                    v-model="priceDefault"
                    :min="0"
                    placeholder="Giá bán"
                    class="w-100"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-button class="w-100" :disabled="(priceInput===0 && quantity===0 && priceDefault===0) || tableDataArr.length<1" @click="fillAll">Áp dụng cho tất cả</el-button>
              </el-col>
            </el-row>
            <el-table
              :data="tableDataArr"
              style="width: 100%"
              border
            >
              <el-table-column
                label="Hình ảnh"
                width="90"
              >
                <template v-slot="scope">
                  <el-upload
                    class="avatar-uploader"
                    :action="uploadUrl+'/upload/productDetailImages'"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess(scope.row)"
                    accept="image/**"
                    :before-upload="beforeAvatarUpload"
                  >
                    <img v-if="scope.row.img" :src="scope.row.img" class="avatar" alt="">
                    <i v-if="!scope.row.img" class="el-icon-plus avatar-uploader-icon" />
                  </el-upload>
                  <el-button v-if="scope.row.img" size="mini" class="w-100" type="danger" icon="el-icon-delete" @click="deleteImage(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column width="180" align="left" label="Thuộc tính">
                <template v-slot="scope">
                  <span v-for="(va, index) in scope.row.variationOptions" :key="index" class="d-block">{{ va.variation.name + ': ' }} <strong>{{ va.value }}</strong> </span>
                </template>
              </el-table-column>
              <el-table-column
                label="Số lượng"
              >
                <template v-slot="{row}">
                  <ValidationProvider v-slot="{ errors }" rules="required|integer|minValue:1|maxValue:99999">
                    <el-input
                      v-model="row.quantity"
                      class="w-100"
                    />
                    <span class="text-danger">{{ messageError('Số lượng', errors[0]) }}</span>
                  </ValidationProvider>
                </template>
              </el-table-column>
              <el-table-column
                label="Giá nhập"
              >
                <template v-slot="{row}">
                  <ValidationProvider v-slot="{ errors }" rules="required|double|minValue:10000|maxValue:9999999">
                    <el-input
                      v-model="row.priceInput"
                      class="w-100"
                    />
                    <span class="text-danger">{{ messageError('Giá nhập', errors[0]) }}</span>
                  </ValidationProvider>
                </template>
              </el-table-column>
              <el-table-column
                label="Giá bán"
              >
                <template v-slot="scope">
                  <ValidationProvider v-slot="{ errors }" rules="required|double|minValue:10000|maxValue:9999999">
                    <el-input
                      v-model="scope.row.priceDefault"
                      class="w-100"
                      @change="changePrice(scope.$index, scope.row)"
                    />
                    <span class="text-danger">{{ messageError('Giá bán', errors[0]) }}</span>
                  </ValidationProvider>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </template>
      </el-form>
      <el-button
        icon="el-icon-position"
        type="primary"
        plain
        style="text-transform: uppercase;"
        :disabled="invalid || listDefaultImg.length < 1"
        @click="handleSubmit(addProduct)"
      >Xác nhận
      </el-button>
    </ValidationObserver>
  </div>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import baseCommon from '@/utils/base-common'
import horizontalScroll from 'el-table-horizontal-scroll'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import Dropzone from '@/components/Dropzone'
import { deleteFileUpload } from '@/api/files-upload'
import { mapGetters } from 'vuex'
import { getListVariation } from '@/api/variation'
import { currency } from '@/filters'
import { addProduct } from '@/api/product'

export default {
  name: 'AddProduct',
  directives: {
    horizontalScroll
  },
  filters: { currency },
  components: { ValidationObserver, ValidationProvider, Dropzone },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      deleteUrl: process.env.VUE_APP_UPLOAD_URL,
      removeAllFile: false,
      tempFile: '',
      imageUrl: '',
      quantity: '',
      priceDefault: '',
      priceInput: '',
      listDefaultImg: [],
      removeFileName: '',
      productForm: {
        name: '',
        categoryId: '',
        defaultImage: '',
        description: '',
        productDetails: [],
        status: true
      },
      categoriesNoLevel: [],
      inputs: [''],
      inputs1: [''],
      listVariation: [],
      isAddVariation: true,
      classifyVisible: false,
      tableDataArr: []
    }
  },
  computed: {
    ...mapGetters(['categories'])
  },
  watch: {
    inputs: {
      deep: true,
      handler() {
        this.tableData()
      }
    },
    inputs1: {
      deep: true,
      handler() {
        this.tableData()
      }
    }
  },
  created() {
    this.getAllCategoriesNoLevel(this.categories)
  },
  beforeRouteLeave(to, from, next) {
    this.$swal({
      title: 'Bạn có chắc chắn?',
      text: 'Chưa hoàn thành chình sửa, bạn có muốn rời khỏi!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      cancelButtonText: 'Không',
      confirmButtonText: 'Chắc chắn'
    }).then((result) => {
      if (result.isConfirmed) {
        window.removeEventListener('beforeunload', this.confirmExit)
        next()
      } else {
        next(false)
      }
    })
  },
  mounted() {
    window.addEventListener('beforeunload', this.confirmExit)
  },
  beforeUnmount() {
    window.removeEventListener('beforeunload', this.confirmExit)
  },
  methods: {
    changePrice(index, data) {
      this.tableDataArr[index].maxSale = data.priceDefault
      this.tableDataArr[index].priceSale = data.priceDefault
    },
    confirmExit(event) {
      // Hiển thị thông báo xác nhận
      event.preventDefault()
      event.returnValue = ''
    },
    deleteImage(data) {
      deleteFileUpload('productDetailImages', data.images).then((res) => {
        data.images = ''
        data.img = ''
        // console.log(this.listDefaultImg)
        // this.$message({ message: 'Delete success', type: 'success' })
      })
    },
    handleAvatarSuccess(row) {
      return (response, file, fileList) => {
        // Lưu đường dẫn ảnh vào trường image của hàng tương ứng
        row.images = response.data
        row.img = URL.createObjectURL(file.raw)
      }
      // this.imageUrl = URL.createObjectURL(file.raw)
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 6

      if (!isJPG) {
        this.$message.error('Avatar picture must be JPG format!')
      }
      if (!isLt2M) {
        this.$message.error('Avatar picture size can not exceed 6MB!')
      }
      return isJPG && isLt2M
    },
    addProduct() {
      this.$swal({
        title: 'Bạn có chắc chắn?',
        text: 'Bạn có chắc chắn thêm sản phẩm này!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Chưa',
        confirmButtonText: 'Chắc chắn'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          this.productForm.productDetails = this.tableDataArr
          this.productForm.defaultImage = this.listDefaultImg.join(',')
          // console.log(this.productForm)
          addProduct(this.productForm).then(res => {
            this.listDefaultImg = []
            this.removeAllFile = true
            this.quantity = ''
            this.priceDefault = ''
            this.priceInput = ''
            this.productForm = {
              name: '',
              categoryId: '',
              defaultImage: '',
              description: '',
              productDetails: []
            }
            this.classifyVisible = false
            this.inputs = ['']
            this.inputs1 = ['']
            this.listVariation = []
            this.tableDataArr = []
            this.$swal({
              title: 'Đã tạo sản phẩm!',
              message: res.message,
              icon: 'success',
              showConfirmButton: false,
              timer: 1500,
              timerProgressBar: true
            })
            console.log(res)
          }).catch(err => {
            console.log(err.response)
          }).finally(() => {
            this.hideLoading()
          })
        }
      })
    },
    fillAll() {
      if (this.quantity !== 0) {
        // eslint-disable-next-line no-return-assign
        for (let i = 0; i < this.tableDataArr.length; i++) {
          const pro = this.tableDataArr[i]
          pro.quantity = this.quantity
        }
      }
      if (this.priceDefault !== 0) {
        // eslint-disable-next-line no-return-assign
        for (let i = 0; i < this.tableDataArr.length; i++) {
          const pro = this.tableDataArr[i]
          pro.priceDefault = this.priceDefault
          pro.maxSale = this.priceDefault
        }
      }
      if (this.priceInput !== 0) {
        // eslint-disable-next-line no-return-assign
        for (let i = 0; i < this.tableDataArr.length; i++) {
          const pro = this.tableDataArr[i]
          pro.priceInput = this.priceInput
        }
      }
    },
    getVariation() {
      getListVariation({ categoryId: this.productForm.categoryId }).then(res => {
        this.listVariation = res.data
        if (this.listVariation.length < 1) {
          this.listVariation = [
            {
              id: null,
              name: '',
              categoryId: this.productForm.categoryId
            },
            {
              id: null,
              name: '',
              categoryId: this.productForm.categoryId
            }
          ]
          this.isAddVariation = false
        } else {
          this.isAddVariation = true
        }
      }).finally(() => {
        this.classifyVisible = true
      })
    },
    deleteInput(index) {
      this.inputs.splice(index, 1)
    },
    addInput(index) {
      if (index === this.inputs.length - 1 && this.inputs[index].trim().length > 0) {
        this.inputs.push('')
      }
    },
    deleteInput1(index) {
      this.inputs1.splice(index, 1)
    },
    addInput1(index) {
      if (index === this.inputs1.length - 1 && this.inputs1[index].trim().length > 0) {
        this.inputs1.push('')
      }
    },
    dropzoneS(file, error, xhr) {
      for (let i = 0; i < error.data.length; i++) {
        file[i].upload.filename = error.data[i]
        this.listDefaultImg.push(error.data[i])
      }
      // console.log(this.listDefaultImg)
      // this.$message({ message: 'Upload success', type: 'success' })
    },
    dropzoneR(file) {
      const imgRemove = this.listDefaultImg.find(img => img === file.upload.filename)
      // console.log(this.listDefaultImg)
      if (imgRemove) {
        deleteFileUpload('productImage', file.upload.filename).then((res) => {
          this.listDefaultImg = this.listDefaultImg.filter(img => img !== imgRemove)
          this.removeFileName = file.name
          // console.log(this.listDefaultImg)
          // this.$message({ message: 'Delete success', type: 'success' })
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
    tableData() {
      const tempTableData = []
      for (let i = 0; i < this.inputs.length - 1; i++) {
        for (let j = 0; j < this.inputs1.length - 1; j++) {
          const data = {
            images: '',
            img: '',
            quantity: '',
            priceInput: '',
            priceDefault: '',
            maxSale: 0,
            priceSale: 0,
            variationOptions: [
              {
                value: this.inputs[i],
                variation: {
                  id: this.listVariation[0].id,
                  categoryId: this.listVariation[0].categoryId,
                  name: this.listVariation[0].name,
                  status: true
                }
              },
              {
                value: this.inputs1[j],
                variation: {
                  id: this.listVariation[1].id,
                  categoryId: this.listVariation[1].categoryId,
                  name: this.listVariation[1].name,
                  status: true
                }
              }
            ]
          }
          tempTableData.push(data)
        }
      }
      const newItems = tempTableData.filter(item => {
        return !this.tableDataArr.some(existingItem => {
          return JSON.stringify(existingItem.variationOptions) === JSON.stringify(item.variationOptions)
        })
      })
      this.tableDataArr = [...this.tableDataArr, ...newItems]

      // Remove items from tableDataArr
      const removeItems = this.tableDataArr.filter(existingItem => {
        return !tempTableData.some(item => {
          return JSON.stringify(existingItem.variationOptions) === JSON.stringify(item.variationOptions)
        })
      })
      if (removeItems.length > 0) {
        for (let i = 0; i < removeItems.length; i++) {
          const data = removeItems[i]
          if (data.images.length > 0) {
            deleteFileUpload('productDetailImages', data.images)
          }
        }
      }
      this.tableDataArr = this.tableDataArr.filter(item => !removeItems.includes(item))
    }
  }
}
</script>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  /*font-size: 2em;*/
  color: #8c939d;
  width: 5em;
  height: 3em;
  /*line-height: 5em;*/
  text-align: center;
}
.avatar {
  width: 100%;
  /*height: 178px;*/
  display: block;
}
</style>
