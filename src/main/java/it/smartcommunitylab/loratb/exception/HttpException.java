package it.smartcommunitylab.loratb.exception;

@SuppressWarnings("serial")
public class HttpException extends Exception {
	private int responseCode;
	
	public HttpException(int responseCode, String message) {
		super(message);
		this.responseCode = responseCode;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
}
