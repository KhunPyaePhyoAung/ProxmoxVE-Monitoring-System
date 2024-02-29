package me.khun.proxmoxnitor.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.khun.proxmoxnitor.controller.MonitorLoginWindowController;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.service.MonitorService;

public class MonitorLoginWindow extends Stage {
	
	private MonitorConfiguration config;
	private MonitorLoginWindowController controller;

	public MonitorLoginWindow(MonitorConfiguration config) {
		this.config = config;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.controller.setMonitorConfiguration(this.config);
			this.setScene(new Scene(view));
			this.setResizable(false);
			this.initModality(Modality.WINDOW_MODAL);
			this.initStyle(StageStyle.UNDECORATED);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setOnLoginSuccess(Runnable onLoginSuccess) {
		controller.setOnLoginSuccess(onLoginSuccess);
	}
	
	public MonitorService getMonitorService() {
		return controller.getMonitorService();
	}
}
