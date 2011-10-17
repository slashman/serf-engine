package net.slashie.lang;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Percentage implements Serializable{
	private double value;
	
	public Percentage(int value) {
		this.value = (double) value / 100.0d;
	}
	
	public Percentage(double value) {
		this.value = value;
	}

	public int transformInt(int value) {
		return (int)Math.round(this.value*(double)value);
	}
}
