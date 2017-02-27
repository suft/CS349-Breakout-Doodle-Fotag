import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

interface IView {
	public void updateView();
}

//Listen to controller
//Tells view what to do

public class Model {
	// data
	private Color selectedColor = Color.BLACK;
	private int strokeThickness = 3;
	private boolean draw = false;
	private int maxX = 321, maxY = 315;
	private int playTick = 0;
	private int tickMax = 0;
	private boolean edited = false;
	
	// 0 - not playing, 1 - start playing, 2 - already playing
	private int play = 0;
	
	// 0 - Full size
	// 1 - Fit
	private int mode = 0;
	
	// all paths
	private ArrayList<Path> paths = new ArrayList<Path>();
	
	// the views
	private IView canvasView;
	private IView paletteView;
	private IView TimelineView;

	// set the view observers
	public void addView(IView view, int n) {
		switch(n) {
			case 0:
				this.canvasView = view;
				break;
			case 1:
				this.paletteView = view;
				break;
			case 2:
				this.TimelineView = view;
				break;
		}
		view.updateView();
	}

	// notify the IView observer
	private void notifyObserver() {
		canvasView.updateView();
		paletteView.updateView();
		TimelineView.updateView();
	}
	
	// update data
	public void addPath() {
		paths.add(new Path());
	}
	
	public void updatePaths0(Point p, int s, Color c) {
		paths.get(paths.size()-1).addPoint(p);
		paths.get(paths.size()-1).updateStroke(s);
		paths.get(paths.size()-1).updateColor(c);
		notifyObserver();
	}
	
	public void updateColor(Color newColor) {
		this.selectedColor = newColor;
		notifyObserver();
	}
	
	public void updateStrokeThickness(int n) {
		this.strokeThickness = n;
		notifyObserver();
	}
	
	public void updateDraw(boolean bool) {
		draw = bool;
		notifyObserver();
	}
	
	public void updateMode(int n) {
		mode = n;
		notifyObserver();
	}
	
	public void updateMax(int n, int m) {
		maxX = n;
		maxY = m;
	}
	
	public void updatePlay(int n) {
		play = n;
		notifyObserver();
	}
	
	public void updatePlayTick(int n) {
		playTick = n;
		notifyObserver();
	}
	
	public void updateEdited (boolean bool) {
		edited = bool;
	}
	
	public void setTickMax(int n) {
		tickMax = n;
		notifyObserver();
	}
	
	public void setPaths(ArrayList<Path> p) {
		paths = p;
		setTickMax(paths.size()*100);
		updatePlayTick(tickMax);
		notifyObserver();
	}
	
	// delete data
	public void deletePaths(int n, int m) {
		if (paths.size() > 0) {
			for (int i = paths.size()-1; i >= n; i--) {
				paths.remove(i);
			}
			double l = (double)m%100/100*paths.get(n-1).getPointList().size();
			for (int i = paths.get(n-1).getPointList().size()-1; i >= l; i-- ) {
				
				paths.get(paths.size()-1).getPointList().remove(i);
			}
			notifyObserver();
		}
	}
	
	public void deleteAllPaths() {
		paths = new ArrayList<Path>();
		notifyObserver();
	}
	
	// get data
	public Color getColor() {
		return this.selectedColor;
	}
	
	public int getStrokeThickness() {
		return this.strokeThickness;
	}
	
	public boolean getDraw() {
		return draw;
	}
	
	public ArrayList<Path> getPaths0() {
		return paths;
	}
	
	public int getMode() {
		return mode;
	}
	
	public int getMaxX() {
		return maxX;
	}
	
	public int getMaxY() {
		return maxY;
	}
	
	public int getPlay() {
		return play;
	}
	
	public int getPlayTick() {
		return playTick;
	}
	
	public int getTickMax() {
		return tickMax;
	}
	
	public boolean getEdited() {
		return edited;
	}
	
}
