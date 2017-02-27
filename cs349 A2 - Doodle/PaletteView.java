import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;

public class PaletteView extends JPanel implements IView {
	
		// the model that this view is showing
		private Model model;
		private JButton colorChooser;
		private JSlider chooseThickness;
		private JButton black;
		private JButton red;
		private JButton blue;
		private JButton yellow;
		private JButton orange;
		private JButton pink;
		private JButton green;
		
		PaletteView(Model model, Controller controller) {
			// set the model 
			this.model = model;
			initPalette();
			
			black.addActionListener(controller);
			red.addActionListener(controller);
			blue.addActionListener(controller);
			yellow.addActionListener(controller);
			orange.addActionListener(controller);
			pink.addActionListener(controller);
			green.addActionListener(controller);
			chooseThickness.addChangeListener(controller);
			colorChooser.addActionListener(controller);
		} 
		
		private void initPalette() {
			black = new JButton("Black");
			red = new JButton("Red");
			blue = new JButton("Blue");
			yellow = new JButton("Yellow");
			orange = new JButton("Orange");
			pink = new JButton("Pink");
			green = new JButton("Green");
			colorChooser = new JButton("<html>Color<br />Chooser</html>");
			chooseThickness = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
			
			black.setForeground(Color.BLACK);
			black.setBackground(Color.BLACK);
			red.setForeground(Color.RED);
			red.setBackground(Color.RED);
			blue.setForeground(Color.BLUE);
			blue.setBackground(Color.BLUE);
			yellow.setForeground(Color.YELLOW);
			yellow.setBackground(Color.YELLOW);
			orange.setForeground(Color.ORANGE);
			orange.setBackground(Color.ORANGE);
			pink.setForeground(Color.PINK);
			pink.setBackground(Color.PINK);
			green.setForeground(Color.GREEN);
			green.setBackground(Color.GREEN);
			colorChooser.setForeground(model.getColor());
			colorChooser.setBackground(model.getColor());
			
			black.setFocusable(false);
			black.setFocusPainted(false);
			red.setFocusable(false);
			red.setFocusPainted(false);
			blue.setFocusable(false);
			blue.setFocusPainted(false);
			yellow.setFocusable(false);
			yellow.setFocusPainted(false);
			orange.setFocusable(false);
			orange.setFocusPainted(false);
			pink.setFocusable(false);
			pink.setFocusPainted(false);
			green.setFocusable(false);
			green.setFocusPainted(false);
			colorChooser.setFocusable(false);
			colorChooser.setFocusPainted(false);
			
			chooseThickness.setPaintTicks(true);
			chooseThickness.setSnapToTicks(true);
			chooseThickness.setPaintLabels(true);
			chooseThickness.setMajorTickSpacing(1);
			chooseThickness.setName("chooseThickness");
			
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.weighty = 0.3;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            this.add(black, gbc);
            gbc.gridy++;
            this.add(yellow, gbc);
            gbc.gridy++;
            this.add(red, gbc);
            gbc.gridy++;
            this.add(blue, gbc);
            gbc.gridy++;
            this.add(green, gbc);
            gbc.gridy++;
            this.add(orange, gbc);
            gbc.gridy++;
            this.add(pink, gbc);
            gbc.gridy++;
            
            JLabel takeSpace = new JLabel("");
            this.add(takeSpace, gbc);
            gbc.gridy++;
            
            gbc.anchor = GridBagConstraints.LAST_LINE_END;
            this.add(chooseThickness, gbc);
            
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx++;
            gbc.weightx = 0;
            gbc.gridy = 0;
            gbc.weighty = 1;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.VERTICAL;
            this.add(colorChooser, gbc);
		}

		// IView interface 
		public void updateView() {
			colorChooser.setForeground(model.getColor());
			colorChooser.setBackground(model.getColor());
			chooseThickness.setValue(model.getStrokeThickness());
			repaint();
		}
		
		@Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        Graphics2D g2d = (Graphics2D) g;

	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);

	        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
	                RenderingHints.VALUE_RENDER_QUALITY);

	        drawStrokeThickness(g2d);
	        resize();

	        Toolkit.getDefaultToolkit().sync();
	    }
		
		private void drawStrokeThickness(Graphics2D g2d) {
			
			g2d.setColor(Color.WHITE);
			g2d.fillRect(chooseThickness.getX(), pink.getY()+pink.getHeight(), chooseThickness.getX()+chooseThickness.getWidth(), chooseThickness.getY()-(pink.getY()+pink.getHeight()));
			g2d.setStroke(new BasicStroke(model.getStrokeThickness()));
			g2d.setColor(model.getColor());
			g2d.drawLine((chooseThickness.getX()+chooseThickness.getWidth())/4, 
						 (chooseThickness.getY()+pink.getY()+pink.getHeight())/2, 
						 (chooseThickness.getX()+chooseThickness.getWidth())*3/4, 
						 (chooseThickness.getY()+pink.getY()+pink.getHeight())/2);
		}
		
		private void resize() {
			int width = this.getTopLevelAncestor().getWidth();
			int height = this.getTopLevelAncestor().getHeight();
			int newWidth = sizeRatio(width);
			this.setPreferredSize(new Dimension(newWidth, height));
		}
		
		private int sizeRatio(int width) {
			int newWidth = (width/50)*50/5;
			return Math.max(newWidth, 140);
		}
}
