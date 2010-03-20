package net.slashie.serf.ui.consoleUI;

import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.game.Equipment;

public class EquipmentMenuItem implements MenuItem, ListItem{
	protected Equipment e;
	
	public EquipmentMenuItem(Equipment e) {
		this.e = e;
	}
	
	private CharAppearance getItemAppearance(){
		return (CharAppearance)e.getItem().getAppearance();
	}
 	
	public char getMenuChar() {
		return getItemAppearance().getChar();
	}
	
	public int getMenuColor() {
		return getItemAppearance().getColor();
	}
	
	public String getMenuDescription(){
 		if (e.getQuantity() == 1){
 			return e.getItem().getDescription();
 		} else {
 			return e.getItem().getDescription() +" x"+e.getQuantity();
 		}
 	}
	
	public char getIndex() {
		return getMenuChar();
	}

	public int getIndexColor() {
		return getMenuColor();
	}

	public String getRow() {
		return getMenuDescription();
	}

	public Equipment getEquipment() {
		return e;
	}
	
}
