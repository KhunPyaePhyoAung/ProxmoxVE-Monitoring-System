package me.khun.proxmoxnitor.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.khun.proxmoxnitor.application.ProxmoxnitorApplication;
import me.khun.proxmoxnitor.controller.MonitorNotificationWindowController;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public class MonitorNotificationWindow extends Stage {

	private MonitorNotificationWindowController controller;
	private MonitorConfiguration config;
	
	public MonitorNotificationWindow(MonitorConfiguration config) {
		this.config = config;
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monitor_notification_window.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.setScene(new Scene(view));
			this.setResizable(true);
			this.initModality(Modality.WINDOW_MODAL);
			this.getIcons().add(ProxmoxnitorApplication.getIconImage());
			this.setTitle("Notification Configuration");
			this.centerOnScreen();
			this.controller.setMonitorConfiguration(this.config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setOnSaveSuccess(Runnable onSaveSuccess) {
		this.controller.setOnSaveSuccess(onSaveSuccess);
	}
}
