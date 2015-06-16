package com.jongseok.gifi.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Sound {
	public AudioFormat format;
	public byte[] bytes;
	
	public String name;
	public int frameCount;
	public float frameRate;
	public int playingTime;

	public Sound(String filepath){
		readSoundFromFile(filepath);
		
		frameRate = format.getFrameRate();
		frameCount = bytes.length / format.getFrameSize();
		playingTime = (int)((float)frameCount * 1000 / frameRate);
	}
	
	public Sound(byte[] bytes){
		this.bytes = bytes;
	}
	

	public void readSoundFromFile(String filepath){
		int totalFramesRead = 0;
		File fileIn = new File(filepath);
		name = fileIn.getName();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// somePathName is a pre-existing string whose value was
		// based on a user selection.

		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
				// some audio formats may have unspecified frame size
				// in that case we may read any amount of bytes
				bytesPerFrame = 1;
			} 
			
			format = audioInputStream.getFormat();

			// Set an arbitrary buffer size of 1024 frames.
			int numBytes = 1024 * bytesPerFrame; 
			byte[] buffer = new byte[numBytes];
			
			try {
				int numBytesRead = 0;
				int numFramesRead = 0;
				// Try to read numBytes bytes from the file.
				while ((numBytesRead = audioInputStream.read(buffer)) != -1) {
					// Calculate the number of frames actually read.
					numFramesRead = numBytesRead / bytesPerFrame;
					totalFramesRead += numFramesRead;
					// Here, do something useful with the audio data that's 
					// now in the audioBytes array...
					bos.write(buffer);
				}
			} catch (Exception ex) { 
				// Handle the error...
			}
		} catch (Exception e) {
			// Handle the error...
		}
		
		bytes = bos.toByteArray();

	}
}
