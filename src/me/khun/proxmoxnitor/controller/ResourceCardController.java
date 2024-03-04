package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import me.khun.proxmoxnitor.dto.PveResourceDto;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceStatus;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceType;

public class ResourceCardController implements Initializable {
	
	@FXML
	private Label resourceNameLabel;
	
	@FXML
	private HBox iconWrapper;
	
	private PveResourceDto resource;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			setupData();
		});
	}
	
	public void setResource(PveResourceDto resource) {
		this.resource = resource;
	}
	
	private void setupData() {
		PveResourceStatus status = PveResourceStatus.valueOf(resource.getStatusString().toUpperCase());
		
		MaterialDesignIconView iconView = new MaterialDesignIconView();
		iconView.setIcon(resource.getType() == PveResourceType.CONTAINER ? MaterialDesignIcon.CUBE : MaterialDesignIcon.LAPTOP_CHROMEBOOK);
		
		if (status == PveResourceStatus.RUNNING) {
//			iconView.setIcon(MaterialDesignIcon.PLAY_CIRCLE);
			iconView.setFill(Paint.valueOf("#00d438"));
		} else {
//			iconView.setIcon(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
			iconView.setFill(Paint.valueOf("#636363"));
		}
		
		iconView.setFocusTraversable(false);
		iconWrapper.getChildren().add(iconView);
		
		resourceNameLabel.setText(String.format("%d (%s)", resource.getVmId(), resource.getName()));
	}

}
