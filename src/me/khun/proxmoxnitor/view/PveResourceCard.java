package me.khun.proxmoxnitor.view;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import me.khun.proxmoxnitor.controller.ResourceCardController;
import me.khun.proxmoxnitor.dto.PveResourceDto;

public class PveResourceCard extends HBox {
	
	private PveResourceDto resource;
	private ResourceCardController controller;

	public PveResourceCard(PveResourceDto resource) {
		this.resource = resource;
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/resource_card.fxml"));
			Parent view = loader.load();
			this.controller = loader.getController();
			this.controller.setResource(resource);
			this.getChildren().add(view);
			this.setFocusTraversable(false);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PveResourceDto getResource() {
		return resource;
	}
}
