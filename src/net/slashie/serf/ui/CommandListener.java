package net.slashie.serf.ui;

public interface CommandListener {

 	public enum Command {
		QUIT,
		SAVE,
		NONE,
		HELP,
		LOOK,
		RESTART,
		SHOWINVEN,
		SHOWHISCORES,
		SHOWSKILLS,
		SHOWSTATS,
		PROMPTQUIT,
		PROMPTSAVE,
		SHOWUNEQUIP,
		SHOWMESSAGEHISTORY,
		SHOWMAP,
		SWITCHMUSIC,
		SWITCHSFX,
		EXAMINELEVELMAP,
		CHARDUMP;
 	}
	
	public void commandSelected(Command pCommand);

	
}