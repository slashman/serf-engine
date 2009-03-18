package net.slashie.serf.ai;

import java.util.ArrayList;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;

public abstract class BasicAI  implements ActionSelector, Cloneable{
	protected ArrayList<RangedActionSpec> rangedActions;
	
	public void setRangedActions(ArrayList<RangedActionSpec> pRangedActions){
		rangedActions = pRangedActions;
    }
	
	public abstract Action selectAction(Actor who);
	public abstract String getID();

	public ActionSelector derive(){
		try {
			return (ActionSelector) clone();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
