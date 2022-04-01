module co.wangming.nsb.javafx.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;
    requires com.codahale.metrics;
//    requires co.wangming.nsb.samples.protobuf;

    opens co.wangming.nsb.javafx to javafx.fxml;
    exports co.wangming.nsb.javafx;
}