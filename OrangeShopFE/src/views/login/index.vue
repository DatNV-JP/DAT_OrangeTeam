<template>
  <div class="h-100">
    <div class="login-container">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on" label-position="left">
        <div class="title-container">
          <h3 class="title">Thông tin đăng nhập</h3>
        </div>
        <el-form-item prop="username">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="username"
            v-model="loginForm.username"
            placeholder="Username"
            name="username"
            type="text"
            tabindex="1"
            autocomplete="on"
          />
        </el-form-item>

        <el-tooltip v-model="capsTooltip" content="Caps lock is On" placement="right" manual>
          <el-form-item prop="password">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="passwordType"
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="Password"
              name="password"
              tabindex="2"
              autocomplete="on"
              @keyup.native="checkCapslock"
              @blur="capsTooltip = false"
              @keyup.enter.native="handleLogin"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
        </el-tooltip>

        <el-button type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">Đăng nhập</el-button>

        <div style="position:relative">
          <div class="tips">
            <el-button type="text" @click="dialogVisible = true;usernameForgot=''">Quên Mật Khẩu</el-button>
          </div>
          <div class="tips">
            <el-button type="text" @click="dialogVisibleCreateAcc = true; userName='', newPass='', newPassRepeat='', firstName='', lastName='', emailUser='', phoneNumber='', image = ''">Đăng Ký</el-button>
          </div>
        </div>
      </el-form>
    </div>
    <div>
      <el-dialog
        title="Quên Mật Khẩu"
        :visible.sync="dialogVisible"
        width="30%"
        :before-close="handleClose"
      >
        <ValidationProvider v-slot="{ invalid, errors }" rules="required">
          <el-form>
            <el-form-item :error="messageError('Username', errors[0])">
              <el-input v-model="usernameForgot" placeholder="Vui lòng nhập username" />
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">Hủy</el-button>
            <el-button type="primary" :disabled="invalid" @click="confirmForgotPassword">Xác Nhận</el-button>
          </span>
        </ValidationProvider>
      </el-dialog>

      <el-dialog
        title="Xác Nhận OTP"
        :visible.sync="dialogVisibleOTP"
        width="30%"
        :before-close="handleClose"
      >
        <ValidationProvider v-slot="{ invalid, errors }" rules="required|regex:^\d{8}$">
          <el-form>
            <el-form-item :error="messageError('OTP', errors[0])">
              <el-input v-model="myOTP" placeholder="Vui lòng nhập mã OTP" />
            </el-form-item>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" :disabled="countdown > 0" @click="confirmForgotPassword">{{ btnTextOTP }}</el-button>
            <el-button type="primary" :disabled="invalid" @click="checkOTP">Xác Nhận</el-button>
          </span>
        </ValidationProvider>
      </el-dialog>
      <el-dialog
        title="Cập Nhật Mật Khẩu"
        :visible.sync="dialogVisibleChangePass"
        width="30%"
        :before-close="handleClose"
      >
        <ValidationObserver v-slot="{invalid}">
          <el-form>
            <ValidationProvider v-slot="{ errors }" name="confirm" rules="required|regexPass|max:25">
              <el-form-item :error="messageError('Mật Khẩu Mới', errors[0])">
                <div class="mb-1">
                  <span class="demo-input-label">Mật Khẩu Mới</span>
                  <el-input v-model="newPass" class="mt-3" placeholder="Vui lòng nhập mật khẩu mới" show-password />
                </div>
              </el-form-item>
            </ValidationProvider>
            <ValidationProvider v-slot="{ errors }" rules="required|password:@confirm">
              <el-form-item :error="messageError('Mật khẩu xác nhận', errors[0])">
                <div>
                  <span class="demo-input-label">Nhập Lại Mật Khẩu</span>
                  <el-input v-model="newPassRepeat" class="mt-3" placeholder="Vui lòng nhập lại mật khẩu mới" show-password />
                </div>
              </el-form-item>
            </ValidationProvider>
          </el-form>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisibleChangePass = false">Hủy</el-button>
            <el-button type="primary" :disabled="invalid" @click="changeNewPassWord">Xác Nhận</el-button>
          </span>
        </ValidationObserver>
      </el-dialog>
    </div>
    <div>
      <el-dialog
        title="Đăng Ký"
        :visible.sync="dialogVisibleCreateAcc"
        width="55%"
        :before-close="handleClose"
      >
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
              @close="closeCr"
              @crop-upload-success="cropSuccess"
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
                <ValidationProvider v-slot="{ errors }" class="col-sm-6" name="confirm" rules="required|regexPass|max:25">
                  <el-form-item :error="messageError('Mật Khẩu', errors[0])">
                    <div>
                      <span class="demo-input-label">Mật Khẩu Mới</span>
                      <el-input v-model="newPass" placeholder="Vui lòng nhập mật khẩu mới" show-password />
                    </div>
                  </el-form-item>
                </ValidationProvider>
                <ValidationProvider v-slot="{ errors }" class="col-sm-6" rules="required|password:@confirm">
                  <el-form-item :error="messageError('Mật khẩu xác nhận', errors[0])">
                    <div>
                      <span class="demo-input-label">Nhập Lại Mật Khẩu</span>
                      <el-input v-model="newPassRepeat" placeholder="Vui lòng nhập lại mật khẩu mới" show-password />
                    </div>
                  </el-form-item>
                </ValidationProvider>
              </el-form>
            </div>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisibleCreateAcc = false">Hủy</el-button>
            <el-button type="primary" :disabled="invalid" @click="createAccount">Đăng Ký</el-button>
          </span>
        </ValidationObserver>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import BaseValidate from '@/utils/BaseValidate'
