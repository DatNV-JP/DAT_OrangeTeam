<template>
  <div>
    <template>
      <el-table
        :data="users"
        style="width: 100%"
      >
        <el-table-column
          label="STT"
          type="index"
          :index="indexMethod"
        />
        <el-table-column
          label="username"
          prop="username"
        />
        <el-table-column
          label="Email"
          prop="email"
        />
        <el-table-column
          label="Phone"
          prop="phone"
        />
        <el-table-column
          label="Name"
          prop="lastName"
        />
        <el-table-column label="role">
          <template slot-scope="{row}">
            <div v-for="( role, index ) in row.roles" :key="index">
              <span v-if="role.name === 'ROLE_USER'">Người dùng</span>
              <span v-if="role.name === 'ROLE_ADMIN'">Quản trị</span>
              <span v-if="role.name === 'ROLE_STAFF'">Nhân viên</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          align="right"
        >
          <template v-slot:header>
            <el-input
              v-model="search"
              size="mini"
              placeholder="Tìm kiếm ..."
              @change="onGetByUsername"
            />
          </template>
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="onDialogChangeUser(scope.row)"
            >Edit</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="onDeleteUser(scope.row)"
            >Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
    <div>
      <el-dialog
        title="Sửa thông tin"
        :visible.sync="dialogVisible"
        width="60%"
        :before-close="handleClose"
        top="3vh"
      >
        <div>
          <div class="row">
            <div class="mr-5 col-sm-12 d-flex justify-content-end">
              <img :src="urlViewImg+'/avatar/'+user.avatar" width="60px" height="60px" class="rounded-circle mr-2">
            </div>
            <div class="col-sm-12 d-flex justify-content-end">
              <span class="m-md-3 fw-bold fs-4">{{ user.username }}</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6">
              <span class="demo-input-label mb-3">Họ</span>
              <el-input v-model="user.firstName" readonly />
            </div>
            <div class="col-sm-6">
              <span class="demo-input-label mb-3">Tên</span>
              <el-input v-model="user.lastName" readonly />
            </div>
          </div>
          <div class="row">
            <span class="demo-input-label">Số điện thoại</span>
            <el-input v-model="user.phone" placeholder="Số điện thoại" readonly />
          </div>
          <div class="row">
            <span class="demo-input-label">Email</span>
            <el-input v-model="user.email" placeholder="Email" readonly />
          </div>
          <div class="row mt-3">
            <span class="demo-input-label">Roles</span>
            <template v-if="user.username !== userIsLoginNow.username">
              <el-radio v-model="roleUser" class="m-1" label="ROLE_ADMIN">Quản trị</el-radio>
              <el-radio v-model="roleUser" class="m-1" label="ROLE_USER">Người dùng</el-radio>
              <el-radio v-model="roleUser" class="m-1" label="ROLE_STAFF">Nhân viên</el-radio>
            </template>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="onChangeUpdateUser">Confirm</el-button>
        </span>
      </el-dialog>
    </div>
    <div class="m-3 d-flex justify-content-center">
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
import { getAllUserStatus, deleteByid, adminUpdateUser, getInfo, findUserByUsername } from '@/api/user'
import baseCommon from '@/utils/base-common'
export default {
  name: 'TableUser',
  mixins: [baseCommon],
  data() {
    return {
      urlViewImg: process.env.VUE_APP_UPLOAD_URL,
      search: '',
      roleUser: '',
      users: [],
      userIsLoginNow: {},
      usersForSearch: [],
      dialogVisible: false,
      user: {}
    }
  },
  created() {
    this.getAllUser()
    this.getInforUser()
  },
  methods: {
    onGetByUsername() {
      this.showLoading()
      if (this.search.trim().length === 0) {
        this.getAllUser()
        this.hideLoading()
        return
      }
      findUserByUsername({ keyWord: this.search }).then(res => {
        if (res.data.length === 0) {
          this.$swal({
            title: 'Không có tên user này trong hệ thống',
            icon: 'warning',
            buttons: true,
            timer: 2500
          })
          this.search = ''
          return
        }
        this.users = res.data
      }).finally(() => {
        this.hideLoading()
      })
    },
    handleCurrentChange(val) {
      this.pagination.pageNumber = val
      this.getAllUser()
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.getAllUser()
    },

    getInforUser() {
      getInfo().then(res => {
        this.userIsLoginNow = res.data
      })
    },
    indexMethod(index) {
      return index + 1
    },
    getAllUser() {
      this.showLoading()
      getAllUserStatus({
        status: true,
        page: this.pagination.pageNumber - 1,
        size: this.pagination.pageSize
      }).then(res => {
        this.hideLoading()
        this.users = res.data.result
        this.pagination.totalItems = res.data.totalItems
      })
    },
    onDialogChangeUser(user) {
      this.user = user
      this.roleUser = this.user.roles.at(0).name
      this.dialogVisible = true
    },
    onChangeUpdateUser() {
      this.showLoading()
      const idRole = this.user.roles.at(0).name === this.roleUser ? null : this.roleUser === 'ROLE_ADMIN' ? 1 : this.roleUser === 'ROLE_USER' ? 2 : 3
      const data = {
        username: this.user.username,
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        phone: this.user.phone,
        email: this.user.email,
        roles: [
          {
            id: idRole
          }
        ]
      }
      adminUpdateUser(data).then(() => {
        this.dialogVisible = false
        this.$swal({
          title: 'Nah! Cập nhật thành công',
          icon: 'success',
          timer: 1500
        }).then(() => {
          this.getAllUser()
          this.hideLoading()
        })
      })
    },
    onDeleteUser(user) {
      if (user.username === this.userIsLoginNow.username) {
        this.$swal({
          title: 'Không thể xóa tài khoản đang đăng nhập',
          icon: 'warning',
          timer: 2500
        })
        return null
      }
      this.$swal({
        title: 'Bạn có chắc chắn xoá y?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Không',
        confirmButtonText: 'Có!'
      }).then((result) => {
        if (result.isConfirmed) {
          this.showLoading()
          deleteByid({ id: user.id, status: false }).then(res => {
            if (res.data.activate === false) {
              this.hideLoading()
              this.$swal({
                title: 'Nah! Bạn đã xóa thành công',
                icon: 'success',
                timer: 1500
              }).then(() => {
                this.getAllUser()
              })
            }
          })
        }
      })
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

