package application;

import javafx.scene.image.Image;

abstract class Item extends Sprite {
	double speed;
	private final static double COLL_SPEED = 4;
	
	Item(double x, double y, Image image) {
		super(x, y, image);
		this.speed = Item.COLL_SPEED;
	}
	
	void move() {
		this.yPos += this.speed;
		if(this.yPos >= Game.WINDOW_HEIGHT) {
			this.vanish();
		}
	}
	
	abstract void checkCollision(Hero hero);
}
