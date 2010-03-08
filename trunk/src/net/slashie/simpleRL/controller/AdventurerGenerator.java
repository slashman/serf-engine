package net.slashie.simpleRL.controller;

import net.slashie.simpleRL.domain.entities.Adventurer;

public class AdventurerGenerator {
	
	public static Adventurer getAdventurerObject(String name){
		Adventurer ret = new Adventurer();
		ret.setName(name);
		return ret;
	}
}
