package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import controllers.ServerSceneController;
import java.net.InetAddress;
import javafx.scene.control.Alert;

public class Server {

    private final ServerSocket sSocket;
    private final ExecutorService es;
    final static int numThreads = 10;
    private ServerSceneController serverController;
    List<CommunicationThread> threadsList = new ArrayList<>();

    public final static Map<String, CommunicationThread> clientsMap = new HashMap<>();

    public Server(final int port) throws IOException {
        sSocket = new ServerSocket(port);
        es = Executors.newFixedThreadPool(numThreads);
    }

    public void setController(final ServerSceneController controller) {
        serverController = controller;
    }

    public void startServer() throws IOException {
        Socket clientSocket;
        System.out.println(InetAddress.getLocalHost().getHostAddress()); //servers ip adress
        while (true) {
            clientSocket = sSocket.accept();
            final CommunicationThread newThread = new CommunicationThread(clientSocket);
            threadsList.add(newThread);
            es.submit(newThread);
        }
    }

    public void sendToAll(final String msg) {
        for (final CommunicationThread tr : threadsList) {
            tr.sendMsgToClient(msg);
        }
    }

    private class CommunicationThread implements Runnable {

        private final Socket communicationSocket;
        public PrintWriter outWrite; // TODO: change?
        public BufferedReader inRead;
        public boolean isFirstRequest = true;

        public CommunicationThread(final Socket communicationSocket) {
            this.communicationSocket = communicationSocket;
        }

        public void sendMsgToClient(final String msg) {
            outWrite.println(msg);
        }

        @Override
        public void run() {
            try {
                outWrite = new PrintWriter(communicationSocket.getOutputStream(), true);
                inRead = new BufferedReader(new java.io.InputStreamReader(communicationSocket.getInputStream()));
                listenAndProcessMessages();
                communicationSocket.close();

            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("nls")
        private void listenAndProcessMessages() throws IOException {
            while (true) {
                String line = inRead.readLine();
                System.out.println("server received:" + line);
                if (isFirstRequest) {
                    if (line.startsWith("LOGIN:")) {
                        final String username = line.replace("LOGIN:", "");
                        serverController.addUserToLoggedUsers(username);
                        clientsMap.put(username, this);
                        isFirstRequest = false;
                    } else { // reject the connection
                        System.out.println("rejecting connection");
                        closeConnection();
                        return;
                    }

                }
                if (line.startsWith("MSG:")) {
                    line = line.replace("MSG:", "");
                    if (line.contains(":")) { // msg one
                        final String msg = line.split(":")[1];
                        final String sender = line.replace("MSG:", "").split(":")[0].split(",")[0];
                        final String receiver = line.replace("MSG:", "").split(":")[0].split(",")[1];
                        final CommunicationThread receiverThread = getClientThreadByName(receiver, clientsMap);
                        receiverThread.sendMsgToClient(String.format("MSGRES:%s:%s", sender, msg));
                    } else {// msg all
                        final String sender = getSender(this, clientsMap);
                        sendToAll(String.format("MSGALLRES:%s:%s", sender, line));
                    }
                }

                if (line.startsWith("LOGOUT:")) {

                    final String sender = getSender(this, clientsMap);
                    threadsList.remove(this);
                    clientsMap.remove(sender);
                    sendToAll("CLIENTLIST:" + String.join(",", clientsMap.keySet()));
                    serverController.removeUserFromListView(sender);
                    this.closeConnection();
                    return;

//                    for (final String user : clientsMap.keySet()) {
//                        if (user.equals(userName)) {
//                            // clientsMap.get(user).close();
//                        }
//                    }
                }

            }
        }

        public CommunicationThread getClientThreadByName(final String name,
                final Map<String, CommunicationThread> allClientsMap) {
            CommunicationThread clientThread = null;
            for (final String user : allClientsMap.keySet()) {
                if (user.equals(name)) {
                    clientThread = allClientsMap.get(user);
                }
            }
            return clientThread;
        }

        public String getSender(final CommunicationThread curThread,
                final Map<String, CommunicationThread> allClientsMap) {
            String sender = "";
            for (final Entry<String, CommunicationThread> curEntry : allClientsMap.entrySet()) {
                if (curThread == curEntry.getValue()) {
                    sender = curEntry.getKey();
                }
            }
            return sender;
        }

        public void closeConnection() throws IOException {
            inRead.close();
            outWrite.flush();
            outWrite.close();
            communicationSocket.close();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(message);
        alert.setHeaderText(null);
        alert.setContentText("Try again");
        alert.showAndWait();
    }
    
}
