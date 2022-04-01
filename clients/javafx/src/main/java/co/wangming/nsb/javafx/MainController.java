package co.wangming.nsb.javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// TODO 完成线程池的资源清理工作(针对未关闭的socket)
public class MainController {

    @FXML
    private Button startBtn;

    @FXML
    private Button endBtn;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField taskNumField;

    @FXML
    private TextField sleepNumField;

    @FXML
    private TextField threadNumField;

    @FXML
    private TextField rebuildNumField;

    @FXML
    private TextField threadPoolField;

    @FXML
    private ListView listView;

    private static final int msgNum = 9;

    @FXML
    protected void onStartButtonClick() {
        if (TaskExecutor.isStart()) {
            System.out.println("已经开始直接返回");
            return;
        }
        
        int threads = Integer.parseInt(threadNumField.getText());
        if (threads == 0) {
            System.out.println("线程数为0直接返回");
            return;
        }

        int cycle = Integer.parseInt(taskNumField.getText());
        if (cycle == 0) {
            System.out.println("循环数为0直接返回");
            return;
        }

        int sleep = Integer.parseInt(sleepNumField.getText());
        int port = Integer.parseInt(portField.getText());
        String host = hostField.getText();
        int rebuild = Integer.parseInt(rebuildNumField.getText());

        TaskExecutor.start();
        TaskExecutor.rebuild(threads, cycle);

        byte[] message = buildMsg();

        Map<String, TextField> map = buildTaskView();

        for (int i = 1; i <= cycle; i++) {
            int msgId = i % msgNum + 1;
            TextField textField = map.get("" + msgId);
            TaskExecutor.submit(new Task(msgId, sleep, message, textField, host, port, rebuild));
        }

        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> threadPoolField.setText(TaskExecutor.threadExecutorInfo()));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("onStartButtonClick ");
    }

    private Map<String, TextField> buildTaskView() {
        ObservableList<TextField> items = FXCollections.observableArrayList();
        Map<String, TextField> map = new HashMap<>();

        for (int i = 1; i <= msgNum; i++) {
            TextField textField = new TextField();
            items.add(textField);
            map.put(i + "", textField);
        }
        FilteredList<TextField> filteredList = new FilteredList<>(items, data -> true);
        listView.setItems(filteredList);
        return map;
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
        TaskExecutor.shutdownNow();
        System.out.println("onEndButtonClick");
    }

}