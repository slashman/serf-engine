package net.slashie.serf.ui;

import net.slashie.serf.ui.CommandListener.Command;

@SuppressWarnings("serial")
public class UserCommand implements java.io.Serializable
{
	/** Links a Command with a KeyCode with which it is triggered */
	private int keyCode;
	private Command command;

	public int getKeyCode()
	{
		return keyCode;
	}

	private void setKeyCode(int value)
	{
		if (value < 0 || value > 115)
			keyCode = 0;
		else
			keyCode = value;
	}

	public Command getCommand()
	{
		return command;
	}

	public UserCommand(Command command, int keycode)
	{
		this.command = command;
		setKeyCode(keycode);
	}
}