package com.jongseok.gifi.audio;

import java.util.ArrayList;

public class GifiAudio {
	
	private ArrayList<SoundCircle> soundCircles;
	private SoundSchedule schedule;
	
	private byte[] bytes;

	public GifiAudio(ArrayList<SoundCircle> soundCircles){
		this.soundCircles = soundCircles;
		schedule = generateSchedule();
	}
	
	public byte[] toByteArray(){
		if(null == bytes){
			
		}
		
		return bytes;
	}
	
	private SoundSchedule generateSchedule(){
		if(null == schedule)
			schedule = new SoundSchedule();
		
		for(int scIndex=0; scIndex<soundCircles.size(); scIndex++){
			SoundCircle sc = soundCircles.get(scIndex);
			
			for(ScheduleRecord r: sc.getScheduleRecords())
				schedule.addScheduleRecord(new ScheduleRecord(r.time, r.isStart, scIndex));
		}
		
		return schedule;
	}
}
