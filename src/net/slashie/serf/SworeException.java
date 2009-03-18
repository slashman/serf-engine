package net.slashie.serf;

public class SworeException extends RuntimeException {
	private String errorMsg;
	public SworeException (String errorMsg){
		this.errorMsg = errorMsg;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	
}
