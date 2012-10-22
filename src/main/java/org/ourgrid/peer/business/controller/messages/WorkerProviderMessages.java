package org.ourgrid.peer.business.controller.messages;

public class WorkerProviderMessages {

	public static String getRemoteWorkerProviderFailureMessage(String rwpAddress) {
		return "The RemoteWorkerProvider [" + rwpAddress + "] has failed.";
	}

	public static String getRemoteWorkerProviderNotRemovedMessage(
			String rwpAddress) {
		return "The RemoteWorkerProvider [" + rwpAddress + "] has failed but it was not removed from DAO.";
	}

}
