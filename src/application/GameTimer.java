package application;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameTimer extends AnimationTimer {
    private long startSpawn;
    private long startShoot;
    private GraphicsContext gc;
    private Hero hero;
    private Scene scene;
    private ArrayList<Soldier> soldiers;
    private ArrayList<Item> collectibles;
    private static boolean goLeft;
    private static boolean goRight;
    private double backgroundY;
    
    private Image background =  new Image( "images/gamebg2.jpg");
    Bullet bullets;
    public final static int MIN_SOLDIERS = 6;
    public final static int MAX_SOLDIERS = 8;
    public final static int SOLDIER_TYPES = 2;
    public final static int WIDTH_PER_SOLDIER = 70;
    public final static int SOLDIER_INITIAL_YPOS = -40;
    public final static double SHOOT_DELAY = 0.2;
    public final static double SPAWN_DELAY = 1;
    private static final double BACKGROUND_SPEED = 1;
    
    
    GameTimer(Scene scene, GraphicsContext gc) {
        this.gc = gc;
        this.scene = scene;
        this.hero = new Hero("Pyra");
        this.soldiers = new ArrayList<Soldier>();
        this.collectibles = new ArrayList<Item>();
        this.startSpawn = this.startShoot = System.nanoTime();
        this.prepareActionHandlers();
    }
    
    @Override
    public void handle(long currentNanoTime) {
        this.redrawBackgroundImage();
        this.autoShootSpawn(currentNanoTime);
        this.renderSprites();
        this.moveSprites();
        
        this.drawScore();
        
        if(!this.hero.isAlive()) {
            this.stop();                          //stops AnimationTimer
//            this.drawGameOver();                  //draw Game Over text
        }
    }
    
    void redrawBackgroundImage() {
        this.gc.drawImage(background, 0, 0);
        this.gc.clearRect(0,0, Game.WINDOW_WIDTH, Game.WINDOW_HEIGHT);
        
        //redraw background image (moving effect)
        this.backgroundY += GameTimer.BACKGROUND_SPEED;
        
        this.gc.drawImage(background, 0, this.backgroundY-this.background.getHeight());
        this.gc.drawImage(background, 0, this.backgroundY);
        
        if(this.backgroundY >= Game.WINDOW_HEIGHT) {
            this.backgroundY = Game.WINDOW_HEIGHT-this.background.getHeight();
        }
    }
    void autoShootSpawn(long currentNanoTime) {
        double spawnElapsedTime = (currentNanoTime - this.startSpawn) / 1000000000.0;
        double shootElapsedTime = (currentNanoTime - this.startShoot) / 1000000000.0;
        
        //shoot
        if(shootElapsedTime > GameTimer.SHOOT_DELAY ) {
            this.hero.shoot();
            this.startShoot = System.nanoTime();
        }
        
        //spawn soldiers
        if(spawnElapsedTime > GameTimer.SPAWN_DELAY) {
            this.spawnsoldiers();
            this.startSpawn = System.nanoTime();
        }
    }
    void renderSprites() {
        //draw hero;
        this.hero.render(this.gc);

        //draw sprite in array list
        for (Soldier soldiers : this.soldiers) {
            soldiers.render(this.gc);
        }
        for (Bullet b : this.hero.getBullets()) {
            b.render(this.gc);
        }
    
        for (Item c : this.collectibles) {
            c.render(this.gc);
        }
    }
    
    void moveSprites() {
        this.moveHero();
        this.movesoldiers();
        this.moveBullets();
        this.moveCollectibles();
    }
    
    private void prepareActionHandlers() {
    this.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
        public void handle(KeyEvent e) {
            String code = e.getCode().toString();
            if(code.equals("LEFT")) {
                GameTimer.goLeft = true;
            }
            else if (code.equals("RIGHT")){
                GameTimer.goRight = true;
            }
        }
    });
    
    this.scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
        public void handle(KeyEvent e) {
            String code = e.getCode().toString();
            if(code.equals("LEFT")) {
                GameTimer.goLeft = false;
            }
            else if(code.equals("RIGHT")){
                GameTimer.goRight = false;
            }
        }
    });
    
    }
    
    private void moveHero() {
    if (GameTimer.goLeft) {
        this.hero.setDX(-Hero.HERO_SPEED);
    }
    else if (GameTimer.goRight) {
        this.hero.setDX(Hero.HERO_SPEED);
    }
    else {
        this.hero.setDX(0);
    }
    this.hero.move();
    }
    
    private void movesoldiers() {
    for(int i = 0; i < this.soldiers.size(); i++) {
        Soldier s = this.soldiers.get(i);
        if(s.isVisible()) {
            s.move();
            s.checkCollision(this.hero);
        }
        else {
            this.soldiers.remove(i);
        }
    }
    }
    
    
    private void moveBullets() {
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : this.hero.getBullets()) {
            bullet.move();
            if (bullet.getYPos() >= Game.WINDOW_HEIGHT) {
                // If bullet is off-screen, mark it for removal
                bulletsToRemove.add(bullet);
            }
        }

        // Remove bullets that are off-screen
        this.hero.getBullets().removeAll(bulletsToRemove);
    }
    
    
    private void moveCollectibles() {
    for(int i = 0; i < this.collectibles.size(); i++) {
        Item c= this.collectibles.get(i);
        if(c.isVisible()) {
            c.move();
            c.checkCollision(this.hero);
        }
        else {
            this.collectibles.remove(i);
        }
    }
    }
    
    
    private void spawnsoldiers() {
        int xPos, yPos = GameTimer.SOLDIER_INITIAL_YPOS, type;
        Random r = new Random();
        
        int aswangCount = r.nextInt(GameTimer.MAX_SOLDIERS-GameTimer.MIN_SOLDIERS+1)+GameTimer.MIN_SOLDIERS;
        for(int i = 0; i < aswangCount; i++) {
            type = r.nextInt(GameTimer.SOLDIER_TYPES);
            
            xPos = i*GameTimer.WIDTH_PER_SOLDIER;
//            this.soldiers.add(new Aswang(type, xPos, yPos, this.collectibles));
            this.soldiers.add(new Soldier(type, xPos, yPos, this.collectibles, this.hero));
        }
    }
    //
    private void drawScore() {
        this.gc.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        this.gc.setFill(Color.YELLOW);
        this.gc.fillText("Score: ",20, 30);
        this.gc.setFont(Font.font("Verdana", FontWeight.BOLD,30));
        this.gc.setFill(Color.WHITE);
        this.gc.fillText(hero.getScore()+"",90, 30);
    }
}
