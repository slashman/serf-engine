package net.slashie.utils.swing;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CleanButton extends JButton{
	private static final long serialVersionUID = 1L;

	public CleanButton(ImageIcon icon, Cursor c){
		super(icon);
		clean();
		setCursor(c);
	}
	
	public CleanButton(Cursor c){
		super();
		clean();
		setCursor(c);
	}
	
	public CleanButton(ImageIcon icon){
		this(icon, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	
	
	public CleanButton(){
		this(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private void clean(){
		setContentAreaFilled(false);
		setOpaque(false);
		setFocusable(false);
		setBorder(null);		
	}
}
