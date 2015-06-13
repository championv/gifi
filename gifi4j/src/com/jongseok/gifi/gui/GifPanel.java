package com.jongseok.gifi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.gif.Gif;

public class GifPanel extends JPanel implements SoundCircleGenerator, MouseListener, MouseMotionListener{
	private static final int DEFAULT_WIDTH = 500;
	private static final int DEFAULT_HEIGHT = 500;
	private static final Color DEHIGHLIGHTED_SC_COLOR = new Color(64, 64, 64, 127); 

	public enum Status{GIF_ONLY, SC_ON_GIF, SC_DRAWING_ON_GIF}
	
	private Gif gif;
	private Status status;
	private int toPaintFrameIndex = -1;
	//private Timer timer;
	private Thread gifPlayer;
	private boolean isPlaying = false;
	private Point newSoundCircleCenter;
	private int newSoundCircleRadius = -1;
	private Color newSoundCircleColor = null;
	private Hashtable<SoundCircleListener, SoundCircle> soundCircleTable;
	private SoundCircleListener soundCircleListener;
	//private Hashtable<SoundCirclePanel> soundCircleTable;
	//private ArrayList<SoundCircleListener> soundCircleListeners;
	
	public GifPanel(){
		status = Status.GIF_ONLY;
		soundCircleTable = new Hashtable<SoundCircleListener, SoundCircle>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.black));
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
		for(SoundCircle sc: soundCircleTable.values()){
			/*if(soundCircleTable.get(sc))
				g.setColor(sc.color);
			else
				g.setColor(DEHIGHLIGHTED_SC_COLOR);*/
			g.setColor(sc.color);
			
			// TODO: represent volume in colors (opacity, color) or 3-D shape
			int x = sc.center.x - sc.radius;
			int y = sc.center.y - sc.radius;
			g.fillOval(x, y, sc.radius*2, sc.radius*2);
		}
	}
	
	public void setSoundCircleListener(SoundCircleListener listener){
		soundCircleListener = listener;
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
		newSoundCircleColor = c;
	}
	
	public void setSoundCirclesVisible(boolean visible){
		if(visible)
			status = Status.SC_ON_GIF;
		else
			status = Status.GIF_ONLY;
		
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		//g.setColor(Color.black);
		//g.drawRect(0, 0, getWidth(), getHeight());
		
		if(-1 == toPaintFrameIndex)
			return;
		
		BufferedImage img = gif.getFrame(toPaintFrameIndex);
		g.drawImage(img, 0, 0, null);
		
		if(Status.SC_ON_GIF == status || Status.SC_DRAWING_ON_GIF == status){
			visualizeSoundCircles(g);
			
			// if the mouse button is pressed
			if(Status.SC_DRAWING_ON_GIF == status){
				g.setColor(newSoundCircleColor);
				int x = newSoundCircleCenter.x - newSoundCircleRadius;
				int y = newSoundCircleCenter.y - newSoundCircleRadius;
				g.fillOval(x, y, newSoundCircleRadius*2, newSoundCircleRadius*2);
			}
		}
		
		//System.out.println("Painted Image[" + toPaintFrameIndex + "]: " + img);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(Status.SC_ON_GIF == status){
		// TODO: if a SC contains the mouse point, then select the SC
			SoundCircle sc = soundCircleTable.get(soundCircleListener);
			if(sc.contains(e.getPoint())){
				// select the SC and changes the status to SC_MOVING_ON_GIF
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("PRESSED! 1");
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
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(Status.SC_DRAWING_ON_GIF == status){
			// generate SC and notify the SC to soundPanel
			SoundCircle sc = new SoundCircle(newSoundCircleRadius, newSoundCircleCenter, newSoundCircleColor);
			soundCircleListener.onGenerated(sc);
			soundCircleTable.put(soundCircleListener, sc);
			
			newSoundCircleCenter = null;
			
			status = Status.SC_ON_GIF;
		}
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
		//System.out.println("DRAGGED 1");
		if(Status.SC_DRAWING_ON_GIF == status){
			//System.out.println("DRAGGED 2");
			newSoundCircleRadius = (int)Math.sqrt( Math.pow(newSoundCircleCenter.x-e.getPoint().x, 2) 
					+ Math.pow(newSoundCircleCenter.y-e.getPoint().y, 2));
			
			// draw circle
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
