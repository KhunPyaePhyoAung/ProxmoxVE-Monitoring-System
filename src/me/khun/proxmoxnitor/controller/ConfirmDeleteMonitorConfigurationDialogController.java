package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;

public class ConfirmDeleteMonitorConfigurationDialogController implements Initializable {
	
	@FXML
	private Label contentLabel;
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private VBox confirmView;
	
	private MonitorConfiguration config;
	private String content;
	private Runnable onDeleteSuccess;
	private Stage stage;
	private MonitorConfigurationService monitorConfigurationService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.monitorConfigurationService = Dependencies.getMonitorConfigurationService();
		Platform.runLater(this::setup);
	}
	
	public void setMonitorConfiguration(MonitorConfiguration config) {
		this.config = config;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setOnDeleteSuccess(Runnable onDeleteSuccess) {
		this.onDeleteSuccess = onDeleteSuccess;
	}
	
	@FXML
	private void confirm() {
		showLoadingView();
		Task<Boolean> deleteTask = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				Thread.sleep(2000);
				return monitorConfigurationService.deleteById(config.getId());
			}
		};
		
		deleteTask.setOnSucceeded(e -> {
			showConfirmView();
			runOnDeleteSuccess();
			close();
		});
		
		Thread deleteThred = new Thread(deleteTask);
		deleteThred.start();
	}
	
	@FXML
	private void deny() {
		close();
	}
	
	private void close() {
		this.stage.close();
	}
	
	private void runOnDeleteSuccess() {
		Platform.runLater(this.onDeleteSuccess::run);
	}
	
	private void showLoadingView() {
		Platform.runLater(() -> {
			this.loadingView.toFront();
			this.confirmView.setDisable(true);
		});
	}
	
	private void showConfirmView() {
		Platform.runLater(() -> {
			this.confirmView.toFront();
			this.loadingView.setDisable(false);
		});
	}
	
	private void setup() {
		this.contentLabel.setText(content == null ? String.format("Are you sure to delete %s?", this.config.getName()) : content);
		this.stage = (Stage)contentLabel.getScene().getWindow();
	}

}
