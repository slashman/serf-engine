package net.slashie.serf.ui;

import java.util.Hashtable;
import java.util.Properties;

import java.util.Map;

import net.slashie.libjcsi.CharKey;
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
	
	protected void init(UserAction[] gameActions, Action advance, Action target, Action attack, UserInterface ui, Properties keyBindings){
		this.ui = ui;
		this.advance = advance;
		this.target = target;
		this.attack = attack;
		for (int i = 0; i < gameActions.length; i++){
			this.gameActions.put(gameActions[i].getKeyCode()+"", gameActions[i]);
		}
		UP1_KEY = Integer.parseInt(keyBindings.getProperty("UP1_KEY"));
		UP2_KEY = Integer.parseInt(keyBindings.getProperty("UP2_KEY"));
		LEFT1_KEY = Integer.parseInt(keyBindings.getProperty("LEFT1_KEY"));
		LEFT2_KEY = Integer.parseInt(keyBindings.getProperty("LEFT2_KEY"));
		RIGHT1_KEY = Integer.parseInt(keyBindings.getProperty("RIGHT1_KEY"));
		RIGHT2_KEY = Integer.parseInt(keyBindings.getProperty("RIGHT2_KEY"));
		DOWN1_KEY = Integer.parseInt(keyBindings.getProperty("DOWN1_KEY"));
		DOWN2_KEY = Integer.parseInt(keyBindings.getProperty("DOWN2_KEY"));
		UPRIGHT1_KEY = Integer.parseInt(keyBindings.getProperty("UPRIGHT1_KEY"));
		UPRIGHT2_KEY  = Integer.parseInt(keyBindings.getProperty("UPRIGHT2_KEY"));
		UPLEFT1_KEY = Integer.parseInt(keyBindings.getProperty("UPLEFT1_KEY"));
		UPLEFT2_KEY = Integer.parseInt(keyBindings.getProperty("UPLEFT2_KEY"));
		DOWNLEFT1_KEY = Integer.parseInt(keyBindings.getProperty("DOWNLEFT1_KEY"));
		DOWNLEFT2_KEY = Integer.parseInt(keyBindings.getProperty("DOWNLEFT2_KEY"));
		DOWNRIGHT1_KEY = Integer.parseInt(keyBindings.getProperty("DOWNRIGHT1_KEY"));
		DOWNRIGHT2_KEY = Integer.parseInt(keyBindings.getProperty("DOWNRIGHT2_KEY"));
		SELF1_KEY = Integer.parseInt(keyBindings.getProperty("SELF1_KEY"));
		SELF2_KEY  = Integer.parseInt(keyBindings.getProperty("SELF2_KEY"));
	}
	
    public boolean isArrow(CharKey input) {
		return toIntDirection(input) != -1;
	}
    
    public static int toIntDirection(CharKey ck){
		if (isKey(ck, UP1_KEY, UP2_KEY))
			return Action.UP;
		else
		if (isKey(ck, LEFT1_KEY, LEFT2_KEY))
			return Action.LEFT;
		else
		if (isKey(ck, RIGHT1_KEY, RIGHT2_KEY))
			return Action.RIGHT;
		else
		if (isKey(ck, DOWN1_KEY, DOWN2_KEY))
			return Action.DOWN;
		else
		if (isKey(ck, UPRIGHT1_KEY, UPRIGHT2_KEY))
			return Action.UPRIGHT;
		else
		if (isKey(ck, UPLEFT1_KEY, UPLEFT2_KEY))
			return Action.UPLEFT;
		else
		if (isKey(ck, DOWNLEFT1_KEY, DOWNLEFT2_KEY))
			return Action.DOWNLEFT;
		else
		if (isKey(ck, DOWNRIGHT1_KEY, DOWNRIGHT2_KEY))
			return Action.DOWNRIGHT;
		if (isKey(ck, SELF1_KEY, SELF2_KEY))
			return Action.SELF;
		return -1;
	}
	private static boolean isKey(CharKey ck, int key1, int key2) {
		return ck.code == key1 || ck.code == key2; 
	}
	
	public static int UP1_KEY, UP2_KEY, LEFT1_KEY, LEFT2_KEY, RIGHT1_KEY, RIGHT2_KEY, DOWN1_KEY, DOWN2_KEY, UPRIGHT1_KEY, UPRIGHT2_KEY,
	UPLEFT1_KEY, UPLEFT2_KEY, DOWNLEFT1_KEY, DOWNLEFT2_KEY, DOWNRIGHT1_KEY, DOWNRIGHT2_KEY, SELF1_KEY, SELF2_KEY;
	
}
