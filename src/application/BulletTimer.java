package application;

public class BulletTimer extends Thread {
	private Hero hero;
	private int time;
	private final static int UPGRADED_TIME = 7;
	
	BulletTimer(Hero hero) {
		this.hero = hero;
		this.time = BulletTimer.UPGRADED_TIME;
	}
	
	void refresh() {
		this.time = BulletTimer.UPGRADED_TIME;
	}
	
	//Counts down and downgrades the bullets after designated time
	private void countDown() {
		while(this.time !=0) {
			try {
				Thread.sleep(1000);
				this.time--;
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		this.hero.downgradeBullets();
	}
	
	@Override
	public void run() {
		this.countDown();
	}
}
