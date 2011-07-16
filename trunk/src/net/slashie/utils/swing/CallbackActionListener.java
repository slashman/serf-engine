package net.slashie.utils.swing;

import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

public abstract class CallbackActionListener<T> implements ActionListener{
	protected BlockingQueue<T> handler;
	
	public CallbackActionListener(BlockingQueue<T> handler){
		this.handler = handler;
	}
}
