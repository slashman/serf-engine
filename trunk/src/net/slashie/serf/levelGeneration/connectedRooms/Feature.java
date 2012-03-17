package net.slashie.serf.levelGeneration.connectedRooms;

import java.util.List;

import net.slashie.utils.Position;

public abstract class Feature {
	public abstract boolean drawOverCanvas(String[][] canvas, Position where, int direction, boolean [][] mask, List<Position> hotspots);
	
	protected boolean isValid(int x,int y,String[][]canvas){
		return x>=0 && x < canvas.length -1 && y >=0 && y <canvas[0].length-1;
	}
}
