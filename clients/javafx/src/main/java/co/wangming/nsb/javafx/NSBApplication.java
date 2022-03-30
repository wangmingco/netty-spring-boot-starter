package co.wangming.nsb.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class NSBApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        URL fxml = getClass().getResource("nsb.fxml");
        if (fxml == null) {
            System.out.println("-----> " + fxml);
            return;
        }

        Parent root = FXMLLoader.load(fxml);

        stage.setTitle("NSB");
//        stage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}