module com.vietnam.history {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.vietnam.history.controller to javafx.fxml;
    exports com.vietnam.history;
}
