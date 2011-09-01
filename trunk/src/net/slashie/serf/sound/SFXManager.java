package net.slashie.serf.sound;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Control.Type;


public class SFXManager {
	private static boolean enabled;
	
	public static void setEnabled(boolean value){
		enabled = value;
	}
	
	private static BlockingQueue<String> playList = new LinkedBlockingQueue<String>();

	static {
		new Thread(new SFXServer()).start();
	}
	
	public static synchronized void play(String fileName){
		if (!enabled)
			return;
		if (fileName.equals(""))
			return;
		if (!playList.isEmpty())
			return;
		if (fileName.endsWith(".wav")){
			try {
				playList.put(fileName);
			} catch (InterruptedException e) {}
		} 
	}
	
	static class SFXServer implements Runnable{
		@Override
		public void run() {
			while (true){
				String playFile = null;
				while (playFile == null){
					try {
						playFile = playList.take();
					} catch (InterruptedException e) {
					}
				}
				if (playFile.equals("KILL")){
					break;
				} else if (playFile.startsWith("VOLUME")){
					lineVolume = (float) Integer.parseInt(playFile.substring(6)) / 100.0f;
				} else {
					play(playFile);
				}
			}
		}
		
		class PlaybackInfo {
			AudioFormat af;
			DataLine.Info info;
		}
		private Map<String, PlaybackInfo> map = new HashMap<String, PlaybackInfo>();
		private float lineVolume;
		
		public void play (String file){
		   try{
			   AudioInputStream ais = AudioSystem.getAudioInputStream (new File(file));
			   PlaybackInfo pbi = map.get(file);
			   if (pbi == null){
				   pbi = new PlaybackInfo();
				   pbi.af = ais.getFormat();
				   pbi.info = new DataLine.Info(SourceDataLine.class, pbi.af);
				   map.put(file, pbi);
			   }
			   
			   int frameRate = (int)pbi.af.getFrameRate();
			   int frameSize = pbi.af.getFrameSize();
			   int bufSize = frameRate * frameSize / 10;
			   SourceDataLine line = (SourceDataLine)AudioSystem.getLine(pbi.info);
			   line.open(pbi.af, bufSize);
			   
			   line.start();
			  
				try {
					FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
					float dB = (float)(Math.log(lineVolume)/Math.log(10.0)*20.0);
					   gainControl.setValue(dB);
				} catch (IllegalArgumentException e) {
					// gain control not supported
				}
			   
			   		   
			   byte[] data = new byte[bufSize];
			   int bytesRead;
			   while ((bytesRead = ais.read( data, 0, data.length )) != -1 ){
				   Thread.yield();
				   line.write(data, 0, bytesRead);
			   }
			   line.drain();
			   line.stop();
			   line.close();
		   }
		   catch (Exception e){
			   addReport("Error playing... "+e.toString());
			   e.printStackTrace();
		   }
	   }
			
		public void addReport(String report){
			System.err.println(report);
		}
	}

	
	public static void setVolume(double sfxVolume) {
		try {
			playList.put("VOLUME"+Math.round(sfxVolume*100.0d));
		} catch (InterruptedException e) {}
	}
}
