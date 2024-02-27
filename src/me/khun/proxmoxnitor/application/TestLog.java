package me.khun.proxmoxnitor.application;

import java.time.LocalDateTime;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.log.JsonFileLogger;
import me.khun.proxmoxnitor.log.LogEntity;
import me.khun.proxmoxnitor.log.Logger;

public class TestLog {

	public static void main(String[] args) {
		MonitorConfiguration config = Dependencies.getMonitorConfigurationService().findById("946d7a8d-6ba9-4b61-82b9-a30281b9b68b");
		
		LogEntity log1 = new LogEntity();
		log1.setHeader("Header1");
		log1.setContent("Content1");
		log1.setDateTime(LocalDateTime.now());
		
		LogEntity log2 = new LogEntity();
		log2.setHeader("Header1");
		log2.setContent("Content1");
		log2.setDateTime(LocalDateTime.now());
		
		Logger logger = new JsonFileLogger();
		logger.setMonitorConfiguration(config);
		logger.log(log1);
		logger.log(log2);
	}
}
