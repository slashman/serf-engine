package net.slashie.simpleRL.controller;

import java.util.Hashtable;

import net.slashie.simpleRL.domain.item.DungeonItem;

public class ItemFactory {
	private static Hashtable<String, DungeonItem> definitions = new Hashtable<String, DungeonItem>();
	public static void init(DungeonItem[] definitions_){
		for (int i = 0; i < definitions_.length; i++)
			definitions.put(definitions_[i].getFullID(), definitions_[i]);
	}
	
	public static DungeonItem createItem(String itemId){
		DungeonItem ret = definitions.get(itemId);
		if (ret == null){
			//ExpeditionGame.crash("Item "+itemId+" not found");
			/*
			return null;*/
		}
		return (DungeonItem) ret.clone();
	}


}
