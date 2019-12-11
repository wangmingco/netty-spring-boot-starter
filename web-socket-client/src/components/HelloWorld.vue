<template>
  <div class="app-container">
    
    <el-button @click="request">Button</el-button>
   
  </div>
</template>

<script>
    import messages from '@/api/search_pb'
    import {send} from '@/api/socket_client'

    export default {
  
  methods: {
    request() {
        var searchRequest = new messages.SearchRequest();
        searchRequest.setQuery("setQuery")
        var bytes = searchRequest.serializeBinary();
        // this.$message('SearchRequest 序列化后大小: ' + bytes.length);

        send(bytes)
    }
  }
  

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
