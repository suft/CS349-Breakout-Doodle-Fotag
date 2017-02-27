import javax.swing.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

class CanvasView extends JPanel implements IView {
	// model
	private Model model;
	
	// max and min of all points, used for scaling
	private int maxX = 321;
	private int maxY = 315;
	private int minX = -1;
	private int minY = -1;
	
	// canvas constructor
	CanvasView(Model model, Controller controller) {
		// set the model 
		this.model = model;
		this.setBackground(Color.WHITE);
		// setup the event to go to the controller
		this.addMouseListener(controller);
		this.addMouseMotionListener(controller);
	} 

	// IView interface 
	public void updateView() {
		if (model.getMode() == 0) { //full size mode
			this.setPreferredSize(new Dimension(maxX, maxY));
		} else if (model.getMode() == 1) { //fit mode
			this.setPreferredSize(null);
		}
		updateMaxMin();
		this.revalidate();
		repaint();
	}
	
	//update min and max
	private void updateMaxMin() {
		ArrayList<Path> currentPaths;
		currentPaths = model.getPaths0();
		int newMaxX = 321, newMinX = 0, newMaxY = 315, newMinY = 0;
		for (int i = 0; i < currentPaths.size(); i++) {
			for (int j = 0; j < currentPaths.get(i).getPointList().size(); j++) {
				ArrayList<Point> drawThisPath = currentPaths.get(i).getPointList();
				for (int k = 0; k < drawThisPath.size(); k++) {
					newMaxX = Math.max(newMaxX, (int)drawThisPath.get(k).getX());
					newMaxY = Math.max(newMaxY, (int)drawThisPath.get(k).getY());
					newMinX = Math.min(newMinX, (int)drawThisPath.get(k).getX());
					newMinY = Math.min(newMinY, (int)drawThisPath.get(k).getY());
				}
			}
		}
		model.updateMax(newMaxX, newMaxY);
		maxX = newMaxX;
		minX = newMinX;
		maxY = newMaxY;
		minY = newMinY;
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        
        if (model.getMode() == 0) { //full size mode
        	drawFullSize(g2d);
        } else if (model.getMode() == 1) { //fit mode
        	drawFit(g2d);
        }
        
        Toolkit.getDefaultToolkit().sync();
    }
	
	// draw in full size
	private void drawFullSize(Graphics2D g2d) {
		// update max and min first
		updateMaxMin();
		
		// get necessary variables
		ArrayList<Path> currentPaths = model.getPaths0();
		int tick = model.getPlayTick();
		
		// loop through all stroke and points
		// if tick = 0 then no stroke, +99 so it draws when 0<tick<100
		for (int i = 0; i < (tick+99)/100; i++) {
			for (int j = 0; j < currentPaths.get(i).getPointList().size(); j++) {
				// get points on selected stroke
				ArrayList<Point> drawThisPath = currentPaths.get(i).getPointList();
				
				// setup color and stroke thickness
				g2d.setColor(currentPaths.get(i).getColor());
				g2d.setStroke(new BasicStroke(currentPaths.get(i).getStroke()));
				
				if (drawThisPath.size() == 1) { // if stroke has only 1 point, to avoid overflow
					g2d.drawLine((int)drawThisPath.get(0).getX(), (int)drawThisPath.get(0).getY(),
							(int)drawThisPath.get(0).getX(), (int)drawThisPath.get(0).getY());
				} else {
					if (i == tick/100) { // draw only part of last stroke (when play slider is moved, part of this stroke got cut)
						for (int k = 1; k < (double)tick%100/100*drawThisPath.size(); k++) {
							g2d.drawLine((int)drawThisPath.get(k-1).getX(), (int)drawThisPath.get(k-1).getY(),
									(int)drawThisPath.get(k).getX(), (int)drawThisPath.get(k).getY());
						}
					} else { // draw this stroke
						for (int k = 1; k < drawThisPath.size(); k++) {
							g2d.drawLine((int)drawThisPath.get(k-1).getX(), (int)drawThisPath.get(k-1).getY(),
									(int)drawThisPath.get(k).getX(), (int)drawThisPath.get(k).getY());
						}
					}
				}
			}
		}
		repaint();
	}
	
	// draw in fit mode
	private void drawFit(Graphics2D g2d) {
		// update max and min first
		updateMaxMin();
		
		// get necessary variables
		ArrayList<Path> currentPaths;
		currentPaths = model.getPaths0();
		int tick = model.getPlayTick();
		
		// loop through all stroke and points
		// if tick = 0 then no stroke, +99 so it draws when 0<tick<100
		for (int i = 0; i < (tick+99)/100; i++) {
			for (int j = 0; j < currentPaths.get(i).getPointList().size(); j++) {
				// get points on selected stroke
				ArrayList<Point> drawThisPath = currentPaths.get(i).getPointList();
				
				// setup color and stroke thickness
				g2d.setColor(currentPaths.get(i).getColor());
				g2d.setStroke(new BasicStroke(currentPaths.get(i).getStroke()));
				
				// use affine transform to scale image so everything is shown
				AffineTransform transform = new AffineTransform();
				transform.translate(-1*(double)minX, -1*(double)minY);
				transform.scale((double)this.getWidth()/maxX, (double)this.getHeight()/maxY);
				g2d.setTransform(transform);
				
				if (drawThisPath.size() == 1) { // if stroke has only 1 point, to avoid overflow
					g2d.drawLine((int)drawThisPath.get(0).getX(), (int)drawThisPath.get(0).getY(),
							(int)drawThisPath.get(0).getX(), (int)drawThisPath.get(0).getY());
				} else {
					if (i == tick/100) { // draw only part of last stroke (when play slider is moved, part of this stroke got cut)
						for (int k = 1; k < (double)tick%100/100*drawThisPath.size(); k++) {
							g2d.drawLine((int)drawThisPath.get(k-1).getX(), (int)drawThisPath.get(k-1).getY(),
									(int)drawThisPath.get(k).getX(), (int)drawThisPath.get(k).getY());
						}
					} else { // draw this stroke
						for (int k = 1; k < drawThisPath.size(); k++) {
							g2d.drawLine((int)drawThisPath.get(k-1).getX(), (int)drawThisPath.get(k-1).getY(),
									(int)drawThisPath.get(k).getX(), (int)drawThisPath.get(k).getY());
						}
					}
				}
			}
		}
		repaint();
	}
}



