package me.khun.proxmoxnitor.service;

import java.time.LocalDateTime;
import java.util.List;

import me.khun.proxmoxnitor.dto.PveResourceDto;
import me.khun.proxmoxnitor.dto.PveRrdDataDto;
import me.khun.proxmoxnitor.dto.PveStatusDto;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.ProxmoxAuthenticationException;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;
import me.khun.proxmoxnitor.exception.ProxmoxHostNotFoundException;
import me.khun.proxmoxnitor.exception.ProxmoxNodeNotFoundException;
import me.khun.proxmoxnitor.pve.PveNode;
import me.khun.proxmoxnitor.pve.PveRealm;
import me.khun.proxmoxnitor.pve.PveRrdDataType;

public interface MonitorService {

	void setConfiguration(MonitorConfiguration config);
	
	MonitorConfiguration getConfiguration();
	
	String loginWithUsernameAndPassword(String username, String password, String realm)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException;
	
	LocalDateTime getLastLoginTime();
	
	void refreshTicket()
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException;
	
	List<String> getNodeNameList()
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException;
	
	PveNode getNode(String name)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException;
	
	PveStatusDto getStatus(String name)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException;
	
	List<PveRealm> getRealms()
			throws ProxmoxConfigurationNotFoundException,
			   ProxmoxHostNotFoundException;
	
	List<PveRrdDataDto> getRrdData(String nodeName, PveRrdDataType type)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException;
	
	List<PveResourceDto> getResources(String nodeName)
			throws ProxmoxConfigurationNotFoundException,
			   ProxmoxHostNotFoundException,
			   ProxmoxAuthenticationException,
			   ProxmoxNodeNotFoundException;
}
