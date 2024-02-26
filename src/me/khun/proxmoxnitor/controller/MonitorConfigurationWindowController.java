package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;

public class MonitorConfigurationWindowController implements Initializable {
	
	@FXML
	private StackPane mainStackPane;
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private VBox formView;
	
	@FXML
	private Label formTitleLabel;
	
	@FXML
	private TextField nameInput;
	
	@FXML
	private TextField hostnameInput;
	
	@FXML
	private TextField portInput;
	
	@FXML
	private Label nameErrorLabel;
	
	@FXML
	private Label hostnameErrorLabel;
	
	@FXML
	private Label portErrorLabel;
	
	private MonitorConfigurationService monitorConfigurationService;
	
	private MonitorConfiguration config;
	
	private Stage stage;
	
	private Runnable onSaveSuccess;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.monitorConfigurationService = Dependencies.getMonitorConfigurationService();
		
		nameInput.textProperty().addListener((l, o, n) -> {
			nameErrorLabel.setText(null);
		});
		
		hostnameInput.textProperty().addListener((l, o, n) -> {
			hostnameErrorLabel.setText(null);
		});
		
		portInput.textProperty().addListener((l, o, n) -> {
			portErrorLabel.setText(null);
			if (n == null || n.isEmpty()) {
				return;
			}
			
			try {
				Long.parseLong(n);
			} catch (NumberFormatException e) {
				Platform.runLater(() -> {
					portInput.setText(o);
				});
			}
			
			Platform.runLater(() -> {
				portInput.positionCaret(portInput.getText().length());
			});
		});
		Platform.runLater(() -> {
			setStage();
			fillFormData();
		});
	}
	
	public void setConfiguration(MonitorConfiguration config) {
		this.config = config;
	}
	
	public void setOnSaveSuccess(Runnable onSaveSuccess) {
		this.onSaveSuccess = onSaveSuccess;
	}
	
	@FXML
	private void saveConfiguration() {
		String name = nameInput.getText();
		String hostname = hostnameInput.getText();
		String port = portInput.getText();
		
		if (!validateInputs(name, hostname, port)) {
			return;
		}
		
		showLoading();
		
		Task<Void> saveConfigTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				
				if (config == null) {
					config = new MonitorConfiguration();
				}
				
				config.setName(nameInput.getText());
				config.setHostname(hostnameInput.getText());
				config.setPort(Integer.parseInt(portInput.getText()));
				monitorConfigurationService.save(config);
				return null;
			}
			
		};
		
		saveConfigTask.setOnSucceeded(e -> {
			runOnSaveSuccess();
			close();
		});
		
		saveConfigTask.setOnFailed(e -> {
			showForm();
		});
		
		Thread thread = new Thread(saveConfigTask);
		thread.setDaemon(true);
		thread.start();
	}
	
	private boolean validateInputs(String name, String hostname, String port) {
		boolean valid = true;
		
		if (name == null || name.trim().isEmpty()) {
			valid = false;
			Platform.runLater(() -> {
				nameErrorLabel.setText("Name cannot be empty");
			});
		} else if (name.trim().length() > 40) {
			valid = false;
			Platform.runLater(() -> {
				nameErrorLabel.setText("Maximum name length is 40");
			});
		}
		
		if (hostname == null || hostname.trim().isEmpty()) {
			valid = false;
			Platform.runLater(() -> {
				hostnameErrorLabel.setText("Hostname cannot be empty");
			});
		}
		
		Pattern ipv4Pattern = Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Pattern urlPattern = Pattern.compile("^(https?|ftp)://[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+(\\/[^\\s]*)?$");
		Matcher ipv4Matcher = ipv4Pattern.matcher(hostname);
		Matcher urlMatcher = urlPattern.matcher(hostname);
        
		if (!ipv4Matcher.matches() && !urlMatcher.matches()) {
			valid = false;
			Platform.runLater(() -> {
				hostnameErrorLabel.setText("Invalid hostname");
			});
		}
		
		if (port == null || port.trim().isEmpty()) {
			valid = false;
			Platform.runLater(() -> {
				portErrorLabel.setText("Port cannot be empty");
			});
		} else {
			long portNo = Long.parseLong(port);
			if (portNo < 0 || portNo > 65535) {
				valid = false;
				Platform.runLater(() -> {
					portErrorLabel.setText("Invalid port");
				});
			}
		}
		
		return valid;
	}
	
	private void fillFormData() {
		if (config != null) {
			this.formTitleLabel.setText("Edit Configuration");
			this.nameInput.setText(this.config.getName());
			this.nameInput.requestFocus();
			this.nameInput.positionCaret(this.nameInput.getText().length());
			this.hostnameInput.setText(this.config.getHostname());
			this.portInput.setText(this.config.getPort().toString());
		} else {
			this.formTitleLabel.setText("New Configuration");
		}
	}
	
	private void showLoading() {
		this.loadingView.toFront();
		this.formView.setDisable(true);
	}
	
	private void showForm() {
		this.formView.toFront();
		this.formView.setDisable(false);
	}
	
	private void runOnSaveSuccess() {
		if (this.onSaveSuccess != null) { 
			Platform.runLater(this.onSaveSuccess::run);
		}
	}
	
	@FXML
	private void close() {
		this.stage.close();
	}
	
	private void setStage() {
		this.stage = (Stage) this.formTitleLabel.getScene().getWindow();
	}

}
