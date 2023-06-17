package com.vietnam.history;

import com.vietnam.history.controller.DetailScene;
import com.vietnam.history.controller.ListEntityScene;
import com.vietnam.history.model.*;
import com.vietnam.history.model.loader.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Start GUI App
 */
public class App extends Application {

    private static Scene scene;

    // Load all data
    public static ObservableList<Dynasty> dynasties = new DynastyLoader().loadData();
    public static ObservableList<Figure> figures = new FigureLoader().loadData();
    public static ObservableList<HistoricalEvent> historicalEvents = new EventLoader().loadData();
    public static ObservableList<Festival> festivals = new FestivalLoader().loadData();
    public static ObservableList<Place> places = new PlaceLoader().loadData();

    /**
     * The main method that launches the application.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the application and loads the start scene.
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("StartScene"));
        stage.setScene(scene);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Tra cứu lịch sử Việt Nam");
        stage.show();
    }

    /**
     * Sets the root of the scene to the specified FXML file.
     *
     * @param fxml the name of the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads the FXML file with the specified name and returns its root node.
     *
     * @param fxml the name of the FXML file
     * @return the root node of the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Opens the about dialog window.
     *
     * @param fxml the name of the FXML file for the about dialog
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void openAbout(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(App.class.getResourceAsStream("images/icon.png")));
        stage.getIcons().add(icon);
        stage.setScene(new Scene(root));
        stage.setTitle("About");
        stage.showAndWait();
        stage.close();
    }

    /**
     * Sets the root of the scene to the specified FXML file and passes the specified entity to the controller.
     *
     * @param fxml   the name of the FXML file
     * @param entity the entity to pass to the controller
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void setRootWithEntity(String fxml, HistoricalEntity entity) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        DetailScene controller = fxmlLoader.getController();
        controller.setData(entity);
        scene.setRoot(root);
    }

    /**
     * Sets the root of the scene to the specified FXML file and passes the specified entity to the controller.
     *
     * @param fxml   the name of the FXML file
     * @param entityList the list of entity to pass to the controller
     * @param entityType the type of entity to pass to the controller
     * @throws IOException if the FXML file cannot be loaded
     */
    public static <T extends HistoricalEntity> void setRootWithEntity(String fxml, ObservableList<T> entityList, String entityType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        ListEntityScene<T> controller = fxmlLoader.getController();
        controller.setData(entityList, entityType);
        scene.setRoot(root);
    }
}