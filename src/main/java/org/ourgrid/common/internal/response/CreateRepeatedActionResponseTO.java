package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;


/**
 * Requirement 302
 */
public class CreateRepeatedActionResponseTO implements IResponseTO {
	

	private final String RESPONSE_TYPE = OurGridResponseConstants.CREATE_REPEATED_ACTION;
	
	
	private String actionName;
	private Object repeatedAction;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Object getRepeatedAction() {
		return repeatedAction;
	}

	public void setRepeatedAction(Object repeatedAction) {
		this.repeatedAction = repeatedAction;
	}
}