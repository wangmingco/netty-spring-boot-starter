package co.wangming.nsb.samples;

import co.wangming.nsb.samples.protobuf.Search;

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

    public static void main(String[] args) throws IOException, InterruptedException {

        Search.SearchRequest request = Search.SearchRequest.newBuilder()
                .setQuery("test")
                .setPageNumber(1002)
                .setResultPerPage(10)
                .build();
        byte[] message = request.toByteArray();


        for (int i = 0; i < 100; i++) {

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 7001));

            System.out.println("RemoteAddress: " + socket.getRemoteSocketAddress());
            System.out.println("LocalAddress: " + socket.getLocalAddress());
            System.out.println("socket.isConnected: " + socket.isConnected());

            OutputStream out = socket.getOutputStream();
            out.write(message.length);
            out.write((int) 1);
            out.write(message);
            out.flush();

            System.out.println("write size:" + message.length);

            InputStream in = socket.getInputStream();
            int size = in.read();
            in.read();

            byte[] responseMessage = new byte[size];
            in.read(responseMessage);

            Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);

            System.out.println("searchResponse:" + searchResponse.getResult());

            socket.close();

            TimeUnit.SECONDS.sleep(20);
        }

    }
}
