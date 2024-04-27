package application;

import javafx.scene.image.Image;

class Cabbage extends Item{
	private final static Image CABBAGE_IMAGE = new Image("images/cabbage_item.png",  50, 50, true, true);
	private final static int LOSE = -10;
	
	Cabbage(double x, double y) {
		super(x,y,Cabbage.CABBAGE_IMAGE);
	}
	
	@Override
	public void checkCollision(Hero hero) {
		if(this.collidesWith(hero)) {
			System.out.println(hero.getName() + " has collected a gem");
			this.vanish();
			hero.gainScore(Cabbage.LOSE);
		}
	}
}
