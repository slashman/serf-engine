package net.slashie.serf.ui.oryxUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;

import javax.swing.JPanel;

public class AddornedBorderPanel extends JPanel {
	private Image UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT;
	private Color OUT_COLOR, IN_COLOR, INSIDE_COLOR;
	private int borderWidth;
	private int insideBound, outsideBound, inBound;
	
	public AddornedBorderPanel(Image UPRIGHT, 
			Image UPLEFT, Image DOWNRIGHT, Image DOWNLEFT,
			Color OUT_COLOR, Color IN_COLOR, Color INSIDE_COLOR,
			int borderWidth, int outsideBound, int inBound, int insideBound) {
		this.UPRIGHT = UPRIGHT; 
		this.UPLEFT = UPLEFT;  
		this.DOWNRIGHT = DOWNRIGHT;
		this.DOWNLEFT = DOWNLEFT;
		this.OUT_COLOR = OUT_COLOR; 
		this.IN_COLOR = IN_COLOR;
		this.INSIDE_COLOR = INSIDE_COLOR;
		this.borderWidth = borderWidth;
		this.insideBound = insideBound;
		this.outsideBound = outsideBound;
		this.inBound = inBound;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.setColor(OUT_COLOR);
		g.fillRoundRect(outsideBound,outsideBound,getWidth()-outsideBound*2,getHeight()-outsideBound*2, 1, 1);
		g.setColor(IN_COLOR);
		g.fillRect(inBound,inBound,getWidth()-inBound*2,getHeight()-inBound*2);
		g.setColor(INSIDE_COLOR);
		g.fillRect(insideBound,insideBound,getWidth()-insideBound*2,getHeight()-insideBound*2);
		g.drawImage(UPLEFT, 0,0, this);
		g.drawImage(UPRIGHT, getWidth()-borderWidth,0, this);
		g.drawImage(DOWNLEFT, 0, getHeight()-borderWidth,this);
		g.drawImage(DOWNRIGHT, getWidth()-borderWidth, getHeight()-borderWidth,this);
	}
	
	public void paintAt(Graphics g, int x, int y){
		g.setColor(INSIDE_COLOR);
		g.fillRect(outsideBound,outsideBound,getWidth()-outsideBound*2,getHeight()-outsideBound*2);
		g.setColor(OUT_COLOR);
		g.drawRect(outsideBound,outsideBound,getWidth()-outsideBound*2,getHeight()-outsideBound*2);
		g.setColor(IN_COLOR);
		g.drawRect(insideBound,insideBound,getWidth()-insideBound*2,getHeight()-insideBound*2);
		g.drawImage(UPLEFT, x,y, this);
		g.drawImage(UPRIGHT, x+getWidth()-borderWidth,y, this);
		g.drawImage(DOWNLEFT, x, y+getHeight()-borderWidth,this);
		g.drawImage(DOWNRIGHT, x+getWidth()-borderWidth, y+getHeight()-borderWidth,this);
	}

}
