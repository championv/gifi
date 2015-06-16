package com.jongseok.gifi.utils;

import java.util.ArrayList;
import java.util.Hashtable;

public class FSM {
	public State context;
	private Hashtable<String, State> stateTable;
	
	private String startStateName;
	private ArrayList<String> endStateNames;
	
	
	public FSM(){
		stateTable = new Hashtable<String, State>();
		
		startStateName = null;
		endStateNames = new ArrayList<String>();
	}
	
	public void setStartState(String stateName){
		startStateName = stateName;
	}
	
	public void addEndStateName(String stateName){
		endStateNames.add(stateName);
	}
	
	public String getCurrentStateName(){
		return context.getName();
	}
	
	public void start(){
		context = stateTable.get(startStateName);
	}
	
	public void transit(String input, Bundle data) throws Exception{
		//System.out.println(stateTable);
		//System.out.println(input);
		//System.out.println(data);
		String nextStateName = context.transit(input, data);
		//System.out.println(nextStateName);
		context = stateTable.get(nextStateName);
		
		if(null == context)
			throw new Exception("Invalid input for the state, " + context.getName() + ": " + input);
		
		System.out.println("Trnasit Complete: " + context.getName());
	}
	
	public boolean isFinished(){
		//return stateTable.contains(context.getName());
		for(String endStateName: endStateNames){
			if(endStateName.equals(context.getName()))
				return true;
		}
		
		return false;
	}
	
	public boolean addState(State state){
		if(null != stateTable.get(state.getName()))
			return false;
		
		stateTable.put(state.getName(), state);
		return true;
	}
}
