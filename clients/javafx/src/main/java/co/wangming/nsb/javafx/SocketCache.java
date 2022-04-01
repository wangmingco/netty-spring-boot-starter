package co.wangming.nsb.javafx;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketCache {

    public static final Map<String, Socket> CACHE = new ConcurrentHashMap<>();

    public static synchronized Socket get(String tid, String host, int port) throws IOException {
        Socket socket = CACHE.get(tid);
        if (socket == null) {
            socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(host, port));
            } catch (IOException e) {
                throw e;
            }
            CACHE.put(tid, socket);
        }
        return socket;
    }

    public static void clear() {
        for (Socket socket : CACHE.values()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CACHE.clear();
    }
}
