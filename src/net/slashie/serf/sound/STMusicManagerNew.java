package net.slashie.serf.sound;

import java.util.Hashtable;
import java.util.Map;

import net.slashie.serf.game.SworeGame;
import net.slashie.utils.sound.midi.STMidiPlayer;
import net.slashie.utils.sound.mp3.JavaZoomBasicPlayerMP3Player;

public class STMusicManagerNew {
	private Thread currentMidiThread;
	private Map<String, String> musics = new Hashtable<String, String>();
	private boolean enabled;
	private String playing = "__nuthin";
	
	public static STMusicManagerNew thus;
	
	public static void initManager() {
		thus = new STMusicManagerNew();
	}
	
	public STMusicManagerNew () {
		//midiPlayer = new MidisLoader();
		STMidiPlayer midiPlayer = new STMidiPlayer();
		currentMidiThread = new Thread(midiPlayer);
		currentMidiThread.start();
	}
	
	public void stopMusic(){
		if (playing.endsWith("mp3")) {
			JavaZoomBasicPlayerMP3Player.doStop();
		} else if (playing.endsWith("mid") || playing.endsWith("midi")) {
			STMidiPlayer.setInstruction(STMidiPlayer.INS_STOP);
			if (currentMidiThread != null)
				currentMidiThread.interrupt();
		}
		playing = "__nuthin";
	}
	
	public void setVolume(double volume){
		JavaZoomBasicPlayerMP3Player.doSetVolume(volume);
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public void die(){
		STMidiPlayer.setInstruction(STMidiPlayer.INS_DIE);
		if (currentMidiThread != null){
			currentMidiThread.interrupt();
		}
		JavaZoomBasicPlayerMP3Player.thus.stop();
	}
	
	public void play(String fileName){
		if (!enabled || playing.equals(fileName))
			return;
		stopMusic();
		try {
			playing = fileName;
			if (fileName.endsWith("mp3")){
				JavaZoomBasicPlayerMP3Player.thus.play(fileName);
			} else if (fileName.endsWith("mid") || fileName.endsWith("midi")) {
				STMidiPlayer.setMidi(fileName);
				STMidiPlayer.setInstruction(STMidiPlayer.INS_LOAD);
				if (currentMidiThread != null){
					currentMidiThread.interrupt();
				}
			}
			
			
		} catch (Exception e){
			SworeGame.crash("Error trying to play "+fileName,e);
		}
	}
	
	public void playOnce(String fileName){
		if (!enabled || playing.equals(fileName))
			return;
		stopMusic();
		try {
			playing = fileName;
			if (fileName.endsWith("mp3")){
				JavaZoomBasicPlayerMP3Player.thus.play(fileName);
			} else if (playing.endsWith("mid") || playing.endsWith("midi")) {
				STMidiPlayer.setMidi(fileName);
				STMidiPlayer.setInstruction(STMidiPlayer.INS_LOAD_ONCE);
				if (currentMidiThread != null){
					currentMidiThread.interrupt();
				}
			}
			
			
		} catch (Exception e){
			SworeGame.crash("Error trying to play "+fileName,e);
		}
	}
	
	public void addMusic(String levelType, String fileName){
		musics.put(levelType, fileName);
	}
	
	public void setEnabled(boolean value){
		enabled = value;
	}
	
	public void playForLevel (String levelType){
		String bgMusic = (String) musics.get(levelType);
		if (bgMusic != null){
			play(bgMusic);
		} else {
			stopMusic();
		}
	}
	
	public void playKey (String key){
		String bgMusic = (String) musics.get(key);
		if (bgMusic != null){
			play(bgMusic);
		} else {
			stopMusic();
		}
	}
	
	public void playKeyOnce (String key){
		String bgMusic = (String) musics.get(key);
		if (bgMusic != null){
			playOnce(bgMusic);
		} else {
			stopMusic();
		}
	}
}

