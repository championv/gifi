package com.jongseok.gifi.audio;

import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.jongseok.gifi.utils.BinaryUtil;
import com.jongseok.gifi.utils.Range;

public class SoundCircle {
	public static String format = "GIFi-SoundCircle";
	public static String version = "2015a";
	
	//private static final int DEFAULT_INITIAL_CIRCLE_TIMING = -1;
	//private static final int DEFAULT_INITIAL_CIRCLE_TIMING = 0;
	
	public Sound sound;
	public int audioFileSize;	// excluding signature, version, and size
	public int radius;
	//public Point center;
	//public int centerX;
	//public int centerY;
	public int minVolume;
	public int maxVolume;
	public Color color;
	
	// TODO: implement it as binary tree
	public Hashtable<Integer, Point> centers;
	public ArrayList<Range> playingTimeSegments;
	
	public byte[] bytes;
	private int size;
	
	//public ArrayList<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
	
	public SoundCircle(int time, int radius, Point center, Color color){
		playingTimeSegments = new ArrayList<Range>();
		centers = new Hashtable<Integer, Point>();
		//centers.put(DEFAULT_INITIAL_CIRCLE_TIMING, center);
		centers.put(time, center);
		
		this.radius = radius;
		//this.center = center;
		this.color = color;
	}
	
	public SoundCircle(String soundFilePath, int time, int radius, Point center, Color color){
		this(new Sound(soundFilePath), time, radius, center, color);
	}
	
	public SoundCircle(Sound sound, int time, int radius, Point center, Color color){
		this(sound, time, radius, center, 0, 0, color);
	}
	
	public SoundCircle(Sound sound, int time, int radius, Point center, int minVolume, int maxVolume, Color color){
		playingTimeSegments = new ArrayList<Range>();
		centers = new Hashtable<Integer, Point>();
		//centers.put(DEFAULT_INITIAL_CIRCLE_TIMING, center);
		if(null != center && -1 != time)
			centers.put(time, center);
		
		if(null != sound){
			this.sound = sound;
			this.audioFileSize = sound.bytes.length;// + 20;
		}
		
		this.radius = radius;
		//this.center = center;
		this.minVolume = minVolume;
		this.maxVolume = maxVolume;
		this.color = color;
	}
	
	
	public void setCircles(Hashtable<Integer, Point> centers){
		this.centers = centers;
	}
	
	public void addCircleAt(int time, Point p){
		centers.put(time, p);
	}
	
	public void removeCircles(){
		centers = new Hashtable<Integer, Point>();
	}
	
	public void removeCirclesAtAndAfter(int time){
		ArrayList<Integer> keysToRemove = new ArrayList<Integer>();
		
		for(int t: centers.keySet()){
			if(t >= time)
				keysToRemove.add(t);
		}
		
		// remove
		for(int key: keysToRemove)
			centers.remove(key);
	}
	
	public void addTimingSegment(Range r){
		
		playingTimeSegments.add(r);
	}

	/*// add offset for playing start point
	public void addPlayingPlan(int start, int end) throws Exception{
		if(start >= end)
			throw new Exception("Invalid time range! [" + start +", " + end + "]");
		
		scheduleRecords.add(new ScheduleRecord(start, true));
		scheduleRecords.add(new ScheduleRecord(end, false));
	}
	
	public ArrayList<ScheduleRecord> getScheduleRecords(){
		return scheduleRecords;
	}*/
	
	public ArrayList<ScheduleRecord> convertTimingSegments2schedulRecords() throws Exception{
		ArrayList<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
		
		for(Range r: playingTimeSegments){
			if(r.from >= r.to)
				throw new Exception("Invalid time range! [" + r.from +", " + r.to + "]");
			
			scheduleRecords.add(new ScheduleRecord(r.from, true));
			scheduleRecords.add(new ScheduleRecord(r.to, false));
		}
		
		return scheduleRecords;
	}
	
	@Override
	public String toString(){
		return "Color:" + color + " Centers:" + centers + " Radius:" + radius + "MinVolume:" + minVolume + " MaxVolume:" + maxVolume;
	}
	
