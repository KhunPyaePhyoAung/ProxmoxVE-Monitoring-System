package me.khun.proxmoxnitor.exception;

public class NoNetworkException extends ProxmoxnitorException {

	private static final long serialVersionUID = 1L;

	public NoNetworkException(String message) {
		super(message);
	}

}
