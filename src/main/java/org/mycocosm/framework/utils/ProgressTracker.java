package org.mycocosm.framework.utils;

public class ProgressTracker {
	private double projectedMaxValue;
	private long counter = 0;
	private double elapsed = 0.0;
	private static final String FORMAT = "%,3.4f%%";
	
	public ProgressTracker(double projectedMaxValue) {
		this.projectedMaxValue=projectedMaxValue;
	}
	public ProgressTracker() {
		this.projectedMaxValue=0.0;
	}
	
	public void reset(double projectedMaxValue) {
		this.projectedMaxValue=projectedMaxValue;
		this.counter = 0;
		this.elapsed = 0.0;
	}
	
	public double getCurrentValue() {
		return elapsed;
	}
	public long getCurrentValueLong() {
		return (long)elapsed;
	}
	public double getProjectedMaxValue() {
		return projectedMaxValue;
	}
	public long getProjectedMaxValueLong() {
		return (long)projectedMaxValue;
	}

	public double getProgress() {
		if (projectedMaxValue!=0.0) {
			return elapsed/projectedMaxValue;
		} else {
			return Double.NaN;
		}
	}
	
	public String getProgressFormatted() {
		double progress = getProgress();
		if (!Double.isNaN(progress)) {
			return String.format(FORMAT, 100.0*progress);
		} else {
			return "N/A";
		}
	}

	public long getCounter() {
		return counter;
	}

	public double increaseAndGetProgress(double delta) {
		elapsed+=delta;
		counter++;
		return getProgress();
	}

	public double increaseAndGetProgress() {
		return increaseAndGetProgress(1.0);
	}
	
	public String increaseAndGetProgressFormatted(double delta) {
		increaseAndGetProgress(delta);
		return getProgressFormatted();
	}

	public String increaseAndGetProgressFormatted() {
		return increaseAndGetProgressFormatted(1.0);
	}
	
}
