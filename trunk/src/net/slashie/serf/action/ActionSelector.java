package net.slashie.serf.action;

public interface ActionSelector extends Cloneable, java.io.Serializable{
	public Action selectAction(Actor who);
	public String getID();
	public ActionSelector derive();

}