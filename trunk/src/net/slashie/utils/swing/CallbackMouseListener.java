package net.slashie.utils.swing;

import java.awt.event.MouseAdapter;
import java.util.concurrent.BlockingQueue;

public abstract class CallbackMouseListener<T> extends MouseAdapter{
	protected BlockingQueue<T>  handler;
	
	public CallbackMouseListener(BlockingQueue<T>  handler){
		this.handler = handler;
	}
}
