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
	
	private boolean isScheduleUpdated;
	
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
	public byte[] toByteArray() throws IOException{
		if(isScheduleUpdated || null==bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// TODO: write format and version
			bos.write(format.getBytes());
			bos.write(version.getBytes());
			bos.write(toSchedulePieces().size());
			
			for(SchedulePiece p: toSchedulePieces()){
				bos.write(p.getEndTime());
				bos.write(p.soundCount());
				
				for(int index=0; index<p.soundCount(); index++)
					bos.write(p.getSoundIndex(index));
			}
			bos.close();
		}
		
		return bytes;
	}
	
	public static SoundSchedule decode(byte[] bytes) throws IOException{
		int bytesIndex = 0;
		
		// check format and version
		if(!format.equals(BinaryUtil.toString(bytes, bytesIndex, format.length())))
			return null;
		
		bytesIndex += format.length();
		if(!version.equals(BinaryUtil.toString(bytes, bytesIndex, version.length())))
			return null;
		
		// decodes schedule pieces
		bytesIndex += version.length();
		int schedulePieceCount = BinaryUtil.toInt(bytes, bytesIndex);
		ArrayList<SchedulePiece> schedulePieces = new ArrayList<SchedulePiece>(schedulePieceCount);
				
		for(int schedulePieceIndex=0; schedulePieceIndex<schedulePieceCount; schedulePieceIndex++){
			bytesIndex += 4;
			int endTime = BinaryUtil.toInt(bytes, bytesIndex);
			
			bytesIndex += 4;
			int scIndexCount = BinaryUtil.toInt(bytes, bytesIndex);
			int[] scIndecies = new int[scIndexCount];
			
			for(int scIndexIndex=0; scIndexIndex<scIndexCount; scIndexIndex++){
				bytesIndex += 4;
				scIndecies[scIndexIndex] = BinaryUtil.toInt(bytes, bytesIndex);
			}
			
			schedulePieces.add(new SchedulePiece(endTime, scIndecies));
		}
		
		bytesIndex += 4;
		return new SoundSchedule(schedulePieces);
	}
	
	public void addScheduleRecord(ScheduleRecord r){
		if(null == r)
			return;
		
		isScheduleUpdated = true;
		scheduleRecords.add(r);
	}

	public void addSchedule(int startTime, int endTime, int scIndex) throws Exception{
		if(startTime >= endTime)
			throw new Exception("Invalid time range! [" + startTime +", " + endTime + "]");
		
		isScheduleUpdated = true;
		scheduleRecords.add(new ScheduleRecord(startTime, true, scIndex));
		scheduleRecords.add(new ScheduleRecord(endTime, false, scIndex));
	}
	
	public ArrayList<SchedulePiece> toSchedulePieces(){
		
		if(isScheduleUpdated){
			schedulePieces = null;
			isScheduleUpdated = false;
			
			// generate initial or additional schedule pieces
			int previousTime = scheduleRecords.get(0).time;
			HashSet<Integer> hset = new HashSet<Integer>();
			hset.add(scheduleRecords.get(0).scIndex);
			Collections.sort(scheduleRecords);
			
			for(int index=1; index<scheduleRecords.size(); index++){
				ScheduleRecord r = scheduleRecords.get(index);
				
				if(r.time != previousTime){
					previousTime = r.time;
					schedulePieces.add(new SchedulePiece(r.time, (Integer[]) hset.toArray()));
				}
				
				if(r.isStart)
					hset.add(r.scIndex);
				else
					hset.remove(r.scIndex);
			}
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
