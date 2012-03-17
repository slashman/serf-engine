package net.slashie.serf.levelGeneration.cave;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.BufferedLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.serf.levelGeneration.StaticGenerator;
import net.slashie.utils.Position;
import net.slashie.utils.Util;
import net.slashie.utils.ca.CARandomInitializer;
import net.slashie.utils.ca.CARule;
import net.slashie.utils.ca.Matrix;
import net.slashie.utils.ca.SZCA;

public abstract class AbstractCaveGenerator extends StaticGenerator{ 	
	public abstract CARule[] getRules();
	public abstract CARandomInitializer getRandomInitializer();
	public abstract String getFloorChar();
	public abstract String getWallChar();
	public abstract int getEvolutions();

	public AbstractLevel generateLevel(BufferedLevel ret, int xdim, int ydim) {
		CARandomInitializer vInit = getRandomInitializer();
		CARule [] vRules = getRules();
	
		int[][] intMap = null;
		int yEntrance = 0;
		int yExit = 0;
		
		Matrix map = new Matrix(xdim,ydim);
		vInit.init(map);
		SZCA.runCA(map, vRules, getEvolutions(), false);
		intMap = map.getArrays();
		//Carve the entrance
		yEntrance = Util.rand(5, intMap[0].length-5);
		intMap[0][yEntrance] = 0;
		for (int i = 1; i < 9; i++){
			intMap[i][yEntrance-1] = 0;
			intMap[i][yEntrance] = 0;
			intMap[i+1][yEntrance] = 0;
			intMap[i][yEntrance+1] = 0;
		}
		//Carve the exit
		yExit = Util.rand(5, intMap[0].length-5);
		intMap[intMap.length-1][yExit] = 0;
		for (int i = 1; i < 9; i++){
			intMap[intMap.length-1-i][yExit-1] = 0;
			intMap[intMap.length-1-i][yExit] = 0;
			intMap[intMap.length-2-i][yExit] = 0;
			intMap[intMap.length-1-i][yExit+1] = 0;
		}
		
		Position start = new Position(0,yEntrance);
		Position end = new Position(intMap.length-1,yExit);
		
		//Run the wisps
		WispSim.setWisps(new Wisp(start, 10,20,5),new Wisp(end, 10,20,5));
		WispSim.run(intMap);
		
		//Put the keys
		Position key1 = null;
		Position key2 = null;
		while (key1 == null){
			int xpos = Util.rand(0,intMap.length-1);
			int ypos = Util.rand(0,intMap[0].length-1);
			if (intMap[xpos][ypos] == 0)
				key1 = new Position(xpos, ypos);
		}
		while (key2 == null){
			int xpos = Util.rand(0,intMap.length-1);
			int ypos = Util.rand(0,intMap[0].length-1);
			if (intMap[xpos][ypos] == 0)
				key2 = new Position(xpos, ypos);
		}
		
		//Run the wisps for the keys
		WispSim.setWisps(new Wisp(key1, 40,30,3),new Wisp(key2, 20,30,3));
		WispSim.run(intMap);
		
		String[] tiles = new String[ydim];
		for (int y = 0; y < ydim; y++)
			tiles[y] = "";
		for (int y = 0; y < ydim; y++)
			for (int x = 0; x < xdim; x++)
				if (intMap[x][y] == 0)
					tiles[y] += getFloorChar();
				else if (intMap[x][y] == 1)
					tiles[y] += getWallChar();
				else
					tiles[y] += getFloorChar();
					
		ret.setCells(new AbstractCell[1][xdim][ydim]);
		renderOverLevel(ret, tiles, new Position(0,0));
		addStart(ret, start);
		addEnd(ret, end);
		ret.setDispatcher(new Dispatcher());
		return ret;
	}
	
	public abstract void addEnd(BufferedLevel ret, Position end);
	
	public abstract void addStart(BufferedLevel ret, Position start);

	

	
}