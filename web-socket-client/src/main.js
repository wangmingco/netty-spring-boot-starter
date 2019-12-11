import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import io from 'socket.io-client'

Vue.use(ElementUI, io)

Vue.config.productionTip = false

new Vue({
    render: h = > h(App),
}).
$mount('#app')
