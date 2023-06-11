module com.vietnam.history {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;

    opens com.vietnam.history.controller to javafx.fxml;
    opens com.vietnam.history.model to com.fasterxml.jackson.databind, javafx.base;
    exports com.vietnam.history;
}
