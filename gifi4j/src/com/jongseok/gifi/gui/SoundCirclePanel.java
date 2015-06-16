package com.jongseok.gifi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.jongseok.gifi.audio.Sound;
import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.utils.Range;

public class SoundCirclePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, FocusListener, SoundCircleListener{
	
	public static int DEFAULT_TEXTFIELD_COLUMN_NUM = 2;
	public static final int TIMELINE_WIDTH_OFFSET = 26; // I don't know why it occurred
	
	private static ArrayList<Color> colorPool = new ArrayList<Color>(8);

	
	private JLabel filenameLabel;
	private JPanel volumePanel;
	private JLabel minVolumeLabel;
	private JLabel maxVolumeLabel;
	private JTextField minVolumeTextField;
	private JTextField maxVolumeTextField;
	private JButton circleSettingButton;
	private JButton deleteButton;
	
	//private Color backgroundColor;
	//private Color heightedColor;
	
	// Timing Rect
	private int animationPlayingTime;
	private Rectangle soundTimingRect;
	private int timingSegmentDrawingHeight;
	private ArrayList<Range> timingSegmentDrawings;
	//private ArrayList<Range> timingSegments;
	private Range newTimingSegmentDrawing;
	private Range newTimingSegment;
	private Range animationTimeRange;
	private int timingLineDrawing;
	private int currentTiming;
	private Point mousePressedPoint;
	
	private SoundCircleGenerator scGenerator;
	
	private Sound sound;
	private SoundCircle sc;
	
	public void fillColorPool(){
		colorPool.add(new Color(0, 255, 255, 127));
		colorPool.add(new Color(9, 255, 0, 127));
		colorPool.add(new Color(192, 192, 192, 127));
		colorPool.add(new Color(255, 0, 255, 127));
		colorPool.add(new Color(255, 200, 0, 127));
		colorPool.add(new Color(255, 175, 175, 127));
		colorPool.add(new Color(0, 0, 255, 127));
		colorPool.add(new Color(255, 255, 0, 127));
	}
	
	
	public Color getColorFromColorPool(){
		if(0 == colorPool.size())
			fillColorPool();
		
		int index = (int)(Math.random() * colorPool.size());
		Color selectedColor = colorPool.get(index);
		colorPool.remove(index);
		
		return selectedColor;
	}
	
	public SoundCirclePanel(String soundPath, SoundCircleGenerator scGenerator, int animationPlayingTime){
		this(new Sound(soundPath), scGenerator, animationPlayingTime);
	}
	
