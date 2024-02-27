package me.khun.proxmoxnitor.application;

import me.khun.proxmoxnitor.log.JsonFileLogger;
import me.khun.proxmoxnitor.log.Logger;
import me.khun.proxmoxnitor.repo.MonitorConfigurationRepo;
import me.khun.proxmoxnitor.repo.impl.FileMonitorConfigurationRepoImpl;
import me.khun.proxmoxnitor.service.EmailNotificationService;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;
import me.khun.proxmoxnitor.service.MonitorService;
import me.khun.proxmoxnitor.service.impl.Cv4PveMonitorServiceImpl;
import me.khun.proxmoxnitor.service.impl.FileMonitorConfigurationServiceImpl;
import me.khun.proxmoxnitor.service.impl.GmailNotificationServiceImpl;

public class Dependencies {
	
	public static MonitorConfigurationService getMonitorConfigurationService() {
		return new FileMonitorConfigurationServiceImpl();
	}

	public static MonitorConfigurationRepo getMonitorConfigurationRepo() {
		return new FileMonitorConfigurationRepoImpl();
	}
	
	public static MonitorService getMonitorService() {
		return new Cv4PveMonitorServiceImpl();
	}
	
	public static EmailNotificationService getEmailNotificationService() {
		return new GmailNotificationServiceImpl();
	}
	
	public static Logger getLogger() {
		return new JsonFileLogger();
	}
}
