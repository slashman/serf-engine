package net.slashie.serf.ui.oryxUI;

import java.awt.Image;

import net.slashie.serf.ui.Appearance;


public class GFXAppearance extends Appearance{
	private Image img;

	private int superWidth, superHeight;
	
	public void setSuperHeight(int superHeight) {
		this.superHeight = superHeight;
	}
	
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
	
	public Image getImage(){
		return img;
	}
}
