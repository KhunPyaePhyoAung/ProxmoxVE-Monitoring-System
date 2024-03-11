package me.khun.proxmoxnitor.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import me.khun.proxmoxnitor.application.Dependencies;
import me.khun.proxmoxnitor.application.ProxmoxnitorApplication;
import me.khun.proxmoxnitor.controller.fx.PveRrdTypeSelectorCellFactory;
import me.khun.proxmoxnitor.dto.PveResourceDto;
import me.khun.proxmoxnitor.dto.PveRrdDataDto;
import me.khun.proxmoxnitor.dto.PveStatusDto;
import me.khun.proxmoxnitor.entiry.MonitorConfiguration;
import me.khun.proxmoxnitor.exception.NoNetworkException;
import me.khun.proxmoxnitor.exception.ProxmoxAuthenticationException;
import me.khun.proxmoxnitor.exception.ProxmoxHostNotFoundException;
import me.khun.proxmoxnitor.exception.ProxmoxNodeNotFoundException;
import me.khun.proxmoxnitor.pve.PveRrdDataType;
import me.khun.proxmoxnitor.pve.PveStatus;
import me.khun.proxmoxnitor.service.EmailNotificationService;
import me.khun.proxmoxnitor.service.MonitorConfigurationService;
import me.khun.proxmoxnitor.service.MonitorService;
import me.khun.proxmoxnitor.view.MonitorLoginWindow;
import me.khun.proxmoxnitor.view.PveResourceCard;

public class MonitorController2 implements Initializable {
	
	@FXML
	private StackPane mainStackPane;
	
	@FXML
	private BorderPane monitorView;
	
	@FXML
	private VBox sessionTimeoutView;
	
	@FXML
	private VBox loadingView;
	
	@FXML
	private VBox serverDownView;
	
	@FXML
	private VBox monitorNodeView;
	
	@FXML
	private VBox nodeErrorView;
	
	@FXML
	private VBox noNetworkView;
	
	@FXML
	private VBox containerView;
	
	@FXML
	private HBox dateLabelWrapper;
	
	@FXML
	private AreaChart<String, Float> loadChart;
	
	@FXML
	private Label nodeErrorLabel;
	
	@FXML
	private Label monitorNameLabel;
	
	@FXML
	private Label monitorAddressLabel;
	
	@FXML
	private Label monitorErrorPageHeaderLabel;
	
	@FXML
	private ComboBox<String> nodeSelector;
	
	@FXML
	private ComboBox<Integer> statusIntervalSelector;
	
	@FXML
	private ComboBox<Integer> rrdIntervalSelector;
	
	@FXML
	private ComboBox<Integer> resourceIntervalSelector;
	
	@FXML
	private ComboBox<PveRrdDataType> rrdTypeSelector;
	
	@FXML
	private Label uptimeLabel;
	
	@FXML
	private Label cpuUsageLabel;
	
	@FXML
	private Label loadAverageLabel;
	
	@FXML
	private Label memoryUsageLabel;
	
	@FXML
	private Label hdSpaceUsageLabel;
	
	@FXML
	private Label ioDelayLabel;
	
	@FXML
	private Label ksmSharedLabel;
	
	@FXML
	private Label swapUsageLabel;
	
	@FXML
	private ProgressBar memoryProgressBar;
	
	@FXML
	private ProgressBar cpuProgressBar;
	
	@FXML
	private ProgressBar hdSpaceProgressBar;
	
	@FXML
	private ProgressBar ioDelayProgressBar;
	
	@FXML
	private ProgressBar swapProgressBar;
	
	@FXML
	private Label cpusLabel;
	
	@FXML
	private Label kernelVersionLabel;
	
	@FXML
	private Label bootModeLabel;
	
	@FXML
	private Label pveManagerVersionLabel;
	
	@FXML
	private CheckBox cpuCheckBox;
	
	@FXML
	private CheckBox ioCheckBox;
	
	private XYChart.Series<String, Float> cpuSeries;
	
	private XYChart.Series<String, Float> ioSeries;
	
	private MonitorService monitorService;
	
	private String activeNode;
	
	private String offlineNode;
	
	private int statusIntervalInSec;
	
	private int rrdIntervalInSec;
	
	private int resourceIntervalInSec;
	
	private volatile Timeline updateStatusTimeline;
	
	private volatile Timeline nodeNodeFoundAlertTimeline;
	
	private volatile Timeline serverDownAlertTimeline;
	
	private volatile Timeline serverRecoveryAlertTimeline;
	
	private volatile Timeline unauthenticatedAlertTimeline;
	
	private volatile Timeline noNetworkAlertTimeline;
	
	private volatile Timeline updateRrdTimeline;
	
	private volatile Timeline updateResourceTimeline;
	
	private volatile Timeline refreshTicketTimeline;
	
	private volatile Task<PveStatusDto> updateStatusTask;
	
