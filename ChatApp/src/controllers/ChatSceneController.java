package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ChatSceneController {

    String username;
    Client client;
    Map<String, List<String>> messagesList = new HashMap<>();

    @FXML
    private Label usernameLabel;

    @FXML
    private ListView<String> onlineUsersListView;

    @FXML
    private TextArea sendMessageTextArea;

    @FXML
    public TextArea conversationTextArea;

    @FXML
    void initialize() {
        Platform.setImplicitExit(false);
        onlineUsersListView.getItems().add("All");
        onlineUsersListView.getSelectionModel().select(0);
        client.setController(this);
    }

    public ChatSceneController() {
        createClient();
    }

    public void setUsername(final String username) throws IOException {
        this.username = username;
        usernameLabel.setText(username);

    }

    public void loginUser(final String username) {
        client.sendMessageToServer("LOGIN:" + username);
        System.out.println("I AM: " + username);
    }

    @SuppressWarnings("nls")
    public void sendMsgToServer() {
        client.sendMessageToServer("some message");
    }

    public void updateOnlineUsersList(final String[] usersArr) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // clear all users but leave All there
                onlineUsersListView.getItems().remove(1, onlineUsersListView.getItems().size());

                // -----setup chat for All-------
                if (!messagesList.containsKey("All")) {// create list for messages with all users
                    messagesList.put("All", new ArrayList<>());
                }

                // -----Specific users-----
                Arrays.sort(usersArr);
                // Show all users that are online on the left side
                for (final String user : usersArr) {
                    if (!(user.equals(username))) {
                        onlineUsersListView.getItems().add(user);// add user to the listView
                        if (!messagesList.containsKey(user)) {
                            // we have a new user, create new list for our messages
                            messagesList.put(user, new ArrayList<>());
                        }
                    }
                }
            }
        });

        final List<String> loggedOutUsers = new ArrayList<>();
        for (final String usrName : messagesList.keySet()) {
            if (!Arrays.asList(usersArr).contains(usrName)) {
                loggedOutUsers.add(usrName);
            }
        }
        for (final String usrToRemove : loggedOutUsers) {
            messagesList.remove(usrToRemove);
        }
    }

    public void messageUser() {
        // get the selected user from the listview
        final String sendMessageTo = onlineUsersListView.getSelectionModel().getSelectedItem();
        final String message = sendMessageTextArea.getText();
        if (message != null && !message.trim().isEmpty()) {
            if (sendMessageTo.equals("All")) {
                client.sendMessageToServer("MSG:" + message.replace(":", ";").replace(",", "."));
            } else {// send message to one person
                updateCurrentConversationWithUser(sendMessageTo, message);
                client.sendMessageToServer("MSG:" + username + "," + sendMessageTo + ":" + message);
            }
        }
        sendMessageTextArea.setText("");
    }

    public void messageReceived(final boolean msgToAll, final String msgFrom, final String msg) {
        if (msgToAll) {
            messagesList.get("All").add(msgFrom + ": " + msg);
            if (onlineUsersListView.getSelectionModel().getSelectedItem().equals("All")) {
                // if chat with All is open, append the message to the chat
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        conversationTextArea.setText(conversationTextArea.getText() + msgFrom + ": " + msg + "\n");
                    }
                });
            }
        } else {
            messagesList.get(msgFrom).add(msgFrom + ": " + msg);
            // message to other users
            if (onlineUsersListView.getSelectionModel().getSelectedItem().equals(msgFrom)) {
                // if chat with this user is open, append the message to the chat
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        conversationTextArea.setText(conversationTextArea.getText() + msgFrom + ": " + msg + "\n");
                    }
                });
            }

        }
    }

    public void updateCurrentConversationWithUser(final String user, final String msg) {
        messagesList.get(user).add(username + ": " + msg + "\n");
        if (onlineUsersListView.getSelectionModel().getSelectedItem().equals(user)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    conversationTextArea.setText(conversationTextArea.getText() + username + ": " + msg + "\n");
                }
            });
        }
    }

    public void loadConversationOnUserClicked(final MouseEvent arg0) {
        final String chosenUser = onlineUsersListView.getSelectionModel().getSelectedItem();
        String conversation = "";
        for (final String comment : messagesList.get(chosenUser)) {
            conversation += comment + "\n";
        }
        conversationTextArea.setText(conversation);
    }

    public void logoutButtonClicked() throws IOException {
        System.out.println("Logging out");
        client.sendMessageToServer("LOGOUT:");
        client.stopConnection();
        showWelcomeScene();
    }

    @SuppressWarnings("nls")
    public void createClient() {
        client = new Client();
        new Thread() {
            @Override
            public void run() {
                try {
                    client.startConnection("127.0.0.1", 4545);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressWarnings({"nls"})
    public void showWelcomeScene() throws IOException {
        final Stage rootStage = (Stage) usernameLabel.getScene().getWindow(); // get the root stage
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scenes/WelcomeScene.fxml"));
        final Parent newRoot = (Parent) fxmlLoader.load();
        rootStage.setScene(new Scene(newRoot)); // set the new scene

        rootStage.show(); // show the new scene
    }

}