	public SoundCirclePanel(Sound sound, SoundCircleGenerator scGenerator, int animationPlayingTime){
		filenameLabel = new JLabel(sound.name);
		volumePanel = new JPanel();
		minVolumeLabel = new JLabel("min");
		maxVolumeLabel = new JLabel("max");
		minVolumeTextField = new JTextField("0", DEFAULT_TEXTFIELD_COLUMN_NUM);
		maxVolumeTextField = new JTextField("0", DEFAULT_TEXTFIELD_COLUMN_NUM);
		circleSettingButton = new JButton("Set Sound Circle");
		deleteButton = new JButton("Remove");
		
		
		// init components
		this.sound = sound;
		this.scGenerator = scGenerator;
		this.animationPlayingTime = animationPlayingTime;
		animationTimeRange = new Range(0, animationPlayingTime);
		setBackground(getColorFromColorPool());
		volumePanel.setBackground(new Color(0, 0, 0, 0));
		filenameLabel.setPreferredSize(new Dimension(MainFrame.FILENAME_LABEL_WIDTH, MainFrame.FILENAME_LABEL_HEIGHT));
		//minVolumeTextField.setEditable(false);
		//maxVolumeTextField.setEditable(false);
		circleSettingButton.addActionListener(this);
		deleteButton.addActionListener(this);
		minVolumeTextField.addFocusListener(this);
		maxVolumeTextField.addFocusListener(this);
		
		
		// set layout
		SpringLayout volumePanelLayout = new SpringLayout();
		volumePanel.setLayout(volumePanelLayout);
		
		volumePanelLayout.putConstraint(SpringLayout.NORTH, minVolumeLabel, 5, SpringLayout.NORTH, volumePanel);
		volumePanelLayout.putConstraint(SpringLayout.NORTH, minVolumeTextField, 0, SpringLayout.SOUTH, minVolumeLabel);
		volumePanelLayout.putConstraint(SpringLayout.NORTH, maxVolumeLabel, 5, SpringLayout.NORTH, volumePanel);
		volumePanelLayout.putConstraint(SpringLayout.NORTH, maxVolumeTextField, 0, SpringLayout.SOUTH, maxVolumeLabel);
		
		volumePanelLayout.putConstraint(SpringLayout.WEST, minVolumeLabel, 0, SpringLayout.WEST, volumePanel);
		volumePanelLayout.putConstraint(SpringLayout.WEST, minVolumeTextField, 0, SpringLayout.WEST, volumePanel);
		
		Component widerone = (minVolumeLabel.getPreferredSize().width > minVolumeTextField.getPreferredSize().width)? minVolumeLabel: minVolumeTextField;
		volumePanelLayout.putConstraint(SpringLayout.WEST, maxVolumeLabel, 3, SpringLayout.EAST, widerone);
		volumePanelLayout.putConstraint(SpringLayout.WEST, maxVolumeTextField, 3, SpringLayout.EAST, widerone);
		
		SpringLayout frameLayout = new SpringLayout();
		setLayout(frameLayout);
		
		frameLayout.putConstraint(SpringLayout.VERTICAL_CENTER, filenameLabel, 0, SpringLayout.VERTICAL_CENTER, this);
		frameLayout.putConstraint(SpringLayout.VERTICAL_CENTER, volumePanel, 0, SpringLayout.VERTICAL_CENTER, this);
		frameLayout.putConstraint(SpringLayout.VERTICAL_CENTER, circleSettingButton, 0, SpringLayout.VERTICAL_CENTER, this);
		frameLayout.putConstraint(SpringLayout.VERTICAL_CENTER, deleteButton, 0, SpringLayout.VERTICAL_CENTER, this);
		
		frameLayout.putConstraint(SpringLayout.WEST, filenameLabel, 5, SpringLayout.WEST, this);
		frameLayout.putConstraint(SpringLayout.WEST, volumePanel, 5 + GifTimelineSliderPanel.JSLIDER_WIDTH - TIMELINE_WIDTH_OFFSET + 5
				+ 5, SpringLayout.EAST, filenameLabel);
		frameLayout.putConstraint(SpringLayout.WEST, circleSettingButton, 10, SpringLayout.EAST, volumePanel);
		frameLayout.putConstraint(SpringLayout.WEST, deleteButton, 0, SpringLayout.EAST, circleSettingButton);
		
		
		// add components
		add(filenameLabel);
		volumePanel.add(minVolumeLabel);
		volumePanel.add(minVolumeTextField);
		volumePanel.add(maxVolumeLabel);
		volumePanel.add(maxVolumeTextField);
		add(volumePanel);
		add(circleSettingButton);
		add(deleteButton);

		
		// pack the panel size
		packPanel();
		addMouseListener(this);
		addMouseMotionListener(this);
		
		
		// setup sound timing rect
		int x = 5 + filenameLabel.getPreferredSize().width + 5;
		int y = 5;
		int width = GifTimelineSliderPanel.JSLIDER_WIDTH - TIMELINE_WIDTH_OFFSET + 5;
		int height = volumePanel.getPreferredSize().height;
		soundTimingRect = new Rectangle(x, y, width, height);
		timingSegmentDrawings = new ArrayList<Range>();
		//timingSegments = new ArrayList<Range>();
		timingSegmentDrawingHeight = height - 7;
		
				
		//System.out.println("filename label size = " + filenameLabel.getSize());
	}
	
	public void setTiming(int xCoordinate, int time){
		currentTiming = time;
		timingLineDrawing = xCoordinate;
		//repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		// draw sound timing rect
		g.setColor(Color.white);
		g.drawRect(soundTimingRect.x, soundTimingRect.y, soundTimingRect.width, soundTimingRect.height);
		
		// draw timing segments
		for(Range r: timingSegmentDrawings){
			//System.out.println("g.fillRect: " + r.from +","+10+","+(r.to-r.from+1)+","+timingSegmentDrawingHeight);
			g.fillRect(r.from, 8, r.to-r.from+1, timingSegmentDrawingHeight);
		}
		
		// draw the new timing segment
		g.setColor(new Color(0, 0, 0, 127));
		if(null != newTimingSegmentDrawing)
			g.fillRect(newTimingSegmentDrawing.from, 10, newTimingSegmentDrawing.to-newTimingSegmentDrawing.from+1, timingSegmentDrawingHeight);
		
		// draw timing line
		g.setColor(Color.black);
		g.drawLine(timingLineDrawing, 0, timingLineDrawing, getPreferredSize().height);
	}
	
	private void packVolumePanel(){
		int width = Math.max(minVolumeLabel.getPreferredSize().width, minVolumeTextField.getPreferredSize().width) + 3
				+ Math.max(maxVolumeLabel.getPreferredSize().width, maxVolumeTextField.getPreferredSize().width);
		int height = 5 + minVolumeLabel.getPreferredSize().height + minVolumeTextField.getPreferredSize().height;
		
		volumePanel.setPreferredSize(new Dimension(width, height));
	}
	
	private void packPanel(){
		packVolumePanel();
		
		int width = 5 + filenameLabel.getPreferredSize().width + 5 + GifTimelineSliderPanel.JSLIDER_WIDTH - TIMELINE_WIDTH_OFFSET + 5 + 5 
				+ volumePanel.getPreferredSize().width + 10 + circleSettingButton.getPreferredSize().width 
				+ deleteButton.getPreferredSize().width; 
		int height = 5 + volumePanel.getPreferredSize().height + 5;
		
		setPreferredSize(new Dimension(width, height));
	}
	
