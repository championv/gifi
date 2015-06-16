package com.jongseok.gifi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeListener;

import com.jongseok.gifi.gif.Gif;

public class GifTimelineSliderPanel extends JPanel{
	
	public static final int JSLIDER_WIDTH = 500;
	public static final int JSLIDER_HEIGHT = 50;
	public static final int JSLIDER_MINOR_TICK_SPACE = 100;
	public static final int JSLIDER_MAJOR_TICK_SPACE = 1000;
	public static final int GIF_TIMELINE_SLIDER_WIDTH = MainFrame.FILENAME_LABEL_WIDTH + JSLIDER_WIDTH;
	public static final int GIF_TIMELINE_SLIDER_HEIGHT = JSLIDER_HEIGHT;
	
	private JLabel label;
	private JSlider slider;

	public GifTimelineSliderPanel(Gif gif){
		
		// init label
		label = new JLabel(gif.getFileName());
		label.setPreferredSize(new Dimension(MainFrame.FILENAME_LABEL_WIDTH, MainFrame.FILENAME_LABEL_HEIGHT));
		label.setToolTipText(gif.getFileName());
		
		
		// init slider
		slider = new JSlider(JSlider.HORIZONTAL, 0,  gif.getPlayingTime(), 0);
		
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>(gif.getFrameCount());
		for(int index=0; index<gif.getFrameCount(); index++)
			labels.put(gif.getFrameStartingTime(index), new JLabel("#" + (index+1)));
		
		slider.setPreferredSize(new Dimension(JSLIDER_WIDTH, JSLIDER_HEIGHT));
		slider.setMinorTickSpacing(JSLIDER_MINOR_TICK_SPACE);
		slider.setMajorTickSpacing(JSLIDER_MAJOR_TICK_SPACE);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setLabelTable(labels);
		//timelineSlider.setPaintTrack(true);
		
		
		// place components
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, label, 0, SpringLayout.VERTICAL_CENTER, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, slider, 0, SpringLayout.VERTICAL_CENTER, label);
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.WEST, slider, 0, SpringLayout.EAST, label);
		
		add(label);
		add(slider);
		setPreferredSize(new Dimension(GIF_TIMELINE_SLIDER_WIDTH, GIF_TIMELINE_SLIDER_HEIGHT));
	}
	
	

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		
		// draw timing line
		int x = getSnappedTimingLineCoordiateX();
		g.setColor(Color.black);
		g.drawLine(x, GIF_TIMELINE_SLIDER_HEIGHT/2, x, GIF_TIMELINE_SLIDER_HEIGHT);
	}
	
	public int getSnappedTimingLineCoordiateX(){
		int snappedValue  = slider.getValue() / 100 * 100;
		return 5 + MainFrame.FILENAME_LABEL_WIDTH + 13
				+ (int)((float)snappedValue / (float)slider.getMaximum() * (JSLIDER_WIDTH - SoundCirclePanel.TIMELINE_WIDTH_OFFSET));
	}
	
	public int getTimingLineCoordinateX(){
		return 5 + MainFrame.FILENAME_LABEL_WIDTH + 13
				+ (int)((float)slider.getValue() / (float)slider.getMaximum() * (JSLIDER_WIDTH - SoundCirclePanel.TIMELINE_WIDTH_OFFSET)); 
	}
	
	public JSlider getSlider(){
		return slider;
	}
	
	public void addChangeListener(ChangeListener listener){
		slider.addChangeListener(listener);
	}
	
	@Override
	public void addMouseMotionListener(MouseMotionListener listener){
		slider.addMouseMotionListener(listener);
	}
	
	@Override
	public void addMouseListener(MouseListener listener){
		slider.addMouseListener(listener);
	}
	
	public void setValue(int time){
		slider.setValue(time);
	}
	
	
	public int getValue(){
		return slider.getValue();
	}
	
	public void increaseValueByMinorTickSpace(){
		int value = getValue();

		if(value+JSLIDER_MINOR_TICK_SPACE > slider.getMaximum())
			return;
		
		setValue(value + JSLIDER_MINOR_TICK_SPACE);
	}
	
	public void decreaseValueByMinorTickSpace(){
		int value = getValue();
		if(value-JSLIDER_MINOR_TICK_SPACE < 0)
			return;
		
		setValue(value - JSLIDER_MINOR_TICK_SPACE);
	}
}
