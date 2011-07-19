package net.slashie.utils.swing;

import java.awt.event.KeyAdapter;
import java.util.concurrent.BlockingQueue;

public abstract class CallbackKeyListener<T> extends KeyAdapter{
	protected BlockingQueue<T>  handler;
	
	public CallbackKeyListener(BlockingQueue<T>  handler){
		this.handler = handler;
	}
}
