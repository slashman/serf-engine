package net.slashie.utils.swing;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public interface CustomGFXMenuItem extends GFXMenuItem{
	void drawMenuItem(SwingSystemInterface si, int x, int y, int index, boolean highlight);
	public boolean showTooltip();
	public void drawTooltip(SwingSystemInterface si, int xpos, int ypos);
}
