package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Image;
import java.util.Enumeration;
import java.util.Vector;

import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;

public class GFXSequentialEffect extends GFXEffect{
	private Vector sequence;
	private Image[] tiles;

	public GFXSequentialEffect(String ID, Vector sequence, Image[] tiles, int delay){
    	super(ID);
    	setAnimationDelay(delay);
		this.tiles = tiles;
		this.sequence = sequence;
    }

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		si.saveLayer();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		int tileIndex = 0;
		Enumeration seq = sequence.elements();
		while (seq.hasMoreElements()){
			Position nextPosition = Position.add(center, (Position) seq.nextElement());
			tileIndex++;
			if (tileIndex == tiles.length)
				tileIndex = 0;
			if (ui.insideViewPort(nextPosition))
				si.drawImage(nextPosition.x*32, nextPosition.y*32, tiles[tileIndex]);
			si.refresh();
			animationPause();
		}
		si.loadLayer();
	}

}