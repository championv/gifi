package com.jongseok.gifi.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jongseok.gifi.gif.Gif;
import com.jongseok.gifi.gif.GifFactory;

public class MainFrame extends JFrame implements ActionListener, ChangeListener, MouseListener, MouseMotionListener{
	
	public static final int FILENAME_LABEL_WIDTH = 125;
	public static final int FILENAME_LABEL_HEIGHT = 30;
	
	private Gif gif;
	private GifPanel gifPanel;
	private JButton openGifButton;
	private JButton openSoundButton;
	private GifTimelineSliderPanel timelineSliderPanel;
	private SoundCirclePanelList scPanelList;
	
	private SpringLayout layout;
	private int prevTimelineZone = 0;
	
	public MainFrame(){
		
		// init components
		gifPanel = new GifPanel();
		openGifButton = new JButton("Open GIF");
		openSoundButton = new JButton("Open Sound");
		
		openGifButton.addActionListener(this);
		openSoundButton.addActionListener(this);
		openSoundButton.setEnabled(false);
		
		// set layout
		layout = new SpringLayout();
		setLayout(layout);
		
		layout.putConstraint(SpringLayout.NORTH, gifPanel, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, gifPanel, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, openGifButton, 50, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, openGifButton, 5, SpringLayout.EAST, gifPanel);
		layout.putConstraint(SpringLayout.WEST, openSoundButton, 5, SpringLayout.EAST, gifPanel);
		//layout.putConstraint(SpringLayout.EAST, openSoundButton, 5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, openSoundButton, 5, SpringLayout.SOUTH, openGifButton);
		
		add(gifPanel);
		add(openGifButton);
		add(openSoundButton);
		
		
		// show
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		packFrameSize();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screenSize.width - this.getSize().width) / 2;
		int y = (screenSize.height - this.getSize().height) / 2;
		
		setLocation(x, y);
		setVisible(true);
	}
	
	public void packFrameSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width1 = 5 + gifPanel.getPreferredSize().width + 5 + openSoundButton.getPreferredSize().width + 5;
		int width2 = 0;
		if(null != scPanelList)
			width2 = 5 + scPanelList.getPreferredSize().width + 5;
		int width = (width1>width2)? width1: width2;
		
		int height = 5 + gifPanel.getPreferredSize().height + 25;
		if(null != scPanelList && null != timelineSliderPanel)
			height +=  5 + timelineSliderPanel.getPreferredSize().height + 5 +scPanelList.getPreferredSize().height;
		
		int x = this.getLocation().x;
		int y = this.getLocation().y;
		
		setBounds(x, y, width, height);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// classify action source
		if(e.getSource() == openGifButton){
			
			// open GIF
			try{
				gif = GifFactory.readGif("Boglio_02.gif");
				gifPanel.openGif(gif);
				gifPanel.showFrame(0);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			// init timeline
			timelineSliderPanel = new GifTimelineSliderPanel(gif);
			timelineSliderPanel.addChangeListener(this);
			timelineSliderPanel.addMouseMotionListener(this);
			layout.putConstraint(SpringLayout.NORTH, timelineSliderPanel, 5, SpringLayout.SOUTH, gifPanel);
			layout.putConstraint(SpringLayout.WEST, timelineSliderPanel, 5, SpringLayout.WEST, this);
			add(timelineSliderPanel);
			
			// init sound circle panel list
			scPanelList = new SoundCirclePanelList(gifPanel);
			scPanelList.setTimingLine(timelineSliderPanel.getSnappedTimingLineCoordiateX()-5, timelineSliderPanel.getValue());
			layout.putConstraint(SpringLayout.NORTH, scPanelList, 5, SpringLayout.SOUTH, timelineSliderPanel);
			layout.putConstraint(SpringLayout.WEST, scPanelList, 5, SpringLayout.WEST, this);
			add(scPanelList);
			//scPanelList.setPreferredSize(new Dimension(600, 600));
			
			packFrameSize();
			openSoundButton.setEnabled(true);
		}
		
		else if(e.getSource() == openSoundButton){
			// create sound panel
			scPanelList.addSoundCirclePanel("giggle.wav", gif.getPlayingTime());
			packFrameSize();
			
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// change the animation frame to corresponding time
		//System.out.println("StateChange!!");
		
		
		if(e.getSource() != timelineSliderPanel.getSlider())
			return;
		
		
		// find the frame of corresponding time
		int selectedTime = timelineSliderPanel.getValue();
		//System.out.println("time: " + selectedTime);
		
		try{
			for(int index=0; index<gif.getFrameCount(); index++){
				//System.out.println("gif.getFrameStartingTime(" + index + "):" + gif.getFrameStartingTime(index) );
				if(selectedTime == gif.getFrameStartingTime(index)){
					gifPanel.showFrame(index);
					// draw line
					return;
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		// TODO: sometimes the state change event is missing. 
		// This symptom occurs non-synchronized time-line between the time-line slider panel and sound circle panels 
		// I believe mouse events are more accurate.
		// draw timing line
		/*int snappedValue = timelineSliderPanel.getValue() / 100 * 100;
		if(Math.abs(snappedValue - prevTimelineZone) >= 100){
			prevTimelineZone = snappedValue; 
			//System.out.println("Repaint! from statechange listener  prevTimelineZone = " + prevTimelineZone + " time=" + timelineSliderPanel.getValue()) ;
			
			timelineSliderPanel.repaint();//.paintImmediately(0, 0, timelineSliderPanel.getPreferredSize().width, timelineSliderPanel.getPreferredSize().height);
			scPanelList.setTimingLine(timelineSliderPanel.getSnappedTimingLineCoordiateX()-6, snappedValue);
		}*/
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == this){
			// TODO: Do i have to set sound circles invisible?
			//gifPanel.setSoundCirclesVisible(false);
			gifPanel.setSoundCircleListener(null);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(e.getSource() == timelineSliderPanel.getSlider()){
			int v = timelineSliderPanel.getValue();
			if(Math.abs(v - prevTimelineZone) >= 100){
				//System.out.println("DRAGGED!!! " + timelineSliderPanel.getValue());
				
				prevTimelineZone = v;
				timelineSliderPanel.repaint();
				scPanelList.setTimingLine(timelineSliderPanel.getSnappedTimingLineCoordiateX()-5, v);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}