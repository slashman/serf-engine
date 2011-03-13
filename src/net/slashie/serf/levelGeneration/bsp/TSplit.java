package net.slashie.serf.levelGeneration.bsp;

import java.util.List;

import net.slashie.utils.Util;

public class TSplit extends SlashSplit{
	@Override
	public List<BSPRoom> splitRoom(BSPRoom room) {
		SlashSplit slash = new SlashSplit();
		List<BSPRoom> ret = slash.splitRoom(room);
		if (ret != null){
			if (ret.size() == 2){
				int index = Util.chance(50) ? 0 : 1;
				slash.forceHorizontal(!slash.getLastHorizontal());
				List<BSPRoom> ret2 = slash.splitRoom(ret.get(index));
				if (ret2 == null){
					return ret;
				} else {
					ret.remove(ret.get(index));
					ret.addAll(ret2);
				}
			} else {
				return ret;
			}
		} else {
			return null;
		}
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

}
