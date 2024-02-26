package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;
import me.khun.proxmoxnitor.view.ConfirmDeleteMonitorConfigurationDialog;
import me.khun.proxmoxnitor.view.MonitorConfigurationCard;
import me.khun.proxmoxnitor.view.MonitorConfigurationWindow;
import me.khun.proxmoxnitor.view.MonitorLoginWindow;
import me.khun.proxmoxnitor.view.MonitorNotificationWindow;
import me.khun.proxmoxnitor.view.MonitorWindow;

public class MainWindowController implements Initializable {
	
	@FXML
	private Button newConfigBtn;;
	
	@FXML
	private Button refreshConfigBtn;
	
	@FXML
	private Label totalConfigurationLabel;
	
	@FXML
	private FlowPane configFlowPane;
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private ScrollPane configScrollView;
	
	private Stage stage;
	
	private MonitorConfigurationService monitorConfigurationService;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.monitorConfigurationService = Dependencies.getMonitorConfigurationService();
		refreshConfigurationList();
		Platform.runLater(this::setStage);
	}
	
	@FXML
	private void refreshConfigurationList() {
		configFlowPane.getChildren().clear();
		totalConfigurationLabel.setText(null);
		showLoading();
		
		Task<List<MonitorConfiguration>> refreshTask = new Task<List<MonitorConfiguration>>() {

			@Override
			protected List<MonitorConfiguration> call() throws Exception {
				Thread.sleep(200);
				List<MonitorConfiguration> configurationList = MainWindowController.this.monitorConfigurationService.findAll();
				return configurationList;
			}
			
		};
		
		refreshTask.setOnSucceeded(e -> {
			List<MonitorConfiguration> configurationList;
			try {
				configurationList = refreshTask.get();
				configurationList.forEach(config -> {
					MonitorConfigurationCard card = new MonitorConfigurationCard(config);
					card.setOnStart(() -> {
						MainWindowController.this.showMonitorLoginWindow(config);
					});
					card.setOnEdit(() -> {
						MainWindowController.this.showMonitorConfigurationForm(config);
					});
					card.setOnEditNotification(() -> {
						MainWindowController.this.showEditNotificationWindow(config);
					});
					card.setOnDelete(() -> {
						MainWindowController.this.showConfirmDeleteMonitorConfigurationDialog(config);
					});
					configFlowPane.getChildren().add(card);
				});
				totalConfigurationLabel.setText(String.format("Total : %d Configuration%s", configurationList.size(), configurationList.size() > 1 ? "s" : ""));
				showConfigurationList();
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		});
		
		Thread refreshThread = new Thread(refreshTask);
		refreshThread.start();
	}

	@FXML
	private void showNewMonitorConfigurationForm(ActionEvent event) {
		showMonitorConfigurationForm(null);
	}
	
	private void showEditNotificationWindow(MonitorConfiguration config) {
		MonitorNotificationWindow window = new MonitorNotificationWindow(config);
		window.initOwner(stage);
		window.setOnSaveSuccess(this::refreshConfigurationList);
		window.show();
	}
	
	private void showMonitorConfigurationForm(MonitorConfiguration config) {
		MonitorConfigurationWindow monitorConfigurationWindow = new MonitorConfigurationWindow(config);
		monitorConfigurationWindow.initOwner(this.stage);
		monitorConfigurationWindow.setOnSaveSuccess(this::refreshConfigurationList);
		monitorConfigurationWindow.show();
	}
	
	private void showConfirmDeleteMonitorConfigurationDialog(MonitorConfiguration config) {
		ConfirmDeleteMonitorConfigurationDialog confirmDialog = new ConfirmDeleteMonitorConfigurationDialog(config, String.format("Are you sure to delete %s?", config.getName()));
		confirmDialog.setOnDeleteSuccess(this::refreshConfigurationList);
		confirmDialog.initOwner(this.stage);
		confirmDialog.show();
	}
	
	private void showMonitorLoginWindow(MonitorConfiguration config) {
		MonitorLoginWindow loginWindow = new MonitorLoginWindow(config);
		loginWindow.initOwner(this.stage);
		loginWindow.setOnLoginSuccess(() -> {
			loginWindow.close();
			MonitorWindow monitorWindow = new MonitorWindow(loginWindow.getMonitorService());
			monitorWindow.show();
		});
		loginWindow.show();
	}
	
	private void showLoading() {
		Platform.runLater(() -> {
			this.newConfigBtn.setDisable(true);
			this.refreshConfigBtn.setDisable(true);
			this.loadingView.toFront();
		});
	}
	
	private void showConfigurationList() {
		Platform.runLater(() -> {
			this.newConfigBtn.setDisable(false);
			this.refreshConfigBtn.setDisable(false);
			this.configScrollView.toFront();
		});
	}
	
	private void setStage() {
		this.stage = (Stage) this.configFlowPane.getScene().getWindow();
	}
}
