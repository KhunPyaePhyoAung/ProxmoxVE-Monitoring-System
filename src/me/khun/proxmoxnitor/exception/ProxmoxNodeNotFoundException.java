package me.khun.proxmoxnitor.exception;

public class ProxmoxNodeNotFoundException extends ProxmoxnitorException {

	private static final long serialVersionUID = 1L;

	public ProxmoxNodeNotFoundException(String message) {
		super(message);
	}

}
