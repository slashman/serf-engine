package net.slashie.serf.ui;

public abstract class Appearance {
	private String ID;
	
	public Appearance(String ID){
		this.ID = ID;
	}

	public String getID(){
		return ID;
	}
}