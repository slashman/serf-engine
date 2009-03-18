package net.slashie.serf.action;

public class NullSelector implements ActionSelector{

	@Override
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ActionSelector derive() {
		return (ActionSelector) clone();
	}

	public String getID() {
		return "null";
	}

	public Action selectAction(Actor who) {
		return null;
	}

}
