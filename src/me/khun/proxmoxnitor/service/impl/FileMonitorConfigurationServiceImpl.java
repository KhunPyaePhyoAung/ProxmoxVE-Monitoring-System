package me.khun.proxmoxnitor.service.impl;

import java.util.List;

import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.repo.MonitorConfigurationRepo;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;

public class FileMonitorConfigurationServiceImpl implements MonitorConfigurationService {

	private MonitorConfigurationRepo monitorConfigurationRepo;
	
	public FileMonitorConfigurationServiceImpl() {
		this.monitorConfigurationRepo = Dependencies.getMonitorConfigurationRepo();
	}
	
	@Override
	public MonitorConfiguration save(MonitorConfiguration config) {
		if (config.getId() == null || config.getId().isEmpty()) {
			return this.monitorConfigurationRepo.create(config);
		}
		return this.monitorConfigurationRepo.update(config);
	}

	@Override
	public List<MonitorConfiguration> findAll() {
		List<MonitorConfiguration> monitorConfigurations = this.monitorConfigurationRepo.findAll();
		monitorConfigurations.sort((a, b) -> {
			return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
		});
		return monitorConfigurations;
	}

	@Override
	public boolean deleteById(String id) {
		return this.monitorConfigurationRepo.deleteById(id);
	}

	@Override
	public MonitorConfiguration findById(String id) {
		return this.monitorConfigurationRepo.findById(id);
	}

}
