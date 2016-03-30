package net.slashie.simpleRL.domain.world;

import net.slashie.serf.level.AbstractCell;
import net.slashie.serf.ui.AppearanceFactory;

public class LevelCell extends AbstractCell
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSpike;

	public LevelCell(String pID, String sdes, boolean isSolid, boolean isOpaque, boolean isSpike)
	{
		super(pID, sdes, sdes, AppearanceFactory.getAppearanceFactory().getAppearance(pID), isSolid, isOpaque);
		this.isSpike = isSpike;
	}

	public boolean isSpike()
	{
		return isSpike;
	}

	public void setSpike(boolean isSpike)
	{
		this.isSpike = isSpike;
	}
}
