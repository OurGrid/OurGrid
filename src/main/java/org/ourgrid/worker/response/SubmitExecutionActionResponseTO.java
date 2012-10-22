package org.ourgrid.worker.response;

import java.util.Map;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.worker.communication.sender.WorkerResponseConstants;




public class SubmitExecutionActionResponseTO implements IResponseTO {
	
	
	private final static String RESPONSE_TYPE = WorkerResponseConstants.SUBMIT_EXECUTION_ACTION;
	
	
	private String command;
	private Map<String, String> envVars;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setEnvVars(Map<String, String> envVars) {
		this.envVars = envVars;
	}

	public Map<String, String> getEnvVars() {
		return envVars;
	}
}
