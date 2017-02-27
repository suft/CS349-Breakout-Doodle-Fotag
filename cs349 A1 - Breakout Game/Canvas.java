import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Rectangle;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.toIntExact;

public class Canvas extends JPanel implements Default {
	/*
	 * Variables
	 */
	//Canvas size
	private int canvasWidth = CANVAS_WIDTH;
	private int canvasHeight = CANVAS_HEIGHT;
	
	//Game state
	private int gameState = SPLASH;
	private boolean gameStarted = false;
	private boolean gamePaused = false;
	
	//Game info
	private int endGameLine;
	private int baseSpeedX, baseSpeedY;
	private int ballSpeed = 5;
	private int FPS = 45;
	private int score = 0;
	
	//Game object
	private Ball ball;
	private Paddle paddle;
	private Brick brickArray[];
	
	//Abilities
	private boolean autoPlay = false;
	private boolean infiniteDamage = false;
	private int autoPlayCounter = 0;
	private int damageAllBlocksCounter = 0;
	private int infiniteDamageCounter = 0;
	
	//Timer
	private Timer timerGame;
	private Timer timerFPS;
	long gameStartTime, gameElapsedSeconds;
	long autoPlayStartTime, autoPlayElapsedSeconds;
	long infiniteDamageStartTime, infiniteDamageElapsedSeconds;
	int numberOfBounceWithoutHittingBrick = 0;
	/* ------------------ Variables End ------------------ */
	
	/*
	 * initiation
	 */
	public Canvas() {
		initCanvas();
		initGameObjects();
	}
	
	public Canvas(int s, int F) {
		ballSpeed = s+2;
		FPS = (F/5)*5;
		initCanvas();
		initGameObjects();
	}
	
	private void initCanvas() {
		//Setup canvas
		setFocusable(true);
		setBackground(Color.WHITE);
		addKeyListener(new TAdapter());
		addMouseListener(new MAdapter());
		
		//Setup timer
		timerGame = new Timer();
		timerFPS = new Timer();
		timerGame.scheduleAtFixedRate(new Update(), 10, 20);
		timerFPS.scheduleAtFixedRate(new Repaint(), 0, 1000/FPS);
	}
	
	private void initGameObjects() {
		//Make game objects
		paddle = new Paddle(canvasWidth/2 - canvasWidth/16, canvasHeight-canvasHeight/8, canvasHeight/35, canvasWidth/8, canvasWidth/170);
		int ballRadius = paddle.getImageHeight();
		int ballInitPosX = paddle.getXPosition()+paddle.getImageWidth()/2-ballRadius/2;
		int ballInitPosY = paddle.getYPosition()-ballRadius;
		ball = new Ball(ballInitPosX, ballInitPosY, ballRadius);
		initBricks();
	}
	
