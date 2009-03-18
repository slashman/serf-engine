package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.utils.Position;


public abstract class CharDirectionalEffect extends CharEffect{
	protected int direction;
	protected int depth;
	
	public CharDirectionalEffect(String id){
		super(id);
	}

	public CharDirectionalEffect(String id, int delay){
		super(id, delay);
	}
	
	public void set(Position position,int pDirection, int pDepth){
		super.set(position);
		direction = pDirection;
		depth = pDepth;		
	}
}
