package com.jongseok.gifi.gif;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GifFactory {
	
	public static Gif readGif(String filepath){
		try {
			return new Gif(filepath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
