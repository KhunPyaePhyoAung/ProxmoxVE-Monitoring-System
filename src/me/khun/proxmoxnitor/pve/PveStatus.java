package me.khun.proxmoxnitor.pve;

public class PveStatus {

	private long uptime;
	private float cpuUsage;
	private int cpus;
	private long totalMemory;
	private long usedMemory;
	private long freeMemory;
	private long totalHdSpace;
	private long usedHdSpace;
	private long freeHdSpace;
	private float ioDelay;
	private long ksm;
	private long totalSwap;
	private long usedSwap;
	private long freeSwap;
	private float[] loadAverage;
	private String kernelVersion;
	private String pveVersion;
	private String cpuModel;
	private float cpuHz;
	private int cpuSockets;
	private int cpuCores;
	private String bootMode;

	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public int getCpus() {
		return cpus;
	}

	public void setCpus(int cpus) {
		this.cpus = cpus;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getTotalHdSpace() {
		return totalHdSpace;
	}

	public void setTotalHdSpace(long totalHdSpace) {
		this.totalHdSpace = totalHdSpace;
	}

	public long getUsedHdSpace() {
		return usedHdSpace;
	}

	public void setUsedHdSpace(long usedHdSpace) {
		this.usedHdSpace = usedHdSpace;
	}

	public long getFreeHdSpace() {
		return freeHdSpace;
	}

	public void setFreeHdSpace(long freeHdSpace) {
		this.freeHdSpace = freeHdSpace;
	}

	public float getIoDelay() {
		return ioDelay;
	}

	public void setIoDelay(float ioDelay) {
		this.ioDelay = ioDelay;
	}

	public long getKsm() {
		return ksm;
	}

	public void setKsm(long ksm) {
		this.ksm = ksm;
	}

	public long getTotalSwap() {
		return totalSwap;
	}

	public void setTotalSwap(long totalSwap) {
		this.totalSwap = totalSwap;
	}

	public long getUsedSwap() {
		return usedSwap;
	}

	public void setUsedSwap(long usedSwap) {
		this.usedSwap = usedSwap;
	}

	public long getFreeSwap() {
		return freeSwap;
	}

	public void setFreeSwap(long freeSwap) {
		this.freeSwap = freeSwap;
	}

	public float[] getLoadAverage() {
		return loadAverage;
	}

	public void setLoadAverage(float[] loadAverage) {
		this.loadAverage = loadAverage;
	}

	public String getKernelVersion() {
		return kernelVersion;
	}

	public void setKernelVersion(String kernelVersion) {
		this.kernelVersion = kernelVersion;
	}

	public String getPveVersion() {
		return pveVersion;
	}

	public void setPveVersion(String pveVersion) {
		this.pveVersion = pveVersion;
	}

	public String getCpuModel() {
		return cpuModel;
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel = cpuModel;
	}

	public float getCpuHz() {
		return cpuHz;
	}

	public void setCpuHz(float cpuHz) {
		this.cpuHz = cpuHz;
	}

	public int getCpuSockets() {
		return cpuSockets;
	}

	public void setCpuSockets(int cpuSockets) {
		this.cpuSockets = cpuSockets;
	}

	public int getCpuCores() {
		return cpuCores;
	}

	public void setCpuCores(int cpuCores) {
		this.cpuCores = cpuCores;
	}

	public String getBootMode() {
		return bootMode;
	}

	public void setBootMode(String bootMode) {
		this.bootMode = bootMode;
	}
	
	

}
