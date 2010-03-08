package net.slashie.simpleRL.domain.world;

import net.slashie.serf.level.BufferedLevel;

public class Level extends BufferedLevel {
	private String musicKey;

	public String getMusicKey() {
		return musicKey;
	}

	public void setMusicKey(String musicKey) {
		this.musicKey = musicKey;
	}
}
