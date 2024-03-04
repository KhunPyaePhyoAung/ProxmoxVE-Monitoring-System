package me.khun.proxmoxnitor.pve;

public class PveResource {

	public enum PveResourceStatus {
		RUNNING, STOPPED
	}
	
	public enum PveResourceType {
		CONTAINER, VM
	}

	private int vmId;
	private String name;
	private PveResourceStatus status;
	private PveResourceType type;

	public int getVmId() {
		return vmId;
	}

	public void setVmId(int vmId) {
		this.vmId = vmId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PveResourceStatus getStatus() {
		return status;
	}

	public void setStatus(PveResourceStatus status) {
		this.status = status;
	}

	public PveResourceType getType() {
		return type;
	}

	public void setType(PveResourceType type) {
		this.type = type;
	}
	

}
