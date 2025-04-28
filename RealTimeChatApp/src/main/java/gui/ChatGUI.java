package gui;

import networking.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatGUI {
    private JFrame loginFrame;
    private JFrame chatFrame;
    private JTextField usernameField;
    private JTextArea messageArea;
    private JTextField messageField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private Client client;
    private boolean connected;

    public ChatGUI() {
        initLoginFrame();
    }

    private void initLoginFrame() {
        loginFrame = new JFrame("Chat Application - Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);
        loginFrame.setLayout(new GridLayout(3, 1));

        JLabel label = new JLabel("Enter Username:", SwingConstants.CENTER);
        usernameField = new JTextField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> connect());

        loginFrame.add(label);
        loginFrame.add(usernameField);
        loginFrame.add(loginButton);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private void initChatFrame() {
        chatFrame = new JFrame("Chat Application");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(600, 400);
        chatFrame.setLayout(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(150, 0));

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        messageField = new JTextField();
        JButton sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatFrame.add(userScrollPane, BorderLayout.WEST);
        chatFrame.add(messageScrollPane, BorderLayout.CENTER);
        chatFrame.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        chatFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
            }
        });

        chatFrame.setLocationRelativeTo(null);
        chatFrame.setVisible(true);
    }

    private void connect() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame, "Username cannot be empty!");
            return;
        }
        try {
            client = new Client("localhost", 12345, username, this);
            client.start();
            loginFrame.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(loginFrame, "Failed to connect: " + e.getMessage());
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && connected) {
            client.sendMessage(message);
            messageField.setText("");
        }
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (connected) {
            SwingUtilities.invokeLater(() -> {
                initChatFrame();
                messageArea.append("Connected to server.\n");
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                if (chatFrame != null) {
                    chatFrame.dispose();
                }
                initLoginFrame();
                JOptionPane.showMessageDialog(loginFrame, "Disconnected from server.");
            });
        }
    }

    public void handleUsernameRejected() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(loginFrame, "Username rejected. Try a different username.");
            usernameField.setText("");
        });
    }

    public void displayMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    public void updateUserList(String userList) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            if (!userList.isEmpty()) {
                for (String user : userList.split(",")) {
                    userListModel.addElement(user);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatGUI());
    }
} 
