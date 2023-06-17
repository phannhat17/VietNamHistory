module com.vietnam.history {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;

    opens com.vietnam.history.controller to javafx.fxml;
    opens com.vietnam.history to javafx.fxml;
    opens com.vietnam.history.model to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
    exports com.vietnam.history.model;
    exports com.vietnam.history.controller;
    exports com.vietnam.history;
}
