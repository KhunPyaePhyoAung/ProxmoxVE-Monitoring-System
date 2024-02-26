package me.khun.proxmoxnitor.view;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.khun.proxmoxnitor.application.ProxmoxnitorApplication;
import me.khun.proxmoxnitor.controller.MonitorConfigurationWindowController;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public class MonitorConfigurationWindow extends Stage {
	
	private MonitorConfiguration config;
	
	private MonitorConfigurationWindowController controller;

	public MonitorConfigurationWindow(MonitorConfiguration config) {
		this.config = config;
		this.setTitle("Monitor Configuration");
		this.setResizable(false);
		this.initModality(Modality.WINDOW_MODAL);
		this.getIcons().add(ProxmoxnitorApplication.getIconImage());
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monitor_config.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.controller.setConfiguration(this.config);
			this.setScene(new Scene(view));;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setOnSaveSuccess(Runnable onSaveSuccess) {
		this.controller.setOnSaveSuccess(onSaveSuccess);
	}
}
