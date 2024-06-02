package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.image.Image;

public class Soldier extends Sprite {
    private int health;
    private Hero hero;
    private List<Item> collectibles;
    private boolean hasItem;
    private final static double SOLDIER_SPEED = 1;
    private final static Image BASIC_SOLDIER_IMAGE = new Image("images/soldier.png", 100, 100, true, true);
    private final static Image MOUNTED_SOLDIER_IMAGE = new Image("images/mounted_soldier.png", 100, 100, true, true);
    private final static int BASIC_SOLDIER_HEALTH = 10;
    private final static int MOUNTED_SOLDIER_HEALTH = 20;
    private final static int ITEM_FREQUENCY = 11;
    private final static int DEATH_SCORE = 1;
    
    public Soldier(int type, double x, double y, List<Item> collectibles, Hero hero) { // Updated constructor
        super(x, y, type == 0 ? Soldier.MOUNTED_SOLDIER_IMAGE : Soldier.BASIC_SOLDIER_IMAGE);
        this.health = type == 0 ? Soldier.MOUNTED_SOLDIER_HEALTH : Soldier.BASIC_SOLDIER_HEALTH;
        this.collectibles = collectibles;
        this.hero = hero;
    }

    void move() {
        this.yPos += Soldier.SOLDIER_SPEED;
        if (this.yPos >= Game.WINDOW_HEIGHT) {
            this.vanish();
        }
    }
    
    private void getHit(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.die();
        }
    }
    
    private void die() {
        int type;
        Item newCollectible = null;
        Random r = new Random();
        
        type = r.nextInt(Soldier.ITEM_FREQUENCY);
        switch (type) {
            case 1: 
                newCollectible = new Momo(this.xPos, this.yPos);
                this.hasItem = true;
                break;
            case 5:
                newCollectible = new Cabbage(this.xPos, this.yPos);
                this.hasItem = true;
                break;
            case 10: 
                newCollectible = new Coin(this.xPos, this.yPos); 
                this.hasItem = true;
                break;
            default: 
                this.hasItem = false;
                break;
        }
        
        if (this.hasItem) {
            this.collectibles.add(newCollectible);
        }
        this.vanish();
        hero.gainScore(Soldier.DEATH_SCORE);
    }
    
    void checkCollision(Hero hero) {
        for (int i = 0; i < hero.getBullets().size(); i++) {
            if (this.collidesWith(hero.getBullets().get(i))) {
                int damage = hero.getBullets().get(i).getDamage();
                this.getHit(damage);
                hero.getBullets().get(i).vanish();
                hero.getBullets().remove(i);
            }
        }
        if (this.collidesWith(hero)) {
            hero.die();
        }
    }
}