	private volatile Task<List<PveRrdDataDto>> updateRrdTask;
	
	private volatile Task<List<PveResourceDto>> updateResourceTask;
	
	private volatile Thread updateStatusThread;
	
	private volatile Thread updateRrdThread;
	
	private volatile Thread updateResourceThread;
	
	private Stage stage;
	
	private static final int DEFAULT_STATUS_INTERVAL = 1;
	
	private static final int DEFAULT_RRD_INTERVAL = 60;
	
	private static final int DEFAULT_RESOURCE_INTERVAL = 60;
	
	private Clip nodeNotFoundSoundClip;
	
	private Clip serverDownSoundClip;
	
	private Clip serverRecoverySoundClip;
	
	private Clip unauthenticatedSoundClip;
	
	private Clip noNetworkSoundClip;
	
	private final int NODE_NOT_FOUND_ALERT_LOOP = 2;
	
	private final int SERVER_DOWN_ALERT_LOOP = 2;
	
	private final int UNAUTHENTICATED_ALERT_LOOP = 1;
	
	private final int NO_NETWORK_ALERT_LOOP = 1;
	
	private final int NODE_NOT_FOUND_ALERT_INTERVAL = 30;
	
	private final int SERVER_DOWN_ALERT_INTERVAL = 15;
	
	private final int SERVER_RECOVERY_ALERT_INTERVAL = 2;
	
	private final int UNAUTHENTICATED_ALERT_INTERVAL = 10;
	
	private final int NO_NETWORK_ALERT_INTERVAL = 10;
	
	private final int REFRESH_TICKET_INTERVAL = 2000;
	
	private boolean showCpuSeries = true;
	
	private boolean showIoSeries = true;
	
	private volatile MonitorStatus status;
	
	private int rootFontSize;
	
	private double fontSizeFactor;
	
	private double screenWidth;
	
	private EmailNotificationService emailNotificationService;
	
	private MonitorConfigurationService configService;
	
	private String configId;
	
	private enum MonitorStatus {
		RUNNING, STOPPED, NODE_NOT_FOUND, SERVER_DOWN, SESSION_TIMEOUT, NO_NETWORK
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		emailNotificationService = Dependencies.getEmailNotificationService();
		configService = Dependencies.getMonitorConfigurationService();
		
		showCpuSeries = cpuCheckBox.isSelected();
		showIoSeries = ioCheckBox.isSelected();
		
		cpuCheckBox.selectedProperty().addListener((l, o, n) -> {
			showCpuSeries = n;
			handleChartCheckBoxChange();
		});
		
		ioCheckBox.selectedProperty().addListener((l, o, n) -> {
			showIoSeries = n;
			handleChartCheckBoxChange();
		});
		
		screenWidth = Screen.getPrimary().getBounds().getWidth();
		rootFontSize = ProxmoxnitorApplication.getRootFontSize(screenWidth);
		fontSizeFactor = (double) rootFontSize / 12;
		initializeSoundClips();
		initializeAlertTimelines();
		
		this.statusIntervalInSec = DEFAULT_STATUS_INTERVAL;
		this.rrdIntervalInSec = DEFAULT_RRD_INTERVAL;
		this.resourceIntervalInSec = DEFAULT_RESOURCE_INTERVAL;
		
		statusIntervalSelector.getItems().clear();
		statusIntervalSelector.getItems().addAll(1, 2, 3);
		statusIntervalSelector.getSelectionModel().select(0);
		
		rrdIntervalSelector.getItems().clear();
		rrdIntervalSelector.getItems().addAll(60, 120);
		rrdIntervalSelector.getSelectionModel().select(0);
		
		resourceIntervalSelector.getItems().clear();
		resourceIntervalSelector.getItems().addAll(60, 120);
		resourceIntervalSelector.getSelectionModel().select(0);
		
		rrdTypeSelector.getItems().clear();
		rrdTypeSelector.getItems().addAll(PveRrdDataType.values());
		rrdTypeSelector.getSelectionModel().select(0);
		
		nodeSelector.getSelectionModel().selectedItemProperty().addListener((l, o, n) -> {
			handleNodeChange(n);
		});
		
		statusIntervalSelector.getSelectionModel().selectedItemProperty().addListener((l, o, n) -> {
			statusIntervalInSec = n;
			restartUpdateStatusThread();
		});
		
		rrdIntervalSelector.getSelectionModel().selectedItemProperty().addListener((l, o, n) -> {
			rrdIntervalInSec = n;
			restartUpdateRrdThread();
		});
		
		resourceIntervalSelector.getSelectionModel().selectedItemProperty().addListener((l, o, n) -> {
			resourceIntervalInSec = n;
			restartUpdateResourceThread();
		});
		
		rrdTypeSelector.getSelectionModel().selectedItemProperty().addListener((l, o, n) -> {
			restartUpdateRrdThread();
		});
		
		
		
