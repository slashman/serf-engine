package net.slashie.serf.ui.oryxUI.effects;

import java.awt.Image;

import net.slashie.serf.action.Action;
import net.slashie.serf.ui.oryxUI.GFXUserInterface;
import net.slashie.serf.ui.oryxUI.SwingSystemInterface;
import net.slashie.utils.Position;

public class GFXMeleeEffect extends GFXDirectionalEffect{
	private Image[] missile;

	
	public void drawEffect(GFXUserInterface ui, SwingSystemInterface si){
		Image icon = null;
		si.saveLayer(getDrawLayer());
		setAnimationDelay(animationDelay);
		Position var = Action.directionToVariation(direction);
		switch (direction){
		case Action.RIGHT:
			icon = missile[0];
			break;
		case Action.UP:
			icon = missile[1];
			break;
		case Action.LEFT:
			icon = missile[2];
			break;
		case Action.DOWN:
			icon = missile[3];
			break;
		case Action.DOWNRIGHT:
			icon = missile[4];
			break;
		case Action.DOWNLEFT:
			icon = missile[5];
			break;
		case Action.UPLEFT:
			icon = missile[6];
			break;
		case Action.UPRIGHT:
			icon = missile[7];
			break;
		}
		if (icon == null){
			si.loadLayer(getDrawLayer());
			si.commitLayer(getDrawLayer());
			return;
		}
			
		Position runner = new Position(getPosition());
		for (int i = 0; i < depth; i++){
			runner.add(var);
			int height = 0;
			if (ui.getPlayer().getLevel().getMapCell(runner) != null)
				height = ui.getPlayer().getLevel().getMapCell(runner).getHeight();
			Position relative = Position.subs(runner, ui.getPlayer().getPosition());
			Position toPrint = Position.add(ui.PC_POS, relative);
			/*if (!ui.insideViewPort(toPrint))
				break;*/
			si.drawImage(getDrawLayer(), toPrint.x()*32+8, toPrint.y()*32+8-4*height, icon);
			si.commitLayer(getDrawLayer());
			animationPause();
			si.loadLayer(getDrawLayer());
		}
		
	}

	public GFXMeleeEffect(String ID, Image[] missile, int delay){
		super(ID, delay);
		setMissile(missile);
	}

	public void setMissile(Image[] value) {
		missile = value;
	} 
}