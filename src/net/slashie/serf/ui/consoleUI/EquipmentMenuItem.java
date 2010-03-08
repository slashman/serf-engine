package net.slashie.serf.ui.consoleUI;

import net.slashie.libjcsi.textcomponents.ListItem;
import net.slashie.libjcsi.textcomponents.MenuItem;
import net.slashie.serf.game.Equipment;

public class EquipmentMenuItem implements MenuItem, ListItem{
	private Equipment e;
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
	
	public String getMenuDescription() {
		return e.getItem().getDescription();
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
	
}
