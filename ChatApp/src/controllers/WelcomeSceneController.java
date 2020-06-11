package controllers;

import application.Server;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class WelcomeSceneController {

    @FXML
    private Button serverBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernameTextField;

    @FXML
    void initialize() {

    }

    @SuppressWarnings({"nls", "unused"})
    public void showServerScene(final ActionEvent event) throws IOException {
        final Stage rootStage = (Stage) serverBtn.getScene().getWindow(); // get the root stage
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/ServerScene.fxml"));// get the
        // scene
        final Parent newRoot = (Parent) fxmlLoader.load();
        final ServerSceneController controller = fxmlLoader.<ServerSceneController>getController();
        rootStage.setScene(new Scene(newRoot)); // set the new scene

        rootStage.show(); // show the new scene
        controller.setipTextField();

    }

    @SuppressWarnings({"nls", "unused"})
    public void showChatScene(final ActionEvent event) throws IOException {
        String userName = usernameTextField.getText();

        String userNamePattern = "^[a-zA-Z0-9_-]{1,10}$";
        Pattern pattern = Pattern.compile(userNamePattern);
        Matcher matcher = pattern.matcher(userName);

        if (matcher.matches()) {
            if (Server.clientsMap.keySet().contains(userName)) {
                showAlert("This user name is alredy used. Try again");
            } else {
                final Stage rootStage = (Stage) loginBtn.getScene().getWindow(); // get the root stage

                final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/ChatScene.fxml"));// get the scene
                final Parent newRoot = (Parent) fxmlLoader.load();
                final ChatSceneController controller = fxmlLoader.<ChatSceneController>getController();

                controller.setUsername(userName);
                controller.loginUser(userName);
                rootStage.setScene(new Scene(newRoot)); // set the new scene
                rootStage.show(); // show the new scene
            }
        } else {
            showAlert("Invalid user name!");
        }

    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
