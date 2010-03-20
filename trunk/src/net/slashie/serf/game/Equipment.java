package net.slashie.serf.game;

import java.io.Serializable;

import net.slashie.serf.baseDomain.AbstractItem;

public class Equipment implements Cloneable, Serializable{
	private AbstractItem item;
	private int quantity;

	public Equipment (AbstractItem pItem, int pQuantity){
		item = pItem;
		quantity = pQuantity;
 	}

 	public boolean isEmpty(){
 		return quantity == 0;
    }
 	
 	public static boolean eqMode = false;
 	public static boolean menuDetail = false;

	public AbstractItem getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int value) {
		quantity = value;
	}

	public void increaseQuantity (int value){
		quantity += value;
	}

	public void reduceQuantity(int value){
		quantity -= value;
	}
	
	@Override
	public Equipment clone()  {
		try {
			return (Equipment)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}