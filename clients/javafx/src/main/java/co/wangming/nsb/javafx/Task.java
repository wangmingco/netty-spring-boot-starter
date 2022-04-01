package co.wangming.nsb.javafx;


import com.codahale.metrics.Histogram;
import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

    int msgId;
    int sleep;
    byte[] message;
    TextField textField;
    String host;
    Integer port;
    int rebuildNum;

    Task(int msgId, int sleep, byte[] message, TextField textField, String host, int port, int rebuildNum) {
        this.msgId = msgId;
        this.sleep = sleep;
        this.message = message;
        this.textField = textField;
        this.host = host;
        this.port = port;
        this.rebuildNum = rebuildNum;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            sendMessage(message, msgId, false, rebuildNum);
            long cost = System.currentTimeMillis() - start;

            Histogram histogram = Metrics.getHistogram(msgId + "");
            histogram.update(cost);

            Platform.runLater(() -> textField.setText(Metrics.getHistogramString(msgId + "")));

            TimeUnit.MILLISECONDS.sleep(sleep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(byte[] message, int commandId, boolean isRecive, int rebuild) {

        if (rebuild != 0) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port));
                sendMsg(message, commandId, isRecive, socket);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                long tid = Thread.currentThread().getId();
                Socket socket = SocketCache.get(tid + "", host, port);
                sendMsg(message, commandId, isRecive, socket);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMsg(byte[] message, int commandId, boolean isRecive, Socket socket) throws IOException, InterruptedException {

        OutputStream out = socket.getOutputStream();
        out.write(commandId);
        out.write(message.length);
        out.write(message);
        out.flush();

        if (!isRecive) {
//            TimeUnit.SECONDS.sleep(1);
            return;
        }

        InputStream in = socket.getInputStream();
        int messageId = in.read();
        int size = in.read();

        byte[] responseMessage = new byte[size];
        in.read(responseMessage);

        Search.SearchResponse searchResponse = Search.SearchResponse.parseFrom(responseMessage);
    }

}
