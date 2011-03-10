package net.slashie.utils;

import java.util.ArrayList;
import java.util.List;

public class Circle {
	private Position center;
	private int radius;
	public Circle (Position p, int radius){
		this.center = p;
		this.radius = radius;
	}
	
	public List<Position> getPoints (){
		List<Position> ret = new ArrayList<Position>();
		int d = 3 - (2 * radius);
		Position runner = new Position(0, radius);
		Position zero = new Position(0,0);
		while (true) {
			if (Position.flatDistance(zero, runner) <= radius)
				addPoints(center, runner.x,runner.y, ret);
			if (d < 0)
				d = d + (4*runner.x)+6;
			else {
				d = d + 4 * (runner.x-runner.y) +10;
				runner.y --;
			}
			runner.x++;
			if (runner.y == 0)
				break;
		}
		return ret;
	}


	private void addPoints(Position center, int x, int y, List<Position> collection){
		collection.add(new Position(center.x + x, center.y + y));
		collection.add(new Position(center.x + x, center.y - y));
		collection.add(new Position(center.x - x, center.y + y));
		collection.add(new Position(center.x - x, center.y - y));
		collection.add(new Position(center.x + y, center.y + x));
		collection.add(new Position(center.x + y, center.y - x));
		collection.add(new Position(center.x - y, center.y + x));
		collection.add(new Position(center.x - y, center.y - x));
	}

}
