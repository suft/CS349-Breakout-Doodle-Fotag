import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Toolbar extends JPanel implements IView {
	private JPanel rating;
	private JButton gridMode;
	private JButton listMode;
	private JButton openButton;
	private JButton resetButton;
	private static Image star_full = null;
	private static Image star_empty = null;
	private static Image grid = null;
	private static Image list = null;
	private static Image open = null;
	private static Image reset = null;
	
	ArrayList<star> stars = new ArrayList<star>();
	
	private ImageCollectionModel ic_model;
	private Controller controller;
	
	private JPanel panelW;
	private JPanel panelE;
	private JLabel labelC;
	private JLabel filter;
	
	Toolbar(ImageCollectionModel ic_model, Controller controller) {
		loadIMG();
		
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
		
		this.ic_model = ic_model;
		this.controller = controller;
		
		//panel for grid/list button
		panelW = new JPanel();
		panelW.setLayout(new BoxLayout(panelW, BoxLayout.X_AXIS));
		panelW.setBackground(Color.WHITE);
		panelW.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		gridMode = new JButton(new ImageIcon(grid));
		gridMode.setBackground(Color.WHITE);
		gridMode.setFocusPainted(false);
		gridMode.setPreferredSize(new Dimension(50, 50));
		gridMode.setSelected(true);	
		gridMode.setEnabled(false);
		gridMode.setName("grid mode");
		gridMode.addActionListener(controller);
		
		listMode = new JButton(new ImageIcon(list));
		listMode.setBackground(Color.WHITE);
		listMode.setFocusPainted(false);
		listMode.setPreferredSize(new Dimension(50, 50));
		listMode.setName("list mode");
		listMode.addActionListener(controller);
		
		panelW.add(gridMode);
		panelW.add(listMode);
		
		//label for word "Fotag!"
		labelC = new JLabel();
		labelC.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
		labelC.setText(" Fotag!");
		
		//label for filter etc.
		panelE = new JPanel();
		panelE.setLayout(new BoxLayout(panelE, BoxLayout.X_AXIS));
		panelE.setBackground(Color.WHITE);
		panelE.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		
		openButton = new JButton(new ImageIcon(open));
		openButton.setSize(new Dimension(50, 50));
		openButton.setBackground(Color.WHITE);
		openButton.setFocusPainted(false);
		openButton.setName("open button");
		openButton.addActionListener(controller);
		
		resetButton = new JButton(new ImageIcon(reset));
		resetButton.setSize(new Dimension(50, 50));
		resetButton.setBackground(Color.WHITE);
		resetButton.setFocusPainted(false);
		resetButton.setName("reset button");
		resetButton.addMouseListener(new MouseAdapter() { //button to reset filter, cant add to controller
			@Override
			public void mousePressed(MouseEvent e) {
				for (int i = 0; i < 5; i++) {
					stars.get(i).setIcon(new ImageIcon(star_empty));
				}
				ic_model.setRateFilter(0);
			}
		});
		
		filter = new JLabel();
		filter.setFont(new Font("Courier New", Font.BOLD, 12));
		filter.setText(" Filter by:");
		
		rating = new JPanel();
		rating.setBackground(Color.WHITE);
		for (int i = 0; i < 5; i++) {
			star s = new star(i);
			stars.add(s);
			rating.add(s);
		}
		
		panelE.add(openButton);
		panelE.add(filter);
		panelE.add(rating);
		panelE.add(resetButton);
		
		add(panelW, BorderLayout.WEST);
		add(labelC, BorderLayout.CENTER);
		add(panelE, BorderLayout.EAST);
	}
	
	//normal toolbar
	private void drawNormal() {
		this.removeAll();
		
		panelE.setLayout(new BoxLayout(panelE, BoxLayout.X_AXIS));
		
		panelE.add(openButton);
		panelE.add(filter);
		panelE.add(rating);
		panelE.add(resetButton);
		
		add(panelW, BorderLayout.WEST);
		add(labelC, BorderLayout.CENTER);
		add(panelE, BorderLayout.EAST);
	}
	
	//toolbar when window too small
	private void drawShrinked() {
		this.removeAll();
		panelE.removeAll();
		
		JPanel panelE = new JPanel();
		panelE.setLayout(new BoxLayout(panelE, BoxLayout.Y_AXIS));
		
		JPanel panelE1 = new JPanel();
		panelE1.add(openButton);
		panelE1.add(resetButton);
		panelE1.setBackground(Color.WHITE);
		
		JPanel panelE2 = new JPanel();
		panelE2.add(filter);
		panelE2.add(rating);
		panelE2.setBackground(Color.WHITE);
		
		panelE.add(panelE1);
		panelE.add(panelE2);
		
		panelW.add(labelC);
		add(panelW, BorderLayout.NORTH);
		add(panelE, BorderLayout.SOUTH);
	}
	
	// load img
	private void loadIMG() {
		if (star_full == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("star-full.png"));
			} catch (IOException e) {
				
			}
			star_full = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		}

		if (star_empty == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("star-empty.png"));
			} catch (IOException e) {
				
			}
			star_empty = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		}
		
		if (grid == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("grid.png"));
			} catch (IOException e) {
				
			}
			grid = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		}

		if (list == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("list.png"));
			} catch (IOException e) {
				
			}
			list = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		}
		
		if (open == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("open.png"));
			} catch (IOException e) {
				
			}
			open = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		}

		if (reset == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("reset.png"));
			} catch (IOException e) {
				
			}
			reset = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		}
	}
	
	private class star extends JLabel {
		
		star(int n) {
			setBackground(Color.WHITE);
			setIcon(new ImageIcon(star_empty));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					updateStars(n);
					ic_model.notifyViews();
				}
			});
		}
		
		public void updateStars(int n) {
			for (int i = 0; i < 5; i++) {
				if (i <= n)
					stars.get(i).setIcon(new ImageIcon(star_full));
				else
					stars.get(i).setIcon(new ImageIcon(star_empty));
			}
			ic_model.setRateFilter(n+1);
		}
	}
	
	public void updateView() {
		if (this.getWidth() < 470) {
			drawShrinked();
		} else {
			drawNormal();
		}
		if (ic_model.getMode() == 0) {
			gridMode.setSelected(true);	
			gridMode.setEnabled(false);
			listMode.setSelected(false);
			listMode.setEnabled(true);
		} else if (ic_model.getMode() == 1) {
			listMode.setSelected(true);	
			listMode.setEnabled(false);
			gridMode.setSelected(false);	
			gridMode.setEnabled(true);
		}
		revalidate();
		repaint();
	}
}
