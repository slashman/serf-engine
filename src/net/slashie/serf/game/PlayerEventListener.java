package net.slashie.serf.game;

public interface PlayerEventListener {
	public void informEvent(int code, Object param);
	public void informEvent(int code);
}