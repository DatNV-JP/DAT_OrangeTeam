<template>
  <el-form>
    <ValidationObserver v-slot="{invalid}">
      <div>
        <div>
          <pan-thumb :image="urlViewImg+'/avatar/'+image" />
        </div>
        <el-button type="primary" icon="el-icon-upload" @click="imagecropperShow=true">
          Cập Nhật Avatar
        </el-button>
        <image-cropper
          v-show="imagecropperShow"
          :key="imagecropperKey"
          :width="300"
          :height="300"
          url="files/upload-avatar"
          lang-type="en"
          @close="closeCr"
          @crop-upload-success="cropSuccess"
        />
      </div>
      <div class="row">
        <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required">
          <el-form>
            <el-form-item class="col-sm-6" label="Họ" :error="messageError('Họ', errors[0])">
              <el-input v-model.trim="user.firstName" />
            </el-form-item>
          </el-form>
        </ValidationProvider>

        <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required">
          <el-form>
            <el-form-item class="col-sm-6" label="Tên" :error="messageError('Tên', errors[0])">
              <el-input v-model.trim="user.lastName" />
            </el-form-item>
          </el-form>
        </ValidationProvider>
      </div>
      <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|email">
        <el-form>
          <el-form-item :error="messageError('Email', errors[0])">
            <span class="demo-input-label mb-3">Email</span>
            <el-input v-model="email" placeholder="Email" />
          </el-form-item>
        </el-form>
      </ValidationProvider>
      <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|phone">
        <el-form>
          <el-form-item :error="messageError('Số điện thoại', errors[0])">
            <span class="demo-input-label mb-3">Số điện thoại</span>
            <el-input v-model="user.phone" placeholder="Số điện thoại" />
          </el-form-item>
        </el-form>
      </ValidationProvider>
      <el-form-item>
        <el-button type="primary" :disabled="invalid" @click="submit">Update</el-button>
      </el-form-item>
    </ValidationObserver>
  </el-form>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import ImageCropper from '@/components/ImageCropper'
import PanThumb from '@/components/PanThumb'
import { updateUserProfile } from '@/api/user'
import { getInfo } from '@/api/user'
import baseCommon from '@/utils/base-common'
export default {
  components: { ImageCropper, PanThumb, ValidationObserver, ValidationProvider },
  mixins: [BaseValidate, baseCommon],
  props: {
    user: {
      type: Object,
      require: true,
      default: null
    }
  },
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL,
      imagecropperShow: false,
      imagecropperKey: 0,
      image: '',
      email: ''
    }
  },
  watch: {
    user(val) {
      this.image = val.avatar
      this.email = val.email
    }
  },
  methods: {
    submit() {
      this.showLoading()
      updateUserProfile({
        username: this.user.username,
        phone: this.user.phone,
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.email,
        avatar: this.image === '' ? this.user.avatar : this.image
      }).then(res => {
        if (res.code === 420) {
          this.$swal({
            title: 'Thông Báo',
            text: res.message,
            icon: 'warning',
            showConfirmButton: true,
            timer: 1000 })
          return
        }
        getInfo().then(res => {
          this.user.avatar = res.data.avatar
          this.$store.dispatch('user/getInfo')
          this.$swal({
            title: 'Thông Báo',
            text: 'Thành Công',
            icon: 'success',
            showConfirmButton: false,
            timer: 1000 })
        })
      }).finally(() => {
        this.hideLoading()
      })
    },
    closeCr() {
      this.imagecropperShow = false
    },
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      this.image = resData
    }
  }
}
</script>
