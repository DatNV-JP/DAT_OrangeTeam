<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />

    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />
      </template>
      <el-dropdown class="errLog-container right-menu-item hover-effect" trigger="click">
        <el-badge :value="getCountNotification" :max="5" style="line-height: 25px;margin-top: -5px;" class="item">
          <i class="fas fa-bell " />
        </el-badge>
        <el-dropdown-menu slot="dropdown" :style="notifications.length > 0 ? 'height: 70%; overflow:auto' : ''">
          <template v-if="notifications.length > 0">
            <div class="clearfix px-3">
              <el-button type="text" @click="readAllNotify">Đọc tất cả</el-button>
              <el-button class="float-right" type="text" @click="deleteAllNotify">Xóa tất cả</el-button>
            </div>
            <el-dropdown-item v-for="(item, index) in notifications" :key="index" divided>
              <div v-if="item.isRead" class="dropdown-item w-100" @click="handleNotify(item)">
                <div> <span class="font-weight-bold">{{ item.title }}</span></div>
                <div> <span class="font-weight-lighter">{{ item.content }}</span></div>
              </div>
              <el-badge v-else is-dot>
                <div class="dropdown-item w-100" @click="handleNotify(item)">
                  <div> <span class="font-weight-bold">{{ item.title }}</span></div>
                  <div> <span class="font-weight-lighter">{{ item.content }}</span></div>
                </div>
              </el-badge>
            </el-dropdown-item>
          </template>
          <template v-else>
            <span>Không có thông báo</span>
          </template>
        </el-dropdown-menu>
      </el-dropdown>
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/profile/index">
            <el-dropdown-item>Profile</el-dropdown-item>
          </router-link>
          <router-link to="/">
            <el-dropdown-item>Dashboard</el-dropdown-item>
          </router-link>
          <el-dropdown-item divided @click.native="logout">
            <span style="display:block;">Log Out</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import Search from '@/components/HeaderSearch'
import { deleteNotification, readAllNotification, readNotification } from '@/api/notification'

export default {
  components: {
    Breadcrumb,
    Hamburger,
    Search
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'notifications',
      'roles',
      'token',
      'username'
    ]),
    getCount() {
      // console.log(this.cart)
      return this.cart.map(item => item.quantity)
        // eslint-disable-next-line no-return-assign
        .reduce((total, quantity) => total += quantity, 0)
    },
    getCountNotification() {
      // console.log(this.cart)
      return this.notifications.reduce((total, notification) => notification.isRead ? total : (total + 1), 0)
    }
  },
  methods: {
    deleteAllNotify() {
      if (this.roles) {
        const role = this.roles.find(r => r === 'admin' || r === 'editor')
        const data = { hashKey: this.username, isDeleteAll: true, ids: [] }
        deleteNotification(data).finally(() => {
          this.chooseRole(this.username, this.roles)
        })
        if (role) {
          data.hashKey = 'private'
          deleteNotification(data).then(res => {
            this.chooseRole(this.username, this.roles)
          })
        }
      }
    },
    readAllNotify() {
      if (this.roles) {
        const role = this.roles.find(r => r === 'admin' || r === 'editor')
        if (role) {
          readAllNotification('private').then(res => {
            this.chooseRole(this.username, this.roles)
          })
        }
        readAllNotification(this.username).then(res => {
          this.chooseRole(this.username, this.roles)
        })
      }
    },
    chooseRole(username, roles) {
      this.$store.dispatch('notifications/getAllNotify', { hashKey1: username, hashKey2: null })
      if (roles) {
        const role = roles.find(r => r === 'admin' || r === 'editor')
        if (role) {
          this.$store.dispatch('notifications/getAllNotify', { hashKey1: username, hashKey2: 'private' })
        }
      }
    },
    handleNotify(notify) {
      notify.isRead = true
      readNotification(notify).then(res => {
        this.chooseRole(this.username, this.roles)
        this.$router.push(notify.url)
      })
    },
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 100%;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 30px;
          height: 30px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
