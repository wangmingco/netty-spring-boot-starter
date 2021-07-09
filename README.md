# netty-spring-boot-starter
基于Netty的Spring Boot Starter工程.

![](https://github.com/wangmingco/netty-spring-boot-starter/blob/master/docs/nsbs.gif?raw=true)

## 介绍
* 支持TCP长连接消息转发到Spring容器
* 支持自定义消息枚举类(`CommandController`, `CommandMapping`)
* 支持自定义通信协议解析(`ProtocolProcessor`)
* 支持不同系统事件通知机制(`EventHandler`)

## 用例

使用类似于Spring MVC 中RestController的CommandController 和类似于GetMapping的CommandMapping 进行消息定义, 系统会自动将其注册进系统里

```java
@CommandController
public class SimpleCommand {

    @Autowired
    private SimpleService simpleService;

    @CommandMapping(id = 1)
    public Search.SearchResponse search(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 1 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());
        return Search.SearchResponse.newBuilder().setResult("查询成功").build();
    }

    @CommandMapping(id = 2)
    public Search.SearchResponse search2(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 2 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());
        simpleService.print();
        return Search.SearchResponse.newBuilder().setResult("查询成功").build();
    }

    @CommandMapping(id = 3)
    public void search3(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 3 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());
    }

    @CommandMapping(id = 4)
    public void search4(Search.SearchRequest searchRequest, String nullParam) {
        log.info("收到SearchRequest 4 --> {}, {}", searchRequest.getQuery(), nullParam);
        simpleService.print();
    }
}
``` 
在上面分别定义了四种方法
1. search() -> 系统会将接收到的byte数组解析成Protobuf SearchRequest对象作为入参, 然后Protobuf SearchResponse对象序列化城数组写回到前端
2. search3() -> 没有返回参数, 则不会进行应答
3. search4() -> 除了Protobuf SearchRequest对象, 还有一个nullParam String类型的参数, 目前还不支持自定义参数拓展, 因此这里会是空

服务端写好后, 可以使用socket client进行消息测试

```java
private static void sendMessage(byte[] message, int commandId) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 7001));

            OutputStream out = socket.getOutputStream();
            out.write(message.length);
            out.write(commandId);
            out.write(message);
            out.flush();

            log.info("commandId:{}, RemoteAddress:{}, LocalAddress:{}, write size::{}", commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length);

            if (commandId == 3 || commandId == 4) {
                return;
            }

            InputStream in = socket.getInputStream();
            int size = in.read();

            byte[] responseMessage = new byte[size];
            in.read(responseMessage);

            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);

            log.info("commandId:{}, searchResponse:{}", commandId, searchResponse.getResult());

        }

}
```

## 自定义通信协议解析
目前系统自带只支持Protobuf和Netty的ChannelHandlerContext的参数, 不过可以自定义参数解析器
```java
@ParserComponet(messageType = GeneratedMessageV3.class)
@Slf4j
public class ProtobufParser implements MessageParser<byte[], GeneratedMessageV3> {

    private Parser parser;

    @Override
    public void setParser(Class parameterType) {
        try {
            Field parserField = parameterType.getDeclaredField("PARSER");
            parserField.setAccessible(true);
            Parser parser = (Parser) parserField.get(parameterType);
            this.parser = parser;
        } catch (NoSuchFieldException e) {
            log.error("", e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    @Override
    public GeneratedMessageV3 parse(ChannelHandlerContext ctx, byte[] bytes) throws Exception{
        return (GeneratedMessageV3)parser.parseFrom(bytes);
    }
}
```
系统会调用参数解析器的setParser()方法, 将参数的parameterType传递进来, 然后就可以构建自己的参数解析器了, 等到调用带有该种参数的方法时就会调用该解析器进行参数解析

## 事件处理
系统同样提供了事件处理机制
```java
@EventRegister
@Slf4j
public class SimpleEventHandler implements EventHandler<String> {
    @Override
    public String channelActive(ChannelActiveEvent channelActiveEvent) {

        log.info("新的连接进来了:{}", channelActiveEvent.getChannelHandlerContext().name());
        return channelActiveEvent.getChannelHandlerContext().name();
    }

    @Override
    public void channelInactive(ChannelInactiveEvent<String> channelActiveEvent) {
        log.info("连接断开了:{}", channelActiveEvent.getContext());
    }

    @Override
    public void exceptionEvent(ExceptionEvent<String> exceptionEvent) {
        log.info("连接断开了:{}", exceptionEvent.getContext());
    }

    @Override
    public void readerIdleEvent(ReaderIdleEvent<String> readerIdleEvent) {
        log.info("连接读超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void writerIdleEvent(WriterIdleEvent<String> readerIdleEvent) {
        log.info("连接写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void allIdleEvent(AllIdleEvent<String> readerIdleEvent) {
        log.info("连接读写超时:{}", readerIdleEvent.getContext());
    }
}
```
通过注解EventRegister和实现EventHandler接口, 即可自定义一个事件处理器, 当前支持

* 连接接入事件
* 连接断开事件
* 异常事件
* 读超时时间
* 写超时时间
* 读写超时时间

## 架构图

![image](https://raw.githubusercontent.com/wangmingco/netty-spring-boot-starter/master/docs/architecture.jpg)

![image](https://raw.githubusercontent.com/wangmingco/netty-spring-boot-starter/master/docs/architecture1.jpg)

## TODO

* 性能优化, 在收发消息时避免申请堆内内存
* 支持其他消息编码([thrift](https://thrift.apache.org/) 等)

