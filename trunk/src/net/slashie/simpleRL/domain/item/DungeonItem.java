package net.slashie.simpleRL.domain.item;

import net.slashie.serf.baseDomain.AbstractItem;

public class DungeonItem extends AbstractItem implements Cloneable{
	private String itemId;
	private String description;
	
	public DungeonItem(String itemID, String description) {
		super(itemID);
		this.itemId = itemID;
		this.description = description;
	}
	
	@Override
	public String getFullID() {
		// Item classifier Id. Used to stack things
		return itemId;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
