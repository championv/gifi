package com.jongseok.gifi.creator.gui;

import com.jongseok.gifi.audio.SoundCircle;

public interface SoundCircleListener {
	
	public void onGenerated(SoundCircle sc);
	public SoundCircle getSoundCircle();
}
