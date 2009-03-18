package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.serf.ui.Effect;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.libjcsi.ConsoleSystemInterface;

public abstract class CharEffect extends Effect{
	public CharEffect(String id){
		super(id);
	}

	public CharEffect(String id, int delay){
		super(id, delay);
	}

	public abstract void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si);
	

}
