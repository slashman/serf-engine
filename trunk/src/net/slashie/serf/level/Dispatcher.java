package net.slashie.serf.level;

import java.util.*;

import net.slashie.serf.action.Actor;
import net.slashie.utils.SZPriorityQueue;


public class Dispatcher implements java.io.Serializable{
	private SZPriorityQueue<Actor> actors;
	private int countdown;
	private Actor fixed;

	public Dispatcher(){
		actors = new SZPriorityQueue<Actor>();
	}

	public boolean contains (Actor what){
		return actors.contains(what);
	}

	public List<Actor> getActors(){
		return actors.getVector();
    }

	public static int ixx = 0;
	public Actor getNextActor(){
		//Debug.say("---"+(ixx++)+"--------");
		if (countdown > 0){
			countdown--;
			return fixed;
		}
		
		//actors.printStatus();
		Actor x = (Actor) actors.unqueue();
		//Debug.say(x);

		while (x != null && x.wannaDie()){
			actors.remove(x);
			x  = (Actor) actors.unqueue();
		}
		//actors.enqueue(x);
		return x;
    }
	
	public void returnActor(Actor what){
		if (!actors.contains(what))
			actors.enqueue(what);
	}

    public void addActor(Actor what){
    	if (!actors.contains(what))
    		actors.enqueue(what);
	}

	public void addActor(Actor what, boolean high, Object classObj){
		if (high){
			if (!actors.contains(what))
				actors.forceToFront(what, classObj);
		} else {
			addActor(what);

		}
	}

	public void addActor(Actor what, boolean high){
		if (high){
			if (!actors.contains(what))
				actors.forceToFront(what);
		} else
			addActor(what);
	}

	public void removeActor(Actor what){
		actors.remove(what);
	}

	public void setFixed(Actor who, int howMuch){
		countdown = howMuch;
		fixed = who;
	}
	
	public void removeAll(){
		actors.removeAll();
	}
}