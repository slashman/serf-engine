package net.slashie.serf.ui.oryxUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseListener;

import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AddornedBorderTextArea extends AddornedBorderPanel{
	private JTextArea textArea; 
	
	public AddornedBorderTextArea(Image UPRIGHT, 
			Image UPLEFT, Image DOWNRIGHT, Image DOWNLEFT,
			Color OUT_COLOR, Color IN_COLOR, Color INSIDE_COLOR, int borderWidth, int outsideBound, int inBound, int insideBound
			) {
		super(UPRIGHT, UPLEFT, DOWNRIGHT, DOWNLEFT, OUT_COLOR, IN_COLOR, INSIDE_COLOR, borderWidth, outsideBound, inBound, insideBound);
		textArea = new JTextArea();
		setLayout(new GridLayout(1,1));
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFocusable(false);
		textArea.setEditable(false);
		textArea.setOpaque(false);
		//textArea.setVisible(false);
		setBorder(new EmptyBorder(borderWidth, borderWidth,borderWidth,borderWidth));
		setOpaque(false);
		add(textArea);
	}
	public void setText(String text){
		textArea.setText(text);
	}
	
	public void setFont(Font font){
		if (textArea != null)
			textArea.setFont(font);
	}
	
	public void setForeground(Color fore){
		if (textArea != null) textArea.setForeground(fore);
	}
	
	public void setBackground(Color fore){
		if (textArea != null) textArea.setBackground(fore);
	}
	
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		textArea.addMouseListener(l);
	}
	
	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		super.removeMouseListener(l);
		textArea.removeMouseListener(l);
	}

	
}
