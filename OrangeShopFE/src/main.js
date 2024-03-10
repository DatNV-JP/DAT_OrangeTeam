/* eslint-disable */
import Vue from 'vue'
import Cookies from 'js-cookie'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import Element from 'element-ui'
import './styles/element-variables.scss'
import 'element-ui/lib/theme-chalk/index.css'
//import enLang from 'element-ui/lib/locale/lang/en'// 如果使用中文语言包请默认支持，无需额外引入，请删除该依赖
import viLang from 'element-ui/lib/locale/lang/vi'
import '@/styles/index.scss' // global css
import App from './App'
import store from './store'
import router from './router'
import './icons' // icon
import './permission' // permission control
import './websocketjs'
import './utils/error-log' // error log
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue'
import moment from 'moment-timezone'
// vue_cookie
import VueCookies from 'vue-cookies'
import VueSweetalert2 from 'vue-sweetalert2'
// If you don't need the styles, do not connect
import 'sweetalert2/dist/sweetalert2.min.css'

Vue.use(VueSweetalert2)
Vue.prototype.$moment = moment
Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
Vue.use(VueCookies)
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock')
//   mockXHR()
// }
Vue.use(Element, {
  size: Cookies.get('size') || 'medium', // set element-ui default size
  locale: viLang
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
