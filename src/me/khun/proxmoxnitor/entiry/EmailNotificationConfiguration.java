package me.khun.proxmoxnitor.entiry;

import java.util.Set;

public class EmailNotificationConfiguration {
	private String groupName;
	private Set<String> recipients;
	private boolean enabled;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
