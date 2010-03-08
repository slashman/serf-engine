package net.slashie.serf.game;

import net.slashie.serf.baseDomain.AbstractItem;

public class Equipment implements Cloneable{
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
 			return item.getDescription();
 		} else {
 			return item.getDescription() +" x"+quantity;
 		}
// 		if (eqMode)
// 			return item.getAttributesDescription() +" x"+quantity+ " ["+item.getDefinition().getMenuDescription()+"]";
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