		Callback<ListView<PveRrdDataType>, ListCell<PveRrdDataType>> rrdSelectorCellFactory = new PveRrdTypeSelectorCellFactory();
		rrdTypeSelector.setButtonCell(rrdSelectorCellFactory.call(null));
		rrdTypeSelector.setCellFactory(rrdSelectorCellFactory);
		
		Platform.runLater(() -> {
			MonitorConfiguration config = configService.findById(configId);
			emailNotificationService.setConfiguration(config);
			setStage();
			mainStackPane.styleProperty().bind(Bindings.format("-fx-font-size: %dpx;", rootFontSize));
		});
		refreshMonitorView();
		
	}
	
	@FXML
	private void refreshMonitorView() {
		onClose();
		status = MonitorStatus.RUNNING;
		stopAllAlert();
		Platform.runLater(() -> {
			showLoadingView();
			MonitorConfiguration config =  configService.findById(configId);
			monitorNameLabel.setText(config.getName());
			monitorAddressLabel.setText(String.format("%s:%s", config.getHostname(), config.getPort()));
			monitorErrorPageHeaderLabel.setText(String.format("%s %s:%s", config.getName(), config.getHostname(), config.getPort()));
			updateStatusTimeline = createNewUpdateStatusTimeline();
			updateRrdTimeline = createNewUpdateRrdTimeline();
			updateResourceTimeline = createNewUpdateResourceTimeline();
			refreshTicketTimeline = createNewRefreshTicketTimeline();
			
			
			List<String> nodeNames = monitorService.getNodeNameList();
			nodeNames.add("test");
			nodeSelector.getItems().clear();
			nodeSelector.getItems().addAll(nodeNames);
			if (!nodeNames.isEmpty()) {
				
				if (nodeSelector.getItems().contains(activeNode)) {
					nodeSelector.getSelectionModel().select(activeNode);
				} else {
					nodeSelector.getSelectionModel().select(0);
				}
			} else {
				activeNode = null;
			}
			
			updateStatusTimeline.play();
			updateRrdTimeline.play();
			updateResourceTimeline.play();
			refreshTicketTimeline.play();
		});
	}
	
	public void setService(MonitorService monitorService) {
		this.monitorService = monitorService;
		configId = monitorService.getConfiguration().getId();
	}
	
