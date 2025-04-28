package networking;

import java.io.*;
import java.net.*;

public class Client {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String username;
    private final gui.ChatGUI chatGUI;

    public Client(String serverAddress, int port, String username, gui.ChatGUI chatGUI) throws IOException {
        this.username = username;
        this.chatGUI = chatGUI;
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    String line = in.readLine();
                    if (line == null) break;
                    if (line.startsWith("SUBMIT_USERNAME")) {
                        out.println(username);
                    } else if (line.startsWith("USERNAME_ACCEPTED")) {
                        chatGUI.setConnected(true);
                    } else if (line.startsWith("USERNAME_REJECTED")) {
                        chatGUI.handleUsernameRejected();
                    } else if (line.startsWith("MESSAGE")) {
                        chatGUI.displayMessage(line.substring(8));
                    } else if (line.startsWith("USER_LIST")) {
                        chatGUI.updateUserList(line.substring(10));
                    }
                }
            } catch (IOException e) {
                chatGUI.displayMessage("Error: " + e.getMessage());
            } finally {
                chatGUI.setConnected(false);
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}