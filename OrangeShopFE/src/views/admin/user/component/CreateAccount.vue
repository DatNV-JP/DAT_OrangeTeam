<template>
  <div class="d-flex justify-content-center mb-5 mt-3">
    <div>
      <ValidationObserver v-slot="{invalid}">
        <div>
          <div>
            <pan-thumb :image="urlViewImage+'/avatar/'+image" />
          </div>
          <el-button type="primary" icon="el-icon-upload" @click="imagecropperShow=true">
            Tải Lên Avatar
          </el-button>
          <image-cropper
            v-show="imagecropperShow"
            :key="imagecropperKey"
            :width="300"
            :height="300"
            url="files/upload-avatar"
            lang-type="en"
            @crop-upload-success="cropSuccess"
            @close="closeCr"
          /></div>
        <div>
          <div class="row mb-3">
            <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required">
              <el-form>
                <el-form-item :error="messageError('Họ', errors[0])">
                  <span class="demo-input-label mb-3">Họ</span>
                  <el-input v-model="firstName" placeholder="Họ" />
                </el-form-item>
              </el-form>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required">
              <el-form>
                <el-form-item :error="messageError('Tên', errors[0])">
                  <span class="demo-input-label mb-3">Tên</span>
                  <el-input v-model="lastName" placeholder="Tên" />
                </el-form-item>
              </el-form>
            </ValidationProvider>
          </div>
          <ValidationProvider v-slot="{ errors }" class="row mb-3" rules="required">
            <el-form>
              <el-form-item :error="messageError('Tài khoản', errors[0])">
                <span class="demo-input-label mb-3">Tài khoản</span>
                <el-input v-model="userName" placeholder="Tài khoản" />
              </el-form-item>
            </el-form>
          </ValidationProvider>
          <div class="row mb-3">
            <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|phone">
              <el-form>
                <el-form-item :error="messageError('Số điện thoại', errors[0])">
                  <span class="demo-input-label mb-3">Số điện thoại</span>
                  <el-input v-model="phoneNumber" placeholder="Số điện thoại" />
                </el-form-item>
              </el-form>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|email">
              <el-form>
                <el-form-item :error="messageError('Email', errors[0])">
                  <span class="demo-input-label mb-3">Email</span>
                  <el-input v-model="emailUser" placeholder="Email" />
                </el-form-item>
              </el-form>
            </ValidationProvider>
          </div>
          <div>
            <el-form class="row">
              <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|password:@confirm">
                <el-form-item :error="messageError('Mật Khẩu Mới', errors[0])">
                  <div>
                    <span class="demo-input-label">Mật Khẩu Mới</span>
                    <el-input v-model="newPass" placeholder="Vui lòng nhập mật khẩu mới" show-password />
                  </div>
                </el-form-item>
              </ValidationProvider>
              <ValidationProvider v-slot="{ errors }" class="col-sm-6" name="confirm" rules="required">
                <el-form-item :error="messageError('Mật khẩu xác nhận', errors[0])">
                  <div>
                    <span class="demo-input-label">Nhập Lại Mật Khẩu</span>
                    <el-input v-model="newPassRepeat" placeholder="Vui lòng nhập lại mật khẩu mới" show-password />
                  </div>
                </el-form-item>
              </ValidationProvider>
            </el-form>
            <div class="my-3">
              <el-radio v-model="role" :label="1">Quản trị</el-radio>
              <el-radio v-model="role" :label="2">Nhân viên</el-radio>
              <el-radio v-model="role" :label="3">Người dùng</el-radio>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer mt-3">
          <el-button type="primary" :disabled="invalid" @click="createAccount">Đăng Ký</el-button>
        </span>
      </ValidationObserver>
    </div>
  </div>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import ImageCropper from '@/components/ImageCropper'
import PanThumb from '@/components/PanThumb'
import baseCommon from '@/utils/base-common'
import { createAccount } from '@/api/user'
export default {
  name: 'CreateAccount',
  components: { ValidationObserver, ValidationProvider, ImageCropper, PanThumb },
  mixins: [baseCommon, BaseValidate],
  data() {
    return {
      imagecropperShow: false,
      imagecropperKey: 0,
      image: '',
      role: 2,
      newPass: '',
      newPassRepeat: '',
      firstName: '',
      lastName: '',
      userName: '',
      phoneNumber: '',
      emailUser: '',
      urlViewImage: process.env.VUE_APP_UPLOAD_URL
    }
  },
  created() {
    this.clearForm()
  },
  methods: {
    closeCr() {
      this.imagecropperShow = false
    },
    clearForm() {
      this.userName = ''
      this.newPass = ''
      this.newPassRepeat = ''
      this.firstName = ''
      this.lastName = ''
      this.emailUser = ''
      this.phoneNumber = ''
      this.image = ''
    },
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      this.image = resData
    },
    createAccount() {
      this.showLoading()
      const data = {
        username: this.userName.trim(),
        passwordHash: this.newPass.trim(),
        email: this.emailUser.trim(),
        phone: this.phoneNumber.trim(),
        firstName: this.firstName.trim(),
        lastName: this.lastName.trim(),
        avatar: this.image.trim(),
        roles: [
          {
            id: this.role
          }
        ]
      }
      createAccount(data)
        .then(res => {
          this.$swal({
            title: 'Thông Báo',
            text: 'Thành Công',
            icon: 'success',
            showConfirmButton: false,
            timer: 1000 })
          this.clearForm()
        }).catch(err => {
          console.log(err)
        }).finally(() => {
          this.hideLoading()
        })
    }
  }
}
</script>

<style scoped>

</style>
