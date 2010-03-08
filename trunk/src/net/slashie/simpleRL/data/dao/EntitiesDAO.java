package net.slashie.simpleRL.data.dao;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;
import net.slashie.serf.ui.consoleUI.CharAppearance;
import net.slashie.simpleRL.domain.item.DungeonItem;
import net.slashie.simpleRL.domain.world.LevelCell;

public class EntitiesDAO {
	
	public static AbstractCell[] getCellDefinitions (AppearanceFactory appFactory){
		return new AbstractCell[]{
			//Overworld cells
			new LevelCell("FLOOR", "Floor", false, false, false),
			new LevelCell("WALL", "Wall", true, true, false),
			new LevelCell("SPIKE", "Spike", false, false, false)
		};
	}
	
	public static CharAppearance[] getCharAppearances(){
		return new CharAppearance[]{
			//Expeditions
			new CharAppearance("ADVENTURER", '@', ConsoleSystemInterface.YELLOW),
			
			new CharAppearance("FLOOR", '.', ConsoleSystemInterface.GREEN),
			new CharAppearance("WALL", '#', ConsoleSystemInterface.GRAY),
			new CharAppearance("SPIKE", '=', ConsoleSystemInterface.RED),
			
			new CharAppearance("SWORD", '/', ConsoleSystemInterface.LIGHT_GRAY),
			
			
			
		};
	};
	
	public static DungeonItem[] getItemDefinitions(AppearanceFactory appFactory){
		return new DungeonItem[]{
			new DungeonItem("SWORD", "Sword")
		};
		
	}
	
}
