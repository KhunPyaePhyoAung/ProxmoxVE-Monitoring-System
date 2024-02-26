package me.khun.proxmoxnitor.repo.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.repo.MonitorConfigurationRepo;
import me.khun.util.JsonUtil;

public class FileMonitorConfigurationRepoImpl implements MonitorConfigurationRepo {

	@Override
	public MonitorConfiguration create(MonitorConfiguration config) {
		
		try {
			config.setId(UUID.randomUUID().toString());
			String content = JsonUtil.objectToJson(config);
			Path path = Paths.get(System.getProperty("user.home"), "Proxmoxnitor", "MonitorConfigurations", config.getId() + ".json");
			Files.write(path, content.getBytes(),
	                 StandardOpenOption.CREATE,
	                 StandardOpenOption.TRUNCATE_EXISTING);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
	
	@Override
	public MonitorConfiguration update(MonitorConfiguration config) {
		try {
			String content = JsonUtil.objectToJson(config);
			Path path = Paths.get(System.getProperty("user.home"), "Proxmoxnitor", "MonitorConfigurations", config.getId() + ".json");
			Files.write(path, content.getBytes(),
	                 StandardOpenOption.CREATE,
	                 StandardOpenOption.TRUNCATE_EXISTING);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	@Override
	public List<MonitorConfiguration> findAll() {
		List<MonitorConfiguration> monitorConfigurations = new LinkedList<>();
		Path dir = Paths.get(System.getProperty("user.home"), "Proxmoxnitor", "MonitorConfigurations");
		try {
			Files.walk(dir)
					.filter(Files::isRegularFile)
					.forEach(path -> {
						
						try {
							Stream<String> lines;
							lines = Files.lines(path);
							String data = lines.collect(Collectors.joining("\n"));
						    MonitorConfiguration config = JsonUtil.JsonToObject(data, MonitorConfiguration.class);
						    monitorConfigurations.add(config);
						    lines.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
			 });
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return monitorConfigurations;
	}

	@Override
	public boolean deleteById(String id) {
		
		try {
			Path path = Paths.get(System.getProperty("user.home"), "Proxmoxnitor", "MonitorConfigurations", id + ".json");
			return Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MonitorConfiguration findById(String id) {
		Path path = Paths.get(System.getProperty("user.home"), "Proxmoxnitor", "MonitorConfigurations", id + ".json");
		try {
			Stream<String> lines;
			lines = Files.lines(path);
			String data = lines.collect(Collectors.joining("\n"));
		    MonitorConfiguration config = JsonUtil.JsonToObject(data, MonitorConfiguration.class);
		    lines.close();
		    return config;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	

}
