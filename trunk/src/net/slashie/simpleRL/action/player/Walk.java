package net.slashie.simpleRL.action.player;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.ui.ActionCancelException;
import net.slashie.simpleRL.domain.entities.Adventurer;
import net.slashie.simpleRL.domain.world.LevelCell;
import net.slashie.utils.Position;

public class Walk extends Action{
	private boolean actionCancelled = false;
	
	@Override
	public boolean canPerform(Actor a) {
		Adventurer p = (Adventurer) a;
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(a.getPosition(), var);
    	Actor actor = a.getLevel().getActorAt(destinationPoint);
    	if (actor != null){
    		invalidationMessage = "You can't walk there";
    		return false;
    	}
        LevelCell cell = (LevelCell) a.getLevel().getMapCell(destinationPoint);
        
        if (cell == null){
        	invalidationMessage = "You can't walk there";
        	return false;
        }
        
        if (cell.isSolid()){
        	invalidationMessage = "You can't walk there";
        	return false;
        }
        
        if (cell.isSpike() && p.getHp() <= 1){
        	invalidationMessage = "That would be too dangerous!";
        	return false;
        }
        
		return true;
	}

	
	@Override
	public void execute() {
		actionCancelled = false;
		Adventurer p = (Adventurer) performer;
		
		
		if (targetDirection == Action.SELF){
			p.getLevel().addMessage("You stand alert.");
			return;
		}
		
        Position var = directionToVariation(targetDirection);
        Position destinationPoint = Position.add(performer.getPosition(), var);
	    try {
			p.landOn(destinationPoint);
		} catch (ActionCancelException e) {
			actionCancelled = true;
		}
	}
	

	@Override
	public String getID() {
		return "WALK";
	}
	
	
	@Override
	public int getCost() {
		if (actionCancelled){
			actionCancelled = false;
			return 0;
		}
		Adventurer p = (Adventurer) performer;
		return p.getSpeed();
	}
	
	

}
