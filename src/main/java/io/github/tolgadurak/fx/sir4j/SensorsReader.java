package io.github.tolgadurak.fx.sir4j;

import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public class SensorsReader extends Application {
	private LineChart<Number, Number> chart;
	private Series<Number, Number> tempDataSeries;
	private NumberAxis xAxis;
	private Timeline animation;
	private int second = 0;
	private static final SystemInfo si = new SystemInfo();
	private static final HardwareAbstractionLayer hal = si.getHardware();
	private static final Sensors sensors = hal.getSensors();
	private double cpuTemperature = 0;
	private int samplingResolutionInMillis = 100;
	private int chartUpdateResolutionInMillis = 1000;

	public SensorsReader() {
		KeyFrame frame = new KeyFrame(Duration.millis(chartUpdateResolutionInMillis), (ActionEvent actionEvent) -> {
			updateChartByCpuTemperature(sensors);
		});
		animation = new Timeline();
		animation.getKeyFrames().add(frame);
		animation.setCycleCount(Animation.INDEFINITE);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		validateSettings();
		primaryStage.setScene(new Scene(createInitialContent(sensors)));
		primaryStage.show();
		play();
		submitCpuTemperatureReaderTask();
	}

	public void validateSettings() {
		if (samplingResolutionInMillis > 1000 || samplingResolutionInMillis > chartUpdateResolutionInMillis) {
			throw new RuntimeException("Invalid settings detected at first");
		}
	}

	public void play() {
		animation.play();
	}

	public void submitCpuTemperatureReaderTask() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				cpuTemperature = sensors.getCpuTemperature();
			}
		}, 0, samplingResolutionInMillis);
	}

	public Parent createInitialContent(Sensors sensors) {
		double cpuTemperature = sensors.getCpuTemperature();
		xAxis = new NumberAxis(0, 60, 3);
		xAxis.setMinorTickVisible(false);
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarkVisible(false);

		final NumberAxis yAxis = new NumberAxis(40, 100, 10);
		chart = new LineChart<>(xAxis, yAxis);
		chart.setId("cpuTempChart");
		final String sensorReaderChartCss = getClass().getResource("ChartStyleForSensorsReader.css").toExternalForm();
		chart.getStylesheets().add(sensorReaderChartCss);
		chart.setAnimated(false);
		chart.setCreateSymbols(false);
		chart.setTitle("CPU Temperature");
		xAxis.setForceZeroInRange(false);
		yAxis.setLabel("Temperature");
		yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, "°C"));
		chart.setVerticalZeroLineVisible(false);
		tempDataSeries = new Series<>();
		tempDataSeries.setName(String.format("%.1f°C", cpuTemperature));
		tempDataSeries.getData().add(new Data<Number, Number>(second, cpuTemperature));
		chart.getData().add(tempDataSeries);
		return chart;
	}

	private void updateChartByCpuTemperature(Sensors sensors) {
		second++;
		tempDataSeries.getData().add(new Data<Number, Number>(second, cpuTemperature));
		tempDataSeries.setName(String.format("%.1f°C", cpuTemperature));
		updatePlot();
	}

	private void updatePlot() {
		if (second > 61) {
			tempDataSeries.getData().remove(0);
		}
		if (second > 60) {
			xAxis.setLowerBound(xAxis.getLowerBound() + 1);
			xAxis.setUpperBound(xAxis.getUpperBound() + 1);
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