	private void initBricks() {
		brickArray = new Brick[NUMBER_OF_BRICKS];
		int k = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 9; j++) {
				int brickPosX = (i+1)*canvasWidth/12;
				int brickPosY = (j+1)*canvasHeight/20;
				int brickWidth = canvasWidth/12;
				int brickHeight = canvasHeight/20;
				switch (i) {
					case 0:
					case 9: 
						brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, YELLOW_BRICK_HEALTH);
						break;
					case 1:
					case 2:
					case 7:
					case 8: 
						if (j>=1&&j<=7) {
							brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, ORANGE_BRICK_HEALTH);
						} else {
							brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, YELLOW_BRICK_HEALTH);
						}
						break;
					case 3:
					case 4:
					case 5:
					case 6: 
						if ((j>=1&&j<=2)||(j>=6&&j<=7)) {
							brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, ORANGE_BRICK_HEALTH);
						} else if (j>=3&&j<=5) {
							brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, RED_BRICK_HEALTH);
						} else {
							brickArray[k] = new Brick(brickPosX, brickPosY, brickWidth, brickHeight, YELLOW_BRICK_HEALTH);
						}
						break;
					default: 
						brickArray[k] = new Brick((i+1)*canvasWidth/12, (j+1)*canvasHeight/20, canvasWidth/12, canvasHeight/20, 1);
				}
				k++;
			}
		}
	}
	/* ------------------ Initiation End ------------------ */
	
	/*
	 * Event handlers
	 */
	//Key event handler
	private class TAdapter extends KeyAdapter {
		@Override
        public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
			//Paddle movement
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				paddle.keyPressed(e);
				break;
			//Pause game
			case KeyEvent.VK_P:
				if (gamePaused) {
					resumeGame();
				} else {
					pauseGame();
				}
				break;
			//Abilities
			case KeyEvent.VK_J:
				if ((autoPlayCounter >= 5)&&(!gamePaused)&&(gameStarted)) {
					autoPlay();
					autoPlayStartTime = System.currentTimeMillis();
					autoPlayCounter = 0;
				}
				break;
			case KeyEvent.VK_K:
				if ((infiniteDamageCounter >= 15)&&(!gamePaused)&&(gameStarted)) {
					infiniteDamage();
					infiniteDamageStartTime = System.currentTimeMillis();
					infiniteDamageCounter = 0;
				}
				break;
			case KeyEvent.VK_L:
				if ((damageAllBlocksCounter >= 20)&&(!gamePaused)&&(gameStarted)) {
					damageAllBlocks();
					damageAllBlocksCounter = 0;
				}
				break;
			//Splash screen&Setting screen
			case KeyEvent.VK_SPACE:
				if (gameState == SPLASH) {
					gameState = GAME;
					resumeGame();
				}
				break;
			case KeyEvent.VK_ENTER:
				if (gameState == SPLASH) {
					gameState = SETTING;
				} else if (gameState == SETTING) {
					gameState = SPLASH;
					Timer newTimerFPS = new Timer();
					newTimerFPS.scheduleAtFixedRate(new Repaint(), 0, 1000/FPS);
					timerFPS.cancel();
					timerFPS = newTimerFPS;
				}
				break;
			//Restart
			case KeyEvent.VK_R:
				if ((gameState == WIN)||(gameState == LOSE)) {
					timerFPS.cancel();
					restartGame();
					initGameObjects();
				}
				break;
			//Quit
			case KeyEvent.VK_Q:
				if ((gameState == SPLASH)||(gameState == WIN)||(gameState == LOSE)) {
					System.exit(0);
				}
				break;
			default:
			}
            
        }
		
		@Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }
	}
	
	//Mouse event handler
	private class MAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)&&(gameState == GAME)) { //Game Click
				if (!gameStarted){
					//Set speed in x and y
					gameStarted = true;
					Random rand = new Random();
					baseSpeedX = rand.nextInt(ballSpeed/2+1)+ballSpeed/2;
					baseSpeedY = (int)Math.sqrt(ballSpeed*ballSpeed-baseSpeedX*baseSpeedX);
					
					//Set initial direction
					int token = rand.nextInt(2);
					if (token == 1) {
						ball.setDirX(-1);
					}
					ball.setSpeed(baseSpeedX*canvasWidth/500, -1*(baseSpeedY+canvasHeight/400));
				} else {
					Point mousePointToScreen = MouseInfo.getPointerInfo().getLocation();
					Point screenLocation = getLocationOnScreen();
					Point mousePoint = new Point((int)(mousePointToScreen.getX() - screenLocation.getX()), 
												 (int)(mousePointToScreen.getY() - screenLocation.getY()));
					abilityScreenClick(mousePoint);
				}
			} else if (SwingUtilities.isLeftMouseButton(e)&&(gameState == SETTING)) { //Setting Click
				Point mousePointToScreen = MouseInfo.getPointerInfo().getLocation();
				Point screenLocation = getLocationOnScreen();
				Point mousePoint = new Point((int)(mousePointToScreen.getX() - screenLocation.getX()), 
											 (int)(mousePointToScreen.getY() - screenLocation.getY()));
				settingScreenClick(mousePoint);
			}
		}
	}
	
	public void settingScreenClick(Point p) {
		Rectangle speedDown = new Rectangle(9*canvasWidth/20, 11*canvasHeight/40, canvasWidth/40, canvasHeight/30);
		Rectangle speedUp = new Rectangle(16*canvasWidth/20, 11*canvasHeight/40, canvasWidth/40, canvasHeight/30);
		Rectangle FPSDown = new Rectangle(9*canvasWidth/20, 15*canvasHeight/40, canvasWidth/40, canvasHeight/30);
		Rectangle FPSUp = new Rectangle(11*canvasWidth/20, 15*canvasHeight/40, canvasWidth/40, canvasHeight/30);
		
		if (speedDown.contains(p)) {
			if (ballSpeed > 3) {
				ballSpeed--;
			}
		} else if (speedUp.contains(p)) {
			if (ballSpeed < 8) {
				ballSpeed++;
			}
		} else if (FPSDown.contains(p)) {
			if (FPS > 5) {
				FPS -= 5;
			}
		} else if (FPSUp.contains(p)) {
			if (FPS < 100) {
				FPS += 5;
			}
		}
	}
	
	public void abilityScreenClick(Point p) {
		Rectangle abilityAutoPlay = new Rectangle(15*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		Rectangle abilityInfiniteDamage = new Rectangle(20*canvasWidth/100,  94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		Rectangle abilityDamageAllBricks = new Rectangle(25*canvasWidth/100,  94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		
		if (abilityAutoPlay.contains(p)) {
			if ((autoPlayCounter >= 5)&&(!gamePaused)&&(gameStarted)) {
				autoPlay();
				autoPlayStartTime = System.currentTimeMillis();
				autoPlayCounter = 0;
			}
		} else if (abilityInfiniteDamage.contains(p)) {
			if ((infiniteDamageCounter >= 15)&&(!gamePaused)&&(gameStarted)) {
				infiniteDamage();
				infiniteDamageStartTime = System.currentTimeMillis();
				infiniteDamageCounter = 0;
			}
		} else if (abilityDamageAllBricks.contains(p)) {
			if ((damageAllBlocksCounter >= 20)&&(!gamePaused)&&(gameStarted)) {
				damageAllBlocks();
				damageAllBlocksCounter = 0;
			}
		}
	}
	
	/* ------------------ Event handler End ------------------ */
	
	/*
	 * Update game
	 */
	private class Update extends TimerTask {
		@Override
		public void run() {
			paddle.move(canvasWidth);//move paddle
			if (!gameStarted) {//ball follow paddle
				int ballRadius = paddle.getImageHeight();
				int ballInitPosX = paddle.getXPosition()+paddle.getImageWidth()/2-ballRadius/2;
				int ballInitPosY = paddle.getYPosition()-ballRadius;
				ball.setParameters(ballInitPosX, ballInitPosY, ballRadius, ballRadius);
			} else {
				ball.move(canvasWidth);//move ball
			}
			checkCollision();//check for collision
			checkAbilityTime();
			if (numberOfBounceWithoutHittingBrick > 10) {
				resetBall();
			}
		}
	}
	/* ------------------ Update game End ------------------ */
	
	/*
	 * Change game state
	 */
	private void restartGame() {
		gameState = SPLASH;
		
		autoPlayCounter = 0;
		damageAllBlocksCounter = 0;
		infiniteDamageCounter = 0;
		
		score = 0;
		gameStarted = false;
		
		timerGame = new Timer();
		timerFPS = new Timer();
		timerGame.scheduleAtFixedRate(new Update(), 10, 20);
		timerFPS.scheduleAtFixedRate(new Repaint(), 0, 1000/FPS);
	}
	
	private void stopGame() {
		timerGame.cancel();
	}
	
	private void pauseGame() {
		timerGame.cancel();
		gamePaused = true;
	}
	
	private void resumeGame() {
		timerGame = new Timer();
		timerGame.scheduleAtFixedRate(new Update(), 10, 20);
		timerFPS.scheduleAtFixedRate(new Repaint(), 0, 1000/FPS);
		gamePaused = false;
	}
	
	private void resetBall() {
		numberOfBounceWithoutHittingBrick = 0;
		gameStarted = false;
		int ballRadius = paddle.getImageHeight();
		int ballInitPosX = paddle.getXPosition()+paddle.getImageWidth()/2-ballRadius/2;
		int ballInitPosY = paddle.getYPosition()-ballRadius;
		ball.setParameters(ballInitPosX, ballInitPosY, ballRadius, ballRadius);
	}
	/* ------------------ Change game state End ------------------ */
	
	/*
	 * Draw
	 */
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        drawGameObjects(g2d);

        Toolkit.getDefaultToolkit().sync();
    }
	
	private void drawGameObjects(Graphics2D g2d){
		if ((getWidth() != canvasWidth)||(getHeight() != canvasHeight)){
			resize();
		}
		
		switch(gameState) {
			case SPLASH:
				drawSplashScreen(g2d);
				break;
			case SETTING:
				drawSettingScreen(g2d);
				break;
			case WIN:
				drawWinScreen(g2d);
				break;
			case LOSE:
				drawLoseScreen(g2d);
				break;
			default:
				drawPaddle(g2d);
				if (autoPlay) {
					drawAutoPlay(g2d);
				}
				drawBall(g2d);
				drawBricks(g2d);
				
				drawEndGameLine(g2d);
				drawScore(g2d);
				drawAbilityBar(g2d);
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void drawPaddle(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 255));
		g2d.fillRoundRect(paddle.getXPosition(), paddle.getYPosition(), paddle.getImageWidth(), paddle.getImageHeight(), 25, 50);
	}
	
	private void drawAutoPlay(Graphics2D g2d) {
		g2d.setColor(new Color(0, 255, 255, 130 - toIntExact(autoPlayElapsedSeconds)*10));
		g2d.fillRect (0, paddle.getYPosition()-paddle.getImageHeight()/2, canvasWidth, paddle.getImageHeight()/2);
	}
	
	private void drawBall(Graphics2D g2d) {
		if (infiniteDamage) {
			g2d.setColor(new Color(255, 0, 255));
		} else {
			g2d.setColor(new Color(0, 255, 0));
		}
		g2d.fillOval(ball.getXPosition(), ball.getYPosition(), ball.getImageWidth(), ball.getImageHeight());
	}
	
	private void drawBricks(Graphics2D g2d) {
		for (int i = 0; i < NUMBER_OF_BRICKS; i++) {
			if (!brickArray[i].checkDestroyed()) {
				switch (brickArray[i].getMaxHealth()) {
					case 1: g2d.setColor(new Color(255, 255, 0, 255*brickArray[i].checkHealth()));
							break;
					case 2: g2d.setColor(new Color(255, 150, 0, 127*brickArray[i].checkHealth()));
							break;
					case 3: g2d.setColor(new Color(255, 50, 0, 85*brickArray[i].checkHealth()));
							break;
					default: g2d.setColor(new Color(255, 50, 0));
				}
				g2d.setStroke(new BasicStroke((float) canvasWidth/333));
				
				int brickPosX = brickArray[i].getXPosition();
				int brickPosY = brickArray[i].getYPosition();
				int brickWidth = brickArray[i].getImageWidth();
				int brickHeight = brickArray[i].getImageHeight();
				g2d.fillRect(brickPosX, brickPosY, brickWidth, brickHeight);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(brickPosX, brickPosY, brickWidth, brickHeight);
			}
		}
	}
	
	//End game line currently not working due to causing game to end when resizing
	//now it serves purpose of separating game place and ability icon/score
	private void drawEndGameLine(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		endGameLine = paddle.getYPosition()+paddle.getImageHeight()*2;
		g2d.drawLine(0, endGameLine, canvasWidth, endGameLine);
	}
	
	private void drawScore(Graphics2D g2d) {
		int fontSize = Math.min(canvasWidth/40, canvasHeight/30);
		Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);
        FontMetrics metr = this.getFontMetrics(font);
        g2d.setFont(font);
        
		g2d.setColor(Color.BLACK);
		g2d.drawString("Score: " + score, 82*canvasWidth/100, 98*canvasHeight/100);
		g2d.drawRect(82*canvasWidth/100, 94*canvasHeight/100, metr.stringWidth("Score: " + score), canvasHeight/20);
	}
	
	private void drawAbilityBar(Graphics2D g2d) {
		int fontSize = Math.min(canvasWidth/40, canvasHeight/30);
		Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);
        g2d.setFont(font);
        
        if (autoPlayCounter < 5) {
			g2d.setColor(new Color(0, 0, 255, autoPlayCounter*255/6));
			g2d.fillRect(15*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, autoPlayCounter*canvasHeight/(23*5));
		} else {
			g2d.setColor(new Color(0, 0, 255, 5*255/6));
			g2d.fillRect(15*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		}
		if (infiniteDamageCounter < 15) {
			g2d.setColor(new Color(0, 255, 0, infiniteDamageCounter*255/15));
			g2d.fillRect(20*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, infiniteDamageCounter*canvasHeight/(23*15));
		} else {
			g2d.setColor(new Color(0, 255, 0));
			g2d.fillRect(20*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		}
		if (damageAllBlocksCounter < 20) {
			g2d.setColor(new Color(255, 0, 0, damageAllBlocksCounter*255/20));
			g2d.fillRect(25*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, damageAllBlocksCounter*canvasHeight/(23*20));
		} else {
			g2d.setColor(new Color(255, 0, 0));
			g2d.fillRect(25*canvasWidth/100, 94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		}
		
		g2d.setColor(Color.BLACK);
		g2d.drawString("J", 16*canvasWidth/100, 98*canvasHeight/100);
		g2d.drawRect(15*canvasWidth/100,  94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		g2d.drawString("K", 21*canvasWidth/100, 98*canvasHeight/100);
		g2d.drawRect(20*canvasWidth/100,  94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
		g2d.drawString("L", 26*canvasWidth/100, 98*canvasHeight/100);
		g2d.drawRect(25*canvasWidth/100,  94*canvasHeight/100, canvasWidth/30, canvasHeight/23);
	}
	
	private void drawSplashScreen(Graphics g2d) {
		resize();
		pauseGame();
		
		int column = canvasWidth/20;
		int row = canvasHeight/20;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, canvasWidth, canvasHeight);
		
		Font font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/40, canvasHeight/30));
		g2d.setFont(font);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Welcome to Simple Breakout Game!", column, row);
		g2d.drawString("Controls:", 2*column, 2*row);
		g2d.drawString("Mouse Left Click: Shoot ball from paddle", 3*column, 3*row);
		g2d.drawString("A/D or Left/Right: Move paddle Left/Right", 3*column, 4*row);
		g2d.drawString("P: Pause/Resume game", 3*column, 5*row);
		g2d.drawString("J, K, L: Abilities", 3*column, 6*row);
		g2d.drawString("Abilities:", 2*column, 7*row);
		g2d.drawString("J: Invinsible Mode - Ball will never drop below paddle", 3*column, 8*row);
		g2d.drawString("Charges when ball hits paddle (takes 5 hits to charge)", 4*column, 9*row);
		g2d.drawString("Note: doesnt work if ball is already below paddle", 4*column, 10*row);
		g2d.drawString("K: Infinite Damage Mode - Bricks doesnt stop ball anymore", 3*column, 11*row);
		g2d.drawString("Charges when ball hits boundary(takes 15 hits to charge)", 4*column, 12*row);
		g2d.drawString("l: Damage All Bricks - Every brick takes 1 damage", 3*column, 13*row);
		g2d.drawString("Charges when ball hits brick (takes 5 hits to charge)", 4*column, 14*row);
		g2d.drawString("Note: brick destroyed by this doesn't reward score", 4*column, 15*row);
		g2d.drawString("How to tell if ability is ready: ", 3*column, 16*row);
		g2d.drawString("An ability is ready if the square on bottom is filled by color", 4*column, 17*row);
		g2d.drawString("Score: Bounce(1), Bricks: Red(10), Orange(5), Yellow(2)", 3*column, 18*row);
		
		g2d.setFont(new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/50, canvasHeight/45)));
		g2d.drawString("Made By: Hecheng Li  UWID: 20428289", 12*column, row);
		
		g2d.setColor(new Color(255, 255, 255, toIntExact(System.currentTimeMillis()%15)*17));
		g2d.drawString("Press Space to Start Game", 2*column, 19*row);
		g2d.drawString("Press Return to Settings", 8*column, 19*row);
		g2d.drawString("Press Q to Quit", 14*column, 19*row);
	}
	
	private void drawSettingScreen(Graphics2D g2d) {
		resize();
		
		int column = canvasWidth/20;
		int row = canvasHeight/20;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, canvasWidth, canvasHeight);
		
		Font font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/30, canvasHeight/20));
		g2d.setFont(font);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Settings", 4*column, 4*row);
		
		font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/40, canvasHeight/30));
		g2d.setFont(font);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Ball Speed:", 6*column, 6*row);
		g2d.drawRect(9*column, 11*row/2, column/2, 2*row/3);
		g2d.drawLine(9*column, 11*row/2+row/3, 9*column+column/2, 11*row/2+row/3);
		
		g2d.drawRect(16*column, 11*row/2, column/2, 2*row/3);
		g2d.drawLine(16*column, 11*row/2+row/3, 16*column+column/2, 11*row/2+row/3);
		g2d.drawLine(16*column+column/4, 11*row/2, 16*column+column/4, 11*row/2+2*row/3);
		for (int i = 3; i <= ballSpeed; i++) {
			g2d.fillRect((i+7)*column, 11*row/2, column/2, 2*row/3);
		}
		
		g2d.drawString("FPS:", 6*column, 8*row);
		g2d.drawRect(9*column, 15*row/2, column/2, 2*row/3);
		g2d.drawLine(9*column, 15*row/2+row/3, 9*column+column/2, 15*row/2+row/3);
		g2d.drawRect(11*column, 15*row/2, column/2, 2*row/3);
		g2d.drawLine(11*column, 15*row/2+row/3, 11*column+column/2, 15*row/2+row/3);
		g2d.drawLine(11*column+column/4, 15*row/2, 11*column+column/4, 15*row/2+2*row/3);
		g2d.drawString(Integer.toString(FPS), 10*column, 8*row);
		
		g2d.setFont(new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/50, canvasHeight/45)));
		g2d.setColor(new Color(255, 255, 255, toIntExact(System.currentTimeMillis()%15)*17));
		g2d.drawString("Press Return to Go Back", 11*column, 19*row);
	}
	
	private void drawWinScreen(Graphics2D g2d) {
		resize();
		
		int column = canvasWidth/20;
		int row = canvasHeight/20;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, canvasWidth, canvasHeight);
		
		Font font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/20, canvasHeight/10));
		g2d.setFont(font);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Congratulations!", 6*column, 8*row);
		g2d.drawString("You Win! :)", 7*column, 10*row);
		g2d.drawString("You Earned " + score + " Points !", 6*column, 12*row);
		
		drawHighscore(g2d);
		
		font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/50, canvasHeight/45));
		g2d.setFont(font);
		g2d.setColor(new Color(255, 255, 255, toIntExact(System.currentTimeMillis()%15)*17));
		g2d.drawString("Press R to Play Agian", 5*column, 19*row);
		g2d.drawString("Press Q to Quit", 12*column, 19*row);
	}
	
	private void drawHighscore(Graphics2D g2d) {
		int column = canvasWidth/20;
		int row = canvasHeight/20;
		
		Font font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/20, canvasHeight/10));
		
		boolean newHighScore = false;
		byte[] scoreByteArray = toByteArray(score);
		byte[] currentHighScore = toByteArray(0);
		File f = new File("save.txt");
		if(f.exists() && !f.isDirectory()) { 
			Path file = Paths.get("save.txt");
		    try {
		    	currentHighScore = Files.readAllBytes(file);
		    	if (fromByteArray(currentHighScore) <= score) {
		    		Files.write(file, scoreByteArray);
		    		newHighScore = true;
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				f.createNewFile();
				Path file = Paths.get("save.txt");
				Files.write(file, scoreByteArray);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/30, canvasHeight/20));
		g2d.setFont(font);
		if (newHighScore) {
			g2d.drawString("New Highscore!", 6*column, 14*row);
			g2d.drawString("Current HighScore: " + fromByteArray(currentHighScore), 6*column, 16*row);
		} else {
			g2d.drawString("Current Highscore: " + fromByteArray(currentHighScore), 6*column, 14*row);
		}
	}
	
	private void drawLoseScreen(Graphics2D g2d) {
		resize();
		
		int column = canvasWidth/20;
		int row = canvasHeight/20;
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, canvasWidth, canvasHeight);
		
		Font font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/20, canvasHeight/10));
		g2d.setFont(font);
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("You Lose!", 8*column, 10*row);
		
		font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/30, canvasHeight/20));
		g2d.setFont(font);
		g2d.drawString("You Lost All Your Points :(", 6*column, 12*row);
		score = 0;
		
		drawHighscore(g2d);
		
		font = new Font("Comic Sans MS", Font.BOLD, Math.min(canvasWidth/50, canvasHeight/45));
		g2d.setFont(font);
		g2d.setColor(new Color(255, 255, 255, toIntExact(System.currentTimeMillis()%15)*17));
		g2d.drawString("Press R to Restart", 5*column, 19*row);
		g2d.drawString("Press Q to Quit", 12*column, 19*row);
	}
	
	private class Repaint extends TimerTask {
		@Override
		public void run() {
			repaint();
		}
	}
	/* ------------------ Draw End ------------------ */
	
	/*
	 * Resize
	 */
	private void resize() {
		int canvasWidth_New = getWidth();
		int canvasHeight_New = getHeight();
		
		double factorX = (double)canvasWidth_New/canvasWidth;
		double factorY = (double)canvasHeight_New/canvasHeight;
		
		if ((factorX < 0.95)||(factorX > 1.01)
			||(factorY < 0.95)||(factorY > 1.01)) {
			//paddle
			resizePaddle(factorX);
			
			//ball
			resizeBall(factorX, factorY);
			
			//bricks
			resizeBricks();
		}
	}
	
	private void resizePaddle(double factorX) {
		int paddleXPos_New = (int)(paddle.getXPosition()*factorX+0.5);
		canvasWidth = getWidth();
		canvasHeight = getHeight();
	
		paddle.setParameters(paddleXPos_New, canvasHeight-canvasHeight/8, canvasHeight/35, canvasWidth/8);
		paddle.setSpeed(canvasWidth/170);
	}
	
	private void resizeBall(double factorX, double factorY) {
		if (!gameStarted) {
			int ballRadius = paddle.getImageHeight();
			int ballInitPosX = paddle.getXPosition()+paddle.getImageWidth()/2-ballRadius/2;
			int ballInitPosY = paddle.getYPosition()-ballRadius;
			ball.setParameters(ballInitPosX, ballInitPosY, ballRadius, ballRadius);
		} else {
			int ballXPos_New = (int)(ball.getXPosition()*factorX+0.5);
			int ballYPos_New = (int)(ball.getYPosition()*factorY+0.5);
			int ballRadius = paddle.getImageHeight();
			ball.setParameters(ballXPos_New, ballYPos_New, ballRadius, ballRadius);
			
			int ballSpeedX_New = canvasWidth/500 * baseSpeedX;
			int ballSpeedY_New = -1 * canvasHeight/400 * baseSpeedY;
			
			ball.setSpeed(ballSpeedX_New, ballSpeedY_New);
		}
	}
	
	private void resizeBricks() {
		int k = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 9; j++) {
				brickArray[k].setParameters((i+1)*canvasWidth/12,
	  						   				(j+1)*canvasHeight/20,
	  						   				canvasHeight/20,
	  						   				canvasWidth/12);
					
				k++;
			}
		}
	}
	/* ------------------ Resize End ------------------ */
	
	/*
	 * Collision Check
	 */
	private boolean checkLoseCondition() {
		return ball.getCollision().getMinY() > canvasHeight;
	}
	
	private boolean checkWinCondition() {
		boolean isAllBlocksDestroyed = true;
		for (int i = 0; i < NUMBER_OF_BRICKS; i++) {
			if (!brickArray[i].checkDestroyed()) {
				isAllBlocksDestroyed = false;
			}
		}
		return isAllBlocksDestroyed;
	}
	
	private void enableAutoPlay() {
		if (ball.getCollision().getMaxY() > paddle.getYPosition()-paddle.getImageHeight()/2) {
			ball.setDirY(1);
			score += 1;
		}
	}
	
	private boolean checkWallBounce() {
		boolean leftWallBounce = false;
		boolean rightWallBounce = false;
		boolean topWallBounce = false;
		if (ball.getXPosition() == 0) {
			leftWallBounce = true;
		} else if (ball.getXPosition()+ball.getImageWidth() == canvasWidth) {
			rightWallBounce = true;
		} else if (ball.getYPosition() == 0) {
			topWallBounce = true;
		}
		return (leftWallBounce||rightWallBounce||topWallBounce);
	}
	
	private void handleWallBounce() {
		score += 1;
		infiniteDamageCounter++;
		numberOfBounceWithoutHittingBrick++;
	}
	
	private boolean checkPaddleCollision() {
		return ball.getCollision().intersects(paddle.getCollision());
	}
	
	private void handlePaddleCollision() {
		score += 1;
		autoPlayCounter++;
		if (ball.getCollision().getMaxY() < paddle.getCollision().getMinY()) {return;}
		
		int paddlePosLeft = (int)paddle.getCollision().getMinX();
		int ballPosLeft = (int)ball.getCollision().getMinX();
		
		int first = paddlePosLeft + paddle.getImageWidth()/10;
        int second = paddlePosLeft + 9*paddle.getImageWidth()/10;
        
        if (ballPosLeft < first) {
            ball.setDirX(-1);
            ball.setDirY(1);
        }

        if (ballPosLeft >= first && ballPosLeft < second) {
            ball.setDirX(ball.getDirX());
            ball.setDirY(1);
        }

        if (ballPosLeft > second) {
            ball.setDirX(1);
            ball.setDirY(1);
        }
	}
	
	private boolean checkBrickCollision(int n) {
		return (ball.getCollision()).intersects(brickArray[n].getCollision());
	}
	
	private void handleBrickCollision(int n) {
		boolean topHit = false;
		boolean bottomHit = false;
		boolean rightHit = false;
		boolean leftHit = false;
		
		int ballLeft = (int)ball.getCollision().getMinX();
		int ballRight = (int)ball.getCollision().getMaxX();
		int ballTop = (int)ball.getCollision().getMinY();
		int ballBottom = (int)ball.getCollision().getMaxY();
		
		Point pointTop = new Point(ballLeft+ball.getImageWidth()/2, ballTop);
		Point pointBottom = new Point(ballLeft+ball.getImageWidth()/2, ballBottom);
		Point pointRight = new Point(ballRight, ballTop+ball.getImageHeight()/2);
		Point pointLeft = new Point(ballLeft, ballTop+ball.getImageHeight()/2);
		
		if (brickArray[n].getCollision().contains(pointTop)) {
			topHit = true;
		} else if (brickArray[n].getCollision().contains(pointBottom)) {
			bottomHit = true;
		}else if (brickArray[n].getCollision().contains(pointRight)) {
			rightHit = true;
		}else if (brickArray[n].getCollision().contains(pointLeft)) {
			leftHit = true;
		}
		
		if (topHit||bottomHit||leftHit||rightHit) {
			numberOfBounceWithoutHittingBrick = 0;
			brickArray[n].takeDamage();
			if (!infiniteDamage) {
				if (topHit||bottomHit) {
					ball.setDirY(-1*ball.getDirY());
				} else if (leftHit||rightHit) {
					ball.setDirX(-1*ball.getDirX());
				}
			}
			damageAllBlocksCounter++;
			if (brickArray[n].checkHealth() < 0) {
				switch (brickArray[n].getMaxHealth()) {
					case 1:
						score += 2;
						break;
					case 2:
						score += 5;
						break;
					case 3:
						score += 10;
						break;
					default:
				}
				brickArray[n].setDestroyed(true);
			} else {
				score += 1;
			}
		}
	}
	
	private void checkCollision() {
		if (checkLoseCondition()) {
			gameState = LOSE;
			stopGame();
		} else if (checkWinCondition()) {
			gameState = WIN;
			stopGame();
		}
		
		if (autoPlay) {
			enableAutoPlay();
		}
		
		if (checkWallBounce()){
			handleWallBounce();
		}
		
		if (checkPaddleCollision()) {
			handlePaddleCollision();
		}
		
		for (int i = 0; i < NUMBER_OF_BRICKS; i++) {
			if (!brickArray[i].checkDestroyed()){
				if (checkBrickCollision(i)){
					handleBrickCollision(i);
				}
			}
		}
	}
	/* ------------------ Collision check End ------------------ */
	
	/*
	 * Abilities
	 */
	private void autoPlay() {
		autoPlay = true;
	}
	
	private void infiniteDamage () {
		infiniteDamage = true;
	}
	
	private void damageAllBlocks() {
		for (int i = 0; i < NUMBER_OF_BRICKS; i++) {
			if (!brickArray[i].checkDestroyed()) {
				brickArray[i].takeDamage();
				if (brickArray[i].checkHealth() < 0) {
					brickArray[i].setDestroyed(true);
				}
			}
		}
	}
	
	private void checkAbilityTime() {
		if (autoPlay) {
			autoPlayElapsedSeconds = (System.currentTimeMillis() - autoPlayStartTime)/1000;
			if (autoPlayElapsedSeconds > 10) {
				autoPlay = false;
			}
		}
		
		if (infiniteDamage) {
			infiniteDamageElapsedSeconds = (System.currentTimeMillis() - infiniteDamageStartTime)/1000;
			if (infiniteDamageElapsedSeconds > 3) {
				infiniteDamage = false;
			}
		}
	}
	/* ------------------ Abilities End ------------------ */
	
	/*
	 * Byte array conversion
	 */
	byte[] toByteArray(int value) {
	    return new byte[] { 
	        (byte)(value >> 24),
	        (byte)(value >> 16),
	        (byte)(value >> 8),
	        (byte)value };
	}

	int fromByteArray(byte[] bytes) {
	     return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}
	/* ------------------ Byte array conversion End ------------------ */
}
