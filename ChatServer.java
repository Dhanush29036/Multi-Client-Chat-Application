package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Chat Server Started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler client = new ClientHandler(socket);
                clientHandlers.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
    }
}

class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter username:");
            username = in.readLine();

            String message;
            while ((message = in.readLine()) != null) {
                ChatServer.broadcast(username + ": " + message, this);
                saveMessage(username, message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.removeClient(this);
        }
    }

    private void saveMessage(String username, String message) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatapp", "root", "root")) {

            String query = "INSERT INTO messages (username, message) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, message);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
