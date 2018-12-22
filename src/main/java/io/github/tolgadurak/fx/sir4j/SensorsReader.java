package io.github.tolgadurak.fx.sir4j;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SensorsReader extends Application {

	private SirContext sirContext = new SirContext();
	private SirChartFactory sirChartFactory = sirContext.buildSirChartFactory();
	private Timeline animation;
	private int second = 0;
	private double cpuTemperature = 0;
	double[] load = null;

	public SensorsReader() {
		KeyFrame frame = new KeyFrame(Duration.millis(SirContext.CHART_UPDATE_RESOULUTION),
				(ActionEvent actionEvent) -> {
					updateChartsByType();
				});
		animation = new Timeline();
		animation.getKeyFrames().add(frame);
		animation.setCycleCount(Animation.INDEFINITE);
		cpuTemperature = sirContext.readCpuTemperature();
		load = sirContext.readCpuLoadPerProcessor();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		VBox vBox = new VBox();
		HBox hBox = new HBox();
		SirChart cpuTemperatureChart = sirChartFactory.createSirChart("cpuTempChart", cpuTemperature, "CPU Temperature",
				"Temperature", "°C", 40, 100, 10, SirChartType.CPU_TEMPERATURE);
		VBox.setMargin(cpuTemperatureChart, new Insets(0, 0, 0, 8));
		vBox.getChildren().add(cpuTemperatureChart);
		for (int i = 0; i < load.length; i++) {
			SirChart cpuLoadPerCore = sirChartFactory.createSirChart("cpuCoreChart" + (i + 1), load[i],
					"CPU " + (i + 1), "", "", 0, 100, 10, SirChartType.CPU_LOAD_PER_CORE, i);
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
				cpuTemperature = sirContext.readCpuTemperature();
				load = sirContext.readCpuLoadPerProcessor();
			}
		}, 0, SirContext.SAMPLING_RESOLUTION);
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
			sirChart.addDataToSeries(new Data<Number, Number>(second, data));
			sirChart.setNameOfSeries(String.format(format, data));
			if (second > 61) {
				sirChart.removeFirstDataOfSeries();
			}
			if (second > 60) {
				sirChart.pushXAxisForward(1);
			}
		});
	}

	public static void main(String[] args) {
		launch();
	}
}