import { getOTPCode, confirmOTP, changePass, createAccount } from '@/api/user'
import baseCommon from '@/utils/base-common'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import ImageCropper from '@/components/ImageCropper'
import PanThumb from '@/components/PanThumb'
export default {
  name: 'Login',
  components: { ValidationObserver, ValidationProvider, ImageCropper, PanThumb },
  mixins: [baseCommon, BaseValidate],
  data() {
    // const validateUsername = (rule, value, callback) => {
    //   if (!validUsername(value)) {
    //     callback(new Error('Please enter the correct user name'))
    //   } else {
    //     callback()
    //   }
    // }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 3) {
        callback(new Error('The password can not be less than 6 digits'))
      } else {
        callback()
      }
    }
    return {
      imagecropperShow: false,
      imagecropperKey: 0,
      image: '',
      dialogVisible: false,
      dialogVisibleOTP: false,
      dialogVisibleChangePass: false,
      dialogVisibleCreateAcc: false,
      usernameForgot: '',
      myOTP: '',
      btnTextOTP: '0',
      countdown: 0,
      newPass: '',
      newPassRepeat: '',
      firstName: '',
      lastName: '',
      userName: '',
      phoneNumber: '',
      emailUser: '',
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur' }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      passwordType: 'password',
      capsTooltip: false,
      showDialog: false,
      showDialog1: false,
      redirect: undefined,
      otherQuery: {},
      urlViewImage: process.env.VUE_APP_UPLOAD_URL
    }
  },
  computed: {
    currentUser() {
      return this.$store.state.user
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  created() {
    // window.addEventListener('storage', this.afterQRScan)
  },
  mounted() {
    if (this.loginForm.username === '') {
      this.$refs.username.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.password.focus()
    }
  },
  destroyed() {
    // window.removeEventListener('storage', this.afterQRScan)
  },
  methods: {
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      this.image = resData
    },
    closeCr() {
      this.imagecropperShow = false
    },
    createAccount() {
      this.showLoading()
      createAccount({ username: this.userName.trim(), passwordHash: this.newPass.trim(), email: this.emailUser.trim(), phone: this.phoneNumber.trim(), firstName: this.firstName.trim(), lastName: this.lastName.trim(), avatar: this.image.trim() })
        .then(res => {
          this.dialogVisibleCreateAcc = false
          if (res.code === 420) {
            this.$swal({
              title: 'Thông Báo',
              text: res.message,
              icon: 'error',
              showConfirmButton: false,
              timer: 1500 }).then(() => {
              this.dialogVisibleCreateAcc = true
            })
            return
          }
          this.$swal({
            title: 'Thông Báo',
            text: 'Thành Công',
            icon: 'success',
            showConfirmButton: false,
            timer: 1000 })
        }).catch(() => {
          this.dialogVisibleCreateAcc = false
        }).finally(() => {
          this.hideLoading()
        })
    },
    handleClose(done) {
      this.$confirm('Bạn muốn thoát ra?')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    confirmForgotPassword() {
      this.dialogVisible = false
      this.showLoading()
      getOTPCode({ userName: this.usernameForgot.trim() }).then(() => {
        this.myOTP = ''
        this.dialogVisibleOTP = true
        this.countdown = 60
        const countdownInterval = setInterval(() => {
          this.countdown--
          this.btnTextOTP = this.countdown
          if (this.countdown <= 0) {
            this.btnTextOTP = 'Gửi Lại'
            clearInterval(countdownInterval)
          }
        }, 1000)
      }).catch(() => {
        this.$swal({
          title: 'Cảnh Báo',
          text: 'Tài Khoản Sai',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33' }).then(() => {
          this.dialogVisible = true
        })
      }).finally(() => {
        this.hideLoading()
      })
    },
    checkOTP() {
      this.dialogVisibleOTP = false
      this.showLoading()
      confirmOTP({ userName: this.usernameForgot.trim(), OTP: this.myOTP.trim() }).then(res => {
        if (res.data === true) {
          this.dialogVisibleChangePass = true
          this.newPass = ''
          this.newPassRepeat = ''
          this.countdown = 0
        } else {
          this.$swal({
            title: 'Lỗi',
            text: 'Mã OTP Của Bạn Đã Nhập Sai',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33' }).then(() => {
            this.dialogVisibleOTP = true
          })
        }
      }).finally(() => {
        this.hideLoading()
      })
    },
    changeNewPassWord() {
      if (this.newPass.trim() !== this.newPassRepeat.trim()) {
        this.$swal({
          title: 'Lỗi',
          text: 'Mật Khẩu Không Giống Nhau , Vui Lòng Kiểm Tra Lại',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33' }).then(() => {
          this.dialogVisibleChangePass = true
        })
      } else {
        changePass({ username: this.usernameForgot, passwordHash: this.newPass }).then(() => {
          this.dialogVisibleChangePass = false
          this.$swal({
            title: 'Thông Báo',
            text: 'Thành Công',
            icon: 'success',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33' })
        })
      }
    },
    checkCapslock(e) {
      const { key } = e
      this.capsTooltip = key && key.length === 1 && (key >= 'A' && key <= 'Z')
    },
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.showLoading()
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: this.redirect || '/', query: this.otherQuery })
              this.hideLoading()
            })
            .catch(() => {
              this.hideLoading()
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    }
    // afterQRScan() {
    //   if (e.key === 'x-admin-oauth-code') {
    //     const code = getQueryObject(e.newValue)
    //     const codeMap = {
    //       wechat: 'code',
    //       tencent: 'code'
    //     }
    //     const type = codeMap[this.auth_type]
    //     const codeName = code[type]
    //     if (codeName) {
    //       this.$store.dispatch('LoginByThirdparty', codeName).then(() => {
    //         this.$router.push({ path: this.redirect || '/' })
    //       })
    //     } else {
    //       alert('第三方登录失败')
    //     }
    //   }
    // }
  }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }

  .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }

  @media only screen and (max-width: 470px) {
    .thirdparty-button {
      display: none;
    }
  }
}
</style>
