import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Path {
	// Data
	private ArrayList<Point> pointList = new ArrayList<Point>(); 
	private int stroke = 3;
	private Color color = Color.BLACK;
	private double timeToDraw;
	
	// update data
	public void addPoint(Point p) {
		pointList.add(p);
	}
	
	public void updateStroke(int n) {
		stroke = n;
	}
	
	public void updateColor(Color c) {
		color = c;
	}
	
	public void updateTimeToDraw(double d) {
		timeToDraw = d;
	}
	
	public void setPath(ArrayList<Point> pL) {
		pointList = pL;
	}
	
	// get data
	public ArrayList<Point> getPointList() {
		return pointList;
	}
	
	public int getStroke() {
		return stroke;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getTimeToDraw() {
		return timeToDraw;
	}
}
