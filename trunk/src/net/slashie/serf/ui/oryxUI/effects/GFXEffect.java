package net.slashie.serf.ui.oryxUI.effects;

import net.slashie.serf.ui.Effect;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public abstract class GFXEffect extends Effect {
	
	public GFXEffect(String ID){
		super(ID);
	}
	
	public GFXEffect(String id, int delay){
		super(id, delay);
	}
	
	public abstract void drawEffect(GFXUserInterface ui, SwingSystemInterface si);

	public int getDrawLayer(){
		return 0;
	}
}
