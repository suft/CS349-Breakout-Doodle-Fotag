import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

public class Main{

	public static void main(String[] args){
		// create Model and initialize it
		Model model = new Model();
		// create Controller, tell it about model
		Controller controller = new Controller(model);
		// create Canvas View, tell it about model and controller
		CanvasView canvasView = new CanvasView(model, controller);
		// create Palette View
		PaletteView paletteView = new PaletteView(model, controller);
		// create Timeline View
		TimelineView timelineView = new TimelineView(model, controller);
		// tell Model about View.
		model.addView(canvasView, 0);
		model.addView(paletteView, 1);
		model.addView(timelineView, 2);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Doodle");
				JPanel panel = new JPanel();
				JScrollPane jsp = new JScrollPane(canvasView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				panel.setLayout(new BorderLayout());
				panel.add(paletteView, BorderLayout.WEST);
				panel.add(jsp, BorderLayout.CENTER);
				panel.add(timelineView, BorderLayout.SOUTH);
				
				frame.setMinimumSize(new Dimension(480, 420));
				frame.setSize(new Dimension(480,420));
				frame.setContentPane(panel);
				
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				Menu.createMenu(frame, controller);
				//frame.pack();
				frame.setVisible(true);
			}
		});
	}
}

class Menu {
	public static void createMenu(JFrame frame, Controller controller) {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem itemNewDoodle = new JMenuItem("NewDoodle", KeyEvent.VK_N);
		JMenuItem itemSaveBinary = new JMenuItem("Save as Binary", KeyEvent.VK_B);
		JMenuItem itemSaveText = new JMenuItem("Save as Text", KeyEvent.VK_T);
		JMenuItem itemLoad = new JMenuItem("Load", KeyEvent.VK_L);
		JMenuItem itemExit = new JMenuItem("Exit", KeyEvent.VK_E);
		
		fileMenu.add(itemNewDoodle);
		fileMenu.add(itemSaveBinary);
		fileMenu.add(itemSaveText);
		fileMenu.add(itemLoad);
		fileMenu.add(itemExit);
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		menuBar.add(viewMenu);
		
		JMenuItem itemFullSize = new JMenuItem("Full-Size", KeyEvent.VK_U);
		JMenuItem itemFit = new JMenuItem("Fit", KeyEvent.VK_I);
		
		viewMenu.add(itemFullSize);
		viewMenu.add(itemFit);
		
		frame.setJMenuBar(menuBar);
		
		itemNewDoodle.addActionListener(controller);
		itemSaveBinary.addActionListener(controller);
		itemSaveText.addActionListener(controller);
		itemLoad.addActionListener(controller);
		itemExit.addActionListener(controller);
		itemFullSize.addActionListener(controller);
		itemFit.addActionListener(controller);
	}
}
