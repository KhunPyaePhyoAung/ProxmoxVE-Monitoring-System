package me.khun.proxmoxnitor.exception;

public class ProxmoxnitorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProxmoxnitorException(String message) {
		super(message);
	}
}
