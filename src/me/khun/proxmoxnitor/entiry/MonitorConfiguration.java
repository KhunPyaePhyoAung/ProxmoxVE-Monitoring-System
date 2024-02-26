package me.khun.proxmoxnitor.entiry;

public class MonitorConfiguration {

	private String id;
	private String name;
	private String hostname;
	private Integer port;
	private EmailNotificationConfiguration emailNotification;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public EmailNotificationConfiguration getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(EmailNotificationConfiguration emailNotification) {
		this.emailNotification = emailNotification;
	}
	
	

}
