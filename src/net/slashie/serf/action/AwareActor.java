package net.slashie.serf.action;

import net.slashie.utils.Line;
import net.slashie.utils.Position;

public abstract class AwareActor extends Actor{
	
	public abstract int getSightRange();
	
	public int stare(Actor target) {
		if (target == null || target.isInvisible() || target.getPosition().z != getPosition().z)
			return -1;
		if (Position.flatDistance(target.getPosition(), getPosition()) <= getSightRange()){
			Position pp = level.getPlayer().getPosition();
			if (pp.x == getPosition().x){
				if (pp.y > getPosition().y){
					return Action.DOWN;
				} else {
                     return Action.UP;
				}
			} else
			if (pp.y == getPosition().y){
				if (pp.x > getPosition().x){
					return Action.RIGHT;
				} else {
					return Action.LEFT;
				}
			} else
			if (pp.x < getPosition().x){
				if (pp.y > getPosition().y)
					return Action.DOWNLEFT;
				else
					return Action.UPLEFT;
			} else {
                if (pp.y > getPosition().y)
					return Action.DOWNRIGHT;
				else
					return Action.UPRIGHT;
			}
		}
		return -1;
	}

	public boolean seesActor(Actor mainTarget) {
		if (wasSeen()){
			Line sight = new Line(getPosition(), level.getPlayer().getPosition());
			Position point = sight.next();
			while(!point.equals(level.getPlayer().getPosition())){
				if (level.getMapCell(point)!= null && level.getMapCell(point).isOpaque()){
					return false;
				}
				point = sight.next();
				if (!level.isValidCoordinate(point))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
