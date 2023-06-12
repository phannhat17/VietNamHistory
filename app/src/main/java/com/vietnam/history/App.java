package com.vietnam.history;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Start GUI App
 */
public class App extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("StartScene"));
        stage.setScene(scene);
        Image icon = new Image(getClass().getResourceAsStream("images/icon.png"));
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
        Image icon = new Image(App.class.getResourceAsStream("images/icon.png"));
        stage.getIcons().add(icon);
        stage.setScene(new Scene(root));
        stage.setTitle("About");
        stage.showAndWait();
        stage.close();
    }

    public static void setRootWithObject(String fxml, Object object) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        Object controller = fxmlLoader.getController();

        Class<?> objectClass = object.getClass();
        Method setMethod = null;
        try {
            setMethod = controller.getClass().getMethod("setData", objectClass);
            setMethod.invoke(controller, object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } 
        scene.setRoot(root);
    }

}