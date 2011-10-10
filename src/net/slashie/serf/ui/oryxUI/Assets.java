package net.slashie.serf.ui.oryxUI;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.util.Hashtable;
import java.util.Map;

import net.slashie.serf.game.SworeGame;

public class Assets {
	private Map<String, Font> fontAssets = new Hashtable<String, Font>();
	private Map<String, Image> imageAssets = new Hashtable<String, Image>();
	private Map<String, Cursor> cursorAssets = new Hashtable<String, Cursor>();
	
	public Font getFontAsset(String assetId){
		Font ret = fontAssets.get(assetId);
		if (ret == null){
			SworeGame.crash("Font Asset not found: "+assetId);
		}
		return ret; 
	}
	
	public Image getImageAsset(String assetId){
		Image ret = imageAssets.get(assetId);
		if (ret == null){
			SworeGame.crash("Image Asset not found: "+assetId);
		}
		return ret;
	}
	
	public Cursor getCursorAsset(String assetId){
		Cursor ret = cursorAssets.get(assetId);
		if (ret == null){
			SworeGame.crash("Cursor Asset not found: "+assetId);
		}
		return ret;
	}
	
	public void addFontAsset(String assetId, Font font){
		fontAssets.put(assetId, font);
	}
	
	public void addImageAsset(String assetId, Image image){
		imageAssets.put(assetId, image);
	}
	
	public void addCursorAsset(String assetId, Cursor cursor){
		cursorAssets.put(assetId, cursor);
	}
}
