package me.khun.proxmoxnitor.view;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import me.khun.proxmoxnitor.controller.MonitorConfigurationCardController;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public class MonitorConfigurationCard extends Pane {

	private MonitorConfiguration config;
	private Runnable onStart;
	private Runnable onEdit;
	private Runnable onDelete;
	private Runnable onEditNotification;
	private MonitorConfigurationCardController controller;

	public MonitorConfigurationCard(MonitorConfiguration config) {
		this.config = config;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/monitor_config_card.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.controller.setConfiguration(config);
			this.getChildren().add(view);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public MonitorConfiguration getConfig() {
		return config;
	}

	public void setConfig(MonitorConfiguration config) {
		this.config = config;
	}

	public void setOnStart(Runnable onStart) {
		this.onStart = onStart;
		this.controller.setOnStart(onStart);
	}

	public void setOnEdit(Runnable onEdit) {
		this.onEdit = onEdit;
		this.controller.setOnEdit(onEdit);
	}

	public void setOnDelete(Runnable onDelete) {
		this.onDelete = onDelete;
		this.controller.setOnDelete(onDelete);
	}
	
	public void setOnEditNotification(Runnable onEditNotification) {
		this.onEditNotification = onEditNotification;
		this.controller.setOnEditNotification(onEditNotification);
	}

	public Runnable getOnStart() {
		return onStart;
	}

	public Runnable getOnEdit() {
		return onEdit;
	}

	public Runnable getOnDelete() {
		return onDelete;
	}
	
	public Runnable getOnEditNotification() {
		return onEditNotification;
	}

}