	private Timeline createNewRefreshTicketTimeline() {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.seconds(REFRESH_TICKET_INTERVAL), 
                new EventHandler<ActionEvent>() {

				   @Override
				   public void handle(ActionEvent event) {
					   Task<Void> refreshTask = new Task<Void>() {
							@Override
							protected Void call() throws Exception {
								System.out.println("TIcket::::::::::::::::::::::::");
								monitorService.refreshTicket();
//								throw new ProxmoxAuthenticationException("Test");
								return null;
							}
						};
						
						refreshTask.setOnFailed(e -> {
							handleFailureExceptions(refreshTask.getException());
						});
						
						Thread thread = new Thread(refreshTask);
						thread.start();
						
				   }
				}
			),
			new KeyFrame(Duration.seconds(REFRESH_TICKET_INTERVAL))
		);
		timeline.setCycleCount(Timeline.INDEFINITE);
		return timeline;
	}
	
	private Timeline createNewUpdateStatusTimeline() {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO, 
                new EventHandler<ActionEvent>() {

				   @Override
				   public void handle(ActionEvent event) {
					   stopUpdateStatusTask();
					   stopUpdateStatusThread();
					   if (status == MonitorStatus.STOPPED) {
						   return;
					   }
					   updateStatusTask = new Task<PveStatusDto>() {
							@Override
							protected PveStatusDto call() throws Exception {
								System.out.println("Fetching Status");
								if (status == MonitorStatus.STOPPED) {
									timeline.stop();
								}
								
								PveStatus status = monitorService.getNode(activeNode).getStatus();
								PveStatusDto statusDto = new PveStatusDto(status);
								return statusDto;
							}
						};
						
						updateStatusTask.setOnSucceeded(e -> {
							System.out.println("Fetching Status Success");
							handleRecoveryEvents();
							
							try {
								showMonitorNodeView();
								updateMonitorLabels(updateStatusTask.get());
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								e1.printStackTrace();
							}
						});
						
						updateStatusTask.setOnFailed(e -> {
							System.out.println("Fetching Status Failed");
							handleFailureExceptions(updateStatusTask.getException());
							
						});
						
						updateStatusThread = new Thread(updateStatusTask);
						updateStatusThread.start();
						
				   }
				}
			),
			new KeyFrame(Duration.seconds(statusIntervalInSec))
		);
		timeline.setCycleCount(Timeline.INDEFINITE);
		return timeline;
	}
	
	private Timeline createNewUpdateRrdTimeline() {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO, 
                new EventHandler<ActionEvent>() {

				   @Override
				   public void handle(ActionEvent event) {
					   stopUpdateRrdTask();
					   stopUpdateRrdThread();
					   if (status == MonitorStatus.STOPPED) {
						   return;
					   }
					   updateRrdTask = new Task<List<PveRrdDataDto>>() {
							@Override
							protected List<PveRrdDataDto> call() throws Exception {
								if (status == MonitorStatus.STOPPED) {
									timeline.stop();
								}
								return monitorService.getRrdData(activeNode, rrdTypeSelector.getValue());
							}
						};
						
						updateRrdTask.setOnSucceeded(e -> {
							handleRecoveryEvents();
							try {
								showMonitorNodeView();
								List<PveRrdDataDto> list = updateRrdTask.get();
								updateMonitorLoadChart(list);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								e1.printStackTrace();
							}
						});
						
						updateRrdTask.setOnFailed(e -> {
							handleFailureExceptions(updateRrdTask.getException());
							
						});
						
						updateRrdThread = new Thread(updateRrdTask);
						updateRrdThread.start();
						
				   }
				}
			),
			new KeyFrame(Duration.seconds(rrdIntervalInSec))
		);
		timeline.setCycleCount(Timeline.INDEFINITE);
		return timeline;
	}
	
	private Timeline createNewUpdateResourceTimeline() {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO, 
                new EventHandler<ActionEvent>() {

				   @Override
				   public void handle(ActionEvent event) {
					   stopUpdateResourceTask();
					   stopUpdateResourceThread();
					   if (status == MonitorStatus.STOPPED) {
						   return;
					   }
					   updateResourceTask = new Task<List<PveResourceDto>>() {
							@Override
							protected List<PveResourceDto> call() throws Exception {
								if (status == MonitorStatus.STOPPED) {
									timeline.stop();
								}
								return monitorService.getResources(activeNode);
							}
						};
						
						updateResourceTask.setOnSucceeded(e -> {
							handleRecoveryEvents();
							try {
								showMonitorNodeView();
								List<PveResourceDto> list = updateResourceTask.get();
								updateResourceList(list);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								e1.printStackTrace();
							}
						});
						
						updateResourceTask.setOnFailed(e -> {
							handleFailureExceptions(updateResourceTask.getException());
						});
						
						updateResourceThread = new Thread(updateResourceTask);
						updateResourceThread.start();
						
				   }
				}
			),
			new KeyFrame(Duration.seconds(resourceIntervalInSec))
		);
		timeline.setCycleCount(Timeline.INDEFINITE);
		return timeline;
	}
	
	private void handleRecoveryEvents() {
//		stopAllAlert();
		handleServerRecovery();
		handleNodeRecovery();
		handleSessionRecovery();
		handleNetworkRecovery();
	}
	

	private void handleFailureExceptions(Throwable e) {
		try {
			throw e;
		} catch (NoNetworkException ex) {
			handleNoNetWork();
		} catch (ProxmoxHostNotFoundException ex) {
			handleServerDown();
		} catch (ProxmoxAuthenticationException ex) {
			handleSessionTimeout();
		} catch (ProxmoxNodeNotFoundException ex) {
			handleNodeNotFound();
		} catch (Throwable ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private void handleNodeNotFound() {
		synchronized (status) {
			if (status != MonitorStatus.NODE_NOT_FOUND ) {
				status = MonitorStatus.NODE_NOT_FOUND;
				offlineNode = activeNode;
				stopAllAlert();
				boolean emptyNodes = nodeSelector.getItems().isEmpty();
				if (!emptyNodes) {
					nodeNodeFoundAlertTimeline.play();
				}
				showNodeErrorView(emptyNodes ? "No node found." : String.format("Node \"%s\" not found.", activeNode));
				runBackground(() -> {
					emailNotificationService.sendNodeOfflineNotification(offlineNode);
				});
			}
		}
		
	}
	
	private void handleNodeChange(String nodeName) {
		activeNode = nodeName;
		stopAllAlert();
		restartUpdateStatusThread();
		restartUpdateResourceThread();
		restartUpdateRrdThread();
	}
	
	private void handleNoNetWork() {
		synchronized (status) {
			if (status != MonitorStatus.NO_NETWORK) {
				status = MonitorStatus.NO_NETWORK;
				runBackground(() -> {
					emailNotificationService.sendNoNetworkNotification();
				});
				stopAllAlert();
				noNetworkAlertTimeline.play();
				showNoNetworkView();
			}
		}
	}
	
	private void handleServerDown() {
		synchronized (status) {
			if (status != MonitorStatus.SERVER_DOWN) {
				status = MonitorStatus.SERVER_DOWN;
				runBackground(() -> {
					emailNotificationService.sendServerDownNotification();
				});
				stopAllAlert();
				serverDownAlertTimeline.play();
				showServerDownView();
				
			}
		}
		
	}
	
	private void handleNetworkRecovery() {
		synchronized (status) {
			if (status == MonitorStatus.NO_NETWORK) {
				status = MonitorStatus.RUNNING;
				runBackground(() -> {
					emailNotificationService.sendNetworkRecoveryNotification();
				});
				stopAllAlert();
				serverRecoveryAlertTimeline.play();
			}
		}
	}
	
	private void handleServerRecovery() {
		synchronized (status) {
			if (status == MonitorStatus.SERVER_DOWN) {
				status = MonitorStatus.RUNNING;
				runBackground(() -> {
					emailNotificationService.sendServerRecoveryNotification();
				});
				stopAllAlert();
				serverRecoveryAlertTimeline.play();
			}
		}
	}
	
	private void handleNodeRecovery() {
		synchronized (status) {
			if (status == MonitorStatus.NODE_NOT_FOUND) {
				status = MonitorStatus.RUNNING;
				stopAllAlert();
				if (offlineNode == activeNode) {
					runBackground(() -> {
						emailNotificationService.sendNodeRecoveryNotification(activeNode);
					});
					serverRecoveryAlertTimeline.play();
				}
				offlineNode = null;
			}
		}
	}
	
	private void handleSessionRecovery() {
		synchronized (status) {
			if (status == MonitorStatus.SESSION_TIMEOUT) {
				status = MonitorStatus.RUNNING;
				runBackground(() -> {
					emailNotificationService.sendSessionRecoveryNotification();
				});
				stopAllAlert();
			}
		}
	}
	
	private void handleSessionTimeout() {
		synchronized (status) {
			if (status != MonitorStatus.SESSION_TIMEOUT) {
				status = MonitorStatus.SESSION_TIMEOUT;
				runBackground(() -> {
					emailNotificationService.sendSessionTimeoutNotification();
				});
				stopAllAlert();
				unauthenticatedAlertTimeline.play();
				showSessionTimeoutView();
			}
		}
	}
	
	@FXML
	private void showLoginWindow() {
		MonitorLoginWindow loginWindow = new MonitorLoginWindow(configService.findById(configId));
		loginWindow.initOwner(stage);
		loginWindow.setOnLoginSuccess(() -> {
			loginWindow.close();
			this.monitorService = loginWindow.getMonitorService();
		});
		loginWindow.show();
	}
	
	private void restartUpdateStatusThread() {
		showLoadingView();
		stopUpdateStatusThread();
		updateStatusTimeline.stop();
		updateStatusTimeline = createNewUpdateStatusTimeline();
		updateStatusTimeline.play();
	}
	
	private void restartUpdateRrdThread() {
		showLoadingView();
		stopUpdateRrdThread();
		updateRrdTimeline.stop();
		updateRrdTimeline = createNewUpdateRrdTimeline();
		updateRrdTimeline.play();
	}
	
	private void restartUpdateResourceThread() {
		showLoadingView();
		stopUpdateResourceThread();
		updateResourceTimeline.stop();
		updateResourceTimeline = createNewUpdateResourceTimeline();
		updateResourceTimeline.play();
	}
	
	private void runBackground(Runnable runnable) {
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				runnable.run();
				return null;
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
	}
	
	private void handleChartCheckBoxChange() {
		
		cpuSeries.getNode().setVisible(showCpuSeries);
		ioSeries.getNode().setVisible(showIoSeries);

		for (XYChart.Data<String, Float> d : cpuSeries.getData()) {
			d.getNode().setVisible(showCpuSeries);
		}
		
		for (XYChart.Data<String, Float> d : ioSeries.getData()) {
			d.getNode().setVisible(showIoSeries);
		}
	}
	
	private void updateMonitorLabels(PveStatusDto statusDto) {
		float[] loadAverage = statusDto.getLoadAverage();
		
		uptimeLabel.setText(statusDto.getUptime(false));
		cpuUsageLabel.setText(String.format("%.2f%% of %d CPU(s)", statusDto.getCpuUsageInPercentage(), statusDto.getCpus()));
		loadAverageLabel.setText(String.format("%.2f , %.2f , %.2f", loadAverage[0], loadAverage[1], loadAverage[2]));
		memoryUsageLabel.setText(String.format("%.2f%% (%s of %s)", statusDto.getUsedMemoryInPercentage(), statusDto.getUsedMemoryFormatted(), statusDto.getTotalMemoryFormatted()));
		hdSpaceUsageLabel.setText(String.format("%.2f%% (%s of %s)", statusDto.getUsedHdSpaceInPercentage(), statusDto.getUsedHdSpaceFormatted(), statusDto.getTotalHdSpaceFormatted()));
		ksmSharedLabel.setText(String.format("%s", statusDto.getKmsFormatted()));
		swapUsageLabel.setText(String.format("%.2f%% (%s of %s)", statusDto.getUsedSwapInPercentage(), statusDto.getUsedSwapFormatted(), statusDto.getTotalSwapFormatted()));
		ioDelayLabel.setText(String.format("%.2f%%", statusDto.getIoDelayInPercentage()));
		
		cpuProgressBar.setProgress(statusDto.getCpuUsageInPercentage() / 100);
		memoryProgressBar.setProgress(statusDto.getUsedMemoryInPercentage() / 100);
		hdSpaceProgressBar.setProgress(statusDto.getUsedHdSpaceInPercentage() / 100);
		ioDelayProgressBar.setProgress(statusDto.getIoDelayInPercentage() / 100);
		swapProgressBar.setProgress(statusDto.getUsedSwapInPercentage() / 100);
		
		cpusLabel.setText(String.format("%d x %s (%d Socket%s)", statusDto.getCpus(), statusDto.getCpuModel(), statusDto.getCpuSockets(), statusDto.getCpuSockets() > 1 ? "s" : ""));
		kernelVersionLabel.setText(statusDto.getKernelVersion());
		bootModeLabel.setText(statusDto.getBootMode().toUpperCase());
		pveManagerVersionLabel.setText(statusDto.getPveVersion());
	}
	
	private void updateMonitorLoadChart(List<PveRrdDataDto> rrdDtos) {
		cpuSeries = new XYChart.Series<String, Float>();
		cpuSeries.setName("CPU Usage");
		cpuSeries.getData().addAll(
			rrdDtos.stream().map(rrd -> new XYChart.Data<String, Float>(rrd.getTimestampFormatted(), rrd.getCpuUsageInPercentage())).collect(Collectors.toList())
		);
		
		ioSeries = new XYChart.Series<String, Float>();
		ioSeries.setName("IO Delay");
		ioSeries.getData().addAll(
				rrdDtos.stream().map(rrd -> new XYChart.Data<String, Float>(rrd.getTimestampFormatted(), rrd.getIoDelayInPercentage())).collect(Collectors.toList())
		);
		
		loadChart.getData().clear();
		loadChart.getData().add(cpuSeries);
		loadChart.getData().add(ioSeries);
		
		cpuSeries.getNode().getStyleClass().add("cpu-series");
		ioSeries.getNode().getStyleClass().add("io-series");
		
		cpuSeries.getData().forEach(d -> {
			d.getNode().setCursor(Cursor.HAND);
			Tooltip t = new Tooltip(String.format("%.2f%%%n%s", d.getYValue(), d.getXValue()));
			t.setFont(Font.font(14));
            Tooltip.install(d.getNode(), t);
		});
		
		ioSeries.getData().forEach(d -> {
			d.getNode().setCursor(Cursor.HAND);
			Tooltip t = new Tooltip(String.format("%.2f%%%n%s", d.getYValue(), d.getXValue()));
			t.setFont(Font.font(t.getFont().getSize() * 1.2));
            Tooltip.install(d.getNode(), t);
		});
		
		handleChartCheckBoxChange();
		
		updateDateAxisLabels();
	}
	
	private void updateResourceList(List<PveResourceDto> resources) {
		containerView.getChildren().clear();
		resources.forEach(r -> {
			PveResourceCard card = new PveResourceCard(r);
			containerView.getChildren().add(card);
		});
	}
	
	private void updateDateAxisLabels() {
		if (loadChart.getData().isEmpty()) {
			return;
		}
		
		List<String> xValues = loadChart.getData().get(0).getData().stream().map(d -> d.getXValue()).collect(Collectors.toList());
		double MIN_LABEL_WIDTH = 100 * fontSizeFactor;
		double wrapperWidth = dateLabelWrapper.getWidth();
		int possibleCount = (int) (wrapperWidth / MIN_LABEL_WIDTH);
		possibleCount = possibleCount < 2 ? 2 : possibleCount;
		int totalCount = xValues.size();
		double factor =  ((double)totalCount / (possibleCount - 1));
		CategoryAxis xAxis = (CategoryAxis) loadChart.getXAxis();
		double xOffset = xAxis.getLayoutX() + loadChart.getYAxis().getTickLength();
		double axisWidth = xAxis.getWidth();
		double tickLabelGap = axisWidth / possibleCount;
		double tickLabelWidth = tickLabelGap + tickLabelGap / possibleCount;
		
		dateLabelWrapper.setPrefWidth(axisWidth);
		dateLabelWrapper.getChildren().clear();
		float accu = 0;
		for (int i = 1; i <= possibleCount; accu += factor, i++) {
			int index = Math.round(accu);
			index = index == totalCount ? index - 1 : index;
			Label xLabel = new Label(xValues.get(index));
			xLabel.setTextFill(Paint.valueOf("white"));
			xLabel.setFont(Font.font(rootFontSize * 1.1));
			xLabel.setAlignment(Pos.CENTER);
			xLabel.setTextAlignment(TextAlignment.CENTER);
			HBox.setHgrow(xLabel, Priority.ALWAYS);
			
			HBox wrapper = new HBox(xLabel);
			wrapper.setPrefWidth(tickLabelWidth);
			wrapper.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(wrapper, Priority.NEVER);
			wrapper.setAlignment(Pos.CENTER);
			dateLabelWrapper.getChildren().add(wrapper);
		}
		
		VBox.setMargin(dateLabelWrapper, new Insets(0, 0, 0, xOffset - tickLabelWidth / 2));
	}

	private void showLoadingView() {
		showMonitorView();
		loadingView.toFront();
	}
	
	private void showMonitorView() {
		monitorView.toFront();
	}
	
	private void showNoNetworkView() {
		noNetworkView.toFront();
	}
	
	private void showServerDownView() {
		serverDownView.toFront();
	}
	
	private void showSessionTimeoutView() {
		sessionTimeoutView.toFront();
	}
	
	private void showMonitorNodeView() {
		monitorView.toFront();
		monitorNodeView.toFront();
	}
	
	private void showNodeErrorView(String message) {
		nodeErrorLabel.setText(message);
		monitorView.toFront();
		nodeErrorView.toFront();
	}
	
	private void initializeSoundClips() {
		try {
			InputStream bufferedIn1 = new BufferedInputStream(getClass().getResourceAsStream("/sound/alert-1.wav"));
            AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(bufferedIn1);
            nodeNotFoundSoundClip = AudioSystem.getClip();
            nodeNotFoundSoundClip.open(audioInputStream1);
            
            InputStream bufferedIn2 = new BufferedInputStream(getClass().getResourceAsStream("/sound/alert-2.wav"));
            AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(bufferedIn2);
            serverDownSoundClip = AudioSystem.getClip();
            serverDownSoundClip.open(audioInputStream2);
            
            InputStream bufferedIn3 = new BufferedInputStream(getClass().getResourceAsStream("/sound/alert-online.wav"));
            AudioInputStream audioInputStream3 = AudioSystem.getAudioInputStream(bufferedIn3);
            serverRecoverySoundClip = AudioSystem.getClip();
            serverRecoverySoundClip.open(audioInputStream3);
            
            InputStream bufferedIn4 = new BufferedInputStream(getClass().getResourceAsStream("/sound/archivo_11.wav"));
            AudioInputStream audioInputStream4 = AudioSystem.getAudioInputStream(bufferedIn4);
            unauthenticatedSoundClip = AudioSystem.getClip();
            unauthenticatedSoundClip.open(audioInputStream4);
            
            InputStream bufferedIn5 = new BufferedInputStream(getClass().getResourceAsStream("/sound/archivo_11.wav"));
            AudioInputStream audioInputStream5 = AudioSystem.getAudioInputStream(bufferedIn5);
            noNetworkSoundClip = AudioSystem.getClip();
            noNetworkSoundClip.open(audioInputStream5);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
	}
	
	private void initializeAlertTimelines() {
		nodeNodeFoundAlertTimeline = new Timeline();
		nodeNodeFoundAlertTimeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						playNodeNotFoundAlert();
					}
					
				}
			),
			new KeyFrame(Duration.seconds(NODE_NOT_FOUND_ALERT_INTERVAL))
		);
		nodeNodeFoundAlertTimeline.setDelay(Duration.seconds(0));
		nodeNodeFoundAlertTimeline.setCycleCount(Timeline.INDEFINITE);
		
		serverDownAlertTimeline = new Timeline();
		serverDownAlertTimeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						playServerDownAlert();
					}
					
				}
			),
			new KeyFrame(Duration.seconds(SERVER_DOWN_ALERT_INTERVAL))
		);
		serverDownAlertTimeline.setDelay(Duration.seconds(0));
		serverDownAlertTimeline.setCycleCount(Timeline.INDEFINITE);
		
		serverRecoveryAlertTimeline = new Timeline();
		serverRecoveryAlertTimeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						playServerRecoveryAlert();
					}
					
				}
			),
			new KeyFrame(Duration.seconds(SERVER_RECOVERY_ALERT_INTERVAL))
		);
		serverRecoveryAlertTimeline.setDelay(Duration.seconds(0));
		serverRecoveryAlertTimeline.setCycleCount(1);
		
		unauthenticatedAlertTimeline = new Timeline();
		unauthenticatedAlertTimeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						playUnauthenticatedAlert();
					}
					
				}
			),
			new KeyFrame(Duration.seconds(UNAUTHENTICATED_ALERT_INTERVAL))
		);
		unauthenticatedAlertTimeline.setDelay(Duration.seconds(0));
		unauthenticatedAlertTimeline.setCycleCount(Timeline.INDEFINITE);
		
		noNetworkAlertTimeline = new Timeline();
		noNetworkAlertTimeline.getKeyFrames().setAll(
			new KeyFrame(
				Duration.ZERO,
				new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						playNoNetworkAlert();
					}
					
				}
			),
			new KeyFrame(Duration.seconds(NO_NETWORK_ALERT_INTERVAL))
		);
		noNetworkAlertTimeline.setDelay(Duration.seconds(0));
		noNetworkAlertTimeline.setCycleCount(Timeline.INDEFINITE);
	}
	
	private void playNodeNotFoundAlert() {
		nodeNotFoundSoundClip.loop(NODE_NOT_FOUND_ALERT_LOOP);
		nodeNotFoundSoundClip.setFramePosition(0);
		nodeNotFoundSoundClip.start();
	}
	
	private void playServerDownAlert() {
		serverDownSoundClip.loop(SERVER_DOWN_ALERT_LOOP);
		serverDownSoundClip.setFramePosition(0);
		serverDownSoundClip.start();
	}
	
	private void playServerRecoveryAlert() {
		serverRecoverySoundClip.setFramePosition(0);
		serverRecoverySoundClip.start();
	}
	
	private void playNoNetworkAlert() {
		noNetworkSoundClip.loop(NO_NETWORK_ALERT_LOOP);
		noNetworkSoundClip.setFramePosition(0);
		noNetworkSoundClip.start();
	}
	
	private void playUnauthenticatedAlert() {
		unauthenticatedSoundClip.loop(UNAUTHENTICATED_ALERT_LOOP);
		unauthenticatedSoundClip.setFramePosition(0);
		unauthenticatedSoundClip.start();
	}
	
	private void stopAllAlert() {
		if (serverDownSoundClip != null) {
			serverDownSoundClip.stop();
		}
		
		if (serverDownAlertTimeline != null) {
			serverDownAlertTimeline.stop();
		}
		
		if (serverRecoverySoundClip != null) {
			serverRecoverySoundClip.stop();
		}
		
		if (serverRecoveryAlertTimeline != null) {
			serverRecoveryAlertTimeline.stop();
		}
		
		if (nodeNotFoundSoundClip != null) {
			nodeNotFoundSoundClip.stop();
		}
		
		if (nodeNodeFoundAlertTimeline != null) {
			nodeNodeFoundAlertTimeline.stop();
		}
		
		if (unauthenticatedSoundClip != null) {
			unauthenticatedSoundClip.stop();
		}
		
		if (unauthenticatedAlertTimeline != null) {
			unauthenticatedAlertTimeline.stop();
		}
		
		if (noNetworkSoundClip != null) {
			noNetworkSoundClip.stop();
		}
		
		if (noNetworkAlertTimeline != null) {
			noNetworkAlertTimeline.stop();
		}
		
	}
	
	private void setStage() {
		this.stage = (Stage) monitorView.getScene().getWindow();
		this.loadChart.getXAxis().widthProperty().addListener((l, o, n) -> {
			updateDateAxisLabels();
		});
		this.stage.setOnCloseRequest(e -> {
			onClose();
		});
	}
	
	private void onClose() {
		synchronized (this) {
			status = MonitorStatus.STOPPED;
			
			stopUpdateStatusTask();
			stopUpdateStatusThread();
			stopUpdateStatusTimeline();
			
			stopUpdateRrdTask();
			stopUpdateRrdThread();
			stopUpdateRrdTimeline();
			
			stopUpdateResourceTask();
			stopUpdateResourceThread();
			stopUpdateResourceTimeline();
			
			stopRefreshTicketTimeline();
			
			stopAllAlert();
		}
	}
	
	private void stopUpdateStatusThread() {
		if (updateStatusThread != null) {
			updateStatusThread.interrupt();
		}
	}
	
	
	
	private void stopUpdateRrdThread() {
		if (updateRrdThread != null) {
			updateRrdThread.interrupt();
		}
	}
	
	private void stopUpdateResourceThread() {
		if (updateResourceThread != null) {
			updateResourceThread.interrupt();
		}
	}
	
	private void stopUpdateStatusTimeline() {
		if (updateStatusTimeline != null) {
			updateStatusTimeline.stop();
		}
	}
	
	private void stopUpdateRrdTimeline() {
		if (updateRrdTimeline != null) {
			updateRrdTimeline.stop();
		}
	}
	
	private void stopUpdateResourceTimeline() {
		if (updateResourceTimeline != null) {
			updateResourceTimeline.stop();
		}
	}
	
	private void stopRefreshTicketTimeline() {
		if (refreshTicketTimeline != null) {
			refreshTicketTimeline.stop();
		}
	}
	
	private void stopUpdateStatusTask() {
		if (updateStatusTask != null) {
			updateStatusTask.cancel();
		}
	}
	
	private void stopUpdateRrdTask() {
		if (updateRrdTask != null) {
			updateRrdTask.cancel();
		}
	}
	
	private void stopUpdateResourceTask() {
		if (updateResourceTask != null) {
			updateResourceTask.cancel();
		}
	}
}
