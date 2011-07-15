package net.slashie.utils.swing;

import java.awt.event.ActionListener;

public abstract class CallbackActionListener implements ActionListener{
	private CallbackHandler handler;
	
	public CallbackActionListener(CallbackHandler handler){
		this.handler = handler;
	}

	public CallbackHandler getHandler() {
		return handler;
	}
	
	
}
