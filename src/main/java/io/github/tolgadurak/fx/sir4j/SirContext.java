package io.github.tolgadurak.fx.sir4j;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

public class SirContext {
	private static final SystemInfo SYSTEM_INFO = new SystemInfo();
	private static final HardwareAbstractionLayer HARDWARE_ABSTRACTION_LAYER = SYSTEM_INFO.getHardware();
	private static final Sensors SENSORS = HARDWARE_ABSTRACTION_LAYER.getSensors();
	private static final CentralProcessor PROCESSOR = HARDWARE_ABSTRACTION_LAYER.getProcessor();
	public static final int SAMPLING_RESOLUTION = 100;
	public static final int CHART_UPDATE_RESOULUTION = 1000;

	public double readCpuTemperature() {
		return SENSORS.getCpuTemperature();
	}

	public double[] readCpuLoadPerProcessor() {
		return PROCESSOR.getProcessorCpuLoadBetweenTicks();
	}

	public SirChartFactory buildSirChartFactory() {
		return new SirChartFactory();
	}
}
