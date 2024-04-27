package application;


import javafx.scene.image.Image;

public class Bullet extends Sprite{
	private int type;
	private int damage;
	
	private final static double BULLET_SPEED = 4;
	final static int ORDINARY_BULLET_DAMAGE = 10;
	private final static int UPGRADED_BULLET_DAMAGE = 25;
	private final static Image ORDINARY_BULLET_IMAGE = new Image("images/fire.gif", 150,150, true, true);
	private final static Image UPGRADED_BULLET_IMAGE = new Image("images/fire.gif", 300, 300, true, true);
	
	public final static int ORDINARY_BULLET = 0;
	public final static int UPGRADED_BULLET = 1;
	
	Bullet(int type, double x, double y) {
		super(x, y, type==Bullet.ORDINARY_BULLET? Bullet.getOrdinaryBulletImage(): Bullet.getUpgradedBulletImage());
		this.type = type;
		this.damage = this.type==Bullet.ORDINARY_BULLET?Bullet.ORDINARY_BULLET_DAMAGE: Bullet.UPGRADED_BULLET_DAMAGE;
	}
	
	int getDamage() {
		return this.damage;
	}
	
	void move() {
		this.yPos -= Bullet.BULLET_SPEED;
		if(this.yPos <= 0) {
			this.vanish();
		}
	}

	public static Image getUpgradedBulletImage() {
		return UPGRADED_BULLET_IMAGE;
	}

	public static Image getOrdinaryBulletImage() {
		return ORDINARY_BULLET_IMAGE;
	}
}
