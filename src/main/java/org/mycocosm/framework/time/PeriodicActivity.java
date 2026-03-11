package org.mycocosm.framework.time;

import java.time.Duration;

public class PeriodicActivity {
	private Duration interval = null;
	private Runnable activity;
	private long lastRun;

	public static final PeriodicActivity doEveryMinute(Runnable activity) {
		return new PeriodicActivity(TimeInterval.MIN_INTERVAL, activity);
	}

	public PeriodicActivity(Duration interval, Runnable activity) {
		this.interval = interval;
		this.activity = activity;
		this.lastRun=0;
	}
	public PeriodicActivity(TimeInterval interval, Runnable activity) {
		this(interval.toDuration(),activity);
	}
	
	public PeriodicActivity excludingInitialRun() {
		this.lastRun=System.currentTimeMillis();
		return this;
	}

	public void mayRun() {
		if (activity!=null) {
			if (interval!=null) {
				if (timeToRun()) {
					activity.run();
					lastRun=System.currentTimeMillis();
				}
			} else {
				activity.run();
			}
		}
	}
	public void run() {
		if (activity!=null) {
			activity.run();
			lastRun=System.currentTimeMillis();
		}
	}
	private boolean timeToRun() {
		return System.currentTimeMillis()-lastRun>interval.toMillis();
	}		

}
