import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'
import UserLayout from '@/userlayout/index'

/* Router Modules */
// import componentsRouter from './modules/components'
// import chartsRouter from './modules/charts'
import promotionRouter from '@/router/modules/promotion'
import orderRouter from '@/router/modules/order'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true }
      }
    ]
  },
  {
    path: '/',
    component: UserLayout,
    redirect: '/home',
    hidden: true,
    children: [
      {
        path: '/home',
        component: () => import('@/views/user/Home'),
        name: 'Home',
        meta: { title: 'Home', noCache: true }
      },
      {
        path: 'shopping-cart',
        component: () => import('@/views/user/ShoppingCartAnymouse'),
        name: 'Cart',
        meta: { title: 'Shopping Cart', icon: 'shopping', noCache: true }
      }
    ]
  },
  {
    path: '/product',
    component: UserLayout,
    redirect: '/product/our-product',
    name: 'Product',
    meta: { title: 'Product', noCache: true },
    hidden: true,
    children: [
      {
        path: 'view-detail/:id',
        component: () => import('@/views/user/DetailProduct'),
        name: 'Detail',
        meta: { title: 'Detail', noCache: true }
      },
      {
        path: 'our-product',
        component: () => import('@/views/user/Shop'),
        name: 'Our Shop',
        meta: { title: 'Our Shop', icon: 'money', noCache: true }
      },
      {
        path: 'category/:cid',
        component: () => import('@/views/user/Shop'),
        name: 'Shop by Category',
        meta: { title: 'Our Shop', noCache: true }
      }
    ]
  },
  {
    path: '/about-me',
    component: () => import('@/views/about-me/index'),
    meta: { title: 'Thông tin về Sanvadio', noCache: true },
    hidden: true
  },
  {
    path: '/contact-me',
    component: () => import('@/views/about-me/contact-me'),
    meta: { title: 'Liên hệ Sanvadio', noCache: true },
    hidden: true
  },
  {
    path: '/user',
    component: UserLayout,
    redirect: 'profile',
    hidden: true,
    meta: { title: 'User', icon: 'user', noCache: true },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/profile/index'),
        name: 'User Profile',
        meta: { title: 'User Profile', icon: 'user', noCache: true }
      },
      {
        path: 'shopping-cart',
        component: () => import('@/views/user/ShoppingCart'),
        name: 'Shopping Cart',
        meta: { title: 'Shopping Cart', icon: 'shopping', noCache: true }
      },
      {
        path: 'tracking-page/:id',
        component: () => import('@/views/user/TrackingPage'),
        name: 'Tracking page',
        meta: { title: 'Tracking page', noCache: true }
      },
      {
        path: 'order',
        component: () => import('@/views/user/Order'),
        name: 'order',
        meta: { title: 'Your Order', noCache: true }
      }
    ]
  },
  {
    path: '/after-payment',
    component: () => import('@/views/user/AfterPayment'),
    hidden: true
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/admin',
    component: Layout,
    redirect: '/dashboard',
    meta: { roles: ['admin', 'editor'] },
    children: [
      {
        path: '/dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: 'Dashboard', icon: 'dashboard', affix: true }
      }
    ]
  },
  orderRouter,
  {
    path: '/product-admin',
    component: Layout,
    redirect: 'product-management',
    name: 'Sản phẩm',
    meta: {
      title: 'Sản phẩm',
      icon: 'component',
      roles: ['admin', 'editor']
    },
    children: [
      {
        path: 'product-management',
        name: 'ProductAdmin',
        component: () => import('@/views/admin/product/index'),
        meta: {
          icon: 'list',
          title: 'Quản lý sản phẩm'
        }
      },
      {
        path: 'add-product',
        name: 'AddProduct',
        component: () => import('@/views/admin/product/component/AddProduct'),
        meta: {
          icon: 'theme',
          title: 'Thêm mới sản phẩm'
        }
      },
      {
        path: 'update-product/:id',
        name: 'UpdateProduct',
        component: () => import('@/views/admin/product/component/UpdateProduct'),
        meta: {
          icon: 'theme',
          title: 'Cập nhật sản phẩm'
        },
        hidden: true
      }
    ]
  },
  promotionRouter,
  {
    path: '/user-manage',
    component: Layout,
    name: 'Người dùng',
    redirect: '/user-manage/manager',
    meta: { roles: ['admin'], icon: 'peoples', title: 'Người dùng' },
    children: [
      {
        path: 'manager',
        component: () => import('@/views/admin/user/index.vue'),
        name: 'Quản lý người dùng',
        meta: { title: 'Quản lý người dùng', icon: 'list', affix: true }
      },
      {
        path: 'create-account',
        component: () => import('@/views/admin/user/component/CreateAccount.vue'),
        name: 'Tạo Tài Khoản',
        meta: { title: 'Tạo Tài Khoản', icon: 'edit', affix: true }
      }
    ]
  },
  /** when your routing map is too long, you can split it into small modules **/
  // componentsRouter,
  // chartsRouter,

  // {
  //   path: '/example',
  //   component: Layout,
  //   redirect: '/example/list',
  //   name: 'Example',
  //   meta: {
  //     title: 'Example',
  //     icon: 'el-icon-s-help'
  //   },
  //   children: [
  //     {
  //       path: 'create',
  //       component: () => import('@/views/example/create'),
  //       name: 'CreateArticle',
  //       meta: { title: 'Create Article', icon: 'edit' }
  //     },
  //     {
  //       path: 'edit/:id(\\d+)',
  //       component: () => import('@/views/example/edit'),
  //       name: 'EditArticle',
  //       meta: { title: 'Edit Article', noCache: true, activeMenu: '/example/list' },
  //       hidden: true
  //     },
  //     {
  //       path: 'list',
  //       component: () => import('@/views/example/list'),
  //       name: 'ArticleList',
  //       meta: { title: 'Article List', icon: 'list' }
  //     }
  //   ]
  // },

  // {
  //   path: '/tab',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/tab/index'),
  //       name: 'Tab',
  //       meta: { title: 'Tab', icon: 'tab' }
  //     }
  //   ]
  // },

  // {
  //   path: '/error',
  //   component: Layout,
  //   redirect: 'noRedirect',
  //   name: 'ErrorPages',
  //   meta: {
  //     title: 'Error Pages',
  //     icon: '404'
  //   },
  //   children: [
  //     {
  //       path: '401',
  //       component: () => import('@/views/error-page/401'),
  //       name: 'Page401',
  //       meta: { title: '401', noCache: true }
  //     },
  //     {
  //       path: '404',
  //       component: () => import('@/views/error-page/404'),
  //       name: 'Page404',
  //       meta: { title: '404', noCache: true }
  //     }
  //   ]
  // },

  // {
  //   path: '/error-log',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'log',
  //       component: () => import('@/views/error-log/index'),
  //       name: 'ErrorLog',
  //       meta: { title: 'Error Log', icon: 'bug' }
  //     }
  //   ]
  // },

  // {
  //   path: '/excel',
  //   component: Layout,
  //   redirect: '/excel/export-excel',
  //   name: 'Excel',
  //   meta: {
  //     title: 'Excel',
  //     icon: 'excel'
  //   },
  //   children: [
  //     {
  //       path: 'export-excel',
  //       component: () => import('@/views/excel/export-excel'),
  //       name: 'ExportExcel',
  //       meta: { title: 'Export Excel' }
  //     },
  //     {
  //       path: 'export-selected-excel',
  //       component: () => import('@/views/excel/select-excel'),
  //       name: 'SelectExcel',
  //       meta: { title: 'Export Selected' }
  //     },
  //     {
  //       path: 'export-merge-header',
  //       component: () => import('@/views/excel/merge-header'),
  //       name: 'MergeHeader',
  //       meta: { title: 'Merge Header' }
  //     },
  //     {
  //       path: 'upload-excel',
  //       component: () => import('@/views/excel/upload-excel'),
  //       name: 'UploadExcel',
  //       meta: { title: 'Upload Excel' }
  //     }
  //   ]
  // },

  // {
  //   path: '/zip',
  //   component: Layout,
  //   redirect: '/zip/download',
  //   alwaysShow: true,
  //   name: 'Zip',
  //   meta: { title: 'Zip', icon: 'zip' },
  //   children: [
  //     {
  //       path: 'download',
  //       component: () => import('@/views/zip/index'),
  //       name: 'ExportZip',
  //       meta: { title: 'Export Zip' }
  //     }
  //   ]
  // },

  // {
  //   path: '/pdf',
  //   component: Layout,
  //   redirect: '/pdf/index',
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/pdf/index'),
  //       name: 'PDF',
  //       meta: { title: 'PDF', icon: 'pdf' }
  //     }
  //   ]
  // },
  // {
  //   path: '/pdf/download',
  //   component: () => import('@/views/pdf/download'),
  //   hidden: true
  // },
  //
  // {
  //   path: '/theme',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/theme/index'),
  //       name: 'Theme',
  //       meta: { title: 'Theme', icon: 'theme' }
  //     }
  //   ]
  // },
  //
  // {
  //   path: '/clipboard',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/clipboard/index'),
  //       name: 'ClipboardDemo',
  //       meta: { title: 'Clipboard', icon: 'clipboard' }
  //     }
  //   ]
  // },
  //
  // {
  //   path: 'external-link',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'https://github.com/PanJiaChen/vue-element-admin',
  //       meta: { title: 'External Link', icon: 'link' }
  //     }
  //   ]
  // },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
