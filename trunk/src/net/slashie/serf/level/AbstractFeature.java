package net.slashie.serf.level;

import java.io.Serializable;

import net.slashie.serf.action.Actor;

/** 
 * A feature is something that stays inside the level but may be moved,
 * destroyed or otherwise affected.
 * 
 * A feature is a mix of a Cell and an Actor
 * 
 * Represents both a definition and an concrete object
 *  
 */

public abstract class AbstractFeature extends Actor implements Cloneable, Serializable {
	
	private boolean isSolid;
	private boolean destroyable;
	private String trigger;
	private String effect;
	private boolean relevant = true;
	private int faint;
	private int light;
	private int currentResistance;
	
	
	public abstract AbstractFeature featureDestroyed(Actor actor);
	
	public AbstractFeature damage(Actor p, int damage){
		if (!destroyable){
			return null;
		}
		currentResistance -= damage;
		if (currentResistance < 0){
			AbstractFeature remnants = featureDestroyed(p);
			p.getLevel().destroyFeature(this);
			return remnants;
		}
		return null;
	}

	public Object clone(){
		AbstractFeature x = (AbstractFeature) super.clone();
		if (getPosition() != null)
			x.setPosition(getPosition().x(), getPosition().y(), getPosition().z());
		return x;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String value) {
		trigger = value;
	}

	public boolean isSolid() {
		return isSolid;
	}

	public void setSolid(boolean value) {
		isSolid = value;
	}

	public boolean isDestroyable() {
		return destroyable;
	}

	public void setDestroyable(boolean value) {
		destroyable = value;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String value) {
		effect = value;
	}

	public boolean isRelevant() {
		return relevant;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}

	public boolean isVisible(){
		return !getAppearance().getID().equals("VOID");
	}

	public int getFaint() {
		return faint;
	}

	public void setFaint(int faint) {
		this.faint = faint;
	}

	public int getLight() {
		return light;
	}
	
	public abstract void onStep(Actor a);
}