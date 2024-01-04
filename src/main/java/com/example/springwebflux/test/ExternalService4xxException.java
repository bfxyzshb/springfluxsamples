package com.example.springwebflux.test;

public class ExternalService4xxException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1108866900458042582L;

	public ExternalService4xxException() {
		super();
	}

	public ExternalService4xxException(String message) {
		super(message);
	}

	public ExternalService4xxException(String message, Throwable cause) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
	}

}