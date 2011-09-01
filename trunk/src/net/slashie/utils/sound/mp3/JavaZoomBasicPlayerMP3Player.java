package net.slashie.utils.sound.mp3;

import java.io.File;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


public class JavaZoomBasicPlayerMP3Player implements BasicPlayerListener{
	public static JavaZoomBasicPlayerMP3Player thus = new JavaZoomBasicPlayerMP3Player();
	
	private BasicPlayer player = new BasicPlayer();
	// BasicPlayer is a BasicController.
	private BasicController control = (BasicController) player;
	// Register BasicPlayerTest to BasicPlayerListener events.
	// It means that this object will be notified on BasicPlayer
	// events such as : opened(...), progress(...), stateUpdated(...)
	
	{
		player.addBasicPlayerListener(this);
		
	}
	private String currentFile;

	private double currentGain = 1.0d;
	
	public void play(String filename){
		currentFile = filename;
		try
		{			
			// Open file, or URL or Stream (shoutcast) to play.
			control.open(new File(filename));
			// control.open(new URL("http://yourshoutcastserver.com:8000"));
			
			// Start playback in a thread.
			control.play();
			
			// Set Volume (0 to 1.0).
			// setGain should be called after control.play().
			control.setGain(currentGain);
			
			// Set Pan (-1.0 to 1.0).
			// setPan should be called after control.play().
			control.setPan(0.0);

			// If you want to pause/resume/pause the played file then
			// write a Swing player and just call control.pause(),
			// control.resume() or control.stop().			
			// Use control.seek(bytesToSkip) to seek file
			// (i.e. fast forward and rewind). seek feature will
			// work only if underlying JavaSound SPI implements
			// skip(...). True for MP3SPI (JavaZOOM) and SUN SPI's
			// (WAVE, AU, AIFF).
			
		}
		catch (BasicPlayerException e)
		{
			e.printStackTrace();
		}
	}
	
	public void stop(){
		try {
			control.stop();
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
	public static void addReport(String report){
		System.err.println(report);
	}

	public void opened(Object arg0, Map arg1) {
	
	}

	public void progress(int arg0, long arg1, byte[] arg2, Map arg3) {
	
	}

	public void setController(BasicController arg0) {
	
	}

	public void stateUpdated(BasicPlayerEvent arg0) {
		// Notification of BasicPlayer states (opened, playing, end of media, ...)
		if (arg0.getCode()==BasicPlayerEvent.EOM)
		{
			play(currentFile);
		}
		
	}
	
	public static void doStop(){
		thus.stop();
	}

	public static void doSetVolume(double volume) {
		thus.setVolume(volume);
		
	}

	private void setVolume(double volume) {
		currentGain  = volume;
		try {
			player.setGain(volume);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
		}
	}
	
}
