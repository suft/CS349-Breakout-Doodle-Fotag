import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

class Controller implements ActionListener, MouseListener, ComponentListener {

	ImageCollectionModel ic_model;

	Controller(ImageCollectionModel model) {
		this.ic_model = model;
	}

	// event from the view's button
	public void actionPerformed(java.awt.event.ActionEvent e){
		switch (((JButton) e.getSource()).getName()) {
			case "open button":
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
		        int returnVal = chooser.showOpenDialog(null);
		        File[] files = null;
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            files = chooser.getSelectedFiles();
		        } else {
		        	return;
		        }
		        if (files.length == 0) {
		        	return;
		        }
		        
		        for (File file:files) {
		        	BufferedImage bimage = null;
		        	try {
		        		bimage = ImageIO.read(file);
		        	} catch (Exception e1) {
		        		
		        	}
		        	
		        	if (bimage != null) {
		        		ImageModel newModel = new ImageModel(file.getAbsolutePath(), ic_model);
		        		ImageView newView = new ImageView(newModel, this);
		        		newModel.addView(newView);
		        		ic_model.addImage(newModel, newView);
		        	}
		        }
		        break;
			case "grid mode":
				ic_model.setMode(0);
				break;
			case "list mode":
				ic_model.setMode(1);
				break;
		}
		        
		        
	}

	public void mouseClicked(MouseEvent e) {
		JFrame frame = new JFrame(((JLabel)e.getSource()).getName());
		frame.setSize(800, 600);
		JLabel picture = new JLabel();
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		
		ArrayList<ImageModel> imageList = ic_model.getImageList();
		BufferedImage bImage = null;
		Image image = null;
		try {
			bImage = ImageIO.read(new File(((JLabel)e.getSource()).getName()));
			double factor = Math.min(800.0/bImage.getWidth(), 600.0/bImage.getHeight());
			factor *= 0.95;
			image = bImage.getScaledInstance((int)(bImage.getWidth()*factor), (int)(bImage.getHeight()*factor), Image.SCALE_SMOOTH);
		} catch (Exception ex) {
			
		}
		if (image != null) {
			picture.setIcon(new ImageIcon(image));
			picture.setAlignmentX(Component.CENTER_ALIGNMENT);
			frame.add(picture);
			frame.setVisible(true);
		}
	}

	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		ic_model.notifyViews();
		ic_model.notifyToolbar();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}