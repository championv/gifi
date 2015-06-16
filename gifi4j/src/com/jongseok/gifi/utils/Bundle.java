package com.jongseok.gifi.utils;

import java.util.Hashtable;

public class Bundle {
	
	private Hashtable<String, Object> dataTable;
	
	public Bundle(){
		dataTable = new Hashtable<String, Object>();
	}

	public void putObject(String key, Object o){
		dataTable.put(key, o);
	}
	
	public Object getObject(String key){
		return dataTable.get(key);
	}
	/*
	public boolean containsAll(String[] keys){
		for(String key: keys){
			if(!dataTable.contains(key))
				return false;
		}
		
		return true;
	}*/
}
