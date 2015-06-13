package com.jongseok.gifi.audio;

public class SchedulePiece {
	
	private int[] soundIndecies;
	private int endTime;	//ms
	
	//private int in;
	
/*	public SchedulePiece(int soundCount, int endTime){
		this.soundCount = soundCount;
		this.endTime = endTime;
		in = 0;
		
		soundIndexes = new int[soundCount];
	}*/
	
	
	public SchedulePiece(int endTime, int[] soundIndecies){
		this.soundIndecies = soundIndecies;
		this.endTime = endTime;
		//in = soundCount - 1;
	}
	
	public SchedulePiece(int endTime, Integer[] soundIndecies){
		this.endTime = endTime;
		
		this.soundIndecies = new int[soundIndecies.length];
		for(int index=0; index<soundIndecies.length; index++)
			this.soundIndecies[index] = soundIndecies[index];
	}
	
	public int getSoundIndex(int index){
		return soundIndecies[index];
	}
	
	public int getEndTime(){
		return endTime;
	}
	
	public int soundCount(){
		return soundIndecies.length;
	}
	
}
