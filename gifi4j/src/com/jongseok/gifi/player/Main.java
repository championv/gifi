package com.jongseok.gifi.simulator;

import com.jongseok.gifi.Gifi;
import com.jongseok.gifi.decoder.GifiDecoder;

public class GifiSimulator {
	
	public static void main(String[] args){
		Gifi gifi = null;
		try{
			gifi = GifiDecoder.decode("hello.gifi");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
