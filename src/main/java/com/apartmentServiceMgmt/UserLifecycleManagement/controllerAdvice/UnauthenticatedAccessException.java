package com.apartmentServiceMgmt.UserLifecycleManagement.controllerAdvice;

public class UnauthenticatedAccessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthenticatedAccessException(String message) {
		super(message);
	}

}