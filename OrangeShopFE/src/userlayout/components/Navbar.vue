<template>
  <!-- Navbar -->
  <nav class="navbar navbar-expand-lg navbar-light bg-light sticky-top">
    <!-- Container wrapper -->
    <div class="container-fluid">
      <!-- Toggle button -->

      <!-- Collapsible wrapper -->
      <div id="navbarSupportedContent" class="collapse navbar-collapse">
        <!-- Navbar brand -->
        <router-link class="navbar-brand mt-2 mt-lg-0" to="/home">
          <img
            src="@/assets/img/sanvadio.png"
            class="w-25"
            alt="MDB Logo"
            loading="lazy"
          >
        </router-link>
        <!-- Left links -->
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <router-link class="linkCard nav-item" to="/home">
            <span class="nav-link">Trang chủ</span>
          </router-link>
          <li class="nav-item d-flex align-items-center">
            <dropdown class="nav-link" :trigger="`hover`">
              <template slot="btn">Danh mục</template>
              <!--              <template slot="icon"><i class="el-icon-arrow-down el-icon&#45;&#45;right" /></template>-->
              <template slot="body">
                <router-link to="/product" class="text-muted hoverLink py-2 pl-3">Tất cả</router-link>
                <el-tree
                  :data="categories"
                  accordion
                >
                  <span slot-scope="{ node }" class="custom-tree-node">
                    <router-link :to="'/product/category/' + node.data.id" class="text-muted hoverLink w-100"><span>{{ node.data.name }}</span></router-link>
                  </span>
                </el-tree>
              </template>
            </dropdown>
          </li>
          <router-link to="/about-me" class="tnav-item"><span class="nav-link">Về chúng tôi</span></router-link>
          <router-link to="/contact-me" class="tnav-item"><span class="nav-link">Liên Hệ</span></router-link>
          <router-link v-if="dashboardVisible" class="nav-item" to="/dashboard">
            <span class="nav-link">Dashboard</span>
          </router-link>
        </ul>
        <!-- Left links -->
      </div>
      <!-- Collapsible wrapper -->
      <!-- Right elements -->
      <div class="d-flex align-items-center">
        <template v-if="token">
          <!-- Icon -->
          <router-link class="text-reset me-3 ml-1" to="/user/shopping-cart">
            <el-badge :value="getCount" :max="99" class="item">
              <i class="fas fa-shopping-cart" />
            </el-badge>
          </router-link>
          <!-- Notifications -->
          <el-dropdown class="mr-3" trigger="click">
            <el-badge :value="getCountNotification" :max="5" class="item">
              <i class="fas fa-bell" />
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
          <!-- Avatar -->
          <div class="dropdown">
            <a
              id="navbarDropdownMenuAvatar"
              class="dropdown-toggle d-flex align-items-center hidden-arrow"
              href="#"
              role="button"
              data-mdb-toggle="dropdown"
              aria-expanded="false"
            >
              <img
                :src="avatar"
                class="rounded-circle"
                height="25"
                alt="Black and White Portrait of a Man"
                loading="lazy"
              >
            </a>
            <ul
              class="dropdown-menu dropdown-menu-end"
              aria-labelledby="navbarDropdownMenuAvatar"
            >
              <router-link to="/user/profile">
                <span class="dropdown-item">Tài khoản</span>
              </router-link>
              <li>
                <router-link to="/user/order">
                  <span class="dropdown-item">Đơn mua</span>
                </router-link>
              </li>
              <li @click="logout">
                <span class="dropdown-item">Đăng xuất</span>
              </li>
            </ul>
          </div>
        </template>
        <template v-if="!token">
          <router-link class="text-reset ml-2" to="/shopping-cart">
            <i class="fas fa-shopping-cart" />
            <span class="badge rounded-pill badge-notification bg-danger">{{ getCount }}</span>
          </router-link>
          <router-link :to="'/login?redirect='+$route.fullPath" class="mx-2 pl-2">
            Đăng nhập
          </router-link>
        </template>
      </div>
      <!-- Right elements -->
    </div>
    <!-- Container wrapper -->
  </nav>
  <!-- Navbar -->
</template>

<script>
import { mapGetters } from 'vuex'
import Dropdown from 'bp-vuejs-dropdown'
import { deleteNotification, readAllNotification, readNotification } from '@/api/notification'
export default {
  components: { Dropdown },
  data() {
    return {
      items: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'roles',
      'token',
      'cart',
      'categories',
      'notifications',
      'username'
    ]),
    dashboardVisible() {
      return !this.roles.length < 1 && !this.roles.includes('user')
    },
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
      await this.$store.dispatch('cart/getCart')
      const path = this.$route.fullPath
      if (!path.startsWith('/home') && !path.startsWith('/product')) {
        this.$router.push('/home')
      }
    }
  }
}
</script>

<style lang="scss">
.right-menu {
  float: right;
  height: 100%;
  line-height: 30px;

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
      //margin-top: 5px;
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
.linkCard:hover {
  text-decoration: none !important;
}

.el-dropdown {
  font-size: var(--mdb-nav-link-font-size);
}

.bp-dropdown {
  margin-bottom: .5rem;
}

.bp-dropdown__btn {
  border: none;
  width: 100%;
  padding-top: 0;
}

.bp-dropdown__sub {
  border: none;
  width: 100%;
  padding-left: .5em;
}

.hoverLink {
  display: block !important;
  width: 100% !important;
}

.hoverLink:hover {
  color: #16181b;
  text-decoration: none;
  background-color: #f8f9fa;
}
</style>
