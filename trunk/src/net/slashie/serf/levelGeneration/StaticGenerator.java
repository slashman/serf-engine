package net.slashie.serf.levelGeneration;

import java.util.*;

import net.slashie.serf.SworeException;
import net.slashie.serf.action.Actor;
import net.slashie.serf.action.ActorFactory;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.baseDomain.AbstractItemFactory;
import net.slashie.serf.game.SworeGame;
import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractFeature;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.BufferedLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.level.FeatureFactory;
import net.slashie.serf.level.MapCellFactory;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class StaticGenerator {
	private static StaticGenerator singleton = new StaticGenerator();
	private Hashtable<String, String> charMap;
	private Hashtable<String, String> inhabitantsMap;
	private String[][] level;
	private String[][] inhabitants;
	//private Position startPosition, endPosition;

	public void reset(){
		charMap = null;
		level = null;
		inhabitantsMap = null;
		inhabitants = null;
	}
	
	public static StaticGenerator getGenerator(){
		return singleton;
    }

	public void renderOverLevel(BufferedLevel l, String[] map, Hashtable<String,String> table, Position where) throws SworeException{
		AbstractCell [][][] cmap = l.getCells();
    	for (int y = 0; y < map.length; y++)
			for (int x = 0; x < map[0].length(); x++) {
				if (map[y].charAt(x) == ' '){
					cmap[where.z][where.x+x][where.y+y] = MapCellFactory.getMapCellFactory().getMapCell("AIR");
					continue;
				}
				String iconic = (String)table.get(map[y].charAt(x)+"");
				if (iconic == null)
					SworeGame.crash("renderOverLevel: "+map[y].charAt(x)+" not found on the level charMap", new Exception());
				String[] cmds = iconic.split(" ");
				if (!cmds[0].equals("NOTHING"))
					cmap[where.z][where.x+x][where.y+y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
				if (cmds.length > 1){
					if (cmds[1].equals("ABS_FEATURE")){
						if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))){
							AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
							vFeature.setPosition(where.x+x,where.y+y,where.z);
							l.addFeature(vFeature);
						}
					}else
					if (cmds[1].equals("ABS_ITEM")){
						AbstractItem vItem = AbstractItemFactory.createItem(cmds[2]);
						if (vItem != null)
							l.addItem(new Position(where.x+x,where.y+y,where.z), vItem);
					}else
					if (cmds[1].equals("ABS_ACTOR")){
						Actor toAdd = ActorFactory.getFactory().createActor(cmds[2]);
						toAdd.setPosition(where.x+x,where.y+y,where.z);
						l.addActor(toAdd);
					}else 
					if (cmds[1].equals("EXIT")){
						l.addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
					} else
					if (cmds[1].equals("EXIT_ABS_FEATURE")){
						l.addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
						AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[3]);
						vFeature.setPosition(where.x+x,where.y+y,where.z);
						l.addFeature(vFeature);
					} else {
						handleSpecialRenderCommand(l, where, cmds,x,y);
					}
				}
			}
	}
	
	/**
	 * Can be overriden to provide special map rendering commands
	 * @param cmds
	 */
	public void handleSpecialRenderCommand(AbstractLevel l, Position where, String[] cmds, int xoff, int yoff) {

		
	}
	
	/**
	 * Can be overriden to provide special map rendering commands
	 * @param cmds
	 */
	public void handleSpecialInhabitantCommand(String[] cmds){
		
	}
	
	public void renderOverLevel(BufferedLevel l, String[][] map, Hashtable<String,String> table, Position where) throws SworeException{
		Position runner = new Position(where);
		runner.z = 0;
		for (int i = 0; i < map.length; i++){
			renderOverLevel(l, map[i], table, runner);
			runner.z++;
		}
	}
	
	public void createLevel(BufferedLevel l) throws SworeException{
		l.setDispatcher(new Dispatcher());
	    AbstractCell [][][] cmap = new AbstractCell[level.length][level[0][0].length()][level[0].length];
	    l.setCells(cmap);
	    Position where = new Position(0,0,0);
	    for (int z=0; z < level.length; z++)
	    	for (int y = 0; y < level[0].length; y++)
				for (int x = 0; x < level[0][0].length(); x++) {
					/*if (level[z][y].charAt(x) == ' '){
						cmap[z][x][y] = MapCellFactory.getMapCellFactory().getMapCell("AIR");
						continue;
					}*/
					String iconic = (String)charMap.get(level[z][y].charAt(x)+"");
					if (iconic == null)
						SworeGame.crash("mapchar "+level[z][y].charAt(x)+" not found on the level charMap", new Exception());
					String[] cmds = iconic.split(" ");
					if (!cmds[0].equals("NOTHING"))
						cmap[z][x][y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
						
					if (cmds.length > 1){
						if (cmds[1].equals("ABS_FEATURE")){
							if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))){
								AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
								vFeature.setPosition(where.x+x,where.y+y,where.z);
								l.addFeature(vFeature);
							}
						}else
						if (cmds[1].equals("ABS_ITEM")){
							AbstractItem vItem = AbstractItemFactory.createItem(cmds[2]);
							if (vItem != null)
								l.addItem(new Position(where.x+x,where.y+y,where.z), vItem);
						}else
						if (cmds[1].equals("ABS_ACTOR")){
							Actor toAdd = ActorFactory.getFactory().createActor(cmds[2]);
							toAdd.setPosition(where.x+x,where.y+y,where.z);
							l.addActor(toAdd);
						}else 
						if (cmds[1].equals("EXIT")){
							l.addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
						} else
						if (cmds[1].equals("EXIT_ABS_FEATURE")){
							l.addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
							AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[3]);
							vFeature.setPosition(where.x+x,where.y+y,where.z);
							l.addFeature(vFeature);
						} else {
							handleSpecialRenderCommand(l, where, cmds, x, y);
						}
					}
						
				}
	    if (inhabitantsMap != null && inhabitants != null){
		    for (int z=0; z < level.length; z++)
		    	for (int y = 0; y < level[0].length; y++)
					for (int x = 0; x < level[0][0].length(); x++) {
						if (level[z][y].charAt(x) == ' ')
							continue;
						if (inhabitantsMap.get(inhabitants[z][y].charAt(x)+"") == null)
							continue;
						String[] cmds = ((String)inhabitantsMap.get(inhabitants[z][y].charAt(x)+"")).split(" ");
						if (cmds[0].equals("ABS_ACTOR")){
							Actor toAdd = ActorFactory.getFactory().createActor(cmds[2]);
							toAdd.setPosition(where.x+x,where.y+y,where.z);
							l.addActor(toAdd);
						} else {
							handleSpecialInhabitantCommand(cmds);
						}
					}
	    }
	}

	public void setCharMap(Hashtable<String,String> value) {
		charMap = value;
	}
	
	public void setInhabitantsMap(Hashtable<String,String> value) {
		inhabitantsMap = value;
	}

	public void setLevel(String[][] value) {
		level = value;
	}
	
	public void setInhabitants(String[][] value) {
		inhabitants = value;
	}

	public void setFlatLevel(String[] value){
		level = new String [1][];
		level[0] = value;
	}
}