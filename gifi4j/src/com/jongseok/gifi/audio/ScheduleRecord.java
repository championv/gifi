package com.jongseok.gifi.audio;


public class ScheduleRecord implements Comparable{
	public int time;
	public boolean isStart;
	public int scIndex;
	
	
	public ScheduleRecord(int time, boolean isStart, int scIndex){
		this.time = time;
		this.isStart = isStart;
		this.scIndex = scIndex;
	}
	
	public ScheduleRecord(int time, boolean isStart){
		this(time, isStart, -1);
	}
	
	@Override
	public int compareTo(Object o) {
		
		if(! (o instanceof ScheduleRecord))
			return 1;
		
		return ((ScheduleRecord) o).time - time;
	}
}