package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import controllers.ChatSceneController;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    public String username;
    public ChatSceneController chatController;

    public void startConnection(final String ip, final int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        startIncomingMsgListenerThread(in);
    }

    @SuppressWarnings("nls")
    public void sendMessageToServer(final String msg) {
        System.out.println("sending to server:" + msg);
        out.println(msg);
    }

    public void setController(final ChatSceneController controller) {
        chatController = controller;
    }

    private void startIncomingMsgListenerThread(final BufferedReader in) {
        final Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = in.readLine();
                        if (msg == null) {
                            return;
                        }
                        if (msg.startsWith("CLIENTLIST:")) {
                            msg = msg.replace("CLIENTLIST:", "");
                            final String[] usersArray = msg.split(",");
                            chatController.updateOnlineUsersList(usersArray);
                            // update interface
                        }
                        if (msg.startsWith("MSGALLRES:")) {
                            msg = msg.replace("MSGALLRES:", "");
                            final int usernameEndIndex = msg.indexOf(":");
                            final String msgFrom = msg.substring(0, usernameEndIndex);
                            final String actualMessage = msg.substring(usernameEndIndex + 1, msg.length());
                            chatController.messageReceived(true, msgFrom, actualMessage);
                        }
                        if (msg.startsWith("MSGRES:")) {
                            msg = msg.replace("MSGRES:", "");
                            final int usernameEndIndex = msg.indexOf(":");
                            final String msgFrom = msg.substring(0, usernameEndIndex);
                            final String actualMessage = msg.substring(usernameEndIndex + 1, msg.length());
                            chatController.messageReceived(false, msgFrom, actualMessage);
                        }
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        readMessage.start();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.flush();
        out.close();
        clientSocket.close();
    }

}