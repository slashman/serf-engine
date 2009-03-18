package net.slashie.serf.ui.consoleUI.effects;

import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.serf.ui.UserInterface;
import net.slashie.serf.ui.consoleUI.ConsoleUserInterface;
import net.slashie.utils.Position;

public class CharSplashEffect extends CharEffect{
	private String tiles;
	private int color;
	private transient ConsoleSystemInterface si;
	private transient UserInterface ui;

    public CharSplashEffect(String ID, String tiles, int color, int delay){
    	super(ID,delay);
		this.tiles = tiles;
		this.color = color;
    }
    
	public void drawEffect(ConsoleUserInterface ui, ConsoleSystemInterface si){
		UserInterface.getUI().getPlayer().see();
		UserInterface.getUI().refresh();
		
		si.setAutoRefresh(false);
		//ui.refresh();
		Position relative = Position.subs(getPosition(), ui.getPlayer().getPosition());
		Position center = Position.add(ui.PC_POS, relative);
		this.si = si;
		this.ui = ui;

		Position oldPoint = null;
		int oldColor = -1;

		Position helper = null;

		si.safeprint(center.x, center.y, tiles.charAt(0), color);

		for (int ring = 1; ring < tiles.length(); ring++){
			drawCircle(ui, center, ring, tiles.charAt(ring), color);
			si.refresh();
			animationPause();
		}
		/*si.cls();
		ui.refresh();*/
	}

	private void drawCircle(ConsoleUserInterface ui, Position p, int radius, char tile, int color){
		int d = 3 - (2 * radius);
		Position runner = new Position(0, radius);
		Position zero = new Position(0,0);
		while (true) {
			if (Position.flatDistance(zero, runner) <= radius)
				drawCirclePixels(ui, p, runner.x,runner.y, tile, color);
			if (d < 0)
				d = d + (4*runner.x)+6;
			else {
				//d = d + 4 * (x-y) + 10;
				d = d + 4 * (runner.x-runner.y) +10;
				runner.y --;
			}
			runner.x++;
			if (runner.y == 0)
				break;
		}
		
	}


	private void drawCirclePixels(ConsoleUserInterface ui, Position center, int x, int y, char tile, int color){
		if (ui.insideViewPort(center.x+x, center.y + y))
			si.safeprint(center.x + x, center.y + y, tile, color);
		if (ui.insideViewPort(center.x+x, center.y - y))
			si.safeprint(center.x + x, center.y - y, tile, color);
		if (ui.insideViewPort(center.x-x, center.y + y))
		si.safeprint(center.x - x, center.y + y, tile, color);
		if (ui.insideViewPort(center.x-x, center.y - y))
		si.safeprint(center.x - x, center.y - y, tile, color);
		if (ui.insideViewPort(center.x+y, center.y + x))
		si.safeprint(center.x + y, center.y + x, tile, color);
		if (ui.insideViewPort(center.x+y, center.y - x))
		si.safeprint(center.x + y, center.y - x, tile, color);
		if (ui.insideViewPort(center.x-y, center.y + x))
		si.safeprint(center.x - y, center.y + x, tile, color);
		if (ui.insideViewPort(center.x-y, center.y - x))
		si.safeprint(center.x - y, center.y - x, tile, color);
	}




}