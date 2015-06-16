package com.jongseok.gifi;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jongseok.gifi.audio.GifiInteractiveSound;
import com.jongseok.gifi.gif.Gif;
import com.jongseok.gifi.utils.BinaryUtil;


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
			
			//bos.write(gif.getFileSizeInByte());
			byte[] gifBytes = gif.toBytes();
			System.out.println("gif.getFileSizeinByte() = " + gif.getFileSizeInByte());
			//bos.write(12344);
			
			// write gif file size
			bos.write(BinaryUtil.intToBytes(gif.getFileSizeInByte()));
			
			// encode gif
			bos.write(gif.toBytes());
			
			// encode gifi interactive sound
			bos.write(iSound.encode());
						
			bytes = bos.toByteArray();
			bos.close();
		}
		
		return bytes;
	}
	
	// filename should end with ".gifi"
	public static void saveGifiAsFile(Gifi gifi, String filePath) throws IOException{
		File f = new File(filePath);
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(gifi.encode());
		fos.close();
	}
}
