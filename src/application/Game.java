package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
    

    public Game() {
        this.root = new Group();
        this.gameScene = new Scene(root);
        this.canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.root.getChildren().add(this.canvas);
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
        		setGame(stage);
        	}
        });
        return vbox;
    }

    private void setGame(Stage stage) {
        stage.setScene(this.gameScene);

        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        GameTimer gameTimer = new GameTimer(gameScene, gc);
        gameTimer.start();
    }
}
