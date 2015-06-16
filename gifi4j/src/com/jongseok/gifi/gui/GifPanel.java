package com.jongseok.gifi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.gif.Gif;
import com.jongseok.gifi.gui.GifPanelFSM.DataKey;
import com.jongseok.gifi.gui.GifPanelFSM.DrawingSC;
import com.jongseok.gifi.gui.GifPanelFSM.NoSC;
import com.jongseok.gifi.gui.GifPanelFSM.PaintingSC;
import com.jongseok.gifi.gui.GifPanelFSM.SCchoosen;
import com.jongseok.gifi.utils.Bundle;

public class GifPanel extends JPanel implements SoundCircleGenerator, MouseListener, MouseMotionListener{
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 500;
	private static final Color DEHIGHLIGHTED_SC_COLOR = new Color(64, 64, 64, 127); 

	//public enum Status{GIF_ONLY, SC_ON_GIF, SC_DRAWING_ON_GIF}
	
	private Gif gif;
	//private Status status;
	private int toPaintFrameIndex = -1;
	private Thread gifPlayer;
	private boolean isPlaying = false;
	//private Point newSoundCircleCenter;
	//private int newSoundCircleRadius = -1;
	//private Color newSoundCircleColor = null;
	//private Hashtable<SoundCircleListener, SoundCircle> soundCircleTable;
	//private SoundCircleListener soundCircleListener;
	private GifPanelFSM fsm;
	private int timing;
	
	
	public GifPanel(){
		//status = Status.GIF_ONLY;
		//soundCircleTable = new Hashtable<SoundCircleListener, SoundCircle>();
		fsm = new GifPanelFSM();

		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public void setTiming(int time){
		timing = time;
	}
	
	public void openGif(Gif gif){
		this.gif = gif;
		
		
		setPreferredSize(new Dimension(gif.getWidth(), gif.getHeight()));

		// print frame info
		/*System.out.println("frame num = " + gif.getFrameCount());
		System.out.println("Frames");
		for(int index=0; index<gif.getFrameCount(); index++)
			System.out.println("  frame[" + index + "]: delay=" + gif.getDelay(index)*10 + " img=" + gif.getFrame(index));*/
		
		// stop current gif
		//stopGifAnimation();
		
		// start gif animation
		//startGifAnimation();
	}
	
	synchronized public void showFrame(int index) throws Exception{
		
		stopGifAnimation();
		
		if(index >= gif.getFrameCount())
			throw new Exception("The index is out of bound.");
		
		else if(toPaintFrameIndex != index){
			toPaintFrameIndex = index;
			repaint();
		}
	}
	
	synchronized public void startGifAnimation(){
		
		stopGifAnimation();

		isPlaying = true;
		
		gifPlayer = new Thread(){
			public void run(){
				
				toPaintFrameIndex = 0;
				
				while(isPlaying){
					
					repaint();
					
					try{
						//System.out.println("[" + toPaintFrameIndex + "] Delay = " + gif.getDelay(toPaintFrameIndex)*10);
						//System.out.println("GOTO sleep at " + System.currentTimeMillis());
						Thread.sleep(gif.getDelay(toPaintFrameIndex));
						//System.out.println("WAKE UP at " + System.currentTimeMillis());
						//System.out.println();
					}catch(Exception e){
						e.printStackTrace();
					}
					
					toPaintFrameIndex = (toPaintFrameIndex+1) % gif.getFrameCount();
				}
			}
		};
		
		gifPlayer.start();	
	}
	
	synchronized public void stopGifAnimation(){
		
		if(null == gifPlayer)
			return;
		
		isPlaying = false;
		//toPaintFrameIndex = -1;
		gifPlayer.notify();
		gifPlayer = null;
	}
	
	public void visualizeSoundCircles(Graphics g){
		//for(SoundCircle sc: soundCircleTable.values()){
		for(SoundCircle sc: fsm.soundCircleTable.values()){
			/*if(soundCircleTable.get(sc))
				g.setColor(sc.color);
			else
				g.setColor(DEHIGHLIGHTED_SC_COLOR);*/
			
			
			// TODO: represent volume in colors (opacity, color) or 3-D shape
			//int x = sc.center.x - sc.radius;
			//int y = sc.center.y - sc.radius;
			Point p = sc.getCirclePointAt(timing);
			if(null == p)
				continue;
			
			g.setColor(sc.color);
			int x = p.x - sc.radius;
			int y = p.y - sc.radius;
			g.fillOval(x, y, sc.radius*2, sc.radius*2);
		}
	}
	
	public void setSoundCircleListener(SoundCircleListener listener){
		if(fsm.getCurrentStateName() == NoSC.class.getName()){
			try{
				fsm.transit(GifPanelFSM.TransitBy.SoundCircleGeneratorEnabled.name(), null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		else if(fsm.getCurrentStateName() == SCchoosen.class.getName()){
			try{
				Bundle data = new Bundle();
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				fsm.transit(GifPanelFSM.TransitBy.SoundCircleGeneratorEnabled.name(), data);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		fsm.soundCircleListener = listener;
	}
	
	public void onEscKeyTyped(){
		try{
			if(fsm.getCurrentStateName().equals(SCchoosen.class.getName())){
				Bundle data = new Bundle();
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				
				fsm.transit(GifPanelFSM.TransitBy.KeyEscTyped.name(), data);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void onCopySoundCircle(){
		try{
			if(fsm.getCurrentStateName().equals(SCchoosen.class.getName()))
				fsm.transit(GifPanelFSM.TransitBy.CopySC.name(), null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void onPasteSoundCircle(){
		try{
			if(fsm.getCurrentStateName().equals(SCchoosen.class.getName())){
				Bundle data = new Bundle();
				data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				
				fsm.transit(GifPanelFSM.TransitBy.PasteSC.name(), data);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void onDeleteSoundCircle(){
		try{
			if(fsm.getCurrentStateName().equals(SCchoosen.class.getName())){
				Bundle data = new Bundle();
				data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				
				fsm.transit(GifPanelFSM.TransitBy.DeleteSC.name(), data);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*private void addSoundCircle(SoundCircleListener listener, SoundCircle sc){
		soundCircleTable.put(listener, sc);
	}
	
	private void removeSoundCircleOf(SoundCircleListener scl){
		if(null != scl)
			soundCircleTable.remove(scl);
	}*/
	
	/*public void highlightSoundCircle(SoundCircle sc){
		soundCircleTable.put(sc, true);
	}
	
	public void dehighlightSoundCircle(SoundCircle sc){
		soundCircleTable.put(sc, false);
	}*/
	
	public void setNewSoundCircleColor(Color c){
		fsm.newSoundCircleColor = c;
	}
	
	/*public void setSoundCirclesVisible(boolean visible){
		if(visible)
			status = Status.SC_ON_GIF;
		else
			status = Status.GIF_ONLY;
		
		repaint();
	}*/
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//g.setColor(Color.black);
		//g.drawRect(0, 0, getWidth(), getHeight());
		
		if(-1 == toPaintFrameIndex)
			return;
		
		BufferedImage img = gif.getFrame(toPaintFrameIndex);
		g.drawImage(img, 0, 0, null);
		
		//if(Status.SC_ON_GIF == status || Status.SC_DRAWING_ON_GIF == status){
			visualizeSoundCircles(g);
			
			// if the mouse button is pressed
			//if(Status.SC_DRAWING_ON_GIF == status){
			if(fsm.getCurrentStateName().equals(DrawingSC.class.getName())){
				
				/*g.setColor(newSoundCircleColor);
				int x = newSoundCircleCenter.x - newSoundCircleRadius;
				int y = newSoundCircleCenter.y - newSoundCircleRadius;
				g.fillOval(x, y, newSoundCircleRadius*2, newSoundCircleRadius*2);*/
				
				g.setColor(fsm.newSoundCircleColor);
				int x = fsm.newSoundCircleCenter.x - fsm.newSoundCircleRadius;
				int y = fsm.newSoundCircleCenter.y - fsm.newSoundCircleRadius;
				g.fillOval(x, y, fsm.newSoundCircleRadius*2, fsm.newSoundCircleRadius*2);
			}
			
			System.out.println("SelectedSC=" + fsm.selectedSC);
			if(fsm.getCurrentStateName().equals(SCchoosen.class.getName()) && null!=fsm.selectedSC){
				// TODO: set opposite color
				Color filledColor = fsm.selectedSC.color;
				Color edgeColor = new Color(255-filledColor.getRed(), 255-filledColor.getGreen(), 255-filledColor.getBlue(), 255);
				g.setColor(edgeColor);
				
				System.out.println("hi~~~~");
				Point p = fsm.selectedSC.getCirclePointAt(timing);
				System.out.println("selected SC potision at the time " + timing + " is " + p);
				int x = p.x - fsm.selectedSC.radius;
				int y = p.y - fsm.selectedSC.radius;
				g.drawOval(x, y, fsm.selectedSC.radius*2, fsm.selectedSC.radius*2);
			}
		//}
		
		//System.out.println("Painted Image[" + toPaintFrameIndex + "]: " + img);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		try{
			Bundle data = new Bundle();
			data.putObject(GifPanelFSM.DataKey.MouseEvent.name(), e);
			data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
			data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
			
			String stateName = fsm.getCurrentStateName();
			if(stateName.equals(PaintingSC.class.getName()) || stateName.equals(SCchoosen.class.getName()))
				fsm.transit(GifPanelFSM.TransitBy.MouseClicked.name(), data);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
			
		/*if(Status.SC_ON_GIF == status){
		// TODO: if a SC contains the mouse point, then select the SC
			SoundCircle sc = soundCircleTable.get(soundCircleListener);
			if(sc.contains(e.getPoint())){
				// select the SC and changes the status to SC_MOVING_ON_GIF
			}
		}*/
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		try{
			Bundle data = new Bundle();
			data.putObject(GifPanelFSM.DataKey.MouseEvent.name(), e);
			data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
			
			String stateName = fsm.getCurrentStateName();
			System.out.println("1111");
			if(stateName.equals(PaintingSC.class.getName())){
				System.out.println("2222");
				fsm.transit(GifPanelFSM.TransitBy.MousePressed.name(), data);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		/*System.out.println("PRESSED! 1");
		if(Status.SC_ON_GIF == status || Status.SC_DRAWING_ON_GIF == status){
			System.out.println("PRESSED! 2");
			
			SoundCircle sc = soundCircleTable.get(soundCircleListener);
			System.out.println("   SC = " + sc);
			
			if(null!=sc && !sc.contains(e.getPoint())){
				soundCircleTable.remove(soundCircleListener);
				repaint();
			}

			newSoundCircleRadius = 0;
			newSoundCircleCenter = e.getPoint();
			status = Status.SC_DRAWING_ON_GIF;
		}*/
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		try{
			String stateName = fsm.getCurrentStateName();
			if(stateName.equals(DrawingSC.class.getName())){
				Bundle data = new Bundle();
				data.putObject(GifPanelFSM.DataKey.MouseEvent.name(), e);
				data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
				
				fsm.transit(GifPanelFSM.TransitBy.MouseReleased.name(), data);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/*if(Status.SC_DRAWING_ON_GIF == status){
			// generate SC and notify the SC to soundPanel
			SoundCircle sc = new SoundCircle(newSoundCircleRadius, newSoundCircleCenter, newSoundCircleColor);
			soundCircleListener.onGenerated(sc);
			soundCircleTable.put(soundCircleListener, sc);
			
			newSoundCircleCenter = null;
			
			status = Status.SC_ON_GIF;
		}*/
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		// if a SC contains the mouse, highlight it by drawing its border
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
		
		try{
			Bundle data = new Bundle();
			data.putObject(GifPanelFSM.DataKey.MouseEvent.name(), e);
			
			String stateName = fsm.getCurrentStateName();
			
			if(stateName.equals(DrawingSC.class.getName())){
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				fsm.transit(GifPanelFSM.TransitBy.MouseDragged.name(), data);
			}
			
			else if(stateName.equals(SCchoosen.class.getName())){
				data.putObject(GifPanelFSM.DataKey.TimeValue.name(), timing);
				data.putObject(GifPanelFSM.DataKey.ComponentToRepaint.name(), this);
				fsm.transit(GifPanelFSM.TransitBy.MouseDragged.name(), data);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		/*
		//System.out.println("DRAGGED 1");
		if(Status.SC_DRAWING_ON_GIF == status){
			//System.out.println("DRAGGED 2");
			newSoundCircleRadius = (int)Math.sqrt( Math.pow(newSoundCircleCenter.x-e.getPoint().x, 2) 
					+ Math.pow(newSoundCircleCenter.y-e.getPoint().y, 2));
			
			// draw circle
			repaint();
		}*/
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}

