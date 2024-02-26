package me.khun.proxmoxnitor.pve;

public class PveRrdData {
	
	private long time;
	private float cpuUsage;
	private float ioDelay;
	private PveRrdDataType type;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public float getIoDelay() {
		return ioDelay;
	}

	public void setIoDelay(float ioDelay) {
		this.ioDelay = ioDelay;
	}

	public PveRrdDataType getType() {
		return type;
	}

	public void setType(PveRrdDataType type) {
		this.type = type;
	}
	

}
