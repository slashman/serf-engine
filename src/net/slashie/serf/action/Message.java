package net.slashie.serf.action;

import net.slashie.utils.Position;

public class Message{
	private String text;
	private Position location;
	private String time;

	public Message (String pText, Position pLocation, String pTime){
		text = pText;
		location = pLocation;
		time = pTime;
	}

	public String getText() {
		return text;
	}

	public Position getLocation() {
		return location;
	}
	
	public String getTime() {
		return time;
	}

	public String toString(){
		return getText();
	}
}