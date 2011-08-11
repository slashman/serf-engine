package net.slashie.serf.ui.oryxUI;

import javax.swing.JTextArea;

public class SwingInformBox extends JTextArea{
	public void clear(){
		boolean wait = false;
		do {
			try {
				setText("");
				wait = false;
			}  catch (Error e){
				wait = true;
			}
		} while (wait);
	}
	
	public boolean isEditable(){
		return false;
	}
	
	public boolean isFocusable(){
		return false;
	}
	
	public synchronized void addText(String txt){
		boolean wait = false;
		do {
			try {
				String separator = ". ";
				if (txt.endsWith("!")){
					separator = " ";
				}
				setText(getText()+txt+separator);
				wait = false;
			}  catch (Error e){
				wait = true;
			}
		} while (wait);
	}
}
