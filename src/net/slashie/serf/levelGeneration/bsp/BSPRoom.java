package net.slashie.serf.levelGeneration.bsp;

public class BSPRoom {
	private int xpos,ypos,width,height;
	public BSPRoom (int xpos, int ypos, int width, int height){
		this.xpos = xpos;
		this.ypos= ypos;
		this.width = width;
		this.height = height;
	}
	public int getXpos() {
		return xpos;
	}
	
	public int getYpos() {
		return ypos;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
}
