package net.slashie.simpleRL.domain.entities;

import net.slashie.serf.action.AwareActor;

public class Monster extends AwareActor{
	private String id;
	private String description;
	private String powers;
	@Override
	public String getClassifierID() {
		return id;
	}
	
	@Override
	public int getSightRange() {
		return 4;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	public String getPowers(){
		return powers;
	}
}
