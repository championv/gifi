package com.jongseok.gifi.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GifiInteractiveSound {
	
	private ArrayList<SoundCircle> soundCircles;
	private SoundSchedule schedule;
	
	private byte[] bytes;

	public GifiInteractiveSound(ArrayList<SoundCircle> soundCircles) throws Exception{
		this.soundCircles = soundCircles;
		schedule = generateSchedule();
	}
	
	public byte[] encode() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			// encode sound schedule
			bos.write(schedule.encode());
			
			// encode sound circles
			for(SoundCircle sc: soundCircles)
				bos.write(sc.encode());
			
			// encode sound binaries
				
			bytes = bos.toByteArray();
			bos.close();
		}
		
		return bytes;
	}
	
	private SoundSchedule generateSchedule() throws Exception{
		if(null == schedule)
			schedule = new SoundSchedule();
		
		for(int scIndex=0; scIndex<soundCircles.size(); scIndex++){
			SoundCircle sc = soundCircles.get(scIndex);
			
			//for(ScheduleRecord r: sc.getScheduleRecords())
			for(ScheduleRecord r: sc.convertTimingSegments2schedulRecords())
				schedule.addScheduleRecord(new ScheduleRecord(r.time, r.isStart, scIndex));
		}
		
		return schedule;
	}
}
