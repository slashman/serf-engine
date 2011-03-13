package net.slashie.serf.ai;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;

public class EnemyAI extends SimpleAI{
	public EnemyAI(Action mainWalk) {
		super(null, mainWalk);
	}
	
	@Override
	public Actor getMainTarget(Actor who) {
		return who.getLevel().getPlayer();
	}

}
