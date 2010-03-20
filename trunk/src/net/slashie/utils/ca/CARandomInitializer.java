package net.slashie.utils.ca;

import net.slashie.util.Util;

public class CARandomInitializer {
	private double [] proportions;
	private boolean useBorder;
	private int border = 1;
	
	public CARandomInitializer(double[] proportions, boolean useBorder){
		this.proportions = proportions;
		this.useBorder =useBorder;
	}
	
	public CARandomInitializer(double[] proportions, int border){
		this(proportions, true);
		this.border = border;
	}
	
	
	
	public void init(Matrix map){
		for (int x = 0; x < map.getWidth(); x++)
			for (int y = 0; y < map.getHeight(); y++)
				map.setFuture(0, x, y);

		if (useBorder){
			for (int x = 0; x < map.getWidth(); x++){
				map.setFuture(border,x,0);
				map.setFuture(border,x,map.getHeight()-1);
			}
			for (int y = 0; y < map.getHeight(); y++){
				map.setFuture(border, 0, y);
				map.setFuture(border, map.getWidth()-1, y);
			}
		}
		
		int cellCount = map.getHeight() * map.getWidth();
		for (int i = 0; i < proportions.length; i++){
			for (int j = 0; j < (int) (proportions[i] * cellCount); j++){
				int xgo = Util.rand(0, map.getWidth()-1);
				int ygo = Util.rand(0, map.getHeight()-1);
				if (map.get(xgo, ygo) == 0)
					map.setFuture(i+1, xgo, ygo);
				else
					j--;
			}
		}
		map.advance();
	}
}
