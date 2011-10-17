package net.slashie.lang;

public class Percentage {
	private double value;
	
	public Percentage(int value) {
		this.value = value;
	}
	
	public Percentage(double value) {
		this.value = value;
	}

	public int transformInt(int value) {
		return (int)Math.round(this.value*(double)value);
	}
}
