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

    private static final int SOCKET_NUM = 100;
    private static final int MESSAGE_NUM = 10000;

    public static void main(String[] args) throws IOException {

        CountDownLatch sendLatch = new CountDownLatch(SOCKET_NUM);

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
                            sendTCPMessage(socket, tcpMessage, 3);
                        } catch (Exception e) {
                            log.error("send error {}", j, e);
                        }
                    }
                    sendLatch.countDown();
                }).start();
        }

        try {
            sendLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Socket socket : sockets) {
            socket.close();
        }
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
    }
}
