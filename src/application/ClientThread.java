package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ClientThread extends Thread {
    private DatagramSocket socket;
    private byte[] incoming = new byte[1024];
    private TextArea textArea;
    private Game game;

    public ClientThread(DatagramSocket socket, TextArea textArea, Game game) {
        this.socket = socket;
        this.textArea = textArea;
        this.game = game;
    }

    @Override
    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String message = new String(packet.getData(), 0, packet.getLength()).trim();
            if (message.equals("start;")) {
                // Handle starting the game here
                Platform.runLater(() -> {
                    try {
                        game.setGame(game.getStage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else if (message.startsWith("Player") || message.startsWith("Lobby")) {
                game.updateLobby(message);
            } else if (message.startsWith("Available Lobbies:")) {
                game.updateLobby(message);
            } else {
                String current = textArea.getText();
                Platform.runLater(() -> {
                    textArea.setText(current + message + "\n");
                    textArea.setScrollTop(Double.MAX_VALUE);
                });
            }
        }
    }
}
