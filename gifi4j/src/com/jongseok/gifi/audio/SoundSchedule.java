package com.jongseok.gifi.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.jongseok.gifi.*;
import com.jongseok.gifi.utils.BinaryUtil;

public class SoundSchedule {
	
	public static String format = "Sound Schedule";
	public static String version = "2014a";

	private ArrayList<SchedulePiece> schedulePieces;
	private ArrayList<ScheduleRecord> scheduleRecords;
	
	private byte[] bytes;
	private int size = -1;
	
	public boolean isScheduleUpdated;
	
	public SoundSchedule(){
		schedulePieces = new ArrayList<SchedulePiece>();
		scheduleRecords = new ArrayList<ScheduleRecord>();
		
		isScheduleUpdated = false;
	}
	
	public SoundSchedule(ArrayList<SchedulePiece> schedulePieces){
		this.schedulePieces = schedulePieces;
		scheduleRecords = new ArrayList<ScheduleRecord>();
		
		isScheduleUpdated = false;
	}
	
	/* Binary Format
	 * 
	 * format 			(13 bytes)
	 * version 			(5 bytes)
	 * size 			(4 bytes)
	 * 
	 * schedulePiece	(N bytes)
	 * 		endTime		(4 bytes)
	 * 		size		(4 bytes)
	 * 		scIndex		(4 bytes)
	 * 		...
	 * 		scIndex		(4 bytes)
	 * ...
	 * schedulePiece	(N bytes)
	 * 
	 * */
	public byte[] encode() throws IOException{
		if(isScheduleUpdated || null==bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// TODO: write format and version
			bos.write(format.getBytes());
			bos.write(version.getBytes());
			bos.write(BinaryUtil.intToBytes(toSchedulePieces().size()));
			
			for(SchedulePiece p: toSchedulePieces()){
				bos.write(BinaryUtil.intToBytes(p.getEndTime()));
				bos.write(BinaryUtil.intToBytes(p.soundCount()));
				
				for(int index=0; index<p.soundCount(); index++)
					bos.write(BinaryUtil.intToBytes(p.getSoundIndex(index)));
			}
			
			bytes = bos.toByteArray();
			size = bytes.length;
			bos.close();
		}
		
		return bytes;
	}
	
	public static SoundSchedule decode(byte[] bytes, int offset) throws IOException{
		int initialOffset = offset;
		int bytesIndex = offset;
		
		// check format and version
		if(!format.equals(BinaryUtil.toString(bytes, bytesIndex, format.length())))
			return null;
		bytesIndex += format.length();
		
		if(!version.equals(BinaryUtil.toString(bytes, bytesIndex, version.length())))
			return null;
		bytesIndex += version.length();
		
		// decodes schedule pieces
		
		int schedulePieceCount = BinaryUtil.toInt(bytes, bytesIndex);
		ArrayList<SchedulePiece> schedulePieces = new ArrayList<SchedulePiece>(schedulePieceCount);
		bytesIndex += 4;
				
		for(int schedulePieceIndex=0; schedulePieceIndex<schedulePieceCount; schedulePieceIndex++){
			int endTime = BinaryUtil.toInt(bytes, bytesIndex);
			bytesIndex += 4;
			
			int scIndexCount = BinaryUtil.toInt(bytes, bytesIndex);
			int[] scIndecies = new int[scIndexCount];
			bytesIndex += 4;
			
			for(int scIndexIndex=0; scIndexIndex<scIndexCount; scIndexIndex++){
				scIndecies[scIndexIndex] = BinaryUtil.toInt(bytes, bytesIndex);
				bytesIndex += 4;
			}
			
			schedulePieces.add(new SchedulePiece(endTime, scIndecies));
		}
		
		SoundSchedule schedule = new SoundSchedule(schedulePieces);
		schedule.size = bytesIndex - initialOffset;
		return schedule;
	}
	
	public int getSize(){
		//return bytes.length;
		return size;
	}
	
	public void addScheduleRecord(ScheduleRecord r){
		if(null == r)
			return;
		
		isScheduleUpdated = true;
		scheduleRecords.add(r);
	}

	/*public void addSchedule(int startTime, int endTime, int scIndex) throws Exception{
		if(startTime >= endTime)
			throw new Exception("Invalid time range! [" + startTime +", " + endTime + "]");
		
		isScheduleUpdated = true;
		scheduleRecords.add(new ScheduleRecord(startTime, true, scIndex));
		scheduleRecords.add(new ScheduleRecord(endTime, false, scIndex));
	}*/
	
	public ArrayList<SchedulePiece> toSchedulePieces(){
		
		if(isScheduleUpdated){
			//schedulePieces = null;
			schedulePieces = new ArrayList<SchedulePiece>();
			isScheduleUpdated = false;
			
			// generate initial or additional schedule pieces
			Collections.sort(scheduleRecords);
			int previousTime = scheduleRecords.get(0).time;
			HashSet<Integer> hset = new HashSet<Integer>();
			hset.add(scheduleRecords.get(0).scIndex);
			
			
			for(int index=1; index<scheduleRecords.size(); index++){
				ScheduleRecord r = scheduleRecords.get(index);
				
				// found the new piece?
				if(r.time != previousTime){
					
					// finish and save the old piece
					//schedulePieces.add(new SchedulePiece(r.time, (Integer[]) hset.toArray()));
					schedulePieces.add(new SchedulePiece(previousTime, hset.toArray()));
					previousTime = r.time;
				}
				
				if(r.isStart)
					hset.add(r.scIndex);
				else
					hset.remove(r.scIndex);
			}
			
			// last piece
			schedulePieces.add(new SchedulePiece(previousTime, hset.toArray()));
		}
		
		return schedulePieces;
	}
	
	/*static class ScheduleRecordAscComparator implements Comparator<ScheduleRecord>{

		@Override
		public int compare(ScheduleRecord o1, ScheduleRecord o2) {
			
			return 0;
		}
	}*/
	
}
