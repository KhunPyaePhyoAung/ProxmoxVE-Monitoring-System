package me.khun.proxmoxnitor.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;

import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;
import me.khun.util.JsonUtil;

public class JsonFileLogger implements Logger {
	
	private MonitorConfiguration config;

	@Override
	public void setMonitorConfiguration(MonitorConfiguration config) {
		this.config = config;
	}

	@Override
	public void log(LogEntity log) throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		try {
			MonitorLog monitorLog = getLog();
			monitorLog.getLogs().add(log);
			String content = JsonUtil.objectToJson(monitorLog);
			Path path = Paths.get(System.getProperty("user.home"), ".Proxmoxnitor", "Logs", config.getId() + ".json");
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
	}

	@Override
	public MonitorLog getLog() throws ProxmoxConfigurationNotFoundException {
		Path path = Paths.get(System.getProperty("user.home"), ".Proxmoxnitor", "Logs", config.getId() + ".json");
		
		if (!Files.exists(path)) {
			return new MonitorLog();
		}
		
		try {
			Stream<String> lines;
			lines = Files.lines(path);
			String data = lines.collect(Collectors.joining("\n"));
			MonitorLog monitorLog = JsonUtil.JsonToObject(data, MonitorLog.class);
		    lines.close();
		    return monitorLog;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
