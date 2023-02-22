package com.luv2code.springdemo.rest;

public class CustomerErrorResponse {
	
	private int ststus;
	private String message;
	private long timeStamp;
	
	public CustomerErrorResponse() {
		
	}

	public CustomerErrorResponse(int ststus, String message, long timeStamp) {
		this.ststus = ststus;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public int getStstus() {
		return ststus;
	}

	public void setStstus(int ststus) {
		this.ststus = ststus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	
}
