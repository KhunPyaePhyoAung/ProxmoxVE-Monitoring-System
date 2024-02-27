package me.khun.proxmoxnitor.log;

import java.util.LinkedList;
import java.util.List;

public class MonitorLog {

	private List<LogEntity> logs;
	
	public MonitorLog() {
		this.logs = new LinkedList<LogEntity>();
	}

	public List<LogEntity> getLogs() {
		if (logs != null) {
			logs.sort((l1, l2) -> {
				return -l1.getDateTime().compareTo(l2.getDateTime());
			});
		}
		return logs;
	}

	public void setLogs(List<LogEntity> logs) {
		this.logs = logs;
	}
	
	
}
