package net.slashie.serf.action;

import java.util.Hashtable;

import net.slashie.utils.Debug;

public class ActionFactory {
	private final static ActionFactory singleton = new ActionFactory();
	private Hashtable<String, Action> definitions = new Hashtable<String, Action>(20);

	public static ActionFactory getActionFactory(){
		return singleton;
    }

	public Action getAction (String id){
		Action ret = (Action) definitions.get(id);
		Debug.doAssert(ret != null, "Tried to get an invalid "+id+" Action");
		return ret;
	}

	public void addDefinition(Action definition){
		definitions.put(definition.getID(), definition);
	}
}
