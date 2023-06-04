module com.vietnam.history {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.vietnam.history.controller to javafx.fxml;
    exports com.vietnam.history;
}
