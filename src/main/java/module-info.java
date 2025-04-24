module taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens controller to javafx.fxml;
    opens dao to javafx.fxml;
    opens model to javafx.fxml;
    opens util to javafx.fxml;
    opens view to javafx.fxml;

    exports app;
}
