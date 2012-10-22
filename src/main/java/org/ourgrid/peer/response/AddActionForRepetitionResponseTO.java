package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class AddActionForRepetitionResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.ADD_ACTION_FOR_REPETITION;

	
	private String actionName;
	private Class<?> actionClass;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setActionClass(Class<?> actionClass) {
		this.actionClass = actionClass;
	}

	public Class<?> getActionClass() {
		return actionClass;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getActionName() {
		return actionName;
	}
}
