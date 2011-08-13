package net.slashie.serf.level;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.slashie.serf.action.Actor;
import net.slashie.serf.action.ActorFactory;
import net.slashie.serf.baseDomain.AbstractItem;
import net.slashie.serf.baseDomain.AbstractItemFactory;
import net.slashie.serf.game.SworeGame;
import net.slashie.utils.Position;
import net.slashie.utils.Util;

public class GridLevelReader extends AbstractLevel {
	private static final int MAX_BUFFERS = 10;
	private int levelHeight, levelWidth;
	private int gridHeight, gridWidth;

	private String levelNameset;
	private Hashtable<String, String> charMap;
	
	public GridLevelReader(String levelNameset, int levelWidth, int levelHeight, int gridWidth, int gridHeight, Hashtable<String, String> charmap, Position startPosition){
		this.levelNameset = levelNameset;
		this.levelWidth = levelWidth;
		this.levelHeight = levelHeight;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.charMap = charmap;
		setDispatcher(new Dispatcher());
		//addExit(mainExit.getB(), mainExit.getA());
		getMapCell(startPosition);
	}

	private Map<String,AbstractCell[][]> buffers = new Hashtable<String, AbstractCell[][]>();
	private Map<String,boolean[][]> visible = new Hashtable<String, boolean[][]>();
	private Map<String,boolean[][]> lit = new Hashtable<String, boolean[][]>();
	private Map<String,boolean[][]> remembered = new Hashtable<String, boolean[][]>();
	
	private List<String> bufferIds = new ArrayList<String>();
	
	@Override
	public void darken() {
		for (String bufferId: bufferIds){
			boolean[][] visibleX = visible.get(bufferId);
			for (int x = 0; x < visibleX.length; x++)
				for (int y = 0; y < visibleX[0].length; y++)
					visibleX[x][y] = false;
		}
	}
	
	@Override
	protected void darken(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = visible.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = visible.get(bufferId);
		}
		buffer[x%gridWidth][y%gridHeight] = false;		
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
		if (!isValidCoordinate(x, y))
			return null;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		AbstractCell[][] buffer = buffers.get(bufferId);
		if (buffer == null){
			//Read Buffer
			buffer = readLevelBuffer(levelNameset, bigX, bigY);
			buffers.put(bufferId, buffer);
			visible.put(bufferId, new boolean[gridWidth][gridHeight]);
			lit.put(bufferId, new boolean[gridWidth][gridHeight]);
			remembered.put(bufferId, new boolean[gridWidth][gridHeight]);
			
			bufferIds.add(bufferId);
			if (bufferIds.size() > MAX_BUFFERS){
				removeOldBuffer();
			}
		}
		return buffer[x%gridWidth][y%gridHeight];
	}
	
	private void removeOldBuffer(){
		String oldestBufferId = bufferIds.get(0);
		bufferIds.remove(0);
		buffers.remove(oldestBufferId);
		visible.remove(oldestBufferId);
		lit.remove(oldestBufferId);
		remembered.remove(oldestBufferId);
	}
	
	private AbstractCell[][] readLevelBuffer(String levelNameset, int bigX, int bigY){
		try {
			int charMultiplier = 1;
			RandomAccessFile raf = new RandomAccessFile(levelNameset+".txt", "r");
			String[] map = new String[gridHeight];
			//raf.seek(2*bigY*(levelWidth+1) + 2*bigX*gridWidth*gridHeight);
			long gridLength = gridWidth * gridHeight;
			int bigcellsWidth = (int)Math.ceil((double)levelWidth/(double)gridWidth);
			raf.seek(charMultiplier * (bigY*gridLength*bigcellsWidth+ bigX*gridLength));
			byte[] buffer = new byte[gridWidth*charMultiplier];
			int bytes = raf.read(buffer);
            
			int yr = 0;
            while (bytes > -1 && yr < gridHeight){
            	map[yr]= new String(buffer);
            	bytes = raf.read(buffer);
            	yr++;
            }
            
            raf.close();
            
            AbstractCell [][] ret = new AbstractCell[map[0].length()][map.length];
    	    Position where = new Position(bigX*gridWidth,bigY*gridHeight,0);
    	    
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

	private void readBuffer(int bigX, int bigY){
		String bufferId = bigX+"-"+bigY;
		//Read Buffer
		AbstractCell[][] buffer = readLevelBuffer(levelNameset, bigX, bigY);
		buffers.put(bufferId, buffer);
		visible.put(bufferId, new boolean[gridWidth][gridHeight]);
		lit.put(bufferId, new boolean[gridWidth][gridHeight]);
		remembered.put(bufferId, new boolean[gridWidth][gridHeight]);
		
		bufferIds.add(bufferId);
		if (bufferIds.size() > MAX_BUFFERS){
			removeOldBuffer();
		}
	}
	@Override
	protected boolean isLit(Position p) {
		int x = p.x;
		int y = p.y;
		if (!isValidCoordinate(p.x, p.y))
			return false;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = lit.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = lit.get(bufferId);
		}
		return buffer[x%gridWidth][y%gridHeight];
	}

	@Override
	public boolean isVisible(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return false;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = visible.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = visible.get(bufferId);
		}
		return buffer[x%gridWidth][y%gridHeight];
	}

	@Override
	protected void markLit(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = lit.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = lit.get(bufferId);
		}
		buffer[x%gridWidth][y%gridHeight] = true;
	}

	@Override
	protected void markRemembered(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = remembered.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = remembered.get(bufferId);
		}
		buffer[x%gridWidth][y%gridHeight] = true;
	}

	@Override
	protected void markVisible(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = visible.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = visible.get(bufferId);
		}
		buffer[x%gridWidth][y%gridHeight] = true;		
	}

	@Override
	protected boolean remembers(int x, int y, int z) {
		if (!isValidCoordinate(x, y, z))
			return false;
		
		//Calculate the bigX and bigY for the requestedCell
		int bigX = (int)Math.floor((double)x/(double)gridWidth);
		int bigY = (int)Math.floor((double)y/(double)gridHeight);
		String bufferId = bigX+"-"+bigY;
		boolean[][] buffer = remembered.get(bufferId);
		if (buffer == null){
			readBuffer(bigX, bigY);
			buffer = remembered.get(bufferId);
		}
		return buffer[x%gridWidth][y%gridHeight];
	}
	
	/**
	 * Can be overriden to provide special map rendering commands
	 * @param cmds
	 */
	public void handleSpecialRenderCommand(Position where, String[] cmds, int xoff, int yoff) {

	}

}
