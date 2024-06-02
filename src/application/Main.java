package application;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Game game = new Game();
        game.setStage(stage);

        ClientThread clientThread = new ClientThread(game.getSocket(), game.getTextArea(), game);
        clientThread.start();
    }
}