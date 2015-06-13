package com.jongseok.gifi.audio;

public class SoundFactory {

	public static Sound readAudio(String filepath){
		return new Sound(filepath);
	}
}


