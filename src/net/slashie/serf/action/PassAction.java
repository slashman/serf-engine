package net.slashie.serf.action;

public class PassAction extends Action{
	private int cost;

	/**
	 * @deprecated Specify cost instead
	 */
	public PassAction() {
		this(50);
	}
	
	public PassAction(int cost) {
		this.cost = cost;
	}
	
	@Override
	public void execute() {
		//Do Nothing
	}
	
	@Override
	public String getID() {
		return "Pass";
	}
	
	@Override
	public int getCost() {
		return cost;
	}
}
