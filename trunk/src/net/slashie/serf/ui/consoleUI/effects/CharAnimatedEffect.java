package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.utils.Position;

public class CharAnimatedEffect extends CharEffect{
	private String frames;
	private int color;

	public CharAnimatedEffect(String ID, Position where, String frames, int color){
		super (ID);
		setFrames(frames);
		setColor(color);
	}

	public String getFrames() {
		return frames;
	}

	public void setFrames(String value) {
		frames = value;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int value) {
		color = value;
	}

	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position absolute = Position.add(ui.PC_POS, relative);
		if (!ui.insideViewPort(absolute))
			return;
		char [] cframes = frames.toCharArray();
		for (int j = 0; j<cframes.length; j++){
			si.print(absolute.x, absolute.y, cframes[j], color);
			animationPause();
		}
	}
}