	public void markSelectedTime(int x){
		
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		SoundCirclePanel p = new SoundCirclePanel(new Sound("giggle.wav"), null, 2300);
		
		frame.add(p);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == circleSettingButton){

			scGenerator.setSoundCircleListener(this);
			scGenerator.setNewSoundCircleColor(getBackground());
			
			/*if(null != sc)
				gifpanel.removeSoundCircle(sc);*/
			//scGenerator.setSoundCirclesVisible(true);
		}
		
		else if(e.getSource() == deleteButton){
			
		}
	}
	
	private void releaseTimingSegmentPreview(){
		newTimingSegment = null;
		newTimingSegmentDrawing = null;
		getParent().repaint();
	} 
	
	private boolean validateSoundCircleTimeRange(Range timingSegment){
		if(timingSegment.isOutOf(animationTimeRange))
			return true;
		
		//for(Range r: timingSegments){
		for(Range r: sc.playingTimeSegments){
			if(r== timingSegment)
				continue;
			
			if(r.conflictWith(timingSegment))
				return true;
		}
		
		return false;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == minVolumeTextField)
			minVolumeTextField.setEditable(true);
		
		else if(e.getSource() == maxVolumeTextField)
			maxVolumeTextField.setEditable(true);
		
		else if(e.getSource() == this){
			// TODO: Do i have to set sound circles invisible?
			//scGenerator.setSoundCirclesVisible(false);
			scGenerator.setSoundCircleListener(null);
			
			// generate timing segment
			if(soundTimingRect.contains(e.getPoint())){
				
				if(validateSoundCircleTimeRange(newTimingSegment)){
					JOptionPane.showMessageDialog(this, "The selected timing is NOT valid!");
					releaseTimingSegmentPreview();
					return;
				}
				
			
				//timingSegments.add(newTimingSegment);
				sc.addTimingSegment(newTimingSegment);
				timingSegmentDrawings.add(newTimingSegmentDrawing);
				newTimingSegment = null;
				newTimingSegmentDrawing = null;
				
				//System.out.println("CLICK! timingSegments=" + timingSegments + "timingSegmentDrawings=" + timingSegmentDrawings);
				
				//repaint();
				getParent().repaint();
				
				// check confliction
			}
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse Pressed!");
		mousePressedPoint = e.getPoint();
		if(e.getSource() == this && soundTimingRect.contains(e.getPoint())){
			//System.out.println("Contains!!");
		
			//TODO
			float ratio = (float)sound.playingTime / (float)animationPlayingTime;
			int drawingTo = timingLineDrawing + (int)(ratio * (GifTimelineSliderPanel.JSLIDER_WIDTH - this.TIMELINE_WIDTH_OFFSET));
			newTimingSegmentDrawing = new Range(timingLineDrawing, drawingTo);
			newTimingSegment = new Range(currentTiming, currentTiming + sound.playingTime);
			repaint();
		}
	}
	

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!mousePressedPoint.equals(e.getPoint()))
			releaseTimingSegmentPreview();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// highlight the panel
		
		// show sound circle
		
		// can I visualize it in 3d?
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// restore background color
		
		// disappear sound circle
		
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == minVolumeTextField && minVolumeTextField.getText().equals("0"))
			minVolumeTextField.setText("");
		
		else if(e.getSource() == maxVolumeTextField && maxVolumeTextField.getText().equals("0"))
			maxVolumeTextField.setText("");
	}

	@Override
	public void focusLost(FocusEvent e) {
		if(e.getSource() == minVolumeTextField){
			//minVolumeTextField.setEditable(false);
			if(minVolumeTextField.getText().equals(""))
					minVolumeTextField.setText("0");
			
			if(!validateIntegerString(minVolumeTextField.getText()))
				minVolumeTextField.setText("0");
		}
		
		else if(e.getSource() == maxVolumeTextField){
			//maxVolumeTextField.setEditable(false);
			
			if(maxVolumeTextField.getText().equals(""))
				maxVolumeTextField.setText("0");
			
			if(!validateIntegerString(maxVolumeTextField.getText()))
				maxVolumeTextField.setText("0");
		}
		
	}
	
	public boolean validateIntegerString(String str){
		
		try{
			Integer.parseInt(str);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Please input INTEGER only");//"'" + str + "' is not an INTEGER.");
			return false;
		}
		
		return true;
	}

	@Override
	public void onGenerated(SoundCircle sc) {
		System.out.println("onGenerated: SC is generated");
		//gifpanel.removeSoundCircle(this.sc);
		sc.sound = sound;
		this.sc = sc;
		
		//gifpanel.addSoundCircle(this, sc);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
