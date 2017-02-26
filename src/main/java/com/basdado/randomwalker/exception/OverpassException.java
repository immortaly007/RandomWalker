package com.basdado.randomwalker.exception;

public class OverpassException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public OverpassException(String message) {
		super(message);
	}
	
	public OverpassException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public OverpassException(Throwable cause) {
		super(cause);
	}

}
