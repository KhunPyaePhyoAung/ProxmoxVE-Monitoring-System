package me.khun.proxmoxnitor.application;
	
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;


public class ProxmoxnitorApplication extends Application {
	
	private static List<MonitorConfiguration> configurationList = new LinkedList<>();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/fxml/main.fxml"))));
			primaryStage.setResizable(true);
			primaryStage.setTitle("Proxmoxnitor");
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.getIcons().add(getIconImage());
			primaryStage.show();
			primaryStage.centerOnScreen();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Path monitorConfigurationFolder = Paths.get(System.getProperty("user.home"), ".Proxmoxnitor", "MonitorConfigurations");
		try {
			Files.createDirectories(monitorConfigurationFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Path logFolder = Paths.get(System.getProperty("user.home"), ".Proxmoxnitor", "Logs");
		try {
			Files.createDirectories(logFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		launch(args);
	}
	
	public static Image getIconImage() {
		return new Image(ProxmoxnitorApplication.class.getResourceAsStream("/image/icon.png"));
	}
	
	public static List<MonitorConfiguration> getMonitorConfigurationList() {
		return configurationList;
	}
	
	public static int getRootFontSize(double screenWidth) {
		if (screenWidth > 7600) {
			return 36;
		} else if (screenWidth > 3800) {
			return 22;
		} else if (screenWidth > 2500) {
			return 16;
		} else if (screenWidth > 1900) {
			return 12;
		} else if (screenWidth > 1200) {
			return 10;
		} else {
			return 8;
		}
	}
	
	public static double getSvgScale(double screenWidth) {
		if (screenWidth > 7600) {
			return 2.5;
		} else if (screenWidth > 3800) {
			return 2;
		} else if (screenWidth > 2500) {
			return 1.5;
		} else if (screenWidth > 1900) {
			return 1;
		} else if (screenWidth > 1200) {
			return 0.8;
		} else {
			return 0.5;
		}
	}
	
	public static String getDefaultGroupName() {
		return "Proxmoxnitor Users";
	}
	
	public static String getDefaultAuthorName() {
		return "Proxmoxnitor";
	}
}
