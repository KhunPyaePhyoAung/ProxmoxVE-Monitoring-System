package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.entiry.EmailNotificationConfiguration;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;

public class MonitorNotificationWindowController implements Initializable {
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private VBox mainView;
	
	@FXML
	private Label nameLabel;
	
	@FXML
	private Label groupNameErrorLabel;
	
	@FXML
	private Label addressLabel;
	
	@FXML
	private TextField emailInput;
	
	@FXML
	private TextField groupNameInput;
	
	@FXML
	private Button btnAddEmail;
	
	@FXML
	private Button btnSave;
	
	@FXML
	private TableView<String> userTableView;
	
	@FXML
	private TableColumn<String, String> emailTableColumn;
	
	@FXML
	private TableColumn<String, String> deleteEmailTableColumn;
	
	@FXML
	private CheckBox notificationEnableCheckbox;
	
	private MonitorConfiguration config;
	
	private Stage stage;
	
	private Runnable onSaveSuccess;
	
	private MonitorConfigurationService monitorConfigurationService;
	
	private static final int MAX_GROUP_NAME_LENGTH = 50;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		monitorConfigurationService = Dependencies.getMonitorConfigurationService();
		
		FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS);
        icon.setSize("1.2em");
        icon.setFill(Paint.valueOf("white"));
        btnAddEmail.setGraphic(icon);
		
        groupNameInput.textProperty().addListener((l, o, n) -> {
        	groupNameErrorLabel.setText(null);
        	
        	boolean disable = n != null && n.trim().length() > MAX_GROUP_NAME_LENGTH;
        	btnSave.setDisable(disable);
    		groupNameErrorLabel.setText(disable ? "Maximum length is " + MAX_GROUP_NAME_LENGTH + " characters" : null);
        });
        
		emailInput.textProperty().addListener((l, o, n) -> {
			boolean disable = true;
			if (n != null) {
				String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(n);
				disable = !matcher.matches();
			}
			btnAddEmail.setDisable(disable);
		});
		emailTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
		
		deleteEmailTableColumn.setCellFactory(new Callback<TableColumn<String,String>, TableCell<String,String>>() {
			
			@Override
			public TableCell<String, String> call(TableColumn<String, String> param) {
				return new TableCell<String, String>() {
					protected void updateItem(String item, boolean empty) {
						Button btn = new Button();
						setAlignment(Pos.CENTER);
						MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.DELETE);
						icon.setFill(Paint.valueOf("red"));
						btn.setGraphic(icon);
						btn.setOnMouseClicked(e -> {
							getTableView().getItems().remove(getIndex());
						});
						btn.getStyleClass().add("btn_remove_user");
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(btn);
						}
					};
				};
			}
		});
		
		showMainView();
		Platform.runLater(() -> {
			setStage();
			setupData();
		});
	}
	
	public void setMonitorConfiguration(MonitorConfiguration config) {
		this.config = config;
	}
	
	public void setOnSaveSuccess(Runnable onSaveSuccess) {
		this.onSaveSuccess = onSaveSuccess;
	}
	
	@FXML
	private void addEmail() {
		String email = emailInput.getText();
		ObservableList<String> users = userTableView.getItems();
		if (!users.contains(email)) {
			users.add(email);
		}
		emailInput.clear();
	}
	
	private void setupData() {
		if (config != null) {
			
			EmailNotificationConfiguration noti = config.getEmailNotification();
			if (noti != null) {
				notificationEnableCheckbox.setSelected(noti.isEnabled());
				if (noti.getRecipients() != null) {
					Set<String> users = noti.getRecipients();
					userTableView.getItems().addAll(users);
				}
				groupNameInput.setText(noti.getGroupName());
				nameLabel.setText(config.getName());
				addressLabel.setText(String.format("%s:%d", config.getHostname(), config.getPort()));
			}
			
			emailInput.requestFocus();
		}
		
	}
	
	@FXML
	private void saveConfiguration() {
		showLoadingView();
		Task<Void> saveTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Set<String> users = new HashSet<String>(Arrays.asList(userTableView.getItems().toArray(new String[] {})));
				EmailNotificationConfiguration noti = new EmailNotificationConfiguration();
				String groupName = groupNameInput.getText().trim();
				noti.setGroupName(groupName);
				noti.setEnabled(notificationEnableCheckbox.isSelected());
				noti.setRecipients(users);
				config.setEmailNotification(noti);
				monitorConfigurationService.save(config);
				return null;
			}
		};
		
		saveTask.setOnSucceeded(e -> {
			closeWindow();
			if (onSaveSuccess != null) {
				onSaveSuccess.run();
			}
		});
		
		Thread thread = new Thread(saveTask);
		thread.start();
	}
	
	private void showLoadingView() {
		loadingView.toFront();
	}
	
	private void showMainView() {
		mainView.toFront();
	}
	
	@FXML
	private void closeWindow() {
		stage.close();
	}
	
	private void setStage() {
		stage = (Stage) userTableView.getScene().getWindow();
	}

}
