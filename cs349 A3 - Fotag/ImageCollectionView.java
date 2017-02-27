import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

public class ImageCollectionView extends JPanel {
	ImageCollectionModel ic_model;
	ArrayList<ImageView> viewList = new ArrayList<ImageView>();
	
	ImageCollectionView(ImageCollectionModel ic_model, Controller controller) {
		this.ic_model = ic_model;
		this.setLayout(new GridBagLayout());
	}
	
	public void addView(ImageView newView) {
		viewList.add(newView);
		this.add(newView);
	}
	
	private void resetPictures() {
		this.removeAll();

		if (ic_model.getMode() == 0) drawGrid();
		if (ic_model.getMode() == 1) drawList();
	}
	
	private void drawGrid() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int numberToPrint = 0;
		for (int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).getModel().getRating() >= ic_model.getRateFilter()) {
				numberToPrint++;
			}
		}
		
		int column = this.getTopLevelAncestor().getWidth()/180;
		int row = numberToPrint/column;
		if (numberToPrint%column != 0) {
			row++;
		}
		
		this.setPreferredSize(new Dimension(column*180, row*220));
		int counter = 0;
		c.ipadx = 20;
		c.ipady = 20;
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		while (counter < viewList.size()) {
			if (counter >= viewList.size()) break;
			if (viewList.get(counter).getModel().getRating() >= ic_model.getRateFilter()) {
				this.add(viewList.get(counter),c);
				c.gridx++;
			}
			counter++;
			if (c.gridx >= column) {
				c.gridy++;
				c.gridx = 0;
			}
		}
		revalidate();
		repaint();
	}
	
	private void drawList() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int numberToPrint = 0;
		for (int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).getModel().getRating() >= ic_model.getRateFilter()) {
				numberToPrint++;
			}
		}
		
		this.setPreferredSize(new Dimension(340, numberToPrint*170));
		
		int counter = 0;
		c.ipadx = 20;
		c.ipady = 20;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		while (counter < viewList.size()) {
			if (counter >= viewList.size()) break;
			if (viewList.get(counter).getModel().getRating() >= ic_model.getRateFilter()) {
				this.add(viewList.get(counter),c);
				c.gridy++;
			}
			counter++;
		}
		
		revalidate();
		repaint();
	}
	
	public void updateView() {
		resetPictures();
	}
}
