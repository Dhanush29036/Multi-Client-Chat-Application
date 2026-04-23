package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClient {

    private static final String SERVER = "localhost";
    private static final int PORT = 12345;

    private BufferedReader in;
    private PrintWriter out;

    private JFrame frame = new JFrame("Chat App");
    private JTextArea chatArea = new JTextArea(20, 50);
    private JTextField inputField = new JTextField(40);
    private JButton sendButton = new JButton("Send");

    public ChatClient() {

        chatArea.setEditable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(inputField);
        bottom.add(sendButton);

        panel.add(bottom, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER, PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String username = JOptionPane.showInputDialog("Enter username:");
            out.println(username);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            out.println(msg);
            chatArea.append("Me: " + msg + "\n");
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
