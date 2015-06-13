package com.jongseok.gifi.audio;

import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SoundCircle {
	public static String format = "GIFi-SoundCircle";
	public static String version = "2015a";
	
	public Sound sound;
	public int audioFileSize;	// excluding signature, version, and size
	public int radius;
	public Point center;
	//public int centerX;
	//public int centerY;
	public int minVolume;
	public int maxVolume;
	public Color color;
	
	public byte[] bytes;
	
	public ArrayList<ScheduleRecord> scheduleRecords = new ArrayList<ScheduleRecord>();
	
	public SoundCircle(int radius, Point center, Color color){
		this.radius = radius;
		this.center = center;
		this.color = color;
	}
	
	public SoundCircle(String soundFilePath, int radius, Point center, Color color){
		this(new Sound(soundFilePath), radius, center, color);
	}
	
	public SoundCircle(Sound sound, int radius, Point center, Color color){
		this(sound, radius, center, 0, 0, color);
	}
	
	public SoundCircle(Sound sound, int radius, Point center, int minVolume, int maxVolume, Color color){
		this.sound = sound;
		this.audioFileSize = sound.bytes.length;// + 20; 
		this.radius = radius;
		this.center = center;
		this.minVolume = minVolume;
		this.maxVolume = maxVolume;
		this.color = color;
	}
	
	@Override
	public String toString(){
		return "Color:" + color + " Center:" + center + " Radius:" + radius + "MinVolume:" + minVolume + " MaxVolume:" + maxVolume;
	}
	
	public boolean contains(Point p){
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
	public byte[] toBytes() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// TODO: error!
			bos.write(format.getBytes());
			bos.write(version.getBytes());
			bos.write(radius);
			bos.write(center.x);
			bos.write(center.y);
			bos.write(minVolume);
			bos.write(maxVolume);
			bos.write(audioFileSize);
			bos.write(sound.bytes);
			bos.close();
			
			bytes = bos.toByteArray();
		}
		
		return bytes;
	}
	
	// add offset for playing start point
	public void addPlayingPlan(int start, int end) throws Exception{
		if(start >= end)
			throw new Exception("Invalid time range! [" + start +", " + end + "]");
		
		scheduleRecords.add(new ScheduleRecord(start, true));
		scheduleRecords.add(new ScheduleRecord(end, false));
	}
	
	public ArrayList<ScheduleRecord> getScheduleRecords(){
		return scheduleRecords;
	}
}
