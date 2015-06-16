package com.jongseok.gifi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.jongseok.gifi.audio.GifiInteractiveSound;
import com.jongseok.gifi.audio.SoundCircle;
import com.jongseok.gifi.gif.Gif;


public class Gifi {
	
	
	byte[] bytes;
	private Gif gif;
	//private ArrayList<SoundCircle> soundCircles;
	private GifiInteractiveSound iSound;

	public Gifi(Gif gif, GifiInteractiveSound iSound){
		this.gif = gif;
		//this.soundCircles = soundCircles;
		this.iSound = iSound;
	}
	
	public byte[] encode() throws IOException{
		if(null == bytes){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bos.write(gif.toBytes());
			
/*			for(SoundCircle c: soundCircles)
				bos.write(c.toBytes());*/
			
			bytes = bos.toByteArray();
		}
		
		return bytes;
	}
}
