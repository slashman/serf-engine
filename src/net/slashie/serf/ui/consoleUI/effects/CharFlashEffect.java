package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;

public class CharFlashEffect extends CharEffect{
	private int color;

    public CharFlashEffect(String ID, int color){
    	super (ID);
    	this.color = color;
    }

	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		si.flash(color);
		//animationPause();
	}

}