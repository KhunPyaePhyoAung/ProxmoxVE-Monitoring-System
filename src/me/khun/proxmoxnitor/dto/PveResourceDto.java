package me.khun.proxmoxnitor.dto;

import me.khun.proxmoxnitor.pve.PveResource;

public class PveResourceDto {

	private PveResource resource;
	
	public PveResourceDto(PveResource resource) {
		this.resource = resource;
	}
	
	public int getVmId() {
		return resource.getVmId();
	}
	
	public String getName() {
		return resource.getName();
	}
	
	public String getStatus() {
		switch (resource.getStatus()) {
			case RUNNING:
				return "Running";
			case STOPPED:
				return "Stopped";
		}
		return "";
	}
}
