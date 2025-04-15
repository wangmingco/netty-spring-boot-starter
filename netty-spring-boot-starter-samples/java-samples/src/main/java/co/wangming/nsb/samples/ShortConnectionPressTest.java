package co.wangming.nsb.samples;

import co.wangming.nsb.samples.protobuf.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 压力测试
 * Created By WangMing On 2019-12-08
 **/
public class ShortConnectionPressTest {

    private static final Logger log = LoggerFactory.getLogger(ShortConnectionPressTest.class);

    public static void main(String[] args) {

        for (int i = 0; i < 64; i++) {
            int tid = i;
            Thread thread = new Thread(() -> {
                for(int j = 0; j < 100000; j++) {
                    Search.SearchRequest tcpRequest = Search.SearchRequest.newBuilder()
                            .setQuery("TCP-Message-" + j)
                            .setPageNumber(1002)
                            .setResultPerPage(10)
                            .build();
                    byte[] tcpMessage = tcpRequest.toByteArray();
                    System.out.println("=================>>>>>>>>>>>>>>> " + tid + " -> " + j);
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
                }
            });
            thread.start();
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
//                log.info("\n******TCP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}\n***************************",
//                        commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length);

                return;
            }

            InputStream in = socket.getInputStream();
            int messageId = in.read();
            int size = in.read();

            byte[] responseMessage = new byte[size];
            in.read(responseMessage);

            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);

//            log.info("\n******TCP消息发送完成**********\ncommandId:{}, \nRemoteAddress:{}, \nLocalAddress:{}, \nwrite size:{}, \nsearchResponse:{}\n***************************",
//                    commandId, socket.getRemoteSocketAddress(), socket.getLocalAddress(), message.length, searchResponse.getResult());
        }

    }

}
