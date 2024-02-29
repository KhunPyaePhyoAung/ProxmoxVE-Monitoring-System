package me.khun.proxmoxnitor.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.khun.proxmoxnitor.application.ProxmoxnitorApplication;
import me.khun.proxmoxnitor.controller.MonitorController;
import me.khun.proxmoxnitor.service.MonitorService;

public class MonitorWindow extends Stage {
	
	private MonitorController controller;

	public MonitorWindow(MonitorService monitorService) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monitor.fxml"));
			Parent view = loader.load();
			controller = loader.getController();
			controller.setService(monitorService);
			this.setScene(new Scene(view));
			this.initModality(Modality.WINDOW_MODAL);
			this.initStyle(StageStyle.DECORATED);
			this.setTitle("Proxmoxnitor");
			this.getIcons().add(ProxmoxnitorApplication.getIconImage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
