package net.slashie.serf.ui.oryxUI;

import net.slashie.utils.Position;

public class TiledLayer {
	private GFXAppearance[][] tiles;
	private GFXAppearance[][] tileBuffer;

	private Position position;
	private int layerIndex;
	private int cellWidth;
	private int cellHeight;
	private SwingSystemInterface si;
	
	public TiledLayer(int width, int height, int cellWidth, int cellHeight, Position position, int layerIndex, SwingSystemInterface si) {
		tiles = new GFXAppearance[width][height];
		tileBuffer = new GFXAppearance[width][height];
		this.position = position;
		this.layerIndex = layerIndex;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.si = si;
	}
	
	public void resetBuffer(){
		for (int y = 0; y < tiles[0].length; y++){
			for (int x=0; x < tiles.length; x++){
				tileBuffer[x][y] = null;
			}
		}
	}
	
	public void setBuffer(int x, int y, GFXAppearance appearance){
		tileBuffer[x][y] = appearance;
	}
	
	public void updateBuffer(){
		System.arraycopy(tileBuffer, 0, tiles, 0, tileBuffer.length);
	}
	
	public void draw(){
		si.cleanLayer(layerIndex);
		for (int y = 0; y < tiles[0].length; y++){
			for (int x=0; x < tiles.length; x++){
				if (tiles[x][y] != null)
					si.drawImage(layerIndex, position.x+x*cellWidth,position.y + y*cellHeight, tiles[x][y].getImage());
			}
		}
	}
}
