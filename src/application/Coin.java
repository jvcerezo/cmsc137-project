package application;

import javafx.scene.image.Image;

class Coin extends Item {
	private final static Image COIN_IMAGE = new Image("images/coin_item.png",  50, 50, true, true);
	private final static int GAIN = 10;
	
	Coin(double x, double y) {
		super(x,y,Coin.COIN_IMAGE);
	}
	
	@Override
	public void checkCollision(Hero hero) {
		if(this.collidesWith(hero)) {
			System.out.println(hero.getName() + " has collected a gem");
			this.vanish();
			hero.gainScore(Coin.GAIN);
		}
	}
}
