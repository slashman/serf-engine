package net.slashie.serf.game;

import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.baseDomain.AbstractItem;

public class Equipment implements MenuItem, ListItem, Cloneable{
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

 	public String getMenuDescription(){
 		if (quantity == 1){
 			return item.getMenuDescription();
 		} else {
 			return item.getMenuDescription() +" x"+quantity;
 		}
// 		if (eqMode)
// 			return item.getAttributesDescription() +" x"+quantity+ " ["+item.getDefinition().getMenuDescription()+"]";
 	}

 	/*Unsafe, Coupled*/
	public char getMenuChar() {
		return item.getMenuChar();
	}
	
	/*Unsafe, Coupled*/
	public int getMenuColor() {
		return item.getMenuColor();
	}
	
	// Remove this, somewhen
	public char getIndex() {
		return getMenuChar();
	}

	// Remove this, somewhen
	public int getIndexColor() {
		return getMenuColor();
	}

	// Remove this, somewhen
	public String getRow() {
		return getMenuDescription();
	}


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