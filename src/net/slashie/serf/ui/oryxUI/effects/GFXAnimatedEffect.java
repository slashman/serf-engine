package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Image;

import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;


public class GFXAnimatedEffect extends GFXEffect{
	private Image[] frames;
	
	private int xoff, yoff;
	
	public GFXAnimatedEffect(String ID, Image[] frames, int delay){
		super (ID, delay);
		this.frames = frames;
	}
	
	public GFXAnimatedEffect(String ID, Image[] frames, int delay, int xoff, int yoff){
		this (ID, frames, delay);
		this.xoff = xoff;
		this.yoff = yoff;
	}

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		int height = 0;
		if (ui.getPlayer().getLevel().getMapCell(getPosition()) != null)
			height = ui.getPlayer().getLevel().getMapCell(getPosition()).getHeight();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position absolute = Position.add(ui.PC_POS, relative);
		if (!ui.insideViewPort(absolute))
			return;
		si.saveLayer(getDrawLayer());
		for (int j = 0; j<frames.length; j++){
			si.drawImage(getDrawLayer(), absolute.x*32+xoff, absolute.y*32-4*height+yoff, frames[j]);
			si.commitLayer(getDrawLayer());
			animationPause();
			si.loadLayer(getDrawLayer());
		}
	}
}
