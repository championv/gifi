package com.jongseok.gifi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.utils.Bundle;
import com.jongseok.gifi.utils.FSM;
import com.jongseok.gifi.utils.State;

public class GifPanelFSM extends FSM{

	enum TransitBy {
		SoundCircleGeneratorEnabled, 
		MousePressed, MouseReleased, MouseDragged, MouseClicked, 
		CopySC, PasteSC, DeleteSC, 
		KeyEscTyped
	}

	enum DataKey { MouseEvent, SoundCircle, SoundCircleSet, TimeValue, ComponentToRepaint }

	// Sound Circle generation
	public Point newSoundCircleCenter;
	public int newSoundCircleRadius = -1;
	public Color newSoundCircleColor = null;
	
	public SoundCircle selectedSC = null;
	public Point scPositionClipboard = null;
	
	public SoundCircleListener soundCircleListener;
	public Hashtable<SoundCircleListener, SoundCircle> soundCircleTable;
	
	public GifPanelFSM(){
		soundCircleTable = new Hashtable<SoundCircleListener, SoundCircle>();
		
		addState(new NoSC());
		addState(new PaintingSC());
		addState(new DrawingSC());
		addState(new SCchoosen());
		
		setStartState(NoSC.class.getName());
		//setStartState(PaintingSC.class.getName());
		start();
	}

	class NoSC extends State {
		public NoSC() {
			super(NoSC.class.getName());
		}

		@Override
		public String transit(String input, Bundle data) {
			
			if (input.equals(TransitBy.SoundCircleGeneratorEnabled.name()))
				return PaintingSC.class.getName();

			else
				return getName();
		}
	}

	class PaintingSC extends State {
		public PaintingSC() {
			super(PaintingSC.class.getName());
		}

		@Override
		public String transit(String input, Bundle data) {

			MouseEvent e = (MouseEvent) data.getObject(DataKey.MouseEvent.name());
			
			if (input.equals(TransitBy.MousePressed.name())) 
				return onMousePressed(e, data);

			else if (input.equals(TransitBy.MouseClicked.name()))
				return onMouseClicked(e, data);

			else
				return getName();
		}
		
		private String onMousePressed(MouseEvent e, Bundle data){

			// read data from bundle
			//MouseEvent e = (MouseEvent) data.getObject(DataKey.MouseEvent.name());
			//Set<SoundCircle> scset = (Set<SoundCircle>) data.getObject(DataKey.SoundCircleSet.name());
			int timeValue = (int) data.getObject(DataKey.TimeValue.name());

			//if (null == e || null == scset || null == data.getObject(DataKey.TimeValue.name()))
				//return null;

			// find a SC which contains the mouse at current time
			/*for (SoundCircle sc : soundCircleTable.values()) {
				// if a SC contains the mouse, stay in the current state.
				if (sc.contains(e.getPoint(), timeValue))
					return getName();
			}*/

			
			SoundCircle prevGeneratedSC = soundCircleTable.get(soundCircleListener);
			if(null != prevGeneratedSC && prevGeneratedSC.centers.size()>0)
				return getName();
			/*if(prevGeneratedSC.contains(e.getPoint(), timeValue))
				return getName();
			*/
			System.out.println("prev SC = " + prevGeneratedSC);
			//if(null != prevGeneratedSC)
				//prevGeneratedSC.removeCirclesAtAndAfter(timeValue);
			
			// mouse is outside of sound circles. Set new sound circle center and radius
			newSoundCircleRadius = 0;
			newSoundCircleCenter = e.getPoint();
			
			return DrawingSC.class.getName();
		}
		
		private String onMouseClicked(MouseEvent e, Bundle data){

			// read data from bundle
			//Set<SoundCircle> scset = (Set<SoundCircle>) data.getObject(DataKey.SoundCircleSet.name());
			int timeValue = (int) data.getObject(DataKey.TimeValue.name());
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			
			//if(null == e || null == scset || null == data.getObject(DataKey.TimeValue.name()))
				//return null;
			
			// find a SC which contains the mouse at current time
			SoundCircle sc = null;
			for (SoundCircle s : soundCircleTable.values()) {
				// if a SC contains the mouse, stay in the current state.
				if (s.contains(e.getPoint(), timeValue)){
					sc = s;
					break;
				}
			}
			
			if(null == sc)
				return getName();
			
			// choose the SC
			selectedSC = sc;
			
			c.repaint();
			return SCchoosen.class.getName();
			
		}
	}

	class DrawingSC extends State {
		public DrawingSC() {
			super(DrawingSC.class.getName());
		}

