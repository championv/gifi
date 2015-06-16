package com.jongseok.gifi.utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class BinaryUtil {
	
	/*public static byte[] cut(byte[] bytes, int offset, int length) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(bytes, offset, length);
		byte[] cut = bos.toByteArray();
		bos.close();
		
		return cut;
	}*/
	
	public static String toString(byte[] bytes, int offset, int length) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		System.out.println("bytes.length=" + bytes.length + " offset=" + offset + " length=" + length);
		bos.write(bytes, offset, length);
		String str = bos.toString();
		bos.close();
		
		return str;
	}
	
	/*public static int toInt(byte[] bytes, int offset) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(bytes, offset, 4);
		byte[] intInBytes = bos.toByteArray();
		bos.close();
		
		return intInBytes[0] & 0xFF | 
				(intInBytes[1] & 0xFF) << 8 | 
				(intInBytes[2] & 0xFF) << 16 | 
				(intInBytes[3] & 0xFF) << 24;
	}*/
	
	public static int toInt(byte[] bytes, int offset){
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, 4);
		return buffer.getInt();
	}
	
	public static byte[] cloneByte(byte[] bytes, int offset, int length) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(bytes, offset, length);
		byte[] clone = bos.toByteArray();
		bos.close();
		
		return clone;
		
	}
	
	public static byte[] intToBytes(int value){
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	
	
	/*public static String byteArray2String(byte[]bytes, int offset, int length){
		ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
		return buffer.get
	}*/
}
