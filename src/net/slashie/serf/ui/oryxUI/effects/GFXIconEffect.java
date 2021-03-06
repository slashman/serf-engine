package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Image;

import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;

public class GFXIconEffect extends GFXEffect{
	private Image tile;

    public GFXIconEffect(String ID, Image tile, int delay){
    	super(ID);
		this.tile = tile;
		setAnimationDelay(delay);
    }

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		si.saveLayer(getDrawLayer());
		//si.setAutoRefresh(false);
		int height = 0;
		if (ui.getPlayer().getLevel().getMapCell(getPosition()) != null)
			height = ui.getPlayer().getLevel().getMapCell(getPosition()).getHeight();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		if (ui.insideViewPort(center))
			si.drawImage(getDrawLayer(), center.x*32, center.y*32-4*height, tile);
		si.commitLayer(getDrawLayer());
		animationPause();
		si.loadLayer(getDrawLayer());
	}
}