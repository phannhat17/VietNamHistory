package com.vietnam.history.gui;

import com.vietnam.history.gui.controller.DetailSceneController;
import com.vietnam.history.gui.controller.ListEntityScene;
import com.vietnam.history.gui.model.*;
import com.vietnam.history.gui.loader.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * Start GUI App
 */
public class App extends Application {

    private static Scene scene;

    private static final Stack<HistoricalEntity> entityStack = new Stack<>();

    public static Stack<HistoricalEntity> getEntityStack() {
        return entityStack;
    }

    public static HistoricalEntity popEntityStack() {
        return entityStack.pop();
    }


    // Load all data
    public static final ObservableList<Dynasty> DYNASTIES = new DynastyLoader().loadData();
    public static final ObservableList<Figure> FIGURES = new FigureLoader().loadData();
    public static final ObservableList<HistoricalEvent> HISTORICAL_EVENTS = new EventLoader().loadData();
    public static final ObservableList<Festival> FESTIVALS = new FestivalLoader().loadData();
    public static final ObservableList<Landmark> LANDMARKS = new LandmarkLoader().loadData();

    private static final Map<String, HistoricalEntity> ENTITY_MAP = new HashMap<>();

    public static void initializeData() {
        for (Dynasty dynasty : DYNASTIES) {
            ENTITY_MAP.put(dynasty.getId(), dynasty);
        }
        for (Figure figure : FIGURES) {
            ENTITY_MAP.put(figure.getId(), figure);
        }
        for (HistoricalEvent event : HISTORICAL_EVENTS) {
            ENTITY_MAP.put(event.getId(), event);
        }
        for (Festival festival : FESTIVALS) {
            ENTITY_MAP.put(festival.getId(), festival);
        }
        for (Landmark landmark : LANDMARKS) {
            ENTITY_MAP.put(landmark.getId(), landmark);
        }
    }


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
        // Initialize the entity map
        initializeData();
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
     * Opens the "About" dialog window.
     *
     * @param fxml the name of the FXML file for the "About" dialog
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void openAbout(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
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
    public static void setRootWithEntity(String fxml, HistoricalEntity entity)throws IOException {
        entityStack.push(entity);
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        DetailSceneController controller = fxmlLoader.getController();
        
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
        entityStack.clear();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        ListEntityScene<T> controller = fxmlLoader.getController();
        controller.setData(entityList, entityType);
        scene.setRoot(root);
    }

    /**
     * returns the entity with the provided ID.
     *
     * @param entityId   the ID of that entity
     */
    public static HistoricalEntity fetchEntity(String entityId) {
        return ENTITY_MAP.get(entityId);
    }
}