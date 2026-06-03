package sars;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sars.view.LoginView;

public class MainApp extends Application {

    public static final String CSS_PATH = "/styles/sars.css";

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView.getRoot(), 480, 560);
        scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        primaryStage.setTitle("SARS — Smart Access Residential System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        sars.database.DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
