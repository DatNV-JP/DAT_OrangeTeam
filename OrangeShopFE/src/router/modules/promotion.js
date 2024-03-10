/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const productRouter = {
  path: '/management',
  component: Layout,
  redirect: '/management/promotion-management',
  name: 'Khuyến mại',
  meta: {
    title: 'Khuyến mại',
    icon: 'el-icon-s-promotion',
    roles: ['admin', 'editor']
  },
  children: [
    {
      path: 'promotion-management',
      component: () => import('@/views/admin/promotion/index'), // Parent router-view
      redirect: '/management/promotion-management/promotion-list',
      name: 'Promotion',
      meta: {
        icon: 'el-icon-discount',
        title: 'Quản lý khuyến mại'
      },
      children: [
        {
          path: 'promotion-list',
          component: () => import('@/views/admin/promotion/component/listPromotion.vue'), // Parent router-view
          name: 'PromotionList',
          hidden: true,
          meta: {
            icon: 'el-icon-discount',
            title: 'Danh sách khuyến mại'
          }
        },
        {
          path: 'promotion-add',
          component: () => import('@/views/admin/promotion/component/AddPromotion.vue'), // Parent router-view
          name: 'PromotionAdd',
          hidden: true,
          meta: {
            icon: 'el-icon-discount',
            title: 'Thêm chương trình khuyến mại'
          }
        },
        {
          path: 'promotion-view/:id',
          component: () => import('@/views/admin/promotion/component/viewPromotion.vue'), // Parent router-view
          name: 'PromotionView',
          hidden: true,
          meta: {
            icon: 'el-icon-discount',
            title: 'Chi tiết chương trình khuyến mại'
          }
        }
      ]
    },
    {
      path: 'voucher-management',
      name: 'Voucher',
      component: () => import('@/views/admin/voucher/index'),
      meta: {
        icon: 'el-icon-s-ticket',
        title: 'Quản lý Voucher'
      }
    },
    {
      path: 'add-voucher',
      name: 'Add Voucher',
      component: () => import('@/views/admin/voucher/component/AddVoucher.vue'),
      hidden: true,
      meta: {
        icon: 'el-icon-s-ticket',
        title: 'Thêm mới Voucher'
      }
    }
  ]
}

export default productRouter
