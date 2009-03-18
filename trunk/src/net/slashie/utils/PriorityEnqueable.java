package net.slashie.utils;

public interface PriorityEnqueable {
	public int getCost();
	public void reduceCost(int value);
}
