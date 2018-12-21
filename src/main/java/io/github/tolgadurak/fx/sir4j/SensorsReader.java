package io.github.tolgadurak.fx.sir4j;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public class SensorsReader extends Application {
	private Timeline animation;
	private int second = 0;
	private static final SystemInfo si = new SystemInfo();
	private static final HardwareAbstractionLayer hal = si.getHardware();
	private static final Sensors sensors = hal.getSensors();
	private static final CentralProcessor processor = hal.getProcessor();
	private double cpuTemperature = sensors.getCpuTemperature();
	double[] load = processor.getProcessorCpuLoadBetweenTicks();
	private static final int SAMPLING_RESOLUTION = 100;
	private static final int CHART_UPDATE_RESOULUTION = 1000;
	private SirChartFactory sirChartFactory = new SirChartFactory();

	public SensorsReader() {
		KeyFrame frame = new KeyFrame(Duration.millis(CHART_UPDATE_RESOULUTION), (ActionEvent actionEvent) -> {
			updateChartsByType();
		});
		animation = new Timeline();
		animation.getKeyFrames().add(frame);
		animation.setCycleCount(Animation.INDEFINITE);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		Parent cpuTemperatureChart = sirChartFactory.createSirChart("cpuTempChart", cpuTemperature, "CPU Temperature",
				"Temperature", "°C", 40, 100, 10, SirChartType.CPU_TEMPERATURE);

		VBox.setMargin(cpuTemperatureChart, new Insets(0, 0, 0, 8));
		vBox.getChildren().add(cpuTemperatureChart);
		for (int i = 0; i < load.length; i++) {
			Parent cpuLoadPerCore = sirChartFactory.createSirChart("cpuCoreChart" + (i + 1), load[i], "CPU " + (i + 1),
					"", "", 0, 100, 10, SirChartType.CPU_LOAD_PER_CORE, i);
			HBox.setMargin(cpuLoadPerCore, new Insets(0, 0, 0, 8));
			hBox.getChildren().add(cpuLoadPerCore);
		}
		VBox.setMargin(hBox, new Insets(0, 0, 0, 8));
		vBox.getChildren().add(hBox);
		primaryStage.setScene(new Scene(vBox));
		primaryStage.show();
		primaryStage.setTitle("sir4j");
		animation.play();
		submitSystemInformationReaderTask();
	}

	public void submitSystemInformationReaderTask() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				cpuTemperature = sensors.getCpuTemperature();
				load = processor.getProcessorCpuLoadBetweenTicks();
			}
		}, 0, SAMPLING_RESOLUTION);
	}

	private void updateChartsByType() {
		second++;
		sirChartFactory.getSirChartList().forEach(sirChart -> {
			SirChartType sirChartType = sirChart.getSirChartType();
			double data = 0;
			String format = null;
			if (SirChartType.CPU_TEMPERATURE.equals(sirChartType)) {
				data = cpuTemperature;
				format = "%.1f°C";
			} else if (SirChartType.CPU_LOAD_PER_CORE.equals(sirChartType)) {
				data = load[sirChart.getSeriesClassNumber()] * 100;
				format = "%.1f%%";
			}
			Series<Number, Number> series = sirChart.getSeries();

			series.getData().add(new Data<Number, Number>(second, data));
			series.setName(String.format(format, data));
			if (second > 61) {
				series.getData().remove(0);
			}
			if (second > 60) {
				NumberAxis xAxis = sirChart.getxAxis();
				xAxis.setLowerBound(xAxis.getLowerBound() + 1);
				xAxis.setUpperBound(xAxis.getUpperBound() + 1);
			}
		});
	}

	public static void main(String[] args) {
		launch();
	}
}
