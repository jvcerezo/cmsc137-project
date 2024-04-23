package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;


public class Main extends Application {
    public static void main(String[] args) {
    	launch(args);
    }
    @Override
    public void start(Stage stage) {
    	Game game = new Game();
    	game.setStage(stage);
    }
}
