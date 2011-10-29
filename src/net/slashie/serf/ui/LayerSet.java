package net.slashie.serf.ui;

import java.util.ArrayList;
import java.util.List;

import net.slashie.serf.ui.oryxUI.AnimatedGFXAppearance;
import net.slashie.serf.ui.oryxUI.GFXAppearance;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.serf.ui.oryxUI.TiledLayer;

public class LayerSet {
	private List<TiledLayer> layers = new ArrayList<TiledLayer>();
	 
	public void addLayer(TiledLayer layer){
		layers.add(layer);
	}
	
	public void drawAll(SwingSystemInterface si){
		int layersQ = layers.size();
		int lastCleanedLayer = -1;
		for (int i = 0; i < layers.size(); i++){
			int layerIndex = layers.get(i).getLayerIndex();
			if (lastCleanedLayer != layerIndex){
				lastCleanedLayer = layerIndex;
				si.cleanLayer(layerIndex);
			}
		}
		
		long currentMillis = System.currentTimeMillis();
		int height = layers.get(0).getHeight();
		int width = layers.get(0).getWidth();
		
		for (int y = 0; y < height; y++){
			for (int x=0; x < width; x++){
				for (int i = 0; i < layersQ; i++){
					TiledLayer layer = layers.get(i);
					int layerIndex = layer.getLayerIndex();
					Appearance[][] tiles = layer.getTiles();
					if (tiles[x][y] != null){
						if (tiles[x][y] instanceof GFXAppearance){
							GFXAppearance app = ((GFXAppearance)tiles[x][y]);
							si.drawImage(layerIndex, layer.getPosition().x+x*layer.getCellWidth()-layer.getSuperWidth() + app.getSuperWidth(),layer.getPosition().y + y*layer.getCellHeight() + layer.getSuperHeight() + app.getSuperHeight(), app.getImage());
						} else if (tiles[x][y] instanceof AnimatedGFXAppearance){
							AnimatedGFXAppearance animated = (AnimatedGFXAppearance)tiles[x][y];
							long animationLength = animated.getDelay()*animated.getFrames();
							long snap = currentMillis % animationLength;
							int frame = (int)Math.floor((double)snap / (double)animated.getDelay());
							si.drawImage(layerIndex, layer.getPosition().x+x*layer.getCellWidth()-layer.getSuperWidth() + animated.getSuperWidth(),layer.getPosition().y + y*layer.getCellHeight() + layer.getSuperHeight() + animated.getSuperHeight(), animated.getImage(frame));
						}
					}
				}
					
			}
		}
	}
}
