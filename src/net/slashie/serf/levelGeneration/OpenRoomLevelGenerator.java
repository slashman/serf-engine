package net.slashie.serf.levelGeneration;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.level.AbstractLevel;
import net.slashie.serf.level.BufferedLevel;
import net.slashie.serf.level.Dispatcher;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public abstract class OpenRoomLevelGenerator extends StaticGenerator {
	private String [][] preLevel;
	private String baseWall, baseFloor, baseDoor;
	
	public void init(String pWall, String pFloor, String pDoor){
		baseWall = pWall;
		baseFloor = pFloor;
		baseDoor = pDoor;
	}
	
	public AbstractLevel generateLevel(BufferedLevel ret, int xdim, int ydim, int buildings, int wallChance){
		preLevel = new String[xdim][ydim];
		for(int x = 0; x < xdim; x++)
			for (int y = 0; y < ydim; y++)
				preLevel[x][y] = baseFloor;
		for(int x = 0; x < xdim; x++){
			preLevel[x][0] = baseWall;
			preLevel[x][ydim-1] = baseWall;
		}
		for (int y = 0; y < ydim; y++){
			preLevel[0][y] = baseWall;
			preLevel[xdim-1][y] = baseWall;
		}
		buildings += Util.rand(0,(int)(buildings*0.1));
		for (int i = 0; i < buildings; i++){
			placeBuilding(wallChance);
		}
		AbstractCell[][][] cells = new AbstractCell[1][xdim][ydim];
		ret.setCells(cells);
		
		String[] levelMap = new String[preLevel[0].length];
		for (int y = 0; y < ydim; y++){
			for (int x = 0; x < xdim; x++){
				if (levelMap[y] == null) levelMap[y] = preLevel[x][y]; else levelMap[y] += preLevel[x][y];  
			}
		}
		renderOverLevel(ret, levelMap, new Position(0,0));

		
		//Place the entrance
		/*
		int yEntrance = Util.rand(5, getHeight() - 5);
		int yExit = Util.rand(5, getHeight() - 5);
		placeEntrance(ret, new Position(0,yEntrance));
		placeExit(ret, new Position(getWidth()-1,yExit));*/
		
		Position entrance = new Position(0,0);
		Position exit = new Position(0,0);
		while (true){
			entrance.x = Util.rand(1,xdim-2);
			entrance.y = Util.rand(1,ydim-2);
			if (ret.isExitPlaceable(entrance)){
				placeEntrance(ret, entrance);
				break;
			}
		}
		
		while (true){
			exit.x = Util.rand(1,xdim-2);
			exit.y = Util.rand(1,ydim-2);
			if (ret.isExitPlaceable(exit)){
				placeExit(ret, exit);
				break;
			}
		}
		
		ret.setDispatcher(new Dispatcher());
		return ret;
	}
	
	public abstract void placeExit(BufferedLevel ret, Position position);

	public abstract void placeEntrance(BufferedLevel ret, Position position);

	private int getWidth(){
		return preLevel.length;
	}
	
	private int getHeight(){
		return preLevel[0].length;
	}
	private void placeBuilding(int wallChance){
		int giveUp = 0;
		int xpos = 0;
		int ypos = 0;
		int width = 0;
		int height = 0;
		
		do {
			xpos = Util.rand(5, getWidth()-20);
			ypos = Util.rand(1, getHeight()-16);
			width = Util.rand(4,15);
			height = Util.rand(4,15);
			giveUp++;
		} while (hasConflicts(xpos, ypos, width, height) && giveUp < 100);
		if (giveUp == 100){
			return;
		}
		for(int x = xpos; x < xpos + width; x++){
			if (Util.chance(wallChance))
				preLevel[x][ypos] = baseWall;
			if (Util.chance(wallChance))
				preLevel[x][ypos+height-1] = baseWall;
			
		}
		for (int y = ypos; y < ypos + height; y++){
			if (Util.chance(wallChance))
				preLevel[xpos][y] = baseWall;
			if (Util.chance(wallChance))
				preLevel[xpos+width-1][y] = baseWall;
		}
		if (Util.chance(50))
			if (Util.chance(50))
				preLevel[xpos][Util.rand(ypos+1, ypos+height-1)] = baseDoor;
			else
				preLevel[xpos+width-1][Util.rand(ypos+1, ypos+height-1)] = baseDoor;
		else
			if (Util.chance(50))
				preLevel[Util.rand(xpos+1, xpos+width-1)][ypos] = baseDoor;
			else
				preLevel[Util.rand(xpos+1, xpos+width-1)][ypos+height-1] = baseDoor;
	}

	private boolean hasConflicts(int xpos, int ypos, int width, int height){
		for(int x = xpos-1; x < xpos + width+1; x++){
			for (int y = ypos-1; y < ypos + height+1; y++){
				if (preLevel[x][y].equals(baseWall) ||
					preLevel[x][y].equals(baseDoor))
					return true;
			}
		}
		return false;
	}
	
}
