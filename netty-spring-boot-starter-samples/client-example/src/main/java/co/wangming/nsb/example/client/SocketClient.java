package co.wangming.nsb.example.client;

import co.wangming.nsb.samples.protobuf.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created By WangMing On 2019-12-08
 **/
public class SocketClient {

    private static final Logger log = LoggerFactory.getLogger("client");

    public static void main(String[] args) throws IOException, InterruptedException {

        for (int i = 0; i < 100; i++) {
            Search.SearchRequest tcpRequest = Search.SearchRequest.newBuilder()
                    .setQuery("TCP-Message-" + i)
                    .setPageNumber(1002)
                    .setResultPerPage(10)
                    .build();
            byte[] tcpMessage = tcpRequest.toByteArray();
            try {
                sendTCPMessage(tcpMessage, 1, true);
                sendTCPMessage(tcpMessage, 2, true);
                sendTCPMessage(tcpMessage, 3, false);
                sendTCPMessage(tcpMessage, 4, false);
                sendTCPMessage(tcpMessage, 5, false);
                sendTCPMessage(tcpMessage, 6, false);
                sendTCPMessage(tcpMessage, 7, false);
                sendTCPMessage(tcpMessage, 8, false);
            } catch (Exception e) {

            }

//            Search.SearchRequest udpRequest = Search.SearchRequest.newBuilder()
//                    .setQuery("UDP-Message-" + i)
//                    .setPageNumber(1002)
//                    .setResultPerPage(10)
//                    .build();
//            byte[] udpMessage = udpRequest.toByteArray();
//            try {
//                sendUDPMessage(udpMessage, 1, true);
//                sendUDPMessage(udpMessage, 2, true);
//                sendUDPMessage(udpMessage, 3, false);
//                sendUDPMessage(udpMessage, 4, false);
//                sendUDPMessage(udpMessage, 5, false);
//                sendUDPMessage(udpMessage, 6, false);
//                sendUDPMessage(udpMessage, 7, false);
//                sendUDPMessage(udpMessage, 8, false);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            TimeUnit.SECONDS.sleep(10);
        }

    }

    private static void sendTCPMessage(byte[] message, int commandId, boolean isRecive) throws Exception {
        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress("localhost", 7800));

            OutputStream out = socket.getOutputStream();
            out.write(commandId);
            out.write(message.length);
            out.write(message);
            out.flush();

            if (!isRecive) {
                log.info("\n******TCP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}\n***************************",
                        commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length);

                TimeUnit.SECONDS.sleep(1);
                return;
            }

            InputStream in = socket.getInputStream();
            int messageId = in.read();
            int size = in.read();

            byte[] responseMessage = new byte[size];
            in.read(responseMessage);

            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);

            log.info("\n******TCP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}, \nsearchResponse:{}\n***************************",
                    commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length, searchResponse.getResult());
        }

    }

//    private static void sendUDPMessage(byte[] message, int commandId, boolean isRecive) throws Exception {
//        try (DatagramSocket socket = new DatagramSocket()) {
//
//            socket.connect(new InetSocketAddress("localhost", 7801));
//
//            ByteBuf buffer = ByteBufAllocator.DEFAULT.heapBuffer(4 + 4 + message.length);
//            buffer.writeByte(commandId);
//            buffer.writeByte(message.length);
//            buffer.writeBytes(message);
//
//            int length = buffer.readableBytes(); // 获取可读字节数
//            byte[] bytes = new byte[length];
//            buffer.getBytes(buffer.readerIndex(), bytes);
//
//            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
//            socket.send(datagramPacket);
//            if (!isRecive) {
//                TimeUnit.SECONDS.sleep(1);
//                log.info("\n******UDP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}\n***************************",
//                        commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length);
//
//                return;
//            }
//
//            byte[] container = new byte[1024];
//            //3.封装成包
//            DatagramPacket packet = new DatagramPacket(container, container.length);
//            socket.receive(packet);
//            byte[] responseMessage = new byte[container[1]];
//            System.arraycopy(container, 2, responseMessage, 0, responseMessage.length);
//            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);
//
//            log.info("\n******UDP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}, \nsearchResponse:{}\n***************************",
//                    commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length, searchResponse.getResult());
//        }
//    }
}
