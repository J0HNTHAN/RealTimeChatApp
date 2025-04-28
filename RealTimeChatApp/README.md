RealTimeChatApp
A real-time chat application built with Java, Maven, and JavaFX for the GUI. Supports multiple users, real-time messaging, and TCP-based networking with a multithreading concurrency model.
Setup

Install Java 17 and Maven.
Download JavaFX SDK 17 from openjfx.io.
Run mvn clean install to install dependencies.
Start the server: java -cp target\classes com.chatapp.server.ChatServer
Start the client: java --module-path "path\to\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml -cp target\classes com.chatapp.client.ChatClientApp

Features

User login with unique usernames.
Real-time messaging.
Online user list (to be implemented).
Error handling and reconnection logic.

Submission

Source code: src/
Report: docs/ProjectReport.docx
Demo: docs/DemoVideo.mp4

 
