package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Color;

import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public class GFXFlashEffect extends GFXEffect{
	private Color color;

    public GFXFlashEffect(String ID, Color color){
    	super (ID);
    	this.color = color;
    }

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		si.flash(color);
		//animationPause();
	}

}