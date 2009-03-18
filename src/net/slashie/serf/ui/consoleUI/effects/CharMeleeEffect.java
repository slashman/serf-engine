package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.action.Action;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.utils.Position;

public class CharMeleeEffect extends CharDirectionalEffect{
	
	private String directionMissileChars;
	private int color;

	public CharMeleeEffect(String ID, String pDirectionMissileChars, int pColor){
		super(ID);
		directionMissileChars = pDirectionMissileChars;
		color = pColor;
	}
	
	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		if (direction == Action.SELF){
			char whippy = '*';
			Position abs = ui.getAbsolutePosition(getPosition());
			si.safeprint(abs.x, abs.y, whippy, color);
			animationPause();
			return;
		}
			
		char whippy = directionMissileChars.charAt(direction);
		Position var = Action.directionToVariation(direction);
		Position abs = ui.getAbsolutePosition(getPosition());
		for (int j = 0; j<depth; j++){
			Position toPrint = Position.add(abs,
			Position.mul(var, j+1));
			si.safeprint(toPrint.x, toPrint.y, whippy, color);
			animationPause();
		}
	}

}