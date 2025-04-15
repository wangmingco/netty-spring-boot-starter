package co.wangming.nsb.samples;

import co.wangming.nsb.samples.protobuf.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * 长连接压力测试
 *
 * 设置Java服务器启动参数，控制堆内存占用在 50M
 * -Xms50m -Xmx50m
 *
 * CPU信息
 * Name                                  NumberOfCores  NumberOfLogicalProcessors
 * 12th Gen Intel(R) Core(TM) i5-12500H  12             16
 *
 * 内存信息
 * 16G内存
 *
 * Created By WangMing On 2019-12-08
 **/
public class LongConnectionPressTest {

    private static final Logger log = LoggerFactory.getLogger(LongConnectionPressTest.class);

    private static final int SOCKET_NUM = 128;
    private static final int MESSAGE_NUM = 10000;
    private static final Map<Long, Long> sendStatics = new HashMap<>(1024);

    public static void main(String[] args) throws IOException {

        CountDownLatch sendLatch = new CountDownLatch(SOCKET_NUM);
        CountDownLatch receiveLatch = new CountDownLatch(SOCKET_NUM);

        List<Socket> sockets = new ArrayList<>();
        for (int i = 0; i < SOCKET_NUM; i++) {
                Socket socket = new Socket();
                sockets.add(socket);
                socket.connect(new InetSocketAddress("localhost", 7800));
                Search.SearchRequest tcpRequest = Search.SearchRequest.newBuilder()
                        .setQuery("TCP-Message-" + i)
                        .setPageNumber(1002)
                        .setResultPerPage(10)
                        .build();

                byte[] tcpMessage = tcpRequest.toByteArray();
                new Thread(() -> {
                    for(int j = 1; j <= MESSAGE_NUM; j++) {
                        System.out.println("send " + tId() + " -> " + j);
                        try {
//                            sendTCPMessage(socket, tcpMessage, 1);
//                            sendTCPMessage(socket, tcpMessage, 2);
                            sendTCPMessage(socket, tcpMessage, 3);
//                            sendTCPMessage(socket, tcpMessage, 4);
//                            sendTCPMessage(socket, tcpMessage, 5);
//                            sendTCPMessage(socket, tcpMessage, 6, false);
//                            sendTCPMessage(socket, tcpMessage, 7);
//                            sendTCPMessage(socket, tcpMessage, 8);
                        } catch (Exception e) {
                            log.error("send error {}", j, e);
                        }
                    }
                    sendLatch.countDown();
                }).start();

//                new Thread(() -> {
//                    for(int j = 1; j <= MESSAGE_NUM; j++) {
//                        System.out.println("receive " + tId() + " -> " + j);
//                        try {
//                            receiveTCPMessage(socket);
//                            receiveTCPMessage(socket);
//                        } catch (Exception e) {
//                            log.error("receive error {}", j, e);
//                        }
//                    }
//                    receiveLatch.countDown();
//                }).start();
        }

        try {
            sendLatch.await();
//            receiveLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Socket socket : sockets) {
            socket.close();
        }
        printSendStatics();
    }

    private static long tId() {
        return Thread.currentThread().getId();
    }

    private static void sendTCPMessage(Socket socket, byte[] message, int commandId) throws Exception {
        OutputStream out = socket.getOutputStream();
        out.write(commandId);
        out.write(message.length);
        out.write(message);
        out.flush();

        long seconds = System.currentTimeMillis() / 1000;
        synchronized (sendStatics) {
            Long aLong = sendStatics.get(seconds);
            if (aLong == null) {
                sendStatics.put(seconds, 1L);
            } else {
                sendStatics.put(seconds, aLong + 1);
            }
        }
    }

    private static void receiveTCPMessage(Socket socket) throws Exception {
        InputStream in = socket.getInputStream();
        int messageId = in.read();
        int size = in.read();

        byte[] responseMessage = new byte[size];
        in.read(responseMessage);
    }

    private static void printSendStatics() {
        System.out.println("统计数量: " + sendStatics.size());
        Map<String, Long> s = new TreeMap<>();
        for (Map.Entry<Long, Long> entry : sendStatics.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();

            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(key), ZoneId.systemDefault());

            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 格式化日期时间
            String formattedDate = dateTime.format(formatter);
            s.put(formattedDate, value);
        }
        for (Map.Entry<String, Long> entry : s.entrySet()) {
            System.out.println("统计结果: " + entry.getKey() + " -> " + entry.getValue());
        }
    }
}
