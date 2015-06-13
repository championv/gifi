package com.jongseok.gifi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;

public class Main {
	/* Test SoundPlayer
	 * public static void main(String[] args) throws IOException {
		Sound s = SoundFactory.readAudio("giggle.wav");
		
		SoundPlayer player = new SoundPlayer();
		try {
			player.play(s);
			//player.play("giggle.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	public static void main(String[] args){
		/*bytes = Files.readAllBytes(new File(filepath).toPath());
		
		ArrayList<GifFrame> frames = new ArrayList<GifFrame>();
		
		final GifImage gif = GifDecoder.read(bytes);
	    final int width = gif.getWidth();
	    final int height = gif.getHeight();
	    final int background = gif.getBackgroundColor();
	    final int frameCount = gif.getFrameCount();
	    for (int i = 0; i < frameCount; i++) {
	        final BufferedImage img = gif.getFrame(i);
	        final int delay = gif.getDelay(i);
	        frames.add(new GifFrame(img));
	        //ImageIO.write(img, "png", new File("frame_" + i + ".png"));
	    }
	    
	    return frames;*/
	}
}