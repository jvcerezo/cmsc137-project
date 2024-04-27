package application;

import javafx.scene.image.Image;

public class Momo extends Item {
	private final static Image MOMO_IMAGE = new Image("images/momo_item.png",  50, 50, true, true);
	
	Momo(double x, double y) {
		super(x, y,Momo.MOMO_IMAGE);
	}
	
	@Override
	void checkCollision(Hero hero) {
		if(this.collidesWith(hero)) {
			System.out.println(hero.getName() + " has eaten a burger!");
			this.vanish();
			hero.upgradeBullets();
		}
	}
}
