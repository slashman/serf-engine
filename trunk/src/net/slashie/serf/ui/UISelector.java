package net.slashie.serf.ui;

import java.util.Hashtable;
import java.util.Map;

import net.slashie.serf.action.Action;
import net.slashie.serf.action.ActionSelector;
import net.slashie.serf.action.Actor;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.utils.*;

public abstract class UISelector implements ActionSelector  {
	protected Map<String, UserAction> gameActions = new Hashtable<String, UserAction>();
	
	protected Action advance;
	protected Action attack;
	protected Action target;
	protected Position defaultTarget;
	protected Actor player;
	protected AbstractLevel level;

	public void setPlayer (Actor p){
		player = p;
		level = player.getLevel();
	}
	
	protected Action getRelatedAction(int keyCode){
    	Debug.enterMethod(this, "getRelatedAction", keyCode+"");
    	UserAction ua = gameActions.get(keyCode+"");
    	if (ua == null){
    		Debug.exitMethod("null");
    		return null;
    	}
    	Action ret = ua.getAction();
		Debug.exitMethod(ret);
		return ret;
	}

	private transient UserInterface ui;
	
	public UserInterface getUI(){
		return ui;
	}
	
	protected void init(UserAction[] gameActions, Action advance, Action target, Action attack, UserInterface ui){
		this.ui = ui;
		this.advance = advance;
		this.target = target;
		this.attack = attack;
		for (int i = 0; i < gameActions.length; i++){
			this.gameActions.put(gameActions[i].getKeyCode()+"", gameActions[i]);
		}
	}
	
}
