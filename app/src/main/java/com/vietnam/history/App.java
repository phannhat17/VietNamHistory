package com.vietnam.history;

import com.vietnam.history.controller.DetailScene;
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

    //    Load all data
    public static ObservableList<Dynasty> dynasties = new DynastyLoader().loadData();
    public static ObservableList<Figure> figures = new FigureLoader().loadData();
    public static ObservableList<HistoricalEvent> historicalEvents = new EventLoader().loadData();
    public static ObservableList<Festival> festivals = new FestivalLoader().loadData();
    public static ObservableList<Place> places = new PlaceLoader().loadData();
     static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("StartScene"));
        stage.setScene(scene);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png")));
        stage.getIcons().add(icon);
        stage.setTitle("Tra cứu lịch sử Việt Nam");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

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

    public static void setRootWithEntity(String fxml, HistoricalEntity entity) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        Object controller = fxmlLoader.getController();

        if (controller instanceof DetailScene) {
            DetailScene<HistoricalEntity> detailsController = (DetailScene<HistoricalEntity>) controller;
            detailsController.setData(entity);
        }

        scene.setRoot(root);
    }
}