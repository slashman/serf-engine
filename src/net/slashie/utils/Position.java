package net.slashie.utils;

@SuppressWarnings("serial")
public class Position implements java.io.Serializable {
	public int x,y,z;

	public int x(){
		return x;
	}
	
	public int y(){
		return y;
	}
	
	public int z(){
		return z;
	}
	
	
	public Position(int px, int py){
		x = px;
		y = py;
	}

	public Position(int px, int py, int pz){
		this(px, py);
		z = pz;
	}

	public Position(Position p){
		x = p.x;
		y = p.y;
		z = p.z;
	}

	public static Position add (Position a, Position b){
		return new Position (a.x + b.x, a.y + b.y, a.z + b.z);
	}

	public static Position subs (Position a, Position b){
		return new Position (a.x - b.x, a.y - b.y, a.z - b.z);
	}

	public static Position mul(Position a, int c){
		return new Position (a.x * c, a.y * c, a.z * c);
	}

	public static Position mul(Position a, Position b){
		return new Position (a.x * b.x, a.y * b.y, a.z * b.z);
	}

	public void mul(Position pos){
		x *= pos.x;
		y *= pos.y;
		z *= pos.z;
	}

	public boolean equals(Object o){
		if (o == null)
			return false;
		try {
			if (((Position)o).x == x && ((Position)o).y == y && ((Position)o).z == z){
				return true;
			}
		} catch (ClassCastException cce){
			throw new RuntimeException("Error comparing points "+this+" "+o, cce);
		}
		return false;
	}
	
	public int hashCode() {
		return toString().hashCode();
	}

	public static String toString(int x, int y, int z){
		return "("+x+","+y+","+z+")";
	}

	public String toString(){
		return "("+x+","+y+","+z+")";
	}

	public static int distance(Position a, Position b){
		return (int) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow (a.y - b.y, 2));
	}

	public static int distance(int x1, int y1, int x2, int y2){
		return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow (y1 - y2, 2));
	}
	
	public static int distanceRound(int x1, int y1, int x2, int y2){
		return (int) Math.floor(Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1-y2)));
	}
	
	public void add(Position p){
		x += p.x;
		y += p.y;
		z += p.z;

	}

}