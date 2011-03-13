package net.slashie.serf.levelGeneration.bsp;

import java.util.ArrayList;
import java.util.List;

import net.slashie.utils.Util;

public class SlashSplit implements BSPSplit{
	protected int minHeightWidth = 5;
	private boolean lastHorizontal;
	private boolean isForcedHorizontal;
	private boolean forcedHorizontal;
	@Override
	public List<BSPRoom> splitRoom(BSPRoom room) {
		List<BSPRoom> ret = new ArrayList<BSPRoom>();
		boolean preferVertical = false;
		boolean preferHorizontal = false;
		if (room.getHeight() <= minHeightWidth){
			preferVertical = true;
		}
		if (room.getWidth() <= minHeightWidth){
			preferHorizontal = true;
		}
		if (preferHorizontal && preferVertical){
			return null;
		}
		
		boolean horizontal = Util.chance(50);
		if (preferHorizontal){
			horizontal = true;
		} else if (preferVertical){
			horizontal = false;
		}
		
		if (isForcedHorizontal){
			horizontal = forcedHorizontal;
			isForcedHorizontal = false;
		}
		

		lastHorizontal = horizontal;
		
		if (horizontal){
			int splitRange = room.getHeight()-minHeightWidth;
			if (splitRange <= minHeightWidth){
				return null;
			}
			int splitHeight = Util.rand(minHeightWidth, splitRange);
			BSPRoom room1 = new BSPRoom(room.getXpos(), room.getYpos(), room.getWidth(),splitHeight);
			BSPRoom room2 = new BSPRoom(room.getXpos(), room.getYpos()+splitHeight, room.getWidth(),room.getHeight()-splitHeight);
			ret.add(room1);
			ret.add(room2);
			return ret;
		} else { // Vertical
			int splitRange = room.getWidth()-minHeightWidth;
			if (splitRange <= minHeightWidth){
				return null;
			}
			int splitWidth = Util.rand(minHeightWidth, splitRange);
			BSPRoom room1 = new BSPRoom(room.getXpos(), room.getYpos(), splitWidth, room.getHeight());
			BSPRoom room2 = new BSPRoom(room.getXpos()+splitWidth, room.getYpos(), room.getWidth()-splitWidth,room.getHeight());
			ret.add(room1);
			ret.add(room2);
			return ret;
		}
	}
	public boolean getLastHorizontal() {
		return lastHorizontal;
	}
	public void forceHorizontal(boolean b) {
		isForcedHorizontal = true;
		forcedHorizontal = b;
	}
	@Override
	public void setTargetBlockArea(int maxBlockArea) {
		minHeightWidth = (int)Math.round(Math.sqrt(maxBlockArea));
	}

}
