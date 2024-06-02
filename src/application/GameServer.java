package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameServer {
    private static final int SERVER_PORT = 9000;
    private static DatagramSocket serverSocket;
    private static List<ClientInfo> clients = new ArrayList<>();
    private static Map<String, List<ClientInfo>> lobbies = new HashMap<>();

    public static void main(String[] args) {
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("Server is running...");

            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (message.startsWith("init;")) {
                    clients.add(new ClientInfo(clientAddress, clientPort));
                    System.out.println("New client connected: " + clientAddress + ":" + clientPort);
                } else if (message.startsWith("createLobby;")) {
                    String lobbyName = message.split(";")[1].trim();
                    createLobby(lobbyName, new ClientInfo(clientAddress, clientPort));
                } else if (message.startsWith("joinLobby;")) {
                    String[] parts = message.split(";");
                    if (parts.length == 3) {
                        String lobbyName = parts[1].trim();
                        String username = parts[2].trim();
                        joinLobby(lobbyName, new ClientInfo(clientAddress, clientPort, username));
                    } else {
                        System.err.println("Invalid joinLobby message: " + message);
                    }
                } else if (message.startsWith("listLobbies;")) {
                    listLobbies(clientAddress, clientPort);
                } else if (message.startsWith("start;")) {
                    String lobbyName = message.split(";")[1].trim();
                    startGame(lobbyName);
                } else if (message.startsWith("updateHero;")) {
                    broadcastMessage(message, clientAddress, clientPort);
                } else if (message.startsWith("bulletUpdate;")) {
                    broadcastMessage(message, clientAddress, clientPort);
                } else if (message.startsWith("enemyUpdate;")) {
                    broadcastMessage(message, clientAddress, clientPort);
                } else {
                    broadcastMessage(message, clientAddress, clientPort);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createLobby(String lobbyName, ClientInfo clientInfo) throws IOException {
        if (!lobbies.containsKey(lobbyName)) {
            lobbies.put(lobbyName, new ArrayList<>());
            joinLobby(lobbyName, clientInfo);
            broadcastMessage("Lobby " + lobbyName + " created.", clientInfo.getAddress(), clientInfo.getPort());
        } else {
            sendMessage("Lobby " + lobbyName + " already exists.", clientInfo.getAddress(), clientInfo.getPort());
        }
    }

    private static void joinLobby(String lobbyName, ClientInfo clientInfo) throws IOException {
        if (lobbies.containsKey(lobbyName)) {
            lobbies.get(lobbyName).add(clientInfo);
            broadcastMessage("Player " + clientInfo.getUsername() + " has joined lobby " + lobbyName + ".", clientInfo.getAddress(), clientInfo.getPort());
        } else {
            sendMessage("Lobby " + lobbyName + " does not exist.", clientInfo.getAddress(), clientInfo.getPort());
        }
    }

    private static void listLobbies(InetAddress clientAddress, int clientPort) throws IOException {
        StringBuilder lobbyList = new StringBuilder("Available Lobbies:\n");
        for (String lobbyName : lobbies.keySet()) {
            lobbyList.append(lobbyName).append("\n");
        }
        sendMessage(lobbyList.toString(), clientAddress, clientPort);
    }

    private static void startGame(String lobbyName) throws IOException {
        if (lobbies.containsKey(lobbyName)) {
            for (ClientInfo client : lobbies.get(lobbyName)) {
                sendMessage("start;", client.getAddress(), client.getPort());
            }
        }
    }

    private static void broadcastMessage(String message, InetAddress senderAddress, int senderPort) throws IOException {
        for (ClientInfo client : clients) {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
            serverSocket.send(sendPacket);
        }
    }

    private static void sendMessage(String message, InetAddress clientAddress, int clientPort) throws IOException {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        serverSocket.send(sendPacket);
    }
}

class ClientInfo {
    private InetAddress address;
    private int port;
    private String username;

    public ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.username = "Player";
    }

    public ClientInfo(InetAddress address, int port, String username) {
        this.address = address;
        this.port = port;
        this.username = username;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}
