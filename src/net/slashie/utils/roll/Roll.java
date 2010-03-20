package net.slashie.utils.roll;

import java.io.Serializable;

import net.slashie.utils.Util;

public class Roll implements Serializable{
	
	private int base;
	private int multiplier;
	private int modifier;
	private double megaMultiplier = 1.0d;
	private Roll modifierRoll;
	
	public void addModifierRoll(Roll modifierRoll_) {
		if (modifierRoll == null)
			modifierRoll = new Roll(modifierRoll_);
		else
			modifierRoll.addModifierRoll(modifierRoll_);
	}
	public double getMegaMultiplier() {
		return megaMultiplier;
	}
	public void affectMegaMultiplier(double megaMultiplier) {
		this.megaMultiplier = megaMultiplier * this.megaMultiplier;
	}
	public int getModifier() {
		return modifier;
	}
	
	public Roll (String rollExpression){
		if (rollExpression.indexOf("+") != -1){
			multiplier = Integer.parseInt(rollExpression.substring(0, rollExpression.indexOf("D")));
			base = Integer.parseInt(rollExpression.substring(rollExpression.indexOf("D")+1, rollExpression.indexOf("+")));
			modifier = Integer.parseInt(rollExpression.substring(rollExpression.indexOf("+")+1));
		} else if (rollExpression.indexOf("D") != -1){
			multiplier = Integer.parseInt(rollExpression.substring(0, rollExpression.indexOf("D")));
			base = Integer.parseInt(rollExpression.substring(rollExpression.indexOf("D")+1));
		}
		else {
			modifier = Integer.parseInt(rollExpression);
			base = 0;
			multiplier = 0;
		}
	}
	
	public Roll (Roll r){
		base = r.getBase();
		multiplier = r.getMultiplier();
		modifier = r.getModifier();
		megaMultiplier = r.getMegaMultiplier();
		if (r.getModifierRoll() != null){
			modifierRoll = new Roll(r.getModifierRoll());
		}
	}
	
	private Roll getModifierRoll() {
		return modifierRoll;
	}
	public Roll (int pBase, int pMultiplier){
		base = pBase;
		multiplier = pMultiplier;
	}
	
	public Roll (int pBase, int pMultiplier, int pModifier){
		base = pBase;
		multiplier = pMultiplier;
		modifier = pModifier;
	}
	
	public int roll(){
		int sum = 0;
		for (int i = 0; i < multiplier; i++){
			sum += Util.rand(1, base);
		}
		if (modifierRoll != null){
			sum += modifierRoll.roll();
		}
		sum = (int)Math.round(megaMultiplier*(sum + modifier)); 
		
		return sum;
	}
	
	public int rollWithSurplus(int surplus){
		int sum = 0;
		for (int i = 0; i < multiplier; i++){
			int roll = Util.rand(1, base) + surplus;
			if (roll > base)
				roll = base;
			sum += roll;
		}
		if (modifierRoll != null){
			sum += modifierRoll.roll();
		}
		sum = (int)Math.round(megaMultiplier*(sum + modifier)); 
		
		return sum;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
	
	public void setModifier(int modifier){
		this.modifier = modifier;
	}
	
	public void addModifier(int modifier){
		this.modifier += modifier;
	}

	public String getString(){
		if (isFixed()){
			return String.valueOf(roll());
		}
		
		if (isZero()){
			return "0";
		}
		
		String ret = "";

		if (multiplier != 0){
			ret = multiplier+"";
			if (base != 1 && base != 0)
				ret = ret + "D" + base;
		}
		
		
		if (modifierRoll != null){
			if (modifierRoll.isZero()){
				// Do naught
			}  else if (modifierRoll.isFixed()){
				int roll = modifierRoll.roll();
				ret = ret + (roll >= 0 ? "+" : "") + roll;
			} else if (modifierRoll.getMegaMultiplier() < 0){
				ret = ret + modifierRoll.getString();
			} else {
				if (multiplier != 0){
					ret = ret + '+';
				}
				ret = ret + modifierRoll.getString();
			}
		}
		
		if (modifier > 0){
			ret = ret + "+" + modifier;
		} else if (modifier < 0) {
			ret = ret + "-" + (-modifier);
		}
		
		if (megaMultiplier != 1.0d){
			if (megaMultiplier == -1.0d){
				ret = "-("+ret+")";
			} else {
				ret = megaMultiplier +"X("+ret+")";
			}
		}
		return ret;
	}
	
	public boolean isZero() {
		return (base == 0 || multiplier == 0) && modifier == 0 && (modifierRoll != null ? modifierRoll.isZero() : true);
	}
	
	public boolean isFixed(){
		return (base == 0 || base == 1) && (modifierRoll != null ? modifierRoll.isFixed() : true);
	}
	
	public int getMax() {
		int sum = base * multiplier + modifier;
		if (modifierRoll != null){
			sum += modifierRoll.getMax();
		}
		sum *= megaMultiplier; 
		return sum;
	}
}