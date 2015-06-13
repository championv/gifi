package com.jongseok.gifi.gif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;

public class Gif {
	private GifImage img;
	private byte[] bytes;
	private int playingTime;	// in milliseconds
	private String filename;
	
	public Gif(String filepath) {
		
		try{
			File file = new File(filepath);
			
			filename = file.getName();
			bytes = Files.readAllBytes(file.toPath());
			img = GifDecoder.read(bytes);
			
			/*playingTime = 0;
			for(int index=0; index<img.getFrameCount(); index++)
				playingTime += img.getDelay(index);
			playingTime *= 10;*/
			
			playingTime = getFrameFinishingTime(getFrameCount()-1);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getFileName(){
		return filename;
	}
	
	public int getPlayingTime(){
		return playingTime;
	}
	
	public byte[] toBytes(){
		return bytes;
	}
	
	public BufferedImage getFrame(int index){
		return img.getFrame(index);
	}
	
	// return frame delay in milliseconds
	public int getDelay(int index){
		return img.getDelay(index) * 10;
	}
	
	public int getFrameStartingTime(int index){
		int elapsedTime = 0;
		for(int elapsedFrameIndex=0; elapsedFrameIndex < index; elapsedFrameIndex++)
			elapsedTime += getDelay(elapsedFrameIndex);
		
		return elapsedTime;
	}
	
	public int getFrameFinishingTime(int index){
		int elapsedTime = 0;
		for(int elapsedFrameIndex=0; elapsedFrameIndex<index+1; elapsedFrameIndex++)
			elapsedTime += getDelay(elapsedFrameIndex);
		
		return elapsedTime;
	}
	
	public int getFrameCount(){
		return img.getFrameCount();
	}
	
	public int getWidth(){
		return img.getWidth();
	}
	
	public int getHeight(){
		return img.getHeight();
	}
	
	public int getBackgroundColor(){
		return img.getBackgroundColor();
	}
}
