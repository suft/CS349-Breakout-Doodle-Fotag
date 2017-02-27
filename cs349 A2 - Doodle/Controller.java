//Tells model what to do
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

class Controller implements ActionListener, ChangeListener ,MouseListener, MouseMotionListener {
	
	// model
	Model model;
	
	// how long mouse is held down
	private double mouseDownTime, mouseUpTime;
	
	// controller constructor
	Controller(Model model) {
		this.model = model;
	}

	/*
	 * Listeners
	 */
	// button listeners
	public void actionPerformed(ActionEvent actionEvent){
		// variables used by save/load
		int save;
		final JFileChooser fc;
		int returnVal;
		File fileCheck;
		
		switch(actionEvent.getActionCommand()) { // check button clicked
			//Color chooser
			case "<html>Color<br />Chooser</html>":
				Color initialBackground = ((Component) actionEvent.getSource()).getBackground();
		        Color background = JColorChooser.showDialog(null, "JColorChooser", initialBackground);
		        if (background != null) {
		        	model.updateColor(background);
		        }
		        break;
		    //constant colors
			case "Black":
				model.updateColor(Color.BLACK);
				break;
			case "Red":
				model.updateColor(Color.RED);
				break;
			case "Blue":
				model.updateColor(Color.BLUE);
				break;
			case "Yellow":
				model.updateColor(Color.YELLOW);
				break;
			case "Orange":
				model.updateColor(Color.ORANGE);
				break;
			case "Pink":
				model.updateColor(Color.PINK);
				break;
			case "Green":
				model.updateColor(Color.GREEN);
				break;
				
			// play button
			case "Play":
				if ((model.getPlay() == 0)&&(model.getTickMax()!=0)) {
					model.updatePlay(1);
				}
				break;
				
			// rewind button
			case "Rewind":
				if (model.getPlay() == 0) {
					model.updatePlay(4);
				}
				break;
			
			// start button
			case "Start":
				model.updatePlayTick(0);
				break;
				
			// end button
			case "End":
				model.updatePlayTick(model.getTickMax());
				break;
				
			// create new doodle
			case "NewDoodle":
				//prompt user to save
				save = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(), "Would you like to save?", "Save",
			            JOptionPane.YES_NO_OPTION);
				if (save == JOptionPane.YES_OPTION) { // user wants to save
					
					// setup and open jfilechooser
					fc = new JFileChooser();
					fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleTxt", "doodleTxt"));
					fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleBin", "doodleBin"));
					returnVal = fc.showSaveDialog((Component) actionEvent.getSource()); 
					
					if (returnVal == JFileChooser.APPROVE_OPTION) { // user clicked ok
						// check if input filename is valid
				        File file = fc.getSelectedFile(); 
				        int i = file.getName().lastIndexOf('.');
						String extension = "";
						if (i > 0) {
							extension = file.getName().substring(i+1);
						}
						if (extension.equals("doodleBin")) {
							if (file.exists()) { // check if file exist
								// prompt user for overwrite
								int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
					            switch(result){
					                case JOptionPane.YES_OPTION: //user says overwrite
					                	saveBinary(file);
					                	newDoodle();
					                case JOptionPane.NO_OPTION:
					                case JOptionPane.CLOSED_OPTION:
					                case JOptionPane.CANCEL_OPTION:
					                    break;
					            }
							}
						} else if (extension.equals("doodleTxt")) {
							if (file.exists()) {// check if file exist
								// prompt user for overwrite
								int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
					            switch(result){
					                case JOptionPane.YES_OPTION:
					                	try {
											saveTxt(file);
											newDoodle();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
					                	break;
					                case JOptionPane.NO_OPTION:
					                case JOptionPane.CLOSED_OPTION:
					                case JOptionPane.CANCEL_OPTION:
					                	JOptionPane.showMessageDialog((Component) actionEvent.getSource(), "Save Failed, please try agian.", "Error Saving", JOptionPane.INFORMATION_MESSAGE);
					                    break;
					            }
							}
						} else {
							JOptionPane.showMessageDialog(null, "Invalid Extension. Must save in .doodleBin or .doodleTxt", "Error Saving", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} else { // user doesnt want to save
					newDoodle();
				}
				break;
				
			// save as binary
			case "Save as Binary":
				// setup jfilechooser
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleBin", "doodleBin"));
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleTxt", "doodleTxt"));
				returnVal = fc.showSaveDialog((Component) actionEvent.getSource()); 
				fileCheck = fc.getSelectedFile();
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {//user clicked save
					if ((fileCheck.exists())||(new File(fileCheck+".doodleBin")).exists()) { // check if file exists
						//prompt user for overwrite
						int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result){
			                case JOptionPane.YES_OPTION: //user wants overwrite
			                	File file = new File(fc.getSelectedFile()+".doodleBin");
						        saveBinary(file);
						        break;
			                case JOptionPane.NO_OPTION:
			                case JOptionPane.CLOSED_OPTION:
			                case JOptionPane.CANCEL_OPTION:
			                    break; //user doesnt want to overwrite, dont do anything
			            }
					} else { //file does not exist yet, just save
						File file = new File(fc.getSelectedFile()+".doodleBin");
				        saveBinary(file);
					}
				}
				break;
				
			// save as text
			case "Save as Text":
				//setup jfilechooser
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleTxt", "doodleTxt"));
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleBin", "doodleBin"));
				returnVal = fc.showSaveDialog((Component) actionEvent.getSource()); 
				fileCheck = fc.getSelectedFile();
				try { //have to use try&catch, no idea why
		        	if (returnVal == JFileChooser.APPROVE_OPTION) { //user clicked save
						if ((fileCheck.exists())||(new File(fileCheck+".doodleTxt")).exists()) { // check if file exist
							int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
				            switch(result){
				                case JOptionPane.YES_OPTION: // user wants to overwrite
				                	File file = new File(fc.getSelectedFile()+".doodleTxt");
				                	saveTxt(file);
				                	break;
				                case JOptionPane.NO_OPTION://user doesnt want overwrite, dont do anything
				                case JOptionPane.CLOSED_OPTION:
				                case JOptionPane.CANCEL_OPTION:
				                    break;
				            }
						}
						else { // file doesnt exist yet, just save
							File file = new File(fc.getSelectedFile()+".doodleTxt");
							saveTxt(file);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        break;
		        
		    // load from file
			case "Load":
				// setup file chooser
				fc = new JFileChooser();
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleTxt", "doodleTxt"));
				fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleBin", "doodleBin"));
				
				//loop until valid file or user cancels
				while (true) {
					returnVal = fc.showOpenDialog((Component) actionEvent.getSource());
					if (returnVal == JFileChooser.APPROVE_OPTION) { // start loading file
						File file = fc.getSelectedFile();
						if (file.exists()) { // check if file exists
							int i = file.getName().lastIndexOf('.');
							String extension = "";
							if (i > 0) {
								extension = file.getName().substring(i+1);
							}
							// check if extension is valid
							if (extension.equals("doodleBin")) { // binary file
								loadBinary(file);
								break;
							} else if (extension.equals("doodleTxt")) { // text file
								loadTxt(file);
								break;
							} else { // invalid file
								JOptionPane.showMessageDialog((Component) actionEvent.getSource(), "Invalid Extension. This program only supports .doodleBin or .doodleTxt", "Error Loading", JOptionPane.INFORMATION_MESSAGE);
							}
						} else { //file doesnt exist
							JOptionPane.showMessageDialog((Component) actionEvent.getSource(), "File not found", "Error Loading", JOptionPane.INFORMATION_MESSAGE);
						}
					} else { // user canceled
						break;
					}
				}
				break;
				
			// exit program
			case "Exit":
				if (model.getEdited()) { // check if image is edited
					// prompt user to save
					save = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(), "Would you like to save?", "Save",
				            JOptionPane.YES_NO_OPTION);
					if (save == JOptionPane.YES_OPTION) { // user wants to save
						// setup jfilechooser
						fc = new JFileChooser();
						fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleTxt", "doodleTxt"));
						fc.addChoosableFileFilter(new FileNameExtensionFilter("doodleBin", "doodleBin"));
						
						boolean exit = true;
						while (exit) {
							returnVal = fc.showSaveDialog((Component) actionEvent.getSource());
							if (returnVal == JFileChooser.APPROVE_OPTION) { // user clicked save
						        File file = fc.getSelectedFile(); //check filename
						        int i = file.getName().lastIndexOf('.');
								String extension = "";
								if (i > 0) {
									extension = file.getName().substring(i+1);
								}
								// check if extension is valid
								if (extension.equals("doodleBin")) { // binary file
									if (file.exists()) { // check if file exists
										// prompt user for overwrite
										int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
							            switch(result){
							                case JOptionPane.YES_OPTION: // user wants overwrite
							                	saveBinary(file);
							                	exit = false;
							                	break;
							                case JOptionPane.NO_OPTION: // user doesnt want to overwrite
							                case JOptionPane.CLOSED_OPTION:
							                case JOptionPane.CANCEL_OPTION:
							                    break;
							            }
									} else {
										saveBinary(file);
										exit = false;
									}
								} else if (extension.equals("doodleTxt")) { // text file
									if (file.exists()) {// check if file exists
										// prompt user for overwrite
										int result = JOptionPane.showConfirmDialog((Component) actionEvent.getSource(),"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
							            switch(result){
							                case JOptionPane.YES_OPTION:// user wants overwrite
							                	try {
													saveTxt(file);
													exit = false;
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
							                case JOptionPane.NO_OPTION:// user doesnt want to overwrite
							                case JOptionPane.CLOSED_OPTION:
							                case JOptionPane.CANCEL_OPTION:
							                    break;
							            }
									} else { // file doesnt exist yet
							            try {
											saveTxt(file);
											exit = false;
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
							        }
								} else { // invalid extension, prompt to save agian
									JOptionPane.showMessageDialog((Component) actionEvent.getSource(), "Invalid Extension. Must save in .doodleBin or .doodleTxt", "Error Saving", JOptionPane.INFORMATION_MESSAGE);
									continue;
								}
							} else {
								exit = false;
							}
						}
					}
				}
				System.exit(0);
				break;
			
			// change to full size mode
			case "Full-Size":
				model.updateMode(0);
				break;
			
			// change to fit mode
			case "Fit": 
				model.updateMode(1);
				break;
		}
	}
	
	// slider listeners
		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				JSlider slider = (JSlider) e.getSource();
				if (slider.getName().equals("chooseThickness")) { // stroke thickness slider
					model.updateStrokeThickness(slider.getValue());
				} else if (slider.getName().equals("playList")) { // play slider
					if (model.getPlay() == 0) {
						model.updatePlayTick(slider.getValue());
					}
				}
			} catch (ClassCastException name){
				
			}
		}
		
		// mouse listeners
		@Override
		public void mousePressed(MouseEvent e) {
			if (model.getPlay() == 0) {
				model.updateEdited(true);
				if ((model.getPlayTick() == 0)&&(model.getTickMax()!=0)) {
					model.deleteAllPaths();
				} else if (model.getPlayTick() < model.getTickMax()) {
					model.deletePaths(model.getPlayTick()/100+1, model.getPlayTick());
				}
				
				mouseDownTime = System.currentTimeMillis();
				model.updateDraw(true);
				JPanel n = (JPanel)e.getSource();
				model.addPath();
				int x = e.getX(), y = e.getY();
				if (model.getMode() == 0) {
					model.updatePaths0(new Point(x, y), model.getStrokeThickness(), model.getColor());
				} else if (model.getMode() == 1) {
					model.updatePaths0(new Point((int) (x*model.getMaxX()/(double)n.getWidth()), (int) (y*model.getMaxY()/(double)n.getHeight())), model.getStrokeThickness(), model.getColor());
				}
				model.setTickMax(model.getPaths0().size()*100);
				model.updatePlayTick(model.getPaths0().size()*100);
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (model.getPlay() == 0) {
				model.updateDraw(false);
				mouseUpTime = System.currentTimeMillis();
				model.getPaths0().get(model.getPaths0().size()-1).updateTimeToDraw(mouseUpTime-mouseDownTime);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (model.getPlay() == 0) {
				JPanel n = (JPanel)e.getSource();
				if (model.getDraw()) {
					int x = e.getX(), y = e.getY();
					if (e.getX() > n.getWidth()) {
						x = n.getWidth();
					} else if (e.getX() < 0) {
						x = 0;
					}
					if (e.getY() > n.getHeight()) {
						y = n.getHeight();
					} else if (e.getY() < 0) {
						y = 0;
					}
					if (model.getMode() == 0) {
						model.updatePaths0(new Point(x, y), model.getStrokeThickness(), model.getColor());
					} else if (model.getMode() == 1) {
						model.updatePaths0(new Point((int) (x*model.getMaxX()/(double)n.getWidth()), (int) (y*model.getMaxY()/(double)n.getHeight())), model.getStrokeThickness(), model.getColor());
					}
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	
	/* ---------- End of Listeners ---------- */
	
	// create a new doodle
	private void newDoodle() {
		model.updatePlayTick(0);
		model.deleteAllPaths();
		model.updateMax(321, 315);
		model.setTickMax(0);
		model.updateDraw(false);
		model.updateStrokeThickness(3);
		model.updateColor(Color.BLACK);
		model.updateMode(0);
	}
	
	/*
	 * Save and Load
	 */
	// save as txt
	private void saveTxt(File f) throws IOException {
		try {
			FileWriter fileWriter = new FileWriter(f);
			// total number of lines drawn
			fileWriter.write("#Line:" + model.getPaths0().size() +"\n");
			
			// save points and data
			for (int i = 0; i < model.getPaths0().size(); i++) {
				// save number of points on this line
				fileWriter.write("#Points:" + model.getPaths0().get(i).getPointList().size()+"\n");
				
				// save all points
				for (int j = 0; j < model.getPaths0().get(i).getPointList().size(); j++) {
					fileWriter.write("XY:" + (int)model.getPaths0().get(i).getPointList().get(j).getX() + ","+(int)model.getPaths0().get(i).getPointList().get(j).getY()+"\n");
				}
				
				// save stroke thickness, color, time
				fileWriter.write("Stroke:" + model.getPaths0().get(i).getStroke()+"\n");
				fileWriter.write("Color:" + model.getPaths0().get(i).getColor().getRGB()+"\n");
				fileWriter.write("Time:" + model.getPaths0().get(i).getTimeToDraw()+"\n");
			}
			
			//close filewriter
			fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// load txt file
	private void loadTxt (File f) {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			//vars
		    String line;
		    int numberOfLines = 0;
		    int numberOfPoints = 0;
		    int x, y;
		    int stroke;
		    int color;
		    double time;
		    ArrayList<Path> paths = new ArrayList<Path>();
		    ArrayList<Point> points = new ArrayList<Point>();
		    Path path = new Path();
		    
		    // read until end of file
		    while ((line = br.readLine()) != null) {
		    	String[] prefix = line.split(":");
		    	switch(prefix[0]) {
		    		case "#Line":
		    			numberOfLines = Integer.parseInt(prefix[1]);
		    			break;
		    		case "#Points":
		    			numberOfPoints = Integer.parseInt(prefix[1]);
		    			break;
		    		case "XY":
		    			String[] xy = prefix[1].split(",");
		    			Point p = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
		    			points.add(p);
		    			numberOfPoints--;
		    			if (numberOfPoints == 0) {
		    				path.setPath(points);
		    				
		    			}
		    			break;
		    		case "Stroke":
		    			path.updateStroke(Integer.parseInt(prefix[1]));
		    			break;
		    		case "Color":
		    			path.updateColor(new Color(Integer.parseInt(prefix[1])));
		    			break;
		    		case "Time":
		    			path.updateTimeToDraw(Double.parseDouble(prefix[1]));
		    			paths.add(path);
	    				points = new ArrayList<Point>();
	    				path = new Path();
	    				break;
		    	}
		    	model.setPaths(paths);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Corrupted File", "Error Loading", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		} catch (NumberFormatException e) { // file corrupted
			JOptionPane.showMessageDialog(null, "Corrupted File", "Error Loading", JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	// save as binary
	private void saveBinary(File f) {
		try {
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			DataOutputStream out1 = new DataOutputStream(baos1);
			out1.writeInt(model.getPaths0().size());
			byte[] bytes1 = null;
			for (int i = 0; i < model.getPaths0().size(); i++) {
				out1.writeInt(model.getPaths0().get(i).getPointList().size());
				
				ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
				DataOutputStream out0 = new DataOutputStream(baos0);
				byte[] bytes0 = null;
				for (Point p : model.getPaths0().get(i).getPointList()) {
					out0.writeInt((int)p.getX());
					out0.writeInt((int)p.getY());
					bytes0 = baos0.toByteArray();
				}
				out1.write(bytes0);
				out1.writeDouble(model.getPaths0().get(i).getTimeToDraw());
				out1.writeInt(model.getPaths0().get(i).getStroke());
				out1.writeInt(model.getPaths0().get(i).getColor().getRed());
				out1.writeInt(model.getPaths0().get(i).getColor().getGreen());
				out1.writeInt(model.getPaths0().get(i).getColor().getBlue());
				out1.writeInt(model.getPaths0().get(i).getColor().getAlpha());
				bytes1 = baos1.toByteArray();
			}
	        Files.write(f.toPath(), bytes1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// load binary
	private void loadBinary(File f) {
		byte[] data;
		try {
			data = Files.readAllBytes(f.toPath());
			int counter = -3;
			ArrayList<Path> newPaths = new ArrayList<Path>();
			Path newPath = new Path();
			ArrayList<Point> pointList = new ArrayList<Point>();
			for (int i = 4; i < data.length; i+=4) {
				if (counter == -3) {
					counter = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16)
					        | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
				} else if (counter > 0) {
				    int n = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16)
					        | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
				    int m = ((data[i+4] & 0xFF) << 24) | ((data[i+5] & 0xFF) << 16)
					        | ((data[i+6] & 0xFF) << 8) | (data[i+7] & 0xFF);
				    pointList.add(new Point(n, m));
				    i+=4;
				    counter--;
				} else if (counter == 0){
					 int upper = (((data[i] & 0xff) << 24)
					            + ((data[i+1] & 0xff) << 16)
					            + ((data[i+2] & 0xff) << 8) + ((data[i+3] & 0xff) << 0));
					 int lower = (((data[i+4] & 0xff) << 24)
					            + ((data[i+5] & 0xff) << 16)
					            + ((data[i+6] & 0xff) << 8) + ((data[i+7] & 0xff) << 0));
					 double d = Double.longBitsToDouble((((long) upper) << 32)
					            + (lower & 0xffffffffl));
					 newPath.updateTimeToDraw(d);
					 i+=4;
					 counter--;
				} else if (counter == -1){
					 int n = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16)
					        | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
					 newPath.updateStroke(n);
					 
					 counter--;
				} else {
					 int r = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16)
					        | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
					 int g = ((data[i+4] & 0xFF) << 24) | ((data[i+5] & 0xFF) << 16)
					        | ((data[i+6] & 0xFF) << 8) | (data[i+7] & 0xFF);
					 int b = ((data[i+8] & 0xFF) << 24) | ((data[i+9] & 0xFF) << 16)
					        | ((data[i+10] & 0xFF) << 8) | (data[i+11] & 0xFF);
					 int a = ((data[i+12] & 0xFF) << 24) | ((data[i+13] & 0xFF) << 16)
					        | ((data[i+14] & 0xFF) << 8) | (data[i+15] & 0xFF);
					 newPath.updateColor(new Color(r,g,b,a));
					 newPath.setPath(pointList);
					 newPaths.add(newPath);
					 newPath = new Path();
					 pointList = new ArrayList<Point>();
					 i+=12;
					 counter--;
				}
			}
			model.setPaths(newPaths);
			model.updateMode(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* ---------- End of Save and Load ---------- */
		
}
