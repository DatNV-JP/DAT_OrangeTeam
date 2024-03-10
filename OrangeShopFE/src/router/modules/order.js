/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const productRouter = {
  path: '/order-management',
  component: Layout,
  redirect: '/order-management/order-management',
  name: 'Đơn hàng',
  meta: {
    title: 'Đơn hàng',
    icon: 'skill',
    roles: ['admin', 'editor']
  },
  children: [
    {
      path: 'order-management',
      component: () => import('@/views/admin/order/index'), // Parent router-view
      name: 'Order Management',
      meta: {
        icon: 'form',
        title: 'Quản lý đơn hàng'
      }
    },
    {
      path: 'order-create',
      name: 'Order',
      component: () => import('@/views/admin/order/component/CreateOrder'),
      meta: {
        icon: 'qq',
        title: 'Tạo đơn hàng'
      }
    },
    {
      path: 'tracking-page/:id',
      component: () => import('@/views/admin/order/component/TrackingPage'),
      name: 'Admin Tracking page',
      meta: { title: 'Tracking page', noCache: true },
      hidden: true
    }
  ]
}

export default productRouter
