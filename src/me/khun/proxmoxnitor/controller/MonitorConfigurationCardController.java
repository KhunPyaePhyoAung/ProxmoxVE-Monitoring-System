package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;

public class MonitorConfigurationCardController implements Initializable {
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private Label hostnameLabel;
	
	private MonitorConfiguration config;
	
	private Runnable onStart;
	
	private Runnable onEdit;
	
	private Runnable onDelete;
	
	private Runnable onEditNotification;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(this::setCardData);
	}
	
	public void setConfiguration(MonitorConfiguration config) {
		this.config = config;
	}
	
	public void setOnStart(Runnable onStart) {
		this.onStart = onStart;
	}
	
	public void setOnEdit(Runnable onDelete) {
		this.onEdit = onDelete;
	}
	
	public void setOnDelete(Runnable onDelete) {
		this.onDelete = onDelete;
	}
	
	public void setOnEditNotification(Runnable onEditNotification) {
		this.onEditNotification = onEditNotification;
	}
	
	@FXML
	private void startMonitor() {
		if (this.onStart != null) {
			Platform.runLater(this.onStart::run);
		}
	}
	
	@FXML
	private void editMonitor() {
		if (this.onEdit != null) {
			Platform.runLater(this.onEdit::run);
		}
	}
	
	@FXML
	private void showEditNotificationWindow() {
		if (this.onEditNotification != null) {
			Platform.runLater(this.onEditNotification::run);
		}
	}
	
	@FXML
	private void deleteMonitor() {
		if (this.onDelete != null) {
			Platform.runLater(this.onDelete::run);
		}
	}
	
	private void setCardData() {
		this.nameLabel.setText(this.config.getName());
		this.hostnameLabel.setText(this.config.getHostname() + ":" + this.config.getPort());
	}

}
