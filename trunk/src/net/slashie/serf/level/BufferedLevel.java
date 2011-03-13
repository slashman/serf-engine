package net.slashie.serf.level;

import net.slashie.utils.Position;

public class BufferedLevel extends AbstractLevel{
	private AbstractCell[][][] map;
	private boolean[][][] visible;
	private boolean[][][] lit;
	private boolean[][][] remembered;
	
	public AbstractCell getMapCell(int x, int y, int z){
		if (z<map.length && x<map[0].length && y < map[0][0].length && x >= 0 && y >= 0 && z >= 0)
			return map[z][x][y];
		else return null;
	}
	
	public void setCells(AbstractCell[][][] what){
		map = what;
		visible= new boolean[what.length][what[0].length][what[0][0].length];
		lit= new boolean[what.length][what[0].length][what[0][0].length];
		remembered= new boolean[what.length][what[0].length][what[0][0].length];
	}
	
	public void initializeCells(int depth, int width, int height){
		map = new AbstractCell[depth][width][height];
		setCells(map);
	}
	
	@Override
	public int getWidth(){
		return map[0].length;
	}

	@Override
	public int getHeight(){
		return map[0][0].length;

	}
	
	@Override
	public int getDepth(){
		return map.length;
	}
	
	@Override
	protected void markLit(int x, int y, int z) {
		lit[z][x][y] = true;
	}

	public AbstractCell[][][] getCells(){
		return map;
	}
	
	@Override
	public void darken(int x, int y, int z){
		if (!isValidCoordinate(x,y,z))
			return;
		visible[z][x][y]= false;
	}

	@Override
	public boolean remembers(int x, int y, int z){
		if (!isValidCoordinate(x,y,z))
			return false;
		return remembered[z][x][y];
	}
	
	@Override
	public boolean isVisible(int x, int y, int z){
		if (!isValidCoordinate(x,y,z))
			return false;
		return visible[z][x][y];
	}
	
	@Override
	protected boolean isLit(Position p) {
		return lit[p.z][p.x][p.y];
	}
	
	@Override
	protected void markRemembered(int x, int y, int z) {
		remembered[z][x][y] = true;
	}
	
	@Override
	protected void markVisible(int x, int y, int z) {
		visible[z][x][y] = true;
	}	
	
	

}
