package net.slashie.serf.levelGeneration;

import java.util.Hashtable;

import net.slashie.serf.level.Unleasher;


public abstract class StaticPattern {
	protected Hashtable<String,String> charMap = new Hashtable<String,String>();
	protected String[][] cellMap;
	protected Hashtable<String,String> inhabitantsMap = new Hashtable<String,String>();;
	protected String[][] inhabitants;
	protected Unleasher[] unleashers;

	public Hashtable<String,String> getCharMap(){
		return charMap;
	}
	
	public String[][] getCellMap(){
		return cellMap;
	}
	
	public Hashtable<String,String> getInhabitantsMap(){
		return inhabitantsMap;
	}
	public String[][] getInhabitants(){
		return inhabitants;
	}
	
	public abstract String getDescription();
	
	public void setup(StaticGenerator gen){
		gen.reset();
		gen.setCharMap(getCharMap());
		gen.setLevel(getCellMap());
		gen.setInhabitantsMap(getInhabitantsMap());
		gen.setInhabitants(getInhabitants());
	}

	public Unleasher[] getUnleashers() {
		return unleashers;
	}

	public void setUnleashers(Unleasher[] unleashers) {
		this.unleashers = unleashers;
	}
}
