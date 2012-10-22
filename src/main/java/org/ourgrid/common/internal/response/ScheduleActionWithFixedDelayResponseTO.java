package org.ourgrid.common.internal.response;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;


public class ScheduleActionWithFixedDelayResponseTO implements IResponseTO {
	

	private final String RESPONSE_TYPE = OurGridResponseConstants.SCHEDULED_ACTION_WITH_FIXED_DELAY;
	
	
	private Serializable handler;
	private String actionName;
	private long initialDelay;
	private long delay;
	private TimeUnit timeUnit;
	private boolean storeFuture = false;
	private boolean hasInitialDelay = false;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public void setHandler(Serializable handler) {
		this.handler = handler;
	}

	public Serializable getHandler() {
		return handler;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
		this.hasInitialDelay = true;
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getDelay() {
		return delay;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setStoreFuture(boolean storeFuture) {
		this.storeFuture = storeFuture;
	}

	public boolean storeFuture() {
		return storeFuture;
	}

	public boolean hasInitialDelay() {
		return hasInitialDelay;
	}
}