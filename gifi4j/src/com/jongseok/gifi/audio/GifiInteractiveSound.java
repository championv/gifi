package com.jongseok.gifi.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.jongseok.gifi.utils.BinaryUtil;

public class GifiInteractiveSound {
	
	private ArrayList<SoundCircle> soundCircles;
	private SoundSchedule schedule;
	
	private byte[] bytes;
	private int size;

	public GifiInteractiveSound(ArrayList<SoundCircle> soundCircles) throws Exception{
		this.soundCircles = soundCircles;
		schedule = generateSchedule();
		System.out.println("isScheduleUpdated? " + schedule.isScheduleUpdated);
	}
	
	public GifiInteractiveSound(SoundSchedule schedule, ArrayList<SoundCircle> soundCircles){
		this.schedule = schedule;
		this.soundCircles = soundCircles;
	}
	
	public int getSize(){
		return size;
	}
	
	public byte[] encode() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// encode sound schedule
			bos.write(schedule.encode());
			
			// encode sound circles
			for(SoundCircle sc: soundCircles)
				bos.write(sc.encode());
			
			/*// encode sound binaries: it's done when write the sound circle. currently assume one sound has a only one sound circle
			for(SoundCircle sc: soundCircles)
				bos.write(sc.sound.bytes);*/
				
			bytes = bos.toByteArray();
			bos.close();
		}
		
		return bytes;
	}
	
	
	
	public static GifiInteractiveSound decode(byte[] bytes, int offset) throws IOException{
		
		// decode sound schedule
		SoundSchedule schedule = SoundSchedule.decode(bytes, offset);
		System.out.println("schedule.size=" + schedule.getSize());
		offset += schedule.getSize();
		
		// decode sound circles
		ArrayList<SoundCircle> soundCircles = new ArrayList<SoundCircle>();
		SoundCircle sc = null;
		while((sc=SoundCircle.decode(bytes, offset)) != null){
			soundCircles.add(sc);
			offset += sc.getSize();
		}
		
		GifiInteractiveSound iSound = new GifiInteractiveSound(schedule, soundCircles);
		iSound.size = offset;
		return iSound;
	}
	
	private SoundSchedule generateSchedule() throws Exception{
		
		if(null == schedule)
			schedule = new SoundSchedule();
		
		System.out.println("SoundSchedule::generateSchedule(): soundCircles.size = " + soundCircles.size());
		for(int scIndex=0; scIndex<soundCircles.size(); scIndex++){
			SoundCircle sc = soundCircles.get(scIndex);
			
			//for(ScheduleRecord r: sc.getScheduleRecords())
			
			for(ScheduleRecord r: sc.convertTimingSegments2schedulRecords())
				schedule.addScheduleRecord(new ScheduleRecord(r.time, r.isStart, scIndex));
		}
		
		return schedule;
	}
}
