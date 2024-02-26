package me.khun.proxmoxnitor.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import me.khun.proxmoxnitor.dto.PveResourceDto;
import me.khun.proxmoxnitor.pve.PveResource.PveResourceStatus;

public class ResourceCardController implements Initializable {
	
	@FXML
	private Label resourceNameLabel;
	
	@FXML
	private SVGPath statusSvg;
	
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
		String runningSvgPath = "M9.984 16.5l6-4.5-6-4.5v9zM12 2.016q4.125 0 7.055 2.93t2.93 7.055-2.93 7.055-7.055 2.93-7.055-2.93-2.93-7.055 2.93-7.055 7.055-2.93z";String stoppedSvgPath = "M14.571 15.857v-7.714c0-0.241-0.188-0.429-0.429-0.429h-7.714c-0.241 0-0.429 0.188-0.429 0.429v7.714c0 0.241 0.188 0.429 0.429 0.429h7.714c0.241 0 0.429-0.188 0.429-0.429zM20.571 12c0 5.679-4.607 10.286-10.286 10.286s-10.286-4.607-10.286-10.286 4.607-10.286 10.286-10.286 10.286 4.607 10.286 10.286z";
		
		PveResourceStatus status = PveResourceStatus.valueOf(resource.getStatus().toUpperCase());
		
		resourceNameLabel.setText(String.format("%d (%s)", resource.getVmId(), resource.getName()));
		statusSvg.setContent(status == PveResourceStatus.RUNNING ? runningSvgPath : stoppedSvgPath);
		statusSvg.setFill(Paint.valueOf(status == PveResourceStatus.RUNNING ? "#00d438" : "#636363"));
	}

}
