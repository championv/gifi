package com.jongseok.gifi.utils;

public abstract class State {

	private String name;
	
	public State(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract String transit(String input, Bundle data);
	
}
