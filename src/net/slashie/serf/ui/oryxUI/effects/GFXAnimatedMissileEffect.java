package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Image;

import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;

public class GFXAnimatedMissileEffect extends GFXDirectedEffect{
	private Image[] missile;

	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		//super.drawEffect(ui, si);
		si.saveLayer(getDrawLayer());
		Position oldPoint = effectLine.next();
		int too = 0;
		
		for (int i = 0; i < depth; i++){
			Position next = effectLine.next();
			oldPoint = new Position(next);
			too++;
			if (too == missile.length)
				too = 0;
			int height = 0;
			if (ui.getPlayer().getLevel().getMapCell(next) != null)
				height = ui.getPlayer().getLevel().getMapCell(next).getHeight();
			Position relative = Position.subs(next, ui.getPlayer().getPosition());
			Position toPrint = Position.add(ui.PC_POS, relative);
			if (!ui.insideViewPort(toPrint))
				continue;
			si.drawImage(getDrawLayer(), toPrint.x()*32, toPrint.y()*32-4*height, missile[too]);
			si.commitLayer(getDrawLayer());
			animationPause();
			si.loadLayer(getDrawLayer());
			
		}
	}

	public GFXAnimatedMissileEffect(String id, Image[] missile, int delay){
		super(id,delay);
		setMissile(missile);
	}
	
	public void setMissile(Image[] value) {
		missile = value;
	}


}
