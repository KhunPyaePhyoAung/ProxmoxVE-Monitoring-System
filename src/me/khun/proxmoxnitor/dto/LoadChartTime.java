package me.khun.proxmoxnitor.dto;

public class LoadChartTime {
	
	private String time;

	public LoadChartTime(String time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public String toString() {
		return time;
	}
	
	
}
