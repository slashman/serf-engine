package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.utils.Position;
import net.slashie.utils.Line;

public abstract class CharDirectedEffect extends CharEffect {
	protected Line effectLine;
	protected int depth;
	
	public CharDirectedEffect(String id){
		super(id);
	}

	public CharDirectedEffect(String id, int delay){
		super(id, delay);
	}
	
	public void set(Position loc, Position startPosition, Position pivotPosition, int depth){
		super.set(loc);
		effectLine = new Line(startPosition, pivotPosition);
		setDepth(depth);
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int value) {
		depth = value;
	}
	

}
