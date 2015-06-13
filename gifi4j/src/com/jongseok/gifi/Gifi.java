package com.jongseok.gifi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.gif.Gif;


public class Gifi {
	
	
	byte[] bytes;
	private Gif gif;
	private ArrayList<SoundCircle> soundCircles;

	public Gifi(Gif gif, ArrayList<SoundCircle> soundCircles){
		this.gif = gif;
		this.soundCircles = soundCircles;
	}
	
	public byte[] toBytes() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(gif.toBytes());
			for(SoundCircle c: soundCircles)
				bos.write(c.toBytes());
			
			bytes = bos.toByteArray();
		}
		
		return bytes;
	}
}
