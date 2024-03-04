package me.khun.proxmoxnitor.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import me.khun.proxmoxnitor.application.ProxmoxnitorApplication;
import me.khun.proxmoxnitor.entiry.EmailNotificationConfiguration;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.ProxmoxConfigurationNotFoundException;
import me.khun.proxmoxnitor.service.EmailNotificationService;
import me.khun.text.StringTemplate;

public class GmailNotificationServiceImpl implements EmailNotificationService {
	
	private String author;
	private String username;
	private String from;
	private String password;
	private Authenticator authenticator;
	private Properties properties;
	private MonitorConfiguration config;
	private String groupName;
	
	public GmailNotificationServiceImpl() {
		
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream("/config/gmail_stmp.properties"));
			properties.setProperty("mail.smtp.host", "smtp.gmail.com");
			properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
			
			author = properties.getProperty("author");
			username = properties.getProperty("username");
			from = properties.getProperty("from");
			password = properties.getProperty("password");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		authenticator = new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
			    if ((username != null) && (username.length() > 0) && (password != null) 
			      && (password.length () > 0)) {

			        return new PasswordAuthentication(username, password);
			    }

			    return null;
			}
		};
	}

	@Override
	public void setConfiguration(MonitorConfiguration config) {
		this.config = config;
		EmailNotificationConfiguration noti = config.getEmailNotification();
		groupName = noti == null ? null : noti.getGroupName();
		groupName = groupName == null || groupName.trim().isEmpty() ? ProxmoxnitorApplication.getDefaultGroupName() : groupName;
	}

	@Override
	public void sendServerDownNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/server_down_email.txt"));
		
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Server Down Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}

	@Override
	public void sendServerRecoveryNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/server_recovery_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Server Recovery Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}
	
	

	@Override
	public void sendNodeOfflineNotification(String node) throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/server_node_down_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("nodeName", node);
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Server Node Offline Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}

	@Override
	public void sendNodeRecoveryNotification(String node) throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/server_node_recovery_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("nodeName", node);
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Server Node Recovery Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}

	@Override
	public void sendSessionTimeoutNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/session_timeout_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Monitor Session Timeout Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}

	@Override
	public void sendSessionRecoveryNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/session_recovery_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Proxmox Session Refresh Notification";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}
	

	@Override
	public void sendNoNetworkNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/no_network_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Loss of Network Connection to Proxmox Server";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}
	
	@Override
	public void sendNetworkRecoveryNotification() throws ProxmoxConfigurationNotFoundException {
		if (config == null) {
			throw new ProxmoxConfigurationNotFoundException("Configuration not found.");
		}
		
		StringTemplate template = new StringTemplate(getClass().getResourceAsStream("/template/network_recovery_email.txt"));
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("serverName", config.getName());
		params.put("author", author);
		params.put("groupName", groupName);
		params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy (E) hh:mm a")));
		
		String subject = "Network Connectivity Restored for Proxmox Server";
		String content = template.bind(params);
		
		sendEmail(subject, content);
	}
	
	private void sendEmail(String subject, String content) {
		EmailNotificationConfiguration noti = config.getEmailNotification();
		if (noti == null || !noti.isEnabled() || noti.getRecipients() == null || noti.getRecipients().isEmpty()) {
			return;
		}
		Set<String> recipients = config.getEmailNotification().getRecipients();
		Session session=Session.getDefaultInstance(properties, authenticator);
		
		MimeMessage message=new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress(from, author));
			message.addRecipients(Message.RecipientType.TO, String.join(",", recipients));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	


}
