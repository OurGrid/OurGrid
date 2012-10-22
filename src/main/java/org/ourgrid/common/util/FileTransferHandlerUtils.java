package org.ourgrid.common.util;

public class FileTransferHandlerUtils {

	public static String getOperationType(String description) {
		return description.split(":")[0];
	}
	
	public static String getDestinationFile(String description) {
		return description.split(":")[1];
	}
	
	public static String getTransferDescription(String operationType, String destinationFile) {
		return operationType + ":" + destinationFile;
	}
}
