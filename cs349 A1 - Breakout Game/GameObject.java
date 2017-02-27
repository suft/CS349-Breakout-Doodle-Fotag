import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class GameObject implements Default {
	/*
	 * GameObject basic info
	 */
	protected int xPos, yPos;
	protected int imageWidth, imageHeight;
	/* ------------------ basic info end ------------------ */
	
	/*
	 * Get basic info about GameObject
	 */
	public int getXPosition() {
		return xPos;
	}
	
	public int getYPosition() {
		return yPos;
	}
	
	public int getImageWidth() {
		return imageWidth;
	}
	
	public int getImageHeight() {
		return imageHeight;
	}
	
	public Rectangle getCollision() {
		Rectangle Collision = new Rectangle(xPos, yPos, imageWidth, imageHeight);
		return Collision;
	}
	/* ------------------ Get basic info end ------------------ */
	
	/*
	 * Set basic info about GameObject
	 */
	public void setParameters(int newX, int newY, int newHeight, int newWidth) {
		this.xPos = newX;
		this.yPos = newY;
		this.imageWidth = newWidth;
		this.imageHeight = newHeight;
	}
	/* ------------------ Set basic info end ------------------ */
}

class Brick extends GameObject {
	/*
	 * Brick special info
	 */
	private boolean isDestroyed;
	private int maxHealth, currentHealth;
	/* ------------------ Brick info end ------------------ */
	
	/*
	 * Brick constructor
	 */
	public Brick(int x, int y, int width, int height, int health) {
		isDestroyed = false;
		this.xPos = x;
		this.yPos = y;
		this.imageWidth = width;
		this.imageHeight = height;
		maxHealth = health;
		currentHealth = health;
	}
	/* ------------------ Brick constructor end ------------------ */
	
	/*
	 * Get info about brick
	 */
	public boolean checkDestroyed() {
		return isDestroyed;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int checkHealth() {
		return currentHealth;
	}
	/* ------------------ Get basic info end ------------------ */
	
	/*
	 * Set info about brick
	 */
	public void setDestroyed(boolean bool) {
		isDestroyed = bool;
	}
	
	public void takeDamage() {
		currentHealth--;
	}
	/* ------------------ Set basic info end ------------------ */
}

class Ball extends GameObject {
	/*
	 * Ball special info
	 */
	private int speedX, speedY; //move speed in x and y axis
	private int dirX = 1; //move direction in x
	private int dirY = 1; //move direction in y
	/* ------------------Ball info end ------------------ */
	
	/*
	 * Ball constructor
	 */
	public Ball(int x, int y, int radius) {
		this.xPos = x;
		this.yPos = y;
		this.imageHeight = radius;
		this.imageWidth = radius;
		speedX = 0;
		speedY = 0;
	}
	/* ------------------ Ball constructor end ------------------ */
	
	/*
	 * Get info about ball
	 */
	public int getBallSpeedX () {
		return speedX;
	}
	
	public int getBallSpeedY () {
		return speedY;
	}
	
	public int getDirX () {
		return dirX;
	}
	
	public int getDirY () {
		return dirY;
	}
	/* ------------------ Get ball info end ------------------ */
	
	/*
	 * Set info about ball
	 */
	public void setSpeed(int s1, int s2) {
		speedX = s1;
		speedY = s2;
	}
	
	public void setDirX (int n) {
		dirX = n;
	}
	
	public void setDirY (int n) {
		dirY = n;
	}
	
	public void move(int boundaryWidth) {
		this.xPos += speedX*dirX;
		this.yPos += speedY*dirY;
		if (this.xPos <= 0) {
			dirX = 1;
		}
		
		if (this.xPos >= boundaryWidth-this.imageWidth) {
			dirX = -1;
		}
		
		if (this.yPos <= 0) {
			dirY = -1;
		}
		
		if (this.xPos >= boundaryWidth-this.imageWidth) {
			this.xPos = boundaryWidth-this.imageWidth;
		}
		
		if (this.yPos <= 0) {
			this.yPos = 0;
		}
	}
	/* ------------------ Set ball info end ------------------ */
}

class Paddle extends GameObject {
	/*
	 * Paddle special info
	 */
	private int movement;
	private int speed;
	/* ------------------ Paddle info end ------------------ */
	
	/*
	 * Paddle constructor
	 */
	public Paddle(int x, int y, int height, int width, int s) {
		this.xPos = x;
		this.yPos = y;
		this.imageHeight = height;
		this.imageWidth = width;
		speed = s;
	}
	/* ------------------ Paddle constructor end ------------------ */
	
	/*
	 * Set paddle info
	 */
	public void move(int boundaryWidth) {
		this.xPos += movement;
		
		if (this.xPos <= 0) {
			this.xPos = 0;
		}
		
		if (this.xPos >= boundaryWidth-this.imageWidth) {
			this.xPos = boundaryWidth-this.imageWidth;
		}
	}
	
	public void setSpeed(int s) {
		speed = s;
	}
	/* ------------------ Set paddle info end ------------------ */
	
	/*
	 * Key event handler
	 */
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
							   movement = -3*speed;
							   break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
							   movement = 3*speed;
							   break;
			default:
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A: 
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
							   movement = 0;
							   break;
			default:
		}
	}
	/* ------------------ Key event handler end ------------------ */
}
