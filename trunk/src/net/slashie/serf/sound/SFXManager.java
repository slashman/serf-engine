package net.slashie.serf.sound;

import net.slashie.serf.game.SworeGame;
import net.slashie.utils.sound.wav.WavPlayer;


public class SFXManager {
	private static Thread currentThread;
	private static boolean enabled;
	
	public static void setEnabled(boolean value){
		enabled = value;
	}
	
	public static synchronized void play(String fileName){
		if (!enabled)
			return;
		if (fileName.equals(""))
			return;
		if (currentThread != null)
			currentThread.interrupt();
		if (fileName.endsWith(".wav")){
			WavPlayer wavPlayer = new WavPlayer(fileName);
			currentThread = new Thread(wavPlayer);
		} 
		try {
			currentThread.start();
		} catch (IllegalThreadStateException itse){
			SworeGame.addReport("Illegal Thread State for "+fileName);
		}
	}
}
