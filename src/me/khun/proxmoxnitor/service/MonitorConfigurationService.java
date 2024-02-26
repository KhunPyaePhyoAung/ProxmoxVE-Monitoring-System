package me.khun.proxmoxnitor.service;

import java.util.List;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public interface MonitorConfigurationService {

	MonitorConfiguration save(MonitorConfiguration config);
	
	List<MonitorConfiguration> findAll();
	
	MonitorConfiguration findById(String id);
	
	boolean deleteById(String id);
}
