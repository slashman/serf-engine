package net.slashie.simpleRL.controller;

import net.slashie.serf.game.Player;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.level.LevelMetaData;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.serf.levelGeneration.StaticPattern;
import net.slashie.simpleRL.data.patterns.AmuletRoom_Pattern;
import net.slashie.simpleRL.data.patterns.Town_Pattern;
import net.slashie.simpleRL.domain.world.Level;
import net.slashie.simpleRL.procedural.level.CaveLevelGenerator;
import net.slashie.utils.Position;

public class LevelMaster {

	public static AbstractLevel createLevel(LevelMetaData levelMetaData, Player p) {
		Level ret = null;
		String levelID = levelMetaData.getLevelID();
		if (levelID.equals("TOWN")){
			ret = new Level();
			
			StaticPattern pattern = new Town_Pattern();
			StaticGenerator generator = new StaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setMusicKey("TOWN");
			ret.setID("TOWN");
		} else if (levelID.startsWith("LEVEL_")){
			CaveLevelGenerator clg = new CaveLevelGenerator();
			clg.init("WALL", "FLOOR");
			ret = clg.generateLevel(20, 20);
			ret.setID(levelID);
			ret.setMusicKey("CAVE");
			ret.setDispatcher(new Dispatcher());
			ret.setDescription("Scary Cave");
		} else if (levelID.equals("AMULET_ROOM")){
			ret = new Level();
			
			StaticPattern pattern = new AmuletRoom_Pattern();
			StaticGenerator generator = new StaticGenerator();
			pattern.setup(generator);
			generator.createLevel(ret);
			ret.setDescription(pattern.getDescription());
			if (pattern.getUnleashers() != null){
				ret.setUnleashers(pattern.getUnleashers());
			}
			ret.setMusicKey("TITLE");
			ret.setID("AMULET_ROOM");
		}
		
		if (ret.getExitFor("_BACK") != null){
			Position pos = ret.getExitFor("_BACK");
			ret.removeExit("_BACK");
			ret.addExit(pos, levelMetaData.getExit("_BACK"));
			
		}
		
		if (ret.getExitFor("_NEXT") != null){
			Position pos = ret.getExitFor("_NEXT");
			ret.removeExit("_NEXT");
			ret.addExit(pos, levelMetaData.getExit("_NEXT"));
		}
		
		return ret;
	}

}
