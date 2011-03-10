package net.slashie.serf.game;

import java.io.Serializable;
import java.util.*;

import net.slashie.serf.action.Actor;


public class GameSessionInfo implements Serializable{
	private Player player;
	private int turns;
	private String deathLevelDescription;
	private int deathCause;
	private Actor killerActor;
	
	private List<String>history = new ArrayList<String>();
	
	public void addHistoryItem(String desc){
		history.add(desc);
	}

	public int getTurns(){
		return turns;

    }

    public void increaseTurns(){
    	turns ++;
    }

	public final static int
		KILLED = 0,
		DROWNED = 1,
		QUIT = 2,
		SMASHED = 3,
		STRANGLED_BY_ARMOR = 4,
		BURNED_BY_LAVA = 5,
		ASCENDED = 6,
		ENDLESS_PIT = 7,
		POISONED_TO_DEATH = 8;

	private Hashtable deathCount = new Hashtable();

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player value) {
		player = value;
	}

/*	public Monster getKillerMonster() {
		return killerMonster;
	}*/


	public void addDeath(Actor who){
		MonsterDeath md = (MonsterDeath) deathCount.get(who.getClassifierID());
		if (md == null)
			deathCount.put(who.getClassifierID(), new MonsterDeath(who.getDescription()));
		else {
			md.increaseDeaths();
		}
    }

	public Hashtable getDeathCount() {
		return deathCount;
	}
	
	public int getDeathCountFor(Actor who){
		MonsterDeath md = (MonsterDeath) deathCount.get(who.getClassifierID());
		if (md == null)
			return 0;
		else 
			return md.getTimes();
	}


	public int getTotalDeathCount(){
		Enumeration x = deathCount.elements();
		int acum = 0;
		while (x.hasMoreElements())
			acum+= ((MonsterDeath)x.nextElement()).getTimes();
		return acum;
	}

	public String getDeathLevelDescription() {
		return deathLevelDescription;
	}

	public void setDeathLevelDescription(String deathLevelDescription) {
		this.deathLevelDescription = deathLevelDescription;
	}
	
	public List<String> getHistory(){
		return history;
	}

	public int getDeathCause() {
		return deathCause;
	}

	public void setDeathCause(int deathCause) {
		this.deathCause = deathCause;
	}

	public Actor getKillerActor() {
		return killerActor;
	}

	public void setKillerActor(Actor killerActor) {
		this.killerActor = killerActor;
	}

}