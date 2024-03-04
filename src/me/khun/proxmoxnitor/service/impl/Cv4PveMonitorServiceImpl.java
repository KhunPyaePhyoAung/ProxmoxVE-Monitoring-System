package me.khun.proxmoxnitor.service.impl;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.corsinvest.proxmoxve.api.PveClient;
import it.corsinvest.proxmoxve.api.PveClient.PVENodes.PVENodeItem;
import it.corsinvest.proxmoxve.api.PveExceptionAuthentication;
import me.khun.proxmoxnitor.dto.PveResourceDto;
import me.khun.proxmoxnitor.dto.PveRrdDataDto;
import me.khun.proxmoxnitor.dto.PveStatusDto;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.NoNetworkException;
import me.khun.proxmoxnitor.exception.ProxmoxAuthenticationException;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;
import me.khun.proxmoxnitor.exception.ProxmoxHostNotFoundException;
import me.khun.proxmoxnitor.exception.ProxmoxNodeNotFoundException;
import me.khun.proxmoxnitor.pve.PveNode;
import me.khun.proxmoxnitor.pve.PveRealm;
import me.khun.proxmoxnitor.pve.PveResource;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceStatus;
import me.khun.proxmoxnitor.pve.PveRrdData;
import me.khun.proxmoxnitor.pve.PveRrdDataType;
import me.khun.proxmoxnitor.pve.PveStatus;
import me.khun.proxmoxnitor.service.MonitorService;

public class Cv4PveMonitorServiceImpl implements MonitorService {
	
	private MonitorConfiguration config;
	
	private PveClient pve;
	
	private String ticket;
	
	private LocalDateTime lastLoginTime;
	
	private String username;
	
	private String realm;
	
	public Cv4PveMonitorServiceImpl() {
	}
	
	public Cv4PveMonitorServiceImpl(MonitorConfiguration config) {
		setConfiguration(config);
	}

	@Override
	public void setConfiguration(MonitorConfiguration config) {
		this.config = config;
		this.pve = new PveClient(config.getHostname(), config.getPort());
	}

	@Override
	public MonitorConfiguration getConfiguration() {
		return config;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String loginWithUsernameAndPassword(String username, String password, String realm)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		synchronized (pve) {
			try {
				pve.login(username, URLEncoder.encode(password), realm);
				int statusCode = pve.getLastResult().getStatusCode();

				switch (statusCode) {
					case 0:
						throw new NoNetworkException("No network connection.");
					case 401:
						throw new ProxmoxAuthenticationException("Login failed.");
					case 404:
						throw new ProxmoxHostNotFoundException("Host not found.");
				}
				
				this.username = username;
				this.realm = realm;
				
				lastLoginTime = LocalDateTime.now();
				ticket = pve.getLastResult().getResponse().getJSONObject("data").getString("ticket");
				return ticket;
			} catch (JSONException | PveExceptionAuthentication e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	@Override
	public LocalDateTime getLastLoginTime() {
		return lastLoginTime;
	}
	
	@Override
	public void refreshTicket()
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException {
		
		if (ticket == null) {
			throw new ProxmoxAuthenticationException("Unauthenticated.");
		}
		
		try {
			loginWithUsernameAndPassword(username, ticket, realm);
		} catch (ProxmoxAuthenticationException e) {
			throw new ProxmoxAuthenticationException("Ticket expired");
		}
	}

	@Override
	public List<String> getNodeNameList()
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		List<String> nodeNames = new LinkedList<>();
		JSONObject response = pve.getNodes().index().getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
			case 401:
				throw new ProxmoxAuthenticationException("Unauthenticated.");
		}
		
		JSONArray nodes = response.getJSONArray("data");
		
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = (JSONObject) nodes.get(i);
			String name = node.getString("node");
			nodeNames.add(name);
		}
		
		return nodeNames;
	}

