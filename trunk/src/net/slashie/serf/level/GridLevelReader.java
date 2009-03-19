package net.slashie.serf.level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import net.slashie.serf.action.Actor;
import net.slashie.serf.action.ActorFactory;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.baseDomain.AbstractItemFactory;
import net.slashie.serf.game.SworeGame;
import net.slashie.util.Pair;
import net.slashie.utils.FileUtil;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class GridLevelReader extends AbstractLevel {
	private int levelHeight, levelWidth;
	private int gridHeight, gridWidth;
	private String levelNameset;
	private Hashtable<String, String> charMap;
	
	public GridLevelReader(String levelNameset, int levelWidth, int levelHeight, int gridWidth, int gridHeight, Hashtable<String, String> charmap, Pair<String, Position> mainExit){
		this.levelNameset = levelNameset;
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.charMap = charmap;
		addExit(mainExit.getB(), mainExit.getA());
	}

	private Map<String,AbstractCell[][]> buffers = new Hashtable<String, AbstractCell[][]>();
	
	@Override
	protected void darken(int x, int y, int z) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getDepth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return levelHeight;
	}

	@Override
	public AbstractCell getMapCell(int x, int y, int z) {
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		AbstractCell[][] buffer = buffers.get(bufferId);
		if (buffer == null){
			//Read Buffer
			buffer = readLevelBuffer(levelNameset, bigX, bigY);
			buffers.put(bufferId, buffer);
		}
		return buffer[x%gridWidth][x%gridHeight];
	}
	
	private AbstractCell[][] readLevelBuffer(String levelNameset, int bigX, int bigY){
		try {
			String filename = "maps/"+levelNameset+"-"+bigX+"-"+bigY+".fragment";
			BufferedReader reader = FileUtil.getReader(filename);
			String[] map = new String[FileUtil.filasEnArchivo(filename)];
            String line = reader.readLine();
            int yr = 0;
            while (line != null){
            	map[yr]=line;
            	line = reader.readLine();
            	yr++;
            }
            reader.close();
            
            AbstractCell [][] ret = new AbstractCell[map[0].length()][map.length];
    	    Position where = new Position(0,0,0);
    	    
	    	for (int y = 0; y < map.length; y++){
				for (int x = 0; x < map[0].length(); x++) {
					String iconic = charMap.get(map[y].charAt(x)+"");
					if (iconic == null)
						SworeGame.crash("mapchar "+map[y].charAt(x)+" not found on the level charMap", new Exception());
					String[] cmds = iconic.split(" ");
					if (!cmds[0].equals("NOTHING"))
						ret[x][y] = MapCellFactory.getMapCellFactory().getMapCell(cmds[0]);
						
					if (cmds.length > 1){
						if (cmds[1].equals("ABS_FEATURE")){
							if (cmds.length < 4 || Util.chance(Integer.parseInt(cmds[3]))){
								AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[2]);
								vFeature.setPosition(where.x+x,where.y+y,where.z);
								addFeature(vFeature);
							}
						}else
						if (cmds[1].equals("ABS_ITEM")){
							AbstractItem vItem = AbstractItemFactory.createItem(cmds[2]);
							if (vItem != null)
								addItem(new Position(where.x+x,where.y+y,where.z), vItem);
						}else
						if (cmds[1].equals("ABS_ACTOR")){
							Actor toAdd = ActorFactory.createActor(cmds[2]);
							toAdd.setPosition(where.x+x,where.y+y,where.z);
							addActor(toAdd);
						}else 
						if (cmds[1].equals("EXIT")){
							addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
						} else
						if (cmds[1].equals("EXIT_ABS_FEATURE")){
							addExit(new Position(where.x+x,where.y+y,where.z), cmds[2]);
							AbstractFeature vFeature = FeatureFactory.getFactory().buildFeature(cmds[3]);
							vFeature.setPosition(where.x+x,where.y+y,where.z);
							addFeature(vFeature);
						} else {
							handleSpecialRenderCommand(where, cmds, x, y);
						}
					}
				}
			}
            return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getWidth() {
		return levelWidth;
	}

	@Override
	protected boolean isLit(Position p) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isVisible(int x, int y, int z) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void markLit(int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void markRemembered(int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void markVisible(int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean remembers(int x, int y, int z) {
		// TODO Auto-generated method stub
		return true;
	}
	
	/**
	 * Can be overriden to provide special map rendering commands
	 * @param cmds
	 */
	public void handleSpecialRenderCommand(Position where, String[] cmds, int xoff, int yoff) {

	}

}
