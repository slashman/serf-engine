package net.slashie.serf.level;

import java.io.Serializable;

import net.slashie.serf.game.SworeGame;

public abstract class Unleasher implements Serializable {
	protected boolean enabled = true;
	
	public boolean enabled(){
		return enabled;
	}
	
	public void disable(){
		enabled = false;
	}
	
	public abstract void unleash(AbstractLevel level, SworeGame game);
	/*This must check condition first*/
	
}