	@Override
	public PveNode getNode(String name)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		PVENodeItem node = pve.getNodes().get(name);
		JSONObject statusResponse = node.getStatus().status().getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
			case 401:
				throw new ProxmoxAuthenticationException("Unauthenticated.");
			case 500:
				throw new ProxmoxNodeNotFoundException(String.format("Node %s not found.", name));
		}
		
		JSONObject data = statusResponse.getJSONObject("data");
		
		PveNode pveNode = new PveNode();
		PveStatus pveStatus = new PveStatus();
		pveNode.setName(name);
		pveNode.setStatus(pveStatus);
		
		
		long uptime = data.getLong("uptime");
		float cpuLoad = data.getFloat("cpu");
		JSONObject currentKernel = data.getJSONObject("current-kernel");
		String systemName = currentKernel.getString("sysname");
		String kernelRelease = currentKernel.getString("release");
		String kernelVersion = currentKernel.getString("version");
		String pveVersion = data.getString("pveversion");
		JSONArray loadAverage = data.getJSONArray("loadavg");
		JSONObject ksm = data.getJSONObject("ksm");
		JSONObject rootfs = data.getJSONObject("rootfs");
		JSONObject memory = data.getJSONObject("memory");
		JSONObject cpuInfo = data.getJSONObject("cpuinfo");
		JSONObject bootInfo = data.getJSONObject("boot-info");
		JSONObject swap = data.getJSONObject("swap");
		float ioDelay = data.getFloat("wait");
		
		pveStatus.setUptime(uptime);
		pveStatus.setCpuUsage(cpuLoad);
		pveStatus.setLoadAverage(new float[] {
					Float.parseFloat(loadAverage.getString(0)),
					Float.parseFloat(loadAverage.getString(1)),
					Float.parseFloat(loadAverage.getString(2))
				});
		pveStatus.setKsm(ksm.getLong("shared"));
		pveStatus.setTotalHdSpace(rootfs.getLong("total"));
		pveStatus.setUsedHdSpace(rootfs.getLong("used"));
		pveStatus.setFreeHdSpace(rootfs.getLong("free"));
		pveStatus.setTotalMemory(memory.getLong("total"));
		pveStatus.setUsedMemory(memory.getLong("used"));
		pveStatus.setFreeMemory(memory.getLong("free"));
		pveStatus.setTotalSwap(swap.getLong("total"));
		pveStatus.setUsedSwap(swap.getLong("used"));
		pveStatus.setFreeSwap(swap.getLong("free"));
		pveStatus.setKernelVersion(String.format("%s %s %s", systemName, kernelRelease, kernelVersion.substring(kernelVersion.indexOf("("))));
		pveStatus.setCpus(cpuInfo.getInt("cpus"));
		pveStatus.setPveVersion(pveVersion);
		pveStatus.setIoDelay(ioDelay);
		pveStatus.setCpuModel(cpuInfo.getString("model"));
		pveStatus.setCpuHz(cpuInfo.getFloat("mhz") / 1000);
		pveStatus.setCpuCores(cpuInfo.getInt("cores"));
		pveStatus.setCpuSockets(cpuInfo.getInt("sockets"));
		pveStatus.setBootMode(bootInfo.getString("mode"));
		
		return pveNode;
	}

	@Override
	public PveStatusDto getStatus(String name)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		PVENodeItem node = pve.getNodes().get(name);
		JSONObject statusResponse = node.getStatus().status().getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
			case 401:
				throw new ProxmoxAuthenticationException("Unauthenticated.");
			case 500:
				throw new ProxmoxNodeNotFoundException(String.format("Node %s not found.", name));
		}
		
		JSONObject data = statusResponse.getJSONObject("data");
		
		PveStatus pveStatus = new PveStatus();
		
		long uptime = data.getLong("uptime");
		float cpuLoad = data.getFloat("cpu");
		JSONObject currentKernel = data.getJSONObject("current-kernel");
		String systemName = currentKernel.getString("sysname");
		String kernelRelease = currentKernel.getString("release");
		String kernelVersion = currentKernel.getString("version");
		String pveVersion = data.getString("pveversion");
		JSONArray loadAverage = data.getJSONArray("loadavg");
		JSONObject ksm = data.getJSONObject("ksm");
		JSONObject rootfs = data.getJSONObject("rootfs");
		JSONObject memory = data.getJSONObject("memory");
		JSONObject cpuInfo = data.getJSONObject("cpuinfo");
		JSONObject bootInfo = data.getJSONObject("boot-info");
		JSONObject swap = data.getJSONObject("swap");
		float ioDelay = data.getFloat("wait");
		
		pveStatus.setUptime(uptime);
		pveStatus.setCpuUsage(cpuLoad);
		pveStatus.setLoadAverage(new float[] {
					Float.parseFloat(loadAverage.getString(0)),
					Float.parseFloat(loadAverage.getString(1)),
					Float.parseFloat(loadAverage.getString(2))
				});
		pveStatus.setKsm(ksm.getLong("shared"));
		pveStatus.setTotalHdSpace(rootfs.getLong("total"));
		pveStatus.setUsedHdSpace(rootfs.getLong("used"));
		pveStatus.setFreeHdSpace(rootfs.getLong("free"));
		pveStatus.setTotalMemory(memory.getLong("total"));
		pveStatus.setUsedMemory(memory.getLong("used"));
		pveStatus.setFreeMemory(memory.getLong("free"));
		pveStatus.setTotalSwap(swap.getLong("total"));
		pveStatus.setUsedSwap(swap.getLong("used"));
		pveStatus.setFreeSwap(swap.getLong("free"));
		pveStatus.setCpus(cpuInfo.getInt("cpus"));
		pveStatus.setKernelVersion(String.format("Test%s %s %s", systemName, kernelRelease, kernelVersion.substring(kernelVersion.indexOf("("))));
		pveStatus.setPveVersion(pveVersion);
		pveStatus.setIoDelay(ioDelay);
		pveStatus.setCpuModel(cpuInfo.getString("model"));
		pveStatus.setCpuHz(cpuInfo.getFloat("mhz") / 1000);
		pveStatus.setCpuCores(cpuInfo.getInt("cores"));
		pveStatus.setCpuSockets(cpuInfo.getInt("sockets"));
		pveStatus.setBootMode(bootInfo.getString("mode"));
		PveStatusDto pveStatusDto = new PveStatusDto(pveStatus);
		return pveStatusDto;
	}

	@Override
	public List<PveRealm> getRealms()
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		JSONObject realmResponse = pve.getAccess().getDomains().index().getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
		}
		
		List<PveRealm> realms = new LinkedList<>();
		JSONArray realmArray = realmResponse.getJSONArray("data");
		for (int i = 0; i < realmArray.length(); i++) {
			JSONObject realmObject = realmArray.getJSONObject(i);
			String realmId = realmObject.getString("realm");
			String realmType = realmObject.getString("type");
			String realmComment = realmObject.getString("comment");
			PveRealm realm = new PveRealm();
			realm.setId(realmId);
			realm.setType(realmType);
			realm.setComment(realmComment);
			realms.add(realm);
		}
		
		realms.sort((a, b) -> a.getId().compareTo(b.getId()));
		return realms;
	}

	@Override
	public List<PveRrdDataDto> getRrdData(String nodeName, PveRrdDataType type)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		PVENodeItem node = pve.getNodes().get(nodeName);
		JSONObject rrdDataResponse = node.getRrddata().rrddata(type.getTimeFrame(), type.getCf()).getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
			case 401:
				throw new ProxmoxAuthenticationException("Unauthenticated.");
			case 500:
				throw new ProxmoxNodeNotFoundException(String.format("Node %s not found.", nodeName));
		}
		
		JSONArray data = rrdDataResponse.getJSONArray("data");
		
		List<PveRrdDataDto> rrdList = new ArrayList<PveRrdDataDto>(data.length());
		
		for (int i = 0; i < data.length(); i++) {
			PveRrdData rrd = new PveRrdData();
			rrd.setType(type);
			JSONObject dataI = data.getJSONObject(i);
			try {
				rrd.setCpuUsage(dataI.getFloat("cpu"));
				rrd.setIoDelay(dataI.getFloat("iowait"));
			} catch (JSONException e) {
				rrd.setCpuUsage(0);
				rrd.setIoDelay(0);
			}
			
			rrd.setTime(dataI.getLong("time"));
			
			rrdList.add(new PveRrdDataDto(rrd));
		}
		
		return rrdList;
	}

	@Override
	public List<PveResourceDto> getResources(String nodeName)
			throws ProxmoxConfigurationNotFoundException,
				   ProxmoxHostNotFoundException,
				   ProxmoxAuthenticationException,
				   ProxmoxNodeNotFoundException {
		
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		PVENodeItem node = pve.getNodes().get(nodeName);
		
		JSONObject containerResponse = node.getLxc().vmlist().getResponse();
		JSONObject vmResponse = node.getQemu().vmlist(false).getResponse();
		
		int statusCode = pve.getLastResult().getStatusCode();
		
		switch (statusCode) {
			case 0:
				throw new NoNetworkException("No network connection.");
			case 404:
				throw new ProxmoxHostNotFoundException("Host not found.");
			case 401:
				throw new ProxmoxAuthenticationException("Unauthenticated.");
			case 500:
				throw new ProxmoxNodeNotFoundException(String.format("Node %s not found.", nodeName));
		}
		
		JSONArray containerArray = containerResponse.getJSONArray("data");
		JSONArray vmArray = vmResponse.getJSONArray("data");
		
		List<PveResourceDto> resourceDtos = new ArrayList<PveResourceDto>(containerArray.length() + vmArray.length());
		
		for (int i = 0; i < containerArray.length(); i++) {
			JSONObject container = containerArray.getJSONObject(i);
			PveResource resource = new PveResource();
			resource.setVmId(container.getInt("vmid"));
			resource.setName(container.getString("name"));
			resource.setStatus(PveResourceStatus.valueOf(container.getString("status").toUpperCase()));
			resourceDtos.add(new PveResourceDto(resource));
		}
		
		for (int i = 0; i < vmArray.length(); i++) {
			JSONObject vm = vmArray.getJSONObject(i);
			PveResource resource = new PveResource();
			resource.setVmId(vm.getInt("vmid"));
			resource.setName(vm.getString("name"));
			resource.setStatus(PveResourceStatus.valueOf(vm.getString("status").toUpperCase()));
			resourceDtos.add(new PveResourceDto(resource));
		}
		
		resourceDtos.sort((a, b) -> a.getVmId() - b.getVmId());
		
		return resourceDtos;
	}
}
