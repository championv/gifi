package com.jongseok.gifi.utils;

public class Range {
	public int from;
	public int to;

	public Range(int from, int to){
		this.from = from;
		this.to = to;
	}
	
	public boolean isOutOf(Range r){
		if(r.from<=from && r.from<=to && from<=r.to && to<=r.to)
			return false;
		
		return true;
	}
	
	public boolean conflictWith(Range r){
		if( (from<=r.from && r.from<=to) || (from<=r.to && r.to<=to))
			return true;
		
		if( (r.from<=from && from<=r.to) || (r.from<=to && to<=r.to))
			return true;
		
		return false;
	}
	
	public Range convertTo(Range currentBase, Range targetBase){
		
		return null;
	}
	
	public String toString(){
		return "from:"+from+", to:"+to;
	}
	
}
