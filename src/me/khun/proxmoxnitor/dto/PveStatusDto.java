package me.khun.proxmoxnitor.dto;

import java.text.DecimalFormat;

import me.khun.proxmoxnitor.pve.PveStatus;

public class PveStatusDto {
	
	private PveStatus status;
	
	private DecimalFormat decimalFormat;
	
	public PveStatusDto(PveStatus status) {
		this.status = status;
		this.decimalFormat = new DecimalFormat("0.00");
		this.decimalFormat.setMaximumFractionDigits(2);
	}

	public String getUptime(boolean showSecond) {
		return secondsToDhms(status.getUptime());
	}
	
	public float getCpuUsage() {
		return formatDecimal(status.getCpuUsage());
	}
	
	public float getCpuUsageInPercentage() {
		return formatDecimal(status.getCpuUsage() * 100);
	}
	
	public int getCpus() {
		return status.getCpus();
	}
	
	public String getCpuModel() {
		return status.getCpuModel();
	}
	
	public String getCpuHzFormatted() {
		return formatHz(status.getCpuHz());
	}
	
	public int getCpuSockets() {
		return status.getCpuSockets();
	}
	
	public int getCpuCores() {
		return status.getCpuCores();
	}
	
	public String getBootMode() {
		return status.getBootMode();
	}
	
	public float[] getLoadAverage() {
		return status.getLoadAverage();
	}
	
	public String getTotalMemoryFormatted() {
		return formatBytes(status.getTotalMemory());
	}
	
	public String getUsedMemoryFormatted() {
		return formatBytes(status.getTotalMemory() - status.getFreeMemory());
	}
	
	public float getUsedMemoryInPercentage() {
		return formatDecimal((float)(status.getUsedMemory() * 100) / status.getTotalMemory());
	}
	
	public float getUsedMemory() {
		return formatDecimal((float)(status.getUsedMemory()));
	}
	
	public String getFreeMemoryFormatted() {
		return formatBytes(status.getFreeMemory());
	}
	
	public float getFreeMemoryInPercentage() {
		return formatDecimal((float) (status.getFreeMemory() * 100) / status.getTotalMemory());
	}
	
	public float getFreeMemory() {
		return formatDecimal((float) (status.getFreeMemory()));
	}
	
	public String getTotalHdSpaceFormatted() {
		return formatBytes(status.getTotalHdSpace());
	}
	
	public String getUsedHdSpaceFormatted() {
		return formatBytes(status.getTotalHdSpace() - status.getFreeHdSpace());
	}
	
	public float getUsedHdSpaceInPercentage() {
		return formatDecimal((float) (status.getUsedHdSpace() * 100) / status.getTotalHdSpace());
	}
	
	public float getUsedHdSpace() {
		return formatDecimal((float) (status.getUsedHdSpace()));
	}
	
	public String getFreeHdSpaceFormatted() {
		return formatBytes(status.getFreeHdSpace());
	}
	
	public float getFreeHdSpaceInPercentage() {
		return formatDecimal((float) (status.getFreeHdSpace() * 100) / status.getTotalHdSpace());
	}
	
	public String getTotalSwapFormatted() {
		return formatBytes(status.getTotalSwap());
	}
	
	public String getUsedSwapFormatted() {
		return formatBytes(status.getTotalSwap() - status.getFreeSwap());
	}
	
	public float getUsedSwapInPercentage() {
		return formatDecimal((float) ((status.getTotalSwap() - status.getFreeSwap()) * 100) / status.getTotalSwap());
	}
	
	public float getUsedSwap() {
		return formatDecimal((float) ((status.getTotalSwap() - status.getFreeSwap())));
	}
	
	public String getFreeSwapFormatted() {
		return formatBytes(status.getFreeSwap());
	}
	
	public float getFreeSwapInPercentage() {
		return formatDecimal((float) (status.getFreeSwap() - status.getTotalSwap()) * 100);
	}
	
	public float getIoDelayInPercentage() {
		return formatDecimal(status.getIoDelay() * 100);
	}
	
	public float getIoDelay() {
		return formatDecimal(status.getIoDelay());
	}
	
	public String getKmsFormatted() {
		return formatBytes(status.getKsm());
	}
	
	public String getKernelVersion() {
		return status.getKernelVersion();
	}
	
	public String getPveVersion() {
		return status.getPveVersion();
	}
	
	private float formatDecimal(float value) {
		return Float.parseFloat(String.format("%.2f", value));
	}
	
	private String formatBytes(long bytes) {
		int k = 1024;
		String[] sizes = new String[] {"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
		int i = (int) Math.floor(Math.log(bytes) / Math.log(k));
		if (i < 0) i = 0;
		float result = (float) (bytes / Math.pow(k, i));
		return String.format(i == 0 ? "%.0f %s" : "%.2f %s", result, sizes[i]);
	}
	
	private String formatHz(double hz) {
		int k = 1024;
		String[] sizes = new String[] {"HZ", "KHZ", "MHZ", "GHZ", "THZ", "PHZ", "EHZ", "ZHZ", "YHZ"};
		int i = (int) Math.floor(Math.log(hz) / Math.log(k));
		if (i < 0) i = 0;
		float result = (float) (hz / Math.pow(k, i));
		return String.format(i == 0 ? "%.0f %s" : "%.2f %s", result, sizes[i]);
	}
	
	private String secondsToDhms(long seconds) {
		int d = (int) Math.floor(seconds / (3600*24));
		int h = (int) Math.floor(seconds % (3600*24) / 3600);
		int m = (int) Math.floor(seconds % 3600 / 60);
		int s = (int) Math.floor(seconds % 60);
		
		return String.format("%d %s %02d:%02d:%02d", d, d > 1 ? "days" : "day", h, m, s);
	}
}
