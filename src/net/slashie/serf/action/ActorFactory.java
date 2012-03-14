package net.slashie.serf.action;

import java.util.Hashtable;
import java.util.Map;

import net.slashie.utils.Debug;

public class ActorFactory {
	private static ActorFactory singleton = new ActorFactory();
	private Map<String, Actor> definitions;
	
	public Actor createActor (String id) {
		Actor x = definitions.get(id);
		if (x != null)
			return (Actor) x.clone();
		Debug.byebye("Actor "+id+" not found");
		return null;
	}

	public String getDescriptionForID(String id){
		Actor x = (Actor) definitions.get(id);
		if (x != null)
			return x.getDescription();
		else
		return "?";
	}

	public void addDefinition(Actor definition){
		definitions.put(definition.getClassifierID(), definition);
	}
	
	public void init(Actor[] defs) {
		for (int i = 0; i < defs.length; i++)
			definitions.put(defs[i].getClassifierID(), defs[i]);
	}

	public ActorFactory(){
		definitions = new Hashtable<String, Actor>(40);
	}

	public static ActorFactory getFactory(){
		return singleton;
	}
}