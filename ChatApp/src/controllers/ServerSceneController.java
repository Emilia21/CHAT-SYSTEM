package controllers;

import java.io.IOException;

import application.Server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ServerSceneController {

    @FXML
    Label serverLabel;

    @FXML
    private TextField ipTextField;

    @FXML
    private ListView<String> loggedUsersListView;

    Server server;

    public ServerSceneController() {
        System.out.println("Trying to create server...");
        createServer();
        System.out.println("Server was created");
    }

    @FXML
    void initialize() {
        server.setController(this);
    }

    public void setipTextField() throws UnknownHostException {
        ipTextField.setText(InetAddress.getLocalHost().getHostAddress());
    }

    public void addUserToLoggedUsers(final String username) {
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loggedUsersListView.getItems().add(username);
                final String usersString = String.join(",", loggedUsersListView.getItems());
                sendToAll("CLIENTLIST:" + usersString);
            }
        });
    }

    public void sendToAll(final String msg) {
        server.sendToAll(msg);
    }

    public void messageAllNow() {
        server.sendToAll("MSGALLRES:ivan:message to all");
    }

    public void createServer() {
        new Thread() {
            @Override
            public void run() {

                try {
                    server = new Server(4545);
                    server.startServer();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void removeUserFromListView(final String user) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loggedUsersListView.getItems().remove(user);
            }
        });
    }

    public ListView<String> getListView() {
        return this.loggedUsersListView;
    }
}
