package co.wangming.nsb.samples;

import co.wangming.nsb.samples.protobuf.Search;
import co.wangming.nsb.util.NetUtils;
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

    private static final Logger log = LoggerFactory.getLogger(SocketClient.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        Search.SearchRequest request = Search.SearchRequest.newBuilder()
                .setQuery("test")
                .setPageNumber(1002)
                .setResultPerPage(10)
                .build();
        byte[] message = request.toByteArray();


        for (int i = 0; i < 100; i++) {

            try {
                sendMessage(message, 1, true);
                sendMessage(message, 2, true);
                sendMessage(message, 3, false);
                sendMessage(message, 4, false);
                sendMessage(message, 5, false);
                sendMessage(message, 6, false);
                sendMessage(message, 7, false);
                sendMessage(message, 8, false);
            } catch (Exception e) {

            }

            TimeUnit.SECONDS.sleep(10);
        }

    }

    private static void sendMessage(byte[] message, int commandId, boolean isRecive) throws Exception {
        try (Socket socket = new Socket()) {

            String ip = NetUtils.getLocalIp();
            log.info("开始与[{}:7800] 通信", ip);

            socket.connect(new InetSocketAddress(ip, 7800));

            OutputStream out = socket.getOutputStream();
            out.write(commandId);
            out.write(message.length);
            out.write(message);
            out.flush();

            log.info("commandId:{}, write size::{}", commandId, message.length);

            if (!isRecive) {
                TimeUnit.SECONDS.sleep(1);
                return;
            }

            InputStream in = socket.getInputStream();
            int messageId = in.read();
            int size = in.read();

            byte[] responseMessage = new byte[size];
            in.read(responseMessage);

            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);

            log.info("commandId:{}, searchResponse:{}", commandId, searchResponse.getResult());

        }

    }
}
