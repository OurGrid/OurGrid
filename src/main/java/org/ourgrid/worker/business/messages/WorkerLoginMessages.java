package org.ourgrid.worker.business.messages;

public class WorkerLoginMessages {

	public static String getWorkerCanNotLoggedMessage(String message) {
		return "This Worker was not able to login, cause: " + message;
	}
	
	public static String getWorkerLoginSucceededMessage() {
		return "This Worker has successfully logged in.";
	}
	
	public static String getWorkerAlreadyLoggedMessage() {
		return "This Worker is already logged in. This message will be ignored.";
	}
}
