package me.khun.proxmoxnitor.log;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;

public interface Logger {

	void setMonitorConfiguration(MonitorConfiguration config);
	
	void log(LogEntity log)
			throws ProxmoxConfigurationNotFoundException;
	
	MonitorLog getLog()
			throws ProxmoxConfigurationNotFoundException;
}
