package com.jongseok.gifi.decoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.jongseok.gifi.Gifi;
import com.jongseok.gifi.audio.GifiInteractiveSound;
import com.jongseok.gifi.gif.Gif;
import com.jongseok.gifi.utils.BinaryUtil;

public class GifiDecoder {
	public static final int BUF_SIZE = 1024;
	
	public static Gifi decode(String filePath) throws IOException{
		File f = new File(filePath);
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		byte[] buf = new byte[BUF_SIZE];
		while((fis.read(buf)) == BUF_SIZE)
			bos.write(buf);
		
		byte[] gifiBytes = bos.toByteArray();
		
		bos.close();
		fis.close();
		
		System.out.println("gifiBytes.length = " + gifiBytes.length);
		
		// decode gif size
		int offset = 0;
		int length = 4;	// read a int
		int gifsize = BinaryUtil.toInt(gifiBytes, offset);
		System.out.println("gif size=" + gifsize);
		offset += length; 
		
		// decode gif
		length = gifsize;
		Gif gif = new Gif(gifiBytes, offset, length);
		offset += length;
		
		// decode gifi interactive sound
		GifiInteractiveSound iSound = GifiInteractiveSound.decode(gifiBytes, offset);
		offset += iSound.getSize();
		
		return new Gifi(gif, iSound);
	}
	
	public static void main(String[] args) throws IOException{
		/*Gifi gifi = null;
		try{
			gifi = GifiDecoder.decode("hello.gifi");
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		byte[] buf;// = new byte[10];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(BinaryUtil.intToBytes(111223));
		buf = bos.toByteArray();
		
		System.out.println("size of buf = " + buf.length);
		System.out.println("decode: " + BinaryUtil.toInt(buf, 0));
		
	}
}
