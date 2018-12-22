package io.github.tolgadurak.fx.sir4j;

import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class SirChart extends LineChart<Number, Number> {
	private Chart chart;
	private Series<Number, Number> series;
	private NumberAxis xAxis;
	private NumberAxis yAxis;
	private SirChartType sirChartType;
	private Integer seriesClassNumber;

	public SirChart(NumberAxis xAxis, NumberAxis yAxis, SirChartType sirChartType) {
		super(xAxis, yAxis);
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.sirChartType = sirChartType;
		this.series = new Series<>();
		this.getData().add(series);
	}

	public SirChart(NumberAxis xAxis, NumberAxis yAxis, Data<Number, Number> initialData, SirChartType sirChartType) {
		this(xAxis, yAxis, sirChartType);
		this.addDataToSeries(initialData);
	}

	public void pushXAxisForward(int tickCount) {
		this.xAxis.setLowerBound(this.xAxis.getLowerBound() + tickCount);
		this.xAxis.setUpperBound(this.xAxis.getUpperBound() + tickCount);
	}

	/**
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public void removeFirstDataOfSeries() throws IndexOutOfBoundsException {
		this.series.getData().remove(0);
	}

	public void setNameOfSeries(String value) {
		this.series.setName(value);
	}

	public void addDataToSeries(Data<Number, Number> data) {
		this.series.getData().add(data);
	}

	public Chart getChart() {
		return chart;
	}

	public Series<Number, Number> getSeries() {
		return series;
	}

	public NumberAxis getxAxis() {
		return xAxis;
	}

	public NumberAxis getyAxis() {
		return yAxis;
	}

	public SirChartType getSirChartType() {
		return sirChartType;
	}

	public Integer getSeriesClassNumber() {
		return seriesClassNumber;
	}

	public void setSeriesClassNumber(Integer seriesClassNumber) {
		this.seriesClassNumber = seriesClassNumber;
	}

}
