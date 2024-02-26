package me.khun.proxmoxnitor.application;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.corsinvest.proxmoxve.api.PveClient;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.pve.PveStatus;
import me.khun.proxmoxnitor.service.MonitorService;
import me.khun.proxmoxnitor.service.impl.Cv4PveMonitorServiceImpl;

public class Test {
	public static void main(String[] args) {
		MonitorConfiguration config = new MonitorConfiguration();
		config.setHostname("192.168.11.237");
		config.setPort(8006);
		MonitorService monitorService = new Cv4PveMonitorServiceImpl(config);
		monitorService.loginWithUsernameAndPassword("root", "jicacbmproject", "pam");
		List<String> nodeNames = monitorService.getNodeNameList();
		System.out.println(nodeNames);
		
		PveStatus status = monitorService.getNode("dev").getStatus();
		System.out.println(status);
		
		PveClient pve = new PveClient("192.168.11.237", 8006);
		JSONObject response = pve.get("/access/domains", null).getResponse();
		JSONArray domains = response.getJSONArray("data");
		for (int i = 0; i < domains.length(); i++) {
			JSONObject domain = domains.getJSONObject(i);
			System.out.println(String.format("%s -> %s", domain.getString("realm"), domain.getString("comment")));
		}
	}
}
