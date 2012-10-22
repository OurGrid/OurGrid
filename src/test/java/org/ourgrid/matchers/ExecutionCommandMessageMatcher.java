package org.ourgrid.matchers;

import java.util.Map;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;

public class ExecutionCommandMessageMatcher implements IArgumentMatcher {
	
	private Map<String, String> envVars;
	
	private String playpenDir;
	
	private String storageDir;
	
	private String clientPubKey;
	
	private String command;
	
	private long requestID;
	
	public ExecutionCommandMessageMatcher(String playpenDir, String storageDir, String clientPubKey, String command,
			long requestID) {
		this.playpenDir = playpenDir;
		this.storageDir = storageDir;
		this.clientPubKey = clientPubKey;
		this.command = command;
		this.requestID = requestID;
	}
	
	public ExecutionCommandMessageMatcher(Map<String, String> envVars, String clientPubKey, String command,
			long requestID) {
		this.envVars = envVars;
		this.storageDir = null;
		this.clientPubKey = null;
		this.clientPubKey = clientPubKey;
		this.command = command;
		this.requestID = requestID;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if(arg0.getClass() != String.class) {
			return false;
		}
		
		String anotherMessage = (String) arg0;
		if (envVars == null) {
			
			return anotherMessage.startsWith("Command scheduled to execution. Command:") 
				&& anotherMessage.endsWith("Client public key: [" + clientPubKey + "].");
			
			
			/*Pattern pattern = Pattern.compile("Command scheduled to execution. Command: " + command + "; RequestID: " + requestID + " ;" +
					" Environment variables: \\{STORAGE=" + ".+\\\\" + storageDir + ", PLAYPEN=" + playpenDir + 
					"\\\\worker-.+" + "\\} ; Client public key: \\[" + clientPubKey + "\\]\\.");
			
			Matcher matcher = pattern.matcher(anotherMessage);

			return matcher.matches();*/
		}
			
		String message = ("Command scheduled to execution. Command: " + command + " ; RequestID: " + requestID + " ;" +
			    " Environment variables: " + envVars + " ; Client public key: [" + clientPubKey + "].");
		
		return message.equals(anotherMessage);
	}
		
	public static String eqMatcher(String playpenDir, String storageDir, String clientPubKey, String command,
			long requestID) {
		EasyMock.reportMatcher(new ExecutionCommandMessageMatcher(playpenDir, storageDir,
				clientPubKey, command, requestID));
		return null;
	}
	
	public static String eqMatcher(Map<String, String> envVars, String clientPubKey, String command,
			long requestID) {
		EasyMock.reportMatcher(new ExecutionCommandMessageMatcher(envVars,
				clientPubKey, command, requestID));
		return null;
	}
}
