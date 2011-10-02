package net.slashie.serf.ui.oryxUI;

import java.awt.Image;

import net.slashie.serf.ui.Appearance;

public class AnimatedGFXAppearance extends Appearance {
	private Image[] frames;

	private int superWidth, superHeight;
	private int delay;
	
	public int getSuperHeight() {
		return superHeight;
	}

	public int getSuperWidth() {
		return superWidth;
	}

	public AnimatedGFXAppearance(String ID, Image[] frames, int superWidth, int superHeight, int delay) {
		super(ID);
		this.frames = frames;
		this.superHeight = superHeight;
		this.superWidth = superWidth;
		this.delay = delay;
	}
	
	public Image getImage(int index){
		return frames[index];
	}
	
	public int getFrames(){
		return frames.length;
	}

	public int getDelay() {
		return delay;
	}
}
