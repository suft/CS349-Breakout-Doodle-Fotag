import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

class ImageView extends JPanel implements IView {

	// the model that this view is showing
	private ImageModel model;
	private Controller controller;
	
	private static Image star_full = null;
	private static Image star_empty = null;
	ArrayList<rating> ratings = new ArrayList<rating>();

	ImageView(ImageModel model, Controller controller) {
		loadIMG();
		this.controller = controller;
		this.model = model;
		gridBox();
	}
	
	private void gridBox() {
		this.setPreferredSize(new Dimension(150, 180));
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		File file = new File(model.getPath());
		BufferedImage bImage = null;
		Image image = null;
		try {
			bImage = ImageIO.read(file);
		} catch (Exception ex) {
			
		}
		image = bImage.getScaledInstance(120, 120, Image.SCALE_FAST);
		JLabel pictureLabel = new JLabel();
		pictureLabel.setName(model.getPath());
		pictureLabel.addMouseListener(controller);
		pictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		pictureLabel.setIcon(new ImageIcon(image));
		add(pictureLabel);
		
		JLabel nameLabel = new JLabel(model.getFileName());
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(nameLabel);
		
		JLabel dateLabel = new JLabel(model.getDate());
		dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(dateLabel);
		
		JPanel ratingBar = new JPanel();
		ratingBar.setBackground(Color.WHITE);
		ratingBar.add(new JLabel("rating:"));
		for (int i = 0; i < 5; i++) {
			rating s = new rating(i);
			ratings.add(s);
			ratingBar.add(s);
		}
		add(ratingBar);

		for (int i = 0; i < 5; i++) {
			if (i <= model.getRating()-1)
				ratings.get(i).setIcon(new ImageIcon(star_full));
			else
				ratings.get(i).setIcon(new ImageIcon(star_empty));
		}
		repaint();
	}
	
	private void listBox() {
		this.setPreferredSize(new Dimension(300, 130));
		this.setBackground(Color.WHITE);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		File file = new File(model.getPath());
		BufferedImage bImage = null;
		Image image = null;
		try {
			bImage = ImageIO.read(file);
		} catch (Exception ex) {
			
		}
		image = bImage.getScaledInstance(120, 120, Image.SCALE_FAST);
		JLabel pictureLabel = new JLabel();
		pictureLabel.setName(model.getPath());
		pictureLabel.addMouseListener(controller);
		pictureLabel.setIcon(new ImageIcon(image));
		add(pictureLabel);
		
		JPanel metadata = new JPanel();
		metadata.setBackground(Color.WHITE);
		metadata.setLayout(new BoxLayout(metadata, BoxLayout.Y_AXIS));
		
		JLabel nameLabel = new JLabel(model.getFileName());
		nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		metadata.add(nameLabel);
		
		JLabel dateLabel = new JLabel(model.getDate());
		dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		metadata.add(dateLabel);
		
		JPanel ratingBar = new JPanel();
		ratingBar.setBackground(Color.WHITE);
		ratingBar.add(new JLabel("rating:"));
		for (int i = 0; i < 5; i++) {
			rating s = new rating(i);
			ratings.add(s);
			ratingBar.add(s);
		}
		ratingBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		metadata.add(ratingBar);
		add(metadata);
		
		for (int i = 0; i < 5; i++) {
			if (i <= model.getRating()-1)
				ratings.get(i).setIcon(new ImageIcon(star_full));
			else
				ratings.get(i).setIcon(new ImageIcon(star_empty));
		}
		repaint();
	}
	
	public ImageModel getModel() {
		return model;
	}
	
	private void loadIMG() {
		if (star_full == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("star-full.png"));
			} catch (IOException e) {
				
			}
			star_full = img.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
		}

		if (star_empty == null) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("star-empty.png"));
			} catch (IOException e) {
				
			}
			star_empty = img.getScaledInstance(10, 10, Image.SCALE_SMOOTH);
		}
	}

	class rating extends JLabel {
		
		rating(int n) {
			setBackground(Color.WHITE);
			setIcon(new ImageIcon(star_empty));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(SwingUtilities.isRightMouseButton(e)){
				        updateRating(-1);
				    } else {
				    	updateRating(n);
				    }
					model.notifyObserver();
				}
			});
		}
		
		public void updateRating(int n) {
			for (int i = 0; i < 5; i++) {
				if (i <= n)
					ratings.get(i).setIcon(new ImageIcon(star_full));
				else
					ratings.get(i).setIcon(new ImageIcon(star_empty));
			}
			model.setRating(n+1);
		}
	}
	
	// IView interface
	public void updateView() {
		ratings = new ArrayList<rating>();
		if (model.ic_model.getMode() == 0) {
			removeAll();
			gridBox();
		}
		
		if (model.ic_model.getMode() == 1) {
			removeAll();
			listBox();
		}
		repaint();
	}
}
