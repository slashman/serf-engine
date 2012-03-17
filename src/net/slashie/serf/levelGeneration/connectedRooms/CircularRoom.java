package net.slashie.serf.levelGeneration.connectedRooms;

import java.util.List;

import net.slashie.serf.action.Action;
import net.slashie.utils.Circle;
import net.slashie.utils.Position;

public class CircularRoom extends Feature {
	protected int width, height;
	private String floor, wall;
	protected Position start;
	public CircularRoom(int width, int height, String floor, String wall) {
		start = new Position(0,0);
		this.width = width;
		this.height = height;
		this.floor = floor;
		this.wall = wall;
	}
	
	public boolean drawOverCanvas(String[][] canvas, Position where, int direction, boolean [][] mask, List<Position> hotspots){
		int rndPin = 0;
		if (width > height)
			height = width;
		else
			width = height;
		if (width % 2 == 0)
			width++;
		if (height % 2 == 0)
			height++;
		int midWidth = (int)(Math.floor(width / 2.0));
		int midHeight = (int)(Math.floor(height / 2.0));
		switch (direction){
		case Action.UP:
			rndPin = midWidth;
			start.x = where.x - rndPin;
			start.y = where.y - height + 1;
			break;
		case Action.DOWN:
			rndPin = midWidth;
			start.x = where.x - rndPin;
			start.y = where.y;
			break;
		case Action.LEFT:
			rndPin = midHeight;
			start.x = where.x - width + 1;
			start.y = where.y - rndPin;
			break;
		case Action.RIGHT:
			rndPin = midHeight;
			start.x = where.x;
			start.y = where.y - rndPin;
			break;
		}
		
		//Check the mask
		
		for (int x = start.x; x < start.x + width; x++){
			for (int y = start.y; y < start.y + height; y++){
				if (!isValid(x,y,canvas) || mask[x][y]){
					return false;
				}
			}
		}
		
		hotspots.add(new Position(start.x,start.y+midHeight));
		hotspots.add(new Position(start.x+width-1,start.y+midHeight));
		hotspots.add(new Position(start.x+midWidth,start.y));
		hotspots.add(new Position(start.x+midWidth,start.y+height-1));
		Circle circle = new Circle(new Position(start.x+midWidth, start.y+midHeight), midHeight);
		
		//Carve
		List<Position> circlePoints = circle.getPoints();
		for (int i = 0; i < circlePoints.size(); i++){
			Position p = (Position)circlePoints.get(i);
			//canvas[p.x][p.y]=floor;
			mask[p.x][p.y]=true;
		}
		
		boolean startPainting = false;
		boolean insideCircle = false;
		
		for (int x = start.x+1; x < start.x + width-1; x++){
			startPainting = false;
			insideCircle = false;
			
			in: for (int y = start.y; y < start.y + height; y++){
				if (!insideCircle && mask[x][y])
					insideCircle = true;
				if (!insideCircle)
					continue in;
				if (!startPainting){
					if (!mask[x][y]){
						startPainting = true;
					} 
				} else {
					if (mask[x][y]){
						startPainting = false;
						break in;
					}
				}
				if (startPainting){
					canvas[x][y]=floor;
					mask[x][y] = true;
				}
				
			}
		}
		mask[start.x][start.y+midHeight] = false;
		mask[start.x+width-1][start.y+midHeight] = false;
		mask[start.x+midWidth][start.y] = false;
		mask[start.x+midWidth][start.y+height-1] = false;
		/*for (int i = 0; i < circlePoints.size(); i++){
			Position p = (Position)circlePoints.get(i);
			canvas[p.x][p.y]=wall;
		}*/
		
		return true;
	}
}
