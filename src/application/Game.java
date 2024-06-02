package application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Game {
    private Stage stage;
    private Scene splashScene;
    private Scene gameScene;
    private Scene lobbyScene;
    private Group root;
    private Canvas canvas;
    private TextArea lobbyTextArea;
    private TextArea listLobbiesTextArea;
    private String username;

    public final static int WINDOW_WIDTH = 628;
    public final static int WINDOW_HEIGHT = 760;

    private static DatagramSocket socket;

    static {
        try {
            socket = new DatagramSocket(); // initializes to any available port
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static InetAddress address;

    static {
        try {
            address = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    private static final int SERVER_PORT = 9000; // Updated port number
    private static final TextArea textArea = new TextArea();
    private static final TextField inputField = new TextField();

    //-------------------------------------------------------
    public Game() throws IOException {
        startThread();
        this.root = new Group();
        this.gameScene = new Scene(root);
        this.canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.root.getChildren().add(this.canvas);
        this.initGameScene();
    }

    public void startThread() {
        ClientThread clientThread = new ClientThread(socket, textArea, this);
        clientThread.start();

        // send initialization message to the server
        String initMessage = "init; " + (username != null ? username : "Player");
        sendMessage(initMessage);
    }

    public void initGameScene() {
        StackPane root = new StackPane();

        // Create a TextArea and set its properties
        textArea.setPromptText("Messages go here...");
        textArea.setPrefSize(50, 75); // Set size to 200x200
        textArea.setEditable(false); // Optionally, make it read-only
        textArea.setFocusTraversable(false); // Disable focus
        textArea.setWrapText(true);

        // Create a TextField for input
        inputField.setPromptText("Send a message...");
        inputField.setFocusTraversable(false);

        // Handle input field key events
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                canvas.requestFocus();
            } else if (event.getCode() == KeyCode.ENTER) {
                String temp = (username != null ? username : "Player") + ": " + inputField.getText(); // message to send
                textArea.setText(textArea.getText() + temp + "\n"); // update message
                textArea.setScrollTop(Double.MAX_VALUE);
                inputField.setText("");

                sendMessage(temp);
            }
        });

        // Create a VBox to hold the TextArea and input field
        VBox vbox = new VBox();
        vbox.getChildren().addAll(textArea, inputField);
        vbox.setAlignment(Pos.TOP_RIGHT); // Align to bottom center
        vbox.setPadding(new Insets(5, 5, 0, 300));

        // Add canvas and VBox to the StackPane
        root.getChildren().addAll(this.canvas, vbox);

        this.gameScene = new Scene(root);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Fire Nation Invasion");

        this.initSplash();

        stage.setScene(this.splashScene);
        stage.setResizable(false);
        stage.show();
    }

    private void initSplash() {
        StackPane root = new StackPane();
        root.getChildren().addAll(this.createCanvas(), this.createVBox());
        this.splashScene = new Scene(root);
    }

    private Canvas createCanvas() {
        Canvas canvas = new Canvas(Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image bg = new Image("images/spaceGameBg.jpg");
        gc.drawImage(bg, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT); // Draw image with specified width and height
        return canvas;
    }

    private VBox createVBox() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Button b1 = new Button("Start the Adventure");
        Button b2 = new Button("Game Lore");
        Button b3 = new Button("Create Lobby");
        Button b4 = new Button("Join Lobby");

        vbox.getChildren().addAll(b1, b2, b3, b4);

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    setGame(stage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                promptUsername(stage, true);
            }
        });

        b4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                promptUsernameForJoin(stage);
            }
        });

        return vbox;
    }

    private void promptUsername(Stage stage, boolean isHost) {
        VBox promptBox = new VBox();
        promptBox.setAlignment(Pos.CENTER);
        promptBox.setPadding(new Insets(10));
        promptBox.setSpacing(8);

        Label label = new Label("Enter your username:");
        TextField usernameField = new TextField();
        Button proceedButton = new Button("Proceed");

        promptBox.getChildren().addAll(label, usernameField, proceedButton);

        Scene promptScene = new Scene(promptBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(promptScene);

        proceedButton.setOnAction(e -> {
            username = usernameField.getText().trim();
            if (username.isEmpty()) {
                username = "Player";
            }
            if (isHost) {
                promptLobbyName(stage);
            } else {
                listLobbies(stage);
            }
        });
    }

    private void promptLobbyName(Stage stage) {
        VBox promptBox = new VBox();
        promptBox.setAlignment(Pos.CENTER);
        promptBox.setPadding(new Insets(10));
        promptBox.setSpacing(8);

        Label label = new Label("Enter lobby name:");
        TextField lobbyNameField = new TextField();
        Button proceedButton = new Button("Create");

        promptBox.getChildren().addAll(label, lobbyNameField, proceedButton);

        Scene promptScene = new Scene(promptBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(promptScene);

        proceedButton.setOnAction(e -> {
            String lobbyName = lobbyNameField.getText().trim();
            if (!lobbyName.isEmpty()) {
                sendMessage("createLobby; " + lobbyName);
                try {
                    setLobby(stage, true, lobbyName);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void promptUsernameForJoin(Stage stage) {
        VBox promptBox = new VBox();
        promptBox.setAlignment(Pos.CENTER);
        promptBox.setPadding(new Insets(10));
        promptBox.setSpacing(8);

        Label label = new Label("Enter your username:");
        TextField usernameField = new TextField();
        Button proceedButton = new Button("Proceed");

        promptBox.getChildren().addAll(label, usernameField, proceedButton);

        Scene promptScene = new Scene(promptBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(promptScene);

        proceedButton.setOnAction(e -> {
            username = usernameField.getText().trim();
            if (username.isEmpty()) {
                username = "Player";
            }
            listLobbies(stage);
        });
    }

    public void setGame(Stage stage) throws IOException {
        stage.setScene(this.gameScene);

        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        GameTimer gameTimer = new GameTimer(gameScene, gc);
        gameTimer.start();
    }

    private void setLobby(Stage stage, boolean isHost, String lobbyName) throws IOException {
        VBox lobbyBox = new VBox();
        lobbyBox.setAlignment(Pos.CENTER);
        lobbyBox.setPadding(new Insets(10));
        lobbyBox.setSpacing(8);

        lobbyTextArea = new TextArea();
        lobbyTextArea.setPrefSize(300, 400);
        lobbyTextArea.setEditable(false);
        lobbyTextArea.setText("Waiting for players...\n");

        Button startGameButton = new Button("Start Game");
        startGameButton.setDisable(!isHost);

        lobbyBox.getChildren().addAll(lobbyTextArea, startGameButton);

        startGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sendMessage("start; " + lobbyName);
            }
        });

        this.lobbyScene = new Scene(lobbyBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(this.lobbyScene);

        if (!isHost) {
            sendMessage("joinLobby; " + lobbyName + "; " + username);
        }
    }

    private void listLobbies(Stage stage) {
        VBox listBox = new VBox();
        listBox.setAlignment(Pos.CENTER);
        listBox.setPadding(new Insets(10));
        listBox.setSpacing(8);

        listLobbiesTextArea = new TextArea();
        listLobbiesTextArea.setPrefSize(300, 400);
        listLobbiesTextArea.setEditable(false);
        listLobbiesTextArea.setText("Fetching available lobbies...\n");

        Button refreshButton = new Button("Refresh");
        Button joinButton = new Button("Join Lobby");

        listBox.getChildren().addAll(listLobbiesTextArea, refreshButton, joinButton);

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sendMessage("listLobbies;");
            }
        });

        joinButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                promptJoinLobby(stage);
            }
        });

        this.lobbyScene = new Scene(listBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(this.lobbyScene);

        sendMessage("listLobbies;");
    }

    private void promptJoinLobby(Stage stage) {
        VBox promptBox = new VBox();
        promptBox.setAlignment(Pos.CENTER);
        promptBox.setPadding(new Insets(10));
        promptBox.setSpacing(8);

        Label label = new Label("Enter lobby name:");
        TextField lobbyNameField = new TextField();
        Button proceedButton = new Button("Join");

        promptBox.getChildren().addAll(label, lobbyNameField, proceedButton);

        Scene promptScene = new Scene(promptBox, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        stage.setScene(promptScene);

        proceedButton.setOnAction(e -> {
            String lobbyName = lobbyNameField.getText().trim();
            if (!lobbyName.isEmpty()) {
                sendMessage("joinLobby; " + lobbyName + "; " + username);
                try {
                    setLobby(stage, false, lobbyName);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void updateLobby(String message) {
        if (lobbyTextArea != null) {
            lobbyTextArea.appendText(message + "\n");
        } else if (listLobbiesTextArea != null) {
            listLobbiesTextArea.appendText(message + "\n");
        }
    }

    public void sendMessage(String message) {
        byte[] msg = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter methods
    public DatagramSocket getSocket() {
        return socket;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public Stage getStage() {
        return stage;
    }
}
