package me.khun.proxmoxnitor.repo;

import java.util.List;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public interface MonitorConfigurationRepo {

	MonitorConfiguration create(MonitorConfiguration config);
	
	MonitorConfiguration update(MonitorConfiguration config);
	
	List<MonitorConfiguration> findAll();
	
	MonitorConfiguration findById(String id);
	
	boolean deleteById(String id);
}
