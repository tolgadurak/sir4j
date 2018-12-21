package io.github.tolgadurak.fx.sir4j;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart.Data;

public class SirChartFactory {

	private List<SirChart> sirChartList = new ArrayList<>();

	public SirChart createSirChart(String id, double initialValue, String title, String label,
			String yAxisTickLabelSuffix, int yAxisLowerBound, int yAxisUpperBound, int yAxisTickUnit,
			SirChartType sirChartType, int seriesClassNumber) {
		SirChart sirChart = this.createSirChart(id, initialValue, title, label, yAxisTickLabelSuffix, yAxisLowerBound,
				yAxisUpperBound, yAxisTickUnit, sirChartType);
		sirChart.setSeriesClassNumber(seriesClassNumber);
		return sirChart;
	}

	public SirChart createSirChart(String id, double initialValue, String title, String label,
			String yAxisTickLabelSuffix, int yAxisLowerBound, int yAxisUpperBound, int yAxisTickUnit,
			SirChartType sirChartType) {
		final NumberAxis xAxis = new NumberAxis(0, 60, 3);
		final NumberAxis yAxis = new NumberAxis(yAxisLowerBound, yAxisUpperBound, yAxisTickUnit);
		final String sensorReaderChartCss = getClass().getResource("ChartStyleForSensorsReader.css").toExternalForm();
		xAxis.setMinorTickVisible(false);
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarkVisible(false);
		SirChart sirChart = new SirChart(xAxis, yAxis, sirChartType);
		sirChart.setId(id);
		sirChart.getStylesheets().add(sensorReaderChartCss);
		sirChart.setAnimated(false);
		sirChart.setCreateSymbols(false);
		sirChart.setTitle(title);
		xAxis.setForceZeroInRange(false);
		yAxis.setLabel(label);
		yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, yAxisTickLabelSuffix));
		sirChart.setVerticalZeroLineVisible(false);
		sirChart.setNameOfSeries(String.format("%.1f" + yAxisTickLabelSuffix, initialValue));
		sirChart.addDataToSeries(new Data<Number, Number>(0, initialValue));
		sirChartList.add(sirChart);
		return sirChart;
	}

	public List<SirChart> getSirChartList() {
		return sirChartList;
	}

}
