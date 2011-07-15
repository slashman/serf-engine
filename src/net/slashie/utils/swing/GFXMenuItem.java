package net.slashie.utils.swing;

import java.awt.Image;

import net.slashie.serf.ui.oryxUI.SwingSystemInterface;

public interface GFXMenuItem extends java.io.Serializable{
	public Image getMenuImage();
	public String getMenuDescription();
	public String getMenuDetail();
	public String getGroupClassifier();
}