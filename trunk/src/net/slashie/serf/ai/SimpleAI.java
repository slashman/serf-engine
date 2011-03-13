package net.slashie.serf.ai;

import java.util.Iterator;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionFactory;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.AwareActor;
import net.slashie.utils.OutParameter;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class SimpleAI extends BasicAI{
	private boolean isStationary;
	private int waitPlayerRange;
	private int approachLimit = 0;
	private int patrolRange = 0;
	private int chargeCounter = 0;
	private int lastDirection = -1;
	/**
	 * Determines the chance of aiming correctly at the newest target position,
	 * if the chance fails, the attack is aimed to the latest known target position.
	 */
	private int accuracy = 30;
	private boolean changeDirection;
	private boolean bumpEnemy;
	private Position lastKnowTargetPosition;
	private Actor mainTarget;
	private Action mainWalk;
	public SimpleAI(Actor mainTarget, Action mainWalk) {
		super();
		this.mainTarget = mainTarget;
		this.mainWalk = mainWalk;
	}
	
	public Actor getMainTarget(Actor performer){
		return mainTarget;
	}

	public Action selectAction(Actor who){
		AwareActor aware = (AwareActor) who;
		int directionToTarget = -1;
		int targetDistance = 500;
		if (getMainTarget(who) != null){
			directionToTarget = aware.stare(getMainTarget(who));
			targetDistance = Position.flatDistance(who.getPosition(), getMainTarget(who).getPosition());
			if (lastKnowTargetPosition == null){
				lastKnowTargetPosition = getMainTarget(who).getPosition();
			}
		}
		if (patrolRange >0 && targetDistance > patrolRange){
			if (lastDirection == -1 || changeDirection){
				lastDirection = Util.rand(0,7);
				changeDirection = false;
			}
			Action ret = mainWalk;
	     	ret.setDirection(lastDirection);
	     	lastKnowTargetPosition = null;
	     	return ret;
		}
		
		if (directionToTarget == -1) {
			if (isStationary || waitPlayerRange > 0) {
				lastKnowTargetPosition = null;
				return pass;
			} else {
				int direction = Util.rand(0,7);
		    	Action ret =  mainWalk;
	            ret.setDirection(direction);
	            lastKnowTargetPosition = null;
	            return ret;
			}
		} else {
			if (waitPlayerRange > 0 && targetDistance > waitPlayerRange){
				lastKnowTargetPosition = null;
				return pass;
			}
			
			
			if (targetDistance < approachLimit){
				//get away from player
				int direction = Action.toIntDirection(Position.mul(Action.directionToVariation(targetDistance), -1));
				Action ret = mainWalk;
	            ret.setDirection(direction);
	            lastKnowTargetPosition = getMainTarget(who).getPosition();
	            return ret;
				
			} else {
				//Randomly decide if will approach the player or attack
				if (aware.seesActor(getMainTarget(who)) && rangedActions != null && Util.chance(80)){
					//Try to attack the player
					inout: for (Iterator iter = rangedActions.iterator(); iter.hasNext();) {
						RangedActionSpec element = (RangedActionSpec) iter.next();
						if (element.getChargeCounter() > 0){
							if (chargeCounter == 0){
								
							}else{
								chargeCounter --;
								break inout;
							}
						}
					}
					for (Iterator iter = rangedActions.iterator(); iter.hasNext();) {
						RangedActionSpec element = (RangedActionSpec) iter.next();
						if (element.getRange() >= targetDistance && Util.chance(element.getFrequency())){
							//Perform the attack
							Action ret = ActionFactory.getActionFactory().getAction(element.getAttackId());
							if (element.getChargeCounter() > 0){
								if (chargeCounter > 0){
									continue;
								} else {
									chargeCounter = element.getChargeCounter();
								}
							}
							
							if (ret instanceof RangedAction){
								((RangedAction)ret).set(
										element.getRange(),
										element.getEffectType(),
										element.getEffectID(),
										element.getEffectWav()
										);
							}
							
							if (ret.needsDirection()){
								ret.setDirection(directionToTarget);
							} else if (ret.needsPosition()){
								if (Util.chance(accuracy)) 
									lastKnowTargetPosition = getMainTarget(who).getPosition();
								ret.setPosition(lastKnowTargetPosition);
							}
							lastKnowTargetPosition = getMainTarget(who).getPosition();
							return ret;
						}
					}
				} 
				if (bumpEnemy){
					Position destination = Position.add(who.getPosition(), Action.directionToVariation(directionToTarget));
					Actor a = who.getLevel().getActorAt(destination);
					if (a == getMainTarget(who)){
						Action ret = mainWalk;
						ret.setDirection(directionToTarget);
						lastKnowTargetPosition = getMainTarget(who).getPosition();
						return ret;
					}
				}
				
				// Couldnt attack the player, so walk to him
				if (isStationary){
					lastKnowTargetPosition = getMainTarget(who).getPosition();
					return pass;
				} else {
					Action ret = mainWalk;
					OutParameter direction1 = new OutParameter();
					OutParameter direction2 = new OutParameter();
					fillAlternateDirections(direction1, direction2, directionToTarget);
					boolean canWalkOverActors = isCanWalkOverActors();
					if (canWalkTowards(who, directionToTarget,canWalkOverActors)){
						ret.setDirection(directionToTarget);
					} else if (canWalkTowards(who, direction1.getIntValue(), canWalkOverActors)){
						ret.setDirection(direction1.getIntValue());
					} else if (canWalkTowards(who, direction2.getIntValue(), canWalkOverActors)){
						ret.setDirection(direction2.getIntValue());
					} else {
						ret.setDirection(Util.rand(0,7));
					}
					lastKnowTargetPosition = getMainTarget(who).getPosition();
		            return ret;
				}
			}
		}
	 }
	
	public boolean isCanWalkOverActors() {
		return false;
	}

	private void fillAlternateDirections(OutParameter direction1, OutParameter direction2, int generalDirection){
		Position var = Action.directionToVariation(generalDirection);
		Position d1 = null;
		Position d2 = null;
		if (var.x == 0){
			d1 = new Position(-1, var.y);
			d2 = new Position(1, var.y);
		} else if (var.y == 0){
			d1 = new Position(var.x, -1);
			d2 = new Position(var.x, 1);
		} else {
			d1 = new Position(var.x, 0);
			d2 = new Position(0, var.y);
		}
		direction1.setIntValue(Action.toIntDirection(d1));
		direction2.setIntValue(Action.toIntDirection(d2));
	}
	
	public boolean canWalkTowards(Actor aMonster, int direction){
		return canWalkTowards(aMonster, direction, false);
	}

	
	public boolean canWalkTowards(Actor aMonster, int direction, boolean canWalkOverActors){
		Position destination = Position.add(aMonster.getPosition(), Action.directionToVariation(direction));
		if (!canWalkOverActors){
			Actor a = aMonster.getLevel().getActorAt(destination);
			if (a != null)
				return false;
		}
		
		if (!aMonster.getLevel().isWalkable(destination)){
			return false;
		} else
			return true;
	}

	 public String getID(){
		 return "BASIC_MONSTER_AI";
	 }

	 public ActionSelector derive(){
 		try {
	 		return (ActionSelector) clone();
	 	} catch (CloneNotSupportedException cnse){
			return null;
	 	}
 	}

	public void setApproachLimit(int limit){
		 approachLimit = limit;
	}
	
	public void setWaitPlayerRange(int limit){
		 waitPlayerRange = limit;
	}
	
	public void setPatrolRange(int limit){
		 patrolRange = limit;
	}
	
	public int getPatrolRange(){
		return patrolRange;
	}

	public void setStationary(boolean isStationary) {
		this.isStationary = isStationary;
	}

	public void setChangeDirection(boolean value) {
		changeDirection = value;
	}
	
	public void setBumpEnemy(boolean bumpEnemy) {
		this.bumpEnemy = bumpEnemy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
	 
}