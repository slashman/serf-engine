package net.slashie.serf.ui.oryxUI;

import java.awt.Image;

import net.slashie.serf.ui.Appearance;


public class GFXAppearance extends Appearance{
	private Image img;
	private Image darkImage;
	private int superWidth, superHeight;
	
	public int getSuperHeight() {
		return superHeight;
	}

	public int getSuperWidth() {
		return superWidth;
	}

	public GFXAppearance(String ID, Image pimg, int superWidth, int superHeight) {
		super(ID);
		img = pimg;
		this.superHeight = superHeight;
		this.superWidth = superWidth;
	}
	
	public GFXAppearance(String ID, Image pimg, Image darkImage, int superWidth, int superHeight) {
		super(ID);
		img = pimg;
		this.darkImage = darkImage;
		
		this.superHeight = superHeight;
		this.superWidth = superWidth;
	}
	
	public Image getImage(){
		return img;
	}

	public Image getDarkImage() {
		return darkImage;
	}

}
