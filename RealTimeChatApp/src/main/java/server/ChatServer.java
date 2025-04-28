package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static HashSet<PrintWriter> writers = new HashSet<>();
    private static HashMap<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);
        try {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class ClientHandler extends Thread {
        private String username;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    out.println("SUBMIT_USERNAME");
                    username = in.readLine();
                    if (username == null) return;
                    synchronized (clients) {
                        if (!username.isEmpty() && !clients.containsKey(username)) {
                            clients.put(username, out);
                            writers.add(out);
                            break;
                        } else {
                            out.println("USERNAME_REJECTED");
                        }
                    }
                }
                out.println("USERNAME_ACCEPTED");
                broadcastUserList();
                broadcast("SERVER: " + username + " has joined");
                String message;
                while ((message = in.readLine()) != null) {
                    if (!message.isEmpty()) {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error with client " + username + ": " + e.getMessage());
            } finally {
                if (username != null) {
                    clients.remove(username);
                    writers.remove(out);
                    broadcast("SERVER: " + username + " has left");
                    broadcastUserList();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : writers) {
                writer.println("MESSAGE " + message);
            }
        }

        private void broadcastUserList() {
            String userList = String.join(",", clients.keySet());
            for (PrintWriter writer : writers) {
                writer.println("USER_LIST " + userList);
            }
        }
    }
} 