		@Override
		public String transit(String input, Bundle data) {

			MouseEvent e = (MouseEvent)data.getObject(DataKey.MouseEvent.name());
			
			if (input.equals(TransitBy.MouseReleased.name()))
				return onMouseReleased(e, data);
			
			else if (input.equals(TransitBy.MouseDragged.name()))
				return onMouseDragged(e, data);
			
			// Invalid input
			else if (input.equals(TransitBy.MousePressed.name()))
				return null;

			else
				return getName();
		}
		
		private String onMouseDragged(MouseEvent e, Bundle data){
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			
			newSoundCircleRadius = (int)Math.sqrt( Math.pow(newSoundCircleCenter.x-e.getPoint().x, 2) 
					+ Math.pow(newSoundCircleCenter.y-e.getPoint().y, 2));
			
			// draw circle
			//repaint();
			c.repaint();
			return DrawingSC.class.getName();
		}
		
		private String onMouseReleased(MouseEvent e, Bundle data){
			// generate SC and notify the SC to soundPanel
			int time = (int)data.getObject(DataKey.TimeValue.name());
					
			SoundCircle sc = new SoundCircle(time, newSoundCircleRadius, newSoundCircleCenter, newSoundCircleColor);
			soundCircleListener.onGenerated(sc);
			soundCircleTable.put(soundCircleListener, sc);
			
			newSoundCircleCenter = null;
			
			return PaintingSC.class.getName();
		}
	}

	class SCchoosen extends State {
		public SCchoosen() {
			super(SCchoosen.class.getName());
		}

		@Override
		public String transit(String input, Bundle data) {

			if (input.equals(TransitBy.MouseDragged.name()))
				return onMouseDragged((MouseEvent)data.getObject(DataKey.MouseEvent.name()), data);

			else if (input.equals(TransitBy.MouseClicked.name())) 
				return onMouseClicked((MouseEvent)data.getObject(DataKey.MouseEvent.name()), data);

			else if (input.equals(TransitBy.SoundCircleGeneratorEnabled.name()))
				return onSoundCircleGeneratorEnabled(data);

			else if (input.equals(TransitBy.DeleteSC.name()))
				return onDeleteSoundCircle(data);

			else if (input.equals(TransitBy.KeyEscTyped.name()))
				return onKeyEscTyped(data);
			
			else if (input.equals(TransitBy.CopySC.name())) 
				return onCopySoundCircle(data);

			else if (input.equals(TransitBy.PasteSC.name())) 
				return onPasteSoundCircle(data);

			else
				return getName();
		}
		
		private String onMouseDragged(MouseEvent e, Bundle data){
			int timeValue = (int)data.getObject(DataKey.TimeValue.name());
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			
			selectedSC.addCircleAt(timeValue, e.getPoint());
			c.repaint();
			return SCchoosen.class.getName();
		}
		
		private String onMouseClicked(MouseEvent e, Bundle data){
			int timeValue = (int)data.getObject(DataKey.TimeValue.name());
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			
			// find a SC which contains the mouse at current time
			for (SoundCircle sc : soundCircleTable.values()) {
				// if a SC contains the mouse, do nothing and stay in the current state.
				if (sc.contains(e.getPoint(), timeValue))
					return getName();
			}
			
			selectedSC = null;
			c.repaint();
			return PaintingSC.class.getName();
		}
		
		private String onSoundCircleGeneratorEnabled(Bundle data){
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			selectedSC = null;
			c.repaint();
			
			return PaintingSC.class.getName();
			
		}
		
		private String onDeleteSoundCircle(Bundle data){
			int time = (int)data.getObject(DataKey.TimeValue.name());
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			
			selectedSC.removeCircles();
			//soundCircleTable.remove
			selectedSC = null;
			c.repaint();
			
			return PaintingSC.class.getName();
		}
		
		private String onKeyEscTyped(Bundle data){
			selectedSC = null;
			
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());
			c.repaint();
			
			return PaintingSC.class.getName();
		}
		
		private String onCopySoundCircle(Bundle data){
			int time = (int)data.getObject(DataKey.TimeValue.name());
			
			scPositionClipboard = selectedSC.getCirclePointAt(time);
			return getName();
		}	
		
		private String onPasteSoundCircle(Bundle data){
			if(null == scPositionClipboard)
				return getName();
			
			int time = (int)data.getObject(DataKey.TimeValue.name());
			Component c = (Component)data.getObject(DataKey.ComponentToRepaint.name());

			selectedSC.addCircleAt(time, scPositionClipboard);
			c.repaint();
			return getName();
		}
	}
}
