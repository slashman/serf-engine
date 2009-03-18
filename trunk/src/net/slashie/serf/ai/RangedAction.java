package net.slashie.serf.ai;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.Actor;
import net.slashie.serf.sound.SFXManager;
import net.slashie.serf.ui.EffectFactory;
import net.slashie.utils.Line;
import net.slashie.utils.Position;

public abstract class RangedAction extends Action{
	private int range;
	private String effectType;
	private String effectID;
	private String effectWav;

	
	public String getEffectWav(){
		return effectWav;
	}
	
	public void set(int pRange, String pEffectType, String pEffectID, String pEffectWav){
		range = pRange;
		effectType = pEffectType;
		effectID = pEffectID;
		effectWav =  pEffectWav;
	}

	
	public String getID(){
		return "RANGED_ACTIOn";
	}
	
	public boolean needsPosition(){
		return true;
	}

	public void execute(){
        Actor target = performer.getLevel().getActorAt(targetPosition);
        
        Line line = new Line(performer.getPosition(), targetPosition);
        for (int i=0; i<range; i++){
			Position destinationPoint = line.next();
			Actor target_ = performer.getLevel().getActorAt(destinationPoint);
			if (target_ != null){
				if (preEffectCheck(target_)){
					int targetDirection = solveDirection(performer.getPosition(), targetPosition);
			        if (effectWav != null){
			        	SFXManager.play(effectWav);
			        }
			        if (effectType.equals("beam")){
			        	drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, effectID,range));        
			        }else if (effectType.equals("melee")) {
			        	drawEffect(EffectFactory.getSingleton().createDirectionalEffect(performer.getPosition(), targetDirection, range, effectID));
			        }else if (effectType.equals("missile")){
			        	drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, effectID, range));
			        }else if (effectType.equals("directionalmissile")){
			        	drawEffect(EffectFactory.getSingleton().createDirectedEffect(performer.getPosition(), targetPosition, effectID , range));
			        }
					actOverTarget(target_);
				}
				break;
			}
		}
	}
	
	public abstract boolean actOverTarget(Actor target);
	public abstract boolean preEffectCheck(Actor target);
	private int solveDirection(Position old, Position newP){
		if (newP.x() == old.x()){
			if (newP.y() > old.y()){
				return Action.DOWN;
			} else {
                 return Action.UP;
			}
		} else
		if (newP.y() == old.y()){
			if (newP.x() > old.x()){
				return Action.RIGHT;
			} else {
				return Action.LEFT;
			}
		} else
		if (newP.x() < old.x()){
			if (newP.y() > old.y())
				return Action.DOWNLEFT;
			else
				return Action.UPLEFT;
		} else {
            if (newP.y() > old.y())
				return Action.DOWNRIGHT;
			else
				return Action.UPRIGHT;
		}
	}



}