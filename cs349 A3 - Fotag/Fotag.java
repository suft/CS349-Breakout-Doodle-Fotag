import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Fotag {
	private static JFrame frame;
	private static JPanel panel;
	
	public static void main(String[] args){
		frame = new JFrame("Fotag!");

		ImageCollectionModel ic_model = new ImageCollectionModel();
		
		Controller controller = new Controller(ic_model);
		
		ImageCollectionView ic_view = new ImageCollectionView(ic_model, controller);
		
		ic_model.addImageCollectionView(ic_view);
		
		Toolbar toolBar = new Toolbar(ic_model, controller);
		ic_model.addToolbar(toolBar);
		
		frame.setSize(new Dimension(800,600));
		frame.setMinimumSize(new Dimension(320, 400));
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentListener(controller);
		
		panel = new JPanel(new BorderLayout());
		
		JScrollPane canvas = new JScrollPane(ic_view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(canvas, BorderLayout.CENTER);
		panel.add(toolBar, BorderLayout.NORTH);
		// i love xiao chong zi ye ye ye
		frame.add(panel);
		frame.setVisible(true);
		
		// event on close
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	String savedata = "";
		        for(int i = 0; i < ic_model.getImageList().size(); i++) {
			    	String s1 = ic_model.getImageList().get(i).getPath();
			    	String s2 = Integer.toString(ic_model.getImageList().get(i).getRating());
			    	savedata = savedata + s1 + "\n" + s2 + "\n";
		        }
		        File file = new File("save.txt");
		        try {
					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}
					
					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(savedata);
					bw.close();
					
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
		    }
		});
		
		// load on start
		Path path = Paths.get("save.txt");
		Charset charset = Charset.forName("US-ASCII");
		File f = new File("save.txt");
		if (f.exists()) {
			try {
				List<String> savedData = Files.readAllLines(path, charset);
				for (int i = 0; i < savedData.size(); i+=2) {
					BufferedImage bimage = null;
					File file = new File(savedData.get(i));
		        	try {
		        		bimage = ImageIO.read(file);
		        	} catch (Exception e1) {
		        		
		        	}
		        	
		        	if (bimage != null) {
		        		ImageModel newModel = new ImageModel(file.getAbsolutePath(), ic_model);
		        		ImageView newView = new ImageView(newModel, controller);
		        		newModel.setRating(Integer.parseInt(savedData.get(i+1)));
		        		newModel.addView(newView);
		        		ic_model.addImage(newModel, newView);
		        	}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
