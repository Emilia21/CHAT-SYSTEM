package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(final Stage primaryStage) {
        try {
            final Parent root = FXMLLoader.load(getClass().getResource("/scenes/WelcomeScene.fxml"));
            primaryStage.setTitle("My Application");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
