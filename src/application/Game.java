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
    private Group root;
    private Canvas canvas;

    public final static int WINDOW_WIDTH = 628;
    public final static int WINDOW_HEIGHT = 760;
    //--------------------------------
    //for chat 
    

	private static final DatagramSocket socket; 
	
	static {
		try {
			socket = new DatagramSocket(); //initializes to any available port 
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final InetAddress address;  
	
	static {
		try { 
			address = InetAddress.getByName("localhost"); 
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	private static final String identifier = "Rachel"; 
	private static final int SERVER_PORT = 8000; //send to server
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
    	try {
    		//thread for receiving messages 
    		ClientThread clientThread = new ClientThread(socket, textArea);
    		clientThread.start();  
    		
    		//send initialization message to the server 
    		byte[]uuid = ("init; " + identifier).getBytes(); 
    		DatagramPacket initialize = new DatagramPacket(uuid, uuid.length, address, SERVER_PORT); 
    		socket.send(initialize); 

    	}catch(IOException e ) {
			throw new RuntimeException(e);

    	}
    }
    public void initGameScene() {
        StackPane root = new StackPane();
        
        // Create a TextArea and set its properties
        textArea.setPromptText("Messages go here...");
        textArea.setPrefSize(50, 75); // Set size to 200x200
        textArea.setEditable(false); // Optionally, make it read-only
        textArea.setFocusTraversable(false); // Disable focus
        textArea.setWrapText(true);

        //textArea.setMouseTransparent(true);//make it non-clickable
        
        // Create a TextField for input
        inputField.setPromptText("Send a message...");
        inputField.setFocusTraversable(false);
        
      //If typing on inputfield, When left arrow or right arrow is pressed, the focus goes back to the canvas 
        inputField.setOnKeyPressed(event -> {
        	if(event.getCode()== KeyCode.LEFT || event.getCode()== KeyCode.RIGHT) {
        		canvas.requestFocus();
        	}else if(event.getCode() == KeyCode.ENTER) {
        		String temp = identifier + ";" + inputField.getText(); //message to send
        		textArea.setText(textArea.getText() +inputField.getText()+"\n");//update message
        		textArea.setScrollTop(Double.MAX_VALUE);
        		byte[] msg = temp.getBytes(); //convert to bytes
        		inputField.setText("");
        		
        		//create a packet & send 
        		DatagramPacket send = new DatagramPacket(msg, msg.length, address, SERVER_PORT);
        		try {
        			socket.send(send);
        		}catch (IOException e) {
        			throw new RuntimeException(e);
        		}
        	}
        });
        
        
        // Create a VBox to hold the TextArea and input field
        VBox vbox = new VBox();
        vbox.getChildren().addAll(textArea, inputField);
        vbox.setAlignment(Pos.TOP_RIGHT); // Align to bottom center
        vbox.setPadding(new Insets(5, 5,0,300));
        
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

        vbox.getChildren().addAll(b1, b2);

        b1.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e) {
        		try {
					setGame(stage);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        });
        return vbox;
    }

    private void setGame(Stage stage) throws IOException {

		
		
        stage.setScene(this.gameScene);

        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        GameTimer gameTimer = new GameTimer(gameScene, gc);
        gameTimer.start();
    }
}
