package me.khun.proxmoxnitor.exception;

public class ProxmoxHostNotFoundException extends ProxmoxnitorException {
	
	private static final long serialVersionUID = 1L;

	public ProxmoxHostNotFoundException(String message) {
		super(message);
	}

}
