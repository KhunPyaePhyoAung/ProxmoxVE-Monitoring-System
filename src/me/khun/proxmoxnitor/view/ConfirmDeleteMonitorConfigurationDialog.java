package me.khun.proxmoxnitor.view;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.khun.proxmoxnitor.controller.ConfirmDeleteMonitorConfigurationDialogController;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public class ConfirmDeleteMonitorConfigurationDialog extends Stage {
	
	private MonitorConfiguration config;
	private String content;
	private Runnable onDeleteSuccess;
	private ConfirmDeleteMonitorConfigurationDialogController controller;
	
	public ConfirmDeleteMonitorConfigurationDialog(MonitorConfiguration config) {
		this(config, null, null);
	}
	
	public ConfirmDeleteMonitorConfigurationDialog(MonitorConfiguration config, String content) {
		this(config, content, null);
	}

	public ConfirmDeleteMonitorConfigurationDialog(MonitorConfiguration config, String content, Runnable onDeleteSuccess) {
		this.content = content;
		this.onDeleteSuccess = onDeleteSuccess;
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/confirm_delete_monitor_config_dialog.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.controller.setContent(content);
			this.controller.setMonitorConfiguration(config);
			this.setScene(new Scene(view));
			this.initModality(Modality.WINDOW_MODAL);
			this.initStyle(StageStyle.UNDECORATED);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMonitorConfiguration(MonitorConfiguration config) {
		this.config = config;
		this.controller.setMonitorConfiguration(this.config);
	}
	
	public void setContent(String content) {
		this.content = content;
		this.controller.setContent(this.content);
	}
	
	public void setOnDeleteSuccess(Runnable onDeleteSuccess) {
		this.onDeleteSuccess = onDeleteSuccess;
		this.controller.setOnDeleteSuccess(this.onDeleteSuccess);
	}
	
}
