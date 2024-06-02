package application;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

class Hero extends Sprite {
    private String name;
    private boolean alive;
    private int bulletType;
    private int score;
    private ArrayList<Bullet> bullets;
    private BulletTimer timer;
    private Game game; // Add a reference to the game

    private final static Image HERO_IMAGE = new Image("images/firebender.png", 100, 100, true, true);
    private final static double INITIAL_X = 150;
    private final static double INITIAL_Y = 640;
    public final static int HERO_SPEED = 10;

    Hero(String name, double xPos, double yPos, Game game) {
        super(xPos, yPos, HERO_IMAGE);
        this.name = name;
        this.alive = true;
        this.bullets = new ArrayList<>();
        this.game = game; // Initialize the game reference
    }

    String getName() {
        return this.name;
    }

    int getScore() {
        return this.score;
    }

    ArrayList<Bullet> getBullets() {
        return this.bullets;
    }

    void shoot() {
        double bulletX;
        if (this.isUpgraded()) {
            bulletX = this.xPos + (this.width / 2) - (Bullet.getUpgradedBulletImage().getWidth() / 2);
        } else {
            bulletX = this.xPos + (this.width / 2) - (Bullet.getOrdinaryBulletImage().getWidth() / 2);
        }
        double bulletY = this.yPos + this.height / 2; // Adjust the height based on bullet image
        Bullet bullet = new Bullet(this.bulletType, bulletX, bulletY);
        this.bullets.add(bullet);
        game.getBullets().add(bullet); // Add bullet to the game's bullet list
        // Send bullet update to the server
        game.sendMessage("bulletUpdate;bullet," + bullet.getXPos() + "," + bullet.getYPos());
    }

    void die() {
        this.alive = false;
        Platform.runLater(this::showDeathPrompt);
    }

    private void showDeathPrompt() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("You have died! Game Over.");
        alert.showAndWait();
    }

    boolean isAlive() {
        return this.alive;
    }

    private boolean isUpgraded() {
        return this.bulletType == Bullet.UPGRADED_BULLET;
    }

    void downgradeBullets() {
        this.bulletType = Bullet.ORDINARY_BULLET;
    }

    void upgradeBullets() {
        if (!this.isUpgraded()) {
            this.bulletType = Bullet.UPGRADED_BULLET;
            this.timer = new BulletTimer(this);
            this.timer.start();
        } else {
            this.timer.refresh();
        }
    }

    void gainScore(int increase) {
        this.score += increase;
    }

    public void move() {
        // Update the x position based on the current speed
        this.xPos += this.dx;

        // Ensure the hero stays within the bounds of the game window
        if (this.xPos < 0) {
            this.xPos = 0;
        } else if (this.xPos + this.width > Game.WINDOW_WIDTH) {
            this.xPos = Game.WINDOW_WIDTH - this.width;
        }
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }
}