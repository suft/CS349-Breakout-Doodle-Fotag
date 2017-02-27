import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;

public class TimelineView extends JPanel implements IView {
	// the view's main user interface
	
	// the model that this view is showing
	private Model model;
	
	// slider and buttons
	JSlider playList;
	JButton playButton;
	JButton startButton;
	JButton endButton;
	
	// constructor
	TimelineView(Model model, Controller controller) {
		this.model = model;
		this.setBackground(Color.GRAY);
		
		initBar();
		
		playButton.addActionListener(controller);
		startButton.addActionListener(controller);
		endButton.addActionListener(controller);
		playList.addChangeListener(controller);
	}
	
	// setup bar
	private void initBar() {
		playButton = new JButton("Play");
		startButton = new JButton("Start");
		endButton = new JButton("End");
		playList = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);
		playList.setPaintTicks(true);
		playList.setName("playList");
		playList.setMajorTickSpacing(100);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        this.add(playButton, gbc);
		
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx++;
		this.add(playList, gbc);
		
		gbc.weightx = 0.1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx++;
		this.add(startButton, gbc);
		
		gbc.gridx++;
		this.add(endButton, gbc);
	}
	
	// play method
	public class Play extends Thread
	{
        public void run()
        {
            super.run();
            model.updatePlay(2);
            int nValue = playList.getValue();
            int nMaxValue = playList.getMaximum();
            do
            {
            	model.updatePlayTick(nValue);
				nValue = nValue + 1;
				try
				{
				    Thread.sleep((long) (model.getPaths0().get((nValue-1)/100).getTimeToDraw()/100));
				}
				catch (InterruptedException e)
				{
				    e.printStackTrace();
				}
				playList.setValue(nValue);
            }
            while (nValue < nMaxValue);
            playButton.setEnabled(true);
            playButton.requestFocus();
            model.updatePlay(0);
            playButton.setText("Rewind");
        }
	};
	
	// reverse play method
	public class Rewind extends Thread
	{
        public void run()
        {
            super.run();
            model.updatePlay(2);
            int nValue = playList.getMaximum();
            do
            {
            	model.updatePlayTick(nValue);
				nValue = nValue - 1;
				try
				{
				    Thread.sleep((long) (model.getPaths0().get((nValue-1)/100).getTimeToDraw()/100));
				}
				catch (InterruptedException e)
				{
				    e.printStackTrace();
				}
				playList.setValue(nValue);
            }
            while (nValue >= 0);
            playButton.setEnabled(true);
            playButton.requestFocus();
            model.updatePlay(0);
            playButton.setText("Play");
        }
	};

	// IView interface 
	public void updateView() {
		//System.out.println("TimelineView: updateView");
		if (model.getPlay() == 1) {
			playList.setValue(0);
			Play objThread = new Play();
            objThread.start();
		} else if (model.getPlay() == 0) {
			playList.setMaximum(model.getTickMax());
			playList.setValue(model.getPlayTick());
		} else if (model.getPlay() == 4) {
			playList.setValue(playList.getMaximum());
			Rewind objThread = new Rewind();
            objThread.start();
		}
		if (model.getTickMax() != model.getPlayTick()) {
			playButton.setText("Play");
		} else if (model.getTickMax() != 0){
			playButton.setText("Rewind");
		}
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

        resize();

        Toolkit.getDefaultToolkit().sync();
    }
	
	private void resize() {
		int width = this.getTopLevelAncestor().getWidth();
		int height = this.getTopLevelAncestor().getHeight();
		int newHeight = sizeRatio(height);
		this.setPreferredSize(new Dimension(width, newHeight));
	}
	
	private int sizeRatio(int height) {
		int newHeight = (height/50)*50/10;
		return newHeight;
	}
}
