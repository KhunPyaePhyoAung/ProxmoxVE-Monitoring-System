package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.controller.fx.PveRealmSelectorCellFactory;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.pve.PveRealm;
import me.khun.proxmoxnitor.service.MonitorService;

public class MonitorLoginWindowController implements Initializable {
	
	@FXML
	private StackPane mainStackPane;
	
	@FXML
	private VBox loginView;
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private VBox hostErrorView;
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private Label hostErrorLabel;
	
	@FXML
	private Label addressLabel;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private TextField usernameInput;
	
	@FXML
	private PasswordField passwordInput;
	
	@FXML
	private ComboBox<PveRealm> realmSelector;
	
	@FXML
	private Label loadingLabel;
	
	private Stage stage;
	
	private MonitorService monitorService;
	
	private Runnable onLoginSuccess;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Callback<ListView<PveRealm>, ListCell<PveRealm>> realmSelectorCellFactory = new PveRealmSelectorCellFactory();
		realmSelector.setButtonCell(realmSelectorCellFactory.call(null));
		realmSelector.setCellFactory(realmSelectorCellFactory);
		
		monitorService = Dependencies.getMonitorService();
		Platform.runLater(() -> {
			setStage();
			
			MonitorConfiguration config = monitorService.getConfiguration();
			nameLabel.setText(config.getName());
			addressLabel.setText(String.format("%s:%s", config.getHostname(), config.getPort()));
			
			setupRealmSelector();
			usernameInput.requestFocus();
			
		});
	}
	
	public void setMonitorConfiguration(MonitorConfiguration config) {
		monitorService.setConfiguration(config);
	}
	
	public void setOnLoginSuccess(Runnable onLoginSuccess) {
		this.onLoginSuccess = onLoginSuccess;
	}
	
	@FXML
	private void setupRealmSelector() {
		showLoadingView("Loading...");
		Task<List<PveRealm>> realmLoader = new Task<List<PveRealm>>() {
			
			@Override
			protected List<PveRealm> call() throws Exception {
				List<PveRealm> realms = monitorService.getRealms();
				return realms;
			}
		};
		
		realmLoader.setOnSucceeded(e -> {
			try {
				realmSelector.getItems().clear();
				List<PveRealm> realms = realmLoader.get();
				realmSelector.getItems().addAll(realmLoader.get());
				if (!realms.isEmpty()) {
					realmSelector.getSelectionModel().selectFirst();
				}
				showLoginView();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		});
		
		realmLoader.setOnFailed(e -> {
			showHostErrorView(realmLoader.getException().getMessage());
		});
		
		Thread realmLoaderThread = new Thread(realmLoader);
		realmLoaderThread.start();
	}
	
	@FXML
	private void login() {
		errorLabel.setText(null);
		showLoadingView("Logging in...");
		String username = usernameInput.getText();
		String password = passwordInput.getText();
		String realm = realmSelector.getValue().getId();
		
		if (username == null || username.trim().isEmpty()) {
			errorLabel.setText("Enter username");
			showLoginView();
			return;
		} else if (password == null || password.trim().isEmpty()) {
			errorLabel.setText("Enter password");
			showLoginView();
			return;
		}
		
		Task<Void> loginTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				monitorService.loginWithUsernameAndPassword(username, password, realm);
				return null;
			}
			
		};
		
		loginTask.setOnSucceeded(e -> {
			if (onLoginSuccess != null) {
				onLoginSuccess.run();
			}
		});
		
		loginTask.setOnFailed(e -> {
			showLoginView();
			errorLabel.setText(loginTask.getException().getMessage());
		});
		
		Thread loginThread = new Thread(loginTask);
		loginThread.start();
	}
	
	public MonitorService getMonitorService() {
		return monitorService;
	}
	
	private void showLoadingView(String message) {
		loadingLabel.setText(message);
		loadingView.toFront();
	}
	
	private void showLoginView() {
		loginView.toFront();
	}
	
	private void showHostErrorView(String message) {
		hostErrorView.toFront();
		hostErrorLabel.setText(message);
	}
	
	@FXML
	private void close() {
		this.stage.close();
	}
	
	private void setStage() {
		this.stage = (Stage) this.nameLabel.getScene().getWindow();
	}

}
