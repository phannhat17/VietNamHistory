module com.vietnam.history {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;

    opens com.vietnam.history.gui.controller to javafx.fxml;
    opens com.vietnam.history.gui.model to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
    exports com.vietnam.history.gui.model;
    exports com.vietnam.history.gui.controller;
    exports com.vietnam.history.gui;
    opens com.vietnam.history.gui to javafx.fxml;
}
