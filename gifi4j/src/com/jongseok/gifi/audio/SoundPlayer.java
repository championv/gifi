package com.jongseok.gifi.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer implements LineListener{
	
	private boolean playCompleted;
	
	
	
	public void play(Sound s) throws LineUnavailableException{
		DataLine.Info info = new DataLine.Info(Clip.class, s.format);
		Clip audioClip = (Clip)AudioSystem.getLine(info);
		audioClip.addLineListener(this);
		audioClip.open(s.format, s.bytes, 0, s.bytes.length);
		audioClip.start();
		
		while (!playCompleted) {
            // wait for the playback completes
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
		
		audioClip.close();
	}
	
	public void play(String audioFilePath) throws LineUnavailableException, IOException, UnsupportedAudioFileException{
		File audioFile = new File(audioFilePath);
		
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.addLineListener(this);
        audioClip.open(audioStream);
        audioClip.start();
		
		while (!playCompleted) {
            // wait for the playback completes
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
		
		audioClip.close();
	}

	@Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
 
    }

}
