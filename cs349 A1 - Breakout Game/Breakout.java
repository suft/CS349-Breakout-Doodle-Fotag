import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.EventQueue;

public class Breakout extends JFrame {
	public Breakout() {
		initUI();
	}
	
	public Breakout(int speed, int FPS) {
		if (((speed > 0)&&(speed < 7))
				&&((FPS > 0)&&(FPS < 105))) {
			initUI(speed, FPS);
		} else {
			warningWindow();
		}
	}
	
	private void initUI() {
		add(new Canvas());
		
		setTitle("Breakout Game");
		setSize(800, 600);
		setMinimumSize(new Dimension(640, 480));
		setMaximumSize(new Dimension(1920, 1080));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void initUI(int speed, int FPS) {
		add(new Canvas(speed, FPS));
		
		setTitle("Breakout Game");
		setSize(800, 600);
		setMinimumSize(new Dimension(640, 480));
		setMaximumSize(new Dimension(1920, 1080));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void warningWindow() {
		setResizable(false);
		JLabel label = new JLabel(" Warning: invalid input! " + "Speed must be between 1 and 6 and FPS must be between 5 and 100 ");
		add(label);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (args.length == 2) {
					Breakout breakoutGame = new Breakout(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
					breakoutGame.setVisible(true);
				} else {
					Breakout breakoutGame = new Breakout();
					breakoutGame.setVisible(true);
				}
			}
		});
	}
	
}
