package co.wangming.nsb.javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO 完成线程池的资源清理工作(针对未关闭的socket)
public class MainController {

    @FXML
    private Button startBtn;

    @FXML
    private Button endBtn;

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    @FXML
    private TextField cycleNum;

    @FXML
    private TextField sleepNum;

    @FXML
    private TextField threadNum;

    @FXML
    private TextField rebuildNum;

    @FXML
    private ListView listView;

    private AtomicBoolean isStart = new AtomicBoolean(false);
    private ThreadPoolExecutor executor = null;

    @FXML
    protected void onStartButtonClick() {
        if (isStart.get() == true) {
            System.out.println("已经开始直接返回");
            return;
        }
        isStart.set(true);
        
        int threads = Integer.parseInt(threadNum.getText());
        if (threads == 0) {
            System.out.println("线程数为0直接返回");
            return;
        }

        int cycle = Integer.parseInt(cycleNum.getText());
        if (cycle == 0) {
            System.out.println("循环数为0直接返回");
            return;
        }

        int sleep = Integer.parseInt(sleepNum.getText());
        int p = Integer.parseInt(port.getText());
        int rebuild = Integer.parseInt(rebuildNum.getText());

        listView.setItems(new FilteredList<>(FXCollections.observableArrayList(), data -> true));
        shutdownNow();

        executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.SECONDS, new ArrayBlockingQueue(2));

        byte[] message = buildMsg();

        ObservableList<TextField> items = FXCollections.observableArrayList();

        for (int i = 0; i < threads; i++) {
            TextField textField = new TextField();
            new Thread(new Task(cycle, sleep, message, textField, host.getText(), p, rebuild)).start();
            items.add(textField);
        }

        FilteredList<TextField> filteredList = new FilteredList<>(items, data -> true);
        listView.setItems(filteredList);

        System.out.println("onStartButtonClick ");
    }

    private byte[] buildMsg() {
        Search.SearchRequest request = Search.SearchRequest.newBuilder()
                .setQuery("test")
                .setPageNumber(1002)
                .setResultPerPage(10)
                .build();
        byte[] message = request.toByteArray();
        return message;
    }

    @FXML
    protected void onEndButtonClick() {
        shutdownNow();
        System.out.println("onEndButtonClick");
    }

    public void shutdownNow() {
        if (executor != null) {
            executor.shutdownNow();
        }

        isStart.set(false);
    }

    public static class Task implements Runnable {

        ThreadLocal<Socket> socketCache = new ThreadLocal();

        int cycle;
        int sleep;
        byte[] message;
        TextField textField;
        String host;
        Integer port;
        int rebuildNum;

        Task(int cycle, int sleep, byte[] message, TextField textField, String host, int port, int rebuildNum) {
            this.cycle = cycle;
            this.sleep = sleep;
            this.message = message;
            this.textField = textField;
            this.host = host;
            this.port = port;
            this.rebuildNum = rebuildNum;
        }

        @Override
        public void run() {
            String tname = Thread.currentThread().getName();
            String log = "线程名称: " + tname + ". 发送次数: ";
            long start = System.currentTimeMillis();

            int count = 0;
            for (int c = 0; c < cycle; c++) {
                for (int j = 1; j <= 8; j++) {

                    try {
                        sendMessage(message, j, false, rebuildNum == 0 || count % rebuildNum == 0);

                        final String text = log + (count++);
                        Platform.runLater(() -> {
                            textField.setText(text);
                        });
                        TimeUnit.MILLISECONDS.sleep(sleep);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            long end = System.currentTimeMillis();
            Platform.runLater(() -> {
                textField.setText(textField.getText() + ". 耗时:" + (end - start));
            });
        }

        private void sendMessage(byte[] message, int commandId, boolean isRecive, boolean rebuild) throws Exception {

            if (rebuild) {
                try (Socket socket = new Socket()) {
                    sendMsg(message, commandId, isRecive, socket);
                }
            } else {
                Socket socket = socketCache.get();
                if (socket == null) {
                    socketCache.set(socket);
                }
                sendMsg(message, commandId, isRecive, socket);
            }
        }

        private void sendMsg(byte[] message, int commandId, boolean isRecive, Socket socket) throws IOException, InterruptedException {
            socket.connect(new InetSocketAddress(host, port));

            OutputStream out = socket.getOutputStream();
            out.write(commandId);
            out.write(message.length);
            out.write(message);
            out.flush();

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
        }

    }

}