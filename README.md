# Chat-App-
# CHAT SYSTEM
## 1. Objective:
The participants must create a chat system following the requirements and protocol described in this document.
## 2. Goals:
Each participant should have a GUI able to connect to the other participant`s servers. The GUI should prompt the user to chose if it wants to be a server or a client in the beginning. Then ask for the necessary input and perform its actions without freezing, crashing or misbehaving. The participants are free to choose whichever design they want if they follow the protocol for the communication. It is more important to implement the logic than the design of the GUI.
3. Server requirements:
When acting as a server the GUI should give information about the IP and port where the connections are to be made. There also should be a list of all the clients connected to the chat.
3.1. INPUT:
- LOGIN:*username*
Input starting with LOGIN should be interpreted as a connection request. THIS MUST BE THE FIRST INPUT FROM THIS CLIENT! In case it`s not, the connection should be refused. Everything after the “:” is considered to be the username of the client trying to connect. Usernames are allowed to have a-z, A-Z, 0-9 without empty space and maximum 10 characters. Incorrect usernames should immediately disconnect the client with a message like “Disconnected. Incorrect username.” If the username is already in use, the server should respond with a proper message. Whenever a new client connects successfully to the server, all currently connected users must see an updated list of clients.
- LOGOUT:
Input starting with LOGOUT logs out the client sending this input. Whenever a client disconnects successfully from the server, all currently connected users must see an updated list of clients.
- MSG:*some message*
This is the default command for chatting with all clients in the server. When this command is received, the server must send this message to all the participants in the chat following the protocol. (MSGRES:*some message*)
- MSG:*username1*,*username2*:*some message*
This is the command to send a private message to the specified users. The server must send the message to all of them but no one else. For each username not found in the clients list, a proper message must be sent (for example “No such user: username”). The number of usernames should not exceed the total number of connected clients. In such case, the client should be disconnected.
NB! All other commands must be considered improper behavior and must result in disconnection of the client even without a message. (if you want, you can provide one, but it`s not required)
3.2 OUTPUT
- MSGRES:*username*:*some message*
This is the output the server must send when a msg is received. In case it`s a server message, username should be “Server” or something of your choosing as long as it is self-explanatory
- CLIENTLIST:*list*
The server must send this after a connection is established with a new client. The server must also send this to all connected clients on every new LOGIN or LOGOUT event in order to update their lists. The usernames must be separated by a comma.
4. Client requirements:
The client needs to ask the user for IP and port to connect to. THE CLIENT MUST FOLLOW PROTOCOL AND SEND LOGIN:username OR A CONNECTION WILL NOT BE ESTABLISHED. Immediately after connection is established the client must receive a list of connected clients.
4.1 INPUT:
- CLIENTLIST:*list*
Whenever this command is received the client must update the list of clients and present it to the user.
- MSGRES:*username*:*message*
Default command for receiving messages from the server. The client must output to the user`s GUI only the username and the message.
4.2 OUTPUT:
- LOGIN:*username*
Clients must start with this output or a connection will be refused.
- LOGOUT:
Client should send this command when the user wants to logout OR CLOSES THE GUI.
- MSG:*some message*
Default command to send messages to all clients.
- MSG:*username1*,*username2*:
This command allows the user to send private messages to the specified users. The usernames must be separated by a comma.
