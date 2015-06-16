package com.jongseok.gifi.creator.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.jongseok.gifi.audio.SoundCircle;

public class SoundCirclePanelList extends JPanel{
	
	//private ButtonGroup soundCircleToggleButtonGroup = new ButtonGroup();
	private GifPanel gifpanel;
	private ArrayList<SoundCirclePanel> scPanels;
	private SpringLayout layout;
	
	private int time;
	private int timingLineX;
	
	
	public SoundCirclePanelList(GifPanel gifpanel){
		this.gifpanel = gifpanel;
		scPanels = new ArrayList<SoundCirclePanel>();
		layout = new SpringLayout();
		setLayout(layout);
	}
/*
	public void markSelectedTime(int x){
		
	}
	
	public void dehighlightPanels(){
		
	}*/
	
	public ArrayList<SoundCircle> getSoundCircles(){
		ArrayList<SoundCircle> soundCircles = new ArrayList<SoundCircle>(scPanels.size());
		
		for(SoundCircleListener scl: scPanels)
			soundCircles.add(scl.getSoundCircle());
		
		return soundCircles;
	}
	
	public void setTimingLine(int x, int time){
		this.time = time;
		timingLineX = x;
		for(SoundCirclePanel scPanel: scPanels)
			scPanel.setTiming(x, time);
		
		repaint();
	}
	
	public void addSoundCirclePanel(String filename, int animationPlayingTime){
		SoundCirclePanel scPanel = new SoundCirclePanel(filename, gifpanel, animationPlayingTime);
		scPanel.setTiming(timingLineX, time);
		
		// set layout
		if(scPanels.size() == 0)
			layout.putConstraint(SpringLayout.NORTH, scPanel, 5, SpringLayout.NORTH, this);
		else
			layout.putConstraint(SpringLayout.NORTH, scPanel, 5, SpringLayout.SOUTH, scPanels.get(scPanels.size()-1));
		layout.putConstraint(SpringLayout.WEST, scPanel, 5, SpringLayout.WEST, this);
		
		add(scPanel);
		scPanels.add(scPanel);
		packPanel();
	}
	
	public void removeSoundCirclePanel(SoundCirclePanel scPanel){
		if(null == scPanel)
			return;
		
		int toRemove = 0;
		boolean isMatch = false;
		
		for(; toRemove<scPanels.size(); toRemove++){
			if(scPanels.get(toRemove) == scPanel){
				isMatch = true;
				break;
			}
		}
		
		if(isMatch)
			scPanels.remove(toRemove);
	}
	
	private void packPanel(){
		if(0 == scPanels.size())
			return;
		
		int width = 5 + scPanels.get(0).getPreferredSize().width + 5;
		int height = scPanels.size() * scPanels.get(0).getPreferredSize().height + (scPanels.size() + 1) * 5;
		setPreferredSize(new Dimension(width, height));
	}
}
