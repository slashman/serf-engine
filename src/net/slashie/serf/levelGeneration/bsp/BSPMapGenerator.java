package net.slashie.serf.levelGeneration.bsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.textcomponents.TextBox;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.utils.Util;

public class BSPMapGenerator {
	private enum Split {
		SLASH (SlashSplit.class),
		//T (TSplit.class)
		;
		
		private Split(Class<? extends BSPSplit> splitterClass){
			try {
				splitter = splitterClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		private BSPSplit splitter;
		
		public BSPSplit getSplitter(){
			return splitter; 
		}
	}
	
	public static List<BSPRoom> generateBSPMap(int width, int height, int minSplitSize, int targetBlockArea){
		Stack<BSPRoom> roomStack = new Stack<BSPRoom>();
		List<BSPRoom> ret = new ArrayList<BSPRoom>();
		BSPRoom firstRoom = new BSPRoom(0,0,width, height);
		roomStack.push(firstRoom);
		while (!roomStack.isEmpty()){
			BSPRoom roomToSplit = roomStack.pop();
			if (!shouldSplitRoom(roomToSplit, targetBlockArea * 2)){
				ret.add(roomToSplit);
				continue;
			}
			BSPSplit splitter = ((Split)Util.randomElementOf(Split.values())).getSplitter();
			splitter.setMinSplitSize(minSplitSize);
			List<BSPRoom> splitRooms = splitter.splitRoom(roomToSplit);
			if (splitRooms == null){
				// Could not split room, we assume it is small enough already
				ret.add(roomToSplit);
			} else {
				for (BSPRoom furtherRoomToSplit: splitRooms){
					if (shouldSplitRoom(furtherRoomToSplit, targetBlockArea)){
						roomStack.push(furtherRoomToSplit);
					} else {
						ret.add(furtherRoomToSplit);
					}
				}
			}
		}
		return ret;
	}

	private static boolean shouldSplitRoom(BSPRoom room, int targetBlockArea) {
		int roomArea = room.getHeight() * room.getWidth();
		return roomArea > targetBlockArea;
	}
	
	public static void main(String[] args){
		List<BSPRoom> rooms = generateBSPMap(79, 24, 10, 60);
		ConsoleSystemInterface csi = new WSwingConsoleInterface("Test");
		for (BSPRoom room: rooms){
			/*for (int x = room.getXpos(); x < room.getXpos()+room.getWidth(); x++){
				csi.print(x, room.getYpos(), "-");
				csi.print(x, room.getYpos()+room.getHeight()-1, "-");
			}
			for (int y = room.getYpos(); y < room.getYpos()+room.getHeight(); y++){
				csi.print(room.getXpos(), y, "|");
				csi.print(room.getXpos()+room.getWidth()-1, y, "|");
			}*/
			TextBox tx = new TextBox(csi);
			tx.setWidth(room.getWidth());
			tx.setHeight(room.getHeight());
			tx.setBorder(true);
			tx.setPosition(room.getXpos(), room.getYpos());
			tx.setText("x");
			tx.draw();
		}
		csi.refresh();
		csi.waitKey(CharKey.SPACE);
	}
	
}
