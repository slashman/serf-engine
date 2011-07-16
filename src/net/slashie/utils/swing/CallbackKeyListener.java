package net.slashie.utils.swing;

import java.awt.event.KeyAdapter;
import java.util.concurrent.BlockingQueue;

public abstract class CallbackKeyListener extends KeyAdapter{
	protected BlockingQueue<String>  handler;
	
	public CallbackKeyListener(BlockingQueue<String>  handler){
		this.handler = handler;
	}
}
