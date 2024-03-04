package me.khun.proxmoxnitor.service;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;

public interface EmailNotificationService {

	void setConfiguration(MonitorConfiguration config);
	
	void sendServerDownNotification() throws ProxmoxConfigurationNotFoundException;
	
	void sendServerRecoveryNotification() throws ProxmoxConfigurationNotFoundException;
	
	void sendNodeOfflineNotification(String node) throws ProxmoxConfigurationNotFoundException;
	
	void sendNodeRecoveryNotification(String node) throws ProxmoxConfigurationNotFoundException;
	
	void sendSessionTimeoutNotification() throws ProxmoxConfigurationNotFoundException;
	
	void sendSessionRecoveryNotification() throws ProxmoxConfigurationNotFoundException;
	
	void sendNoNetworkNotification()  throws ProxmoxConfigurationNotFoundException;
	
	void sendNetworkRecoveryNotification()  throws ProxmoxConfigurationNotFoundException;
}
