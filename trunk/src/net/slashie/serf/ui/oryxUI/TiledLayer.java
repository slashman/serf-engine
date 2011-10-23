package net.slashie.serf.ui.oryxUI;

import net.slashie.serf.ui.Appearance;
import net.slashie.utils.Position;

public class TiledLayer {
	private Appearance[][] tiles;
	private Appearance[][] tileBuffer;

	private Position position;
	private int layerIndex;
	private int cellWidth;
	private int cellHeight;
	private int superWidth;
	private int superHeight;
	private SwingSystemInterface si;
	
	
	public TiledLayer(int width, int height, int cellWidth, int cellHeight, int superWidth, int superHeight, Position position, int layerIndex, SwingSystemInterface si) {
		tiles = new Appearance[width][height];
		tileBuffer = new Appearance[width][height];
		this.position = position;
		this.layerIndex = layerIndex;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.superWidth = superWidth;
		this.superHeight = superHeight;
		this.si = si;
	}
	
	/*public TiledLayer(int width, int height, int cellWidth, int cellHeight, Position position, int layerIndex, SwingSystemInterface si) {
		this(width, height, cellWidth, cellHeight, 0, 0, position, layerIndex, si);
	}*/
	
	public void resetBuffer(){
		for (int y = 0; y < tiles[0].length; y++){
			for (int x=0; x < tiles.length; x++){
				tileBuffer[x][y] = null;
			}
		}
	}
	
	public void setBuffer(int x, int y, Appearance appearance){
		tileBuffer[x][y] = appearance;
	}
	
	public void updateBuffer(){
		System.arraycopy(tileBuffer, 0, tiles, 0, tileBuffer.length);
	}
	
	public void draw(boolean clean){
		if (clean){
			si.cleanLayer(layerIndex);
		}
		long currentMillis = System.currentTimeMillis();
		for (int y = 0; y < tiles[0].length; y++){
			for (int x=0; x < tiles.length; x++){
				if (tiles[x][y] != null){
					if (tiles[x][y] instanceof GFXAppearance){
						si.drawImage(layerIndex, position.x+x*cellWidth-superWidth,position.y + y*cellHeight + superHeight, ((GFXAppearance)tiles[x][y]).getImage());
					} else if (tiles[x][y] instanceof AnimatedGFXAppearance){
						AnimatedGFXAppearance animated = (AnimatedGFXAppearance)tiles[x][y];
						long animationLength = animated.getDelay()*animated.getFrames();
						long snap = currentMillis % animationLength;
						int frame = (int)Math.floor((double)snap / (double)animated.getDelay());
						si.drawImage(layerIndex, position.x+ x*cellWidth -superWidth,position.y + y*cellHeight + superHeight, animated.getImage(frame));
					}
				}
					
			}
		}
	}

	public void commit() {
		si.commitLayer(layerIndex);
	}
}
