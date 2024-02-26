package me.khun.proxmoxnitor.pve;

public class PveResource {

	public enum PveResourceStatus {
		RUNNING, STOPPED
	}

	private int vmId;
	private String name;
	private PveResourceStatus status;

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

}