	// find the positions right before and right after the input time in the table.
	public Point getCirclePointAt(int time){
		System.out.println("centers.size=" + centers.size());
		if(centers.size() == 0)
			return null;

		// get the circle position at the specified time.
		else if(centers.size() == 1)
			//return centers.get(0);
			return centers.values().iterator().next();
		
		else if(null != centers.get(time))
			return centers.get(time);
		
		//int rightBeforeTime = Integer.MIN_VALUE;
		int rightBeforeTime = -1;
		int rightAfterTime = Integer.MAX_VALUE;
		
		for(int t: centers.keySet()){
			if(t<time && (time-t) < (time-rightBeforeTime))
				rightBeforeTime = t;
			
			else if(time<t && (t-time) < (rightAfterTime-time))
				rightAfterTime = t;
		}
		
		// case 1:  
		if(Integer.MAX_VALUE == rightAfterTime)
			return centers.get(rightBeforeTime);
		
		// case 2:
		if(-1 == rightBeforeTime)
			//return null;
			return centers.get(rightAfterTime);
		
		// case 3: both right before and after time exist.
		// linearly interpolate two points.
		Point rightBefore = centers.get(rightBeforeTime);
		Point rightAfter = centers.get(rightAfterTime);
		System.out.println("RigtBefore: " + rightBefore + " At " + rightBeforeTime);
		System.out.println("RightAfter: " + rightAfter + " At " + rightAfterTime);
		
		double delta_t = rightAfterTime-rightBeforeTime;
		int x =(int)( ((double)(rightAfter.x-rightBefore.x)) / delta_t * ((double)time-rightBeforeTime) + rightBefore.x );
		int y =(int)( ((double)(rightAfter.y-rightBefore.y)) / delta_t * ((double)time-rightBeforeTime) + rightBefore.y );
		
		return new Point(x,y);
	}
	
	public boolean contains(Point p, int time){
		//int d = (int)Math.sqrt(Math.pow(center.x-p.x, 2) + Math.pow(center.y-p.y, 2));

		Point center = getCirclePointAt(time);
		if(null == center)
			return false;
		
		int d = (int)Math.sqrt(Math.pow(center.x-p.x, 2) + Math.pow(center.y-p.y, 2));
		return d <= radius;
	}
	
	public void setSound(Sound s){
		this.sound = s;
	}
	
	public void setMinVolume(int v){
		minVolume = v;
	}
	
	public void setMaxVolume(int v){
		maxVolume = v;
	}
	
	public int getSize(){
		return size;
	}
	
	
	// TODO: fix it!
	/* Binary Format
	 * 
	 * format 			(13 bytes)
	 * version 			(5 bytes)
	 * radius			(4 bytes)
	 * centerX			(4 bytes)
	 * centerY			(4 bytes)
	 * minVolume		(4 bytes)
	 * maxVolume		(4 bytes)
	 * audio size 		(4 bytes)
	 * audio file		(N bytes)
	 * 
	 * */
	public byte[] encode() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// TODO: error!
			bos.write(format.getBytes());
			bos.write(version.getBytes());
			bos.write(BinaryUtil.intToBytes(radius));
			//bos.write(center.x);
			//bos.write(center.y);
			bos.write(BinaryUtil.intToBytes(minVolume));
			bos.write(BinaryUtil.intToBytes(maxVolume));
			bos.write(BinaryUtil.intToBytes(centers.size()));
			
			for(int time: centers.keySet()){
				Point p = centers.get(time);
				bos.write(BinaryUtil.intToBytes(time));
				bos.write(BinaryUtil.intToBytes(p.x));
				bos.write(BinaryUtil.intToBytes(p.y));
			}
			
			bos.write(BinaryUtil.intToBytes(audioFileSize));
			bos.write(sound.bytes);
			bos.close();
			
			bytes = bos.toByteArray();
			size = bytes.length;
		}
		
		return bytes;
	}
	
	public static SoundCircle decode(byte[] bytes, int offset) throws IOException{
		int initialOffset = offset;
		
		// check format and version
		if(!format.equals(BinaryUtil.toString(bytes, offset, format.length())))
			return null;
		offset += format.length();
		
		if(!version.equals(BinaryUtil.toString(bytes, offset, version.length())))
			return null;
		offset += version.length();
		
		int radius = BinaryUtil.toInt(bytes, offset);
		offset += 4;
		
		int minVolume = BinaryUtil.toInt(bytes, offset);
		offset += 4;
		
		int maxVolume = BinaryUtil.toInt(bytes, offset);
		offset += 4;
		
		int centersCount = BinaryUtil.toInt(bytes, offset);
		offset += 4;
		
		Hashtable<Integer, Point> centers = new Hashtable<Integer, Point>();
		for(int index=0; index<centersCount; index++){
			int time = BinaryUtil.toInt(bytes, offset);
			offset += 4;
			
			int x = BinaryUtil.toInt(bytes, offset);
			offset += 4;
			
			int y = BinaryUtil.toInt(bytes, offset);
			offset += 4;
			
			centers.put(time, new Point(x, y));
		}
		
		int audioFileSize = BinaryUtil.toInt(bytes, offset);
		offset += 4;
		
		byte[] soundBytes = new byte[audioFileSize];
		soundBytes = BinaryUtil.cloneByte(bytes, offset, audioFileSize);
		Sound sound = new Sound(soundBytes);
		offset += audioFileSize;
		
		//TODO: read sound from bytes
		//public SoundCircle(Sound sound, int time, int radius, Point center, int minVolume, int maxVolume, Color color){
		SoundCircle sc = new SoundCircle(sound, -1, radius, null, minVolume, maxVolume, null);
		sc.size = offset - initialOffset;
		return sc;
	}
	
}
