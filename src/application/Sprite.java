package application;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
	protected Image img;
	protected double xPos, yPos, dx, dy;
	protected boolean visible;
	protected double width;
	protected double height;
	
	public Sprite (double xPos, double yPos, Image image) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.loadImage(image);
		this.visible = true;
	}
	
	private Rectangle2D getBounds() {
		return new Rectangle2D(this.xPos, this.yPos, this.width, this.height);
	}
	
	private void setSize() {
		this.width = this.img.getWidth();
		this.height = this.img.getHeight();
	}
	
	protected boolean collidesWith(Sprite rect2) {
		Rectangle2D rectangle1 = this.getBounds();
		Rectangle2D rectangle2 = rect2.getBounds();
		
		return rectangle1.intersects(rectangle2);
	}
	
	protected void loadImage(Image image) {
		try {
			this.img = image;
			this.setSize(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void render(GraphicsContext gc) {
		gc.drawImage(this.img, this.xPos, this.yPos);
	}
	
	public Image getImage() {
		return this.img;
	}
	
	public double getXPos() {
		return this.xPos;
	}
	
	public double getYPos() {
		return this.yPos;
	}
	
	public void setDX(int val) {
		this.dx = val;
	}
	
	public void setDY(int val) {
		this.dy = val;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void vanish() {
		this.visible = false;
	}
}