package me.khun.proxmoxnitor.dto;

import me.khun.proxmoxnitor.pve.PveResource;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceStatus;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceType;

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
	
	public String getStatusString() {
		switch (resource.getStatus()) {
			case RUNNING:
				return "Running";
			case STOPPED:
				return "Stopped";
		}
		return "";
	}
	
	public PveResourceStatus getStatus() {
		return resource.getStatus();
	}
	
	public PveResourceType getType() {
		return resource.getType();
	}
}
