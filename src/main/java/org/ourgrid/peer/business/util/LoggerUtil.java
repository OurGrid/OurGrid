package org.ourgrid.peer.business.util;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.response.DataBaseLoggerResponseTO;

public class LoggerUtil {
	
	public static IResponseTO enter() {
		return new DataBaseLoggerResponseTO("Entering " + getMethodName(), DataBaseLoggerResponseTO.TRACE);
	}
	
	public static IResponseTO leave() {
		return new DataBaseLoggerResponseTO("Exiting " + getMethodName(), DataBaseLoggerResponseTO.TRACE);
	}
	
	public static IResponseTO exception(Exception e){
		return new DataBaseLoggerResponseTO("An Error Ocurred: ", DataBaseLoggerResponseTO.ERROR, e);
	}
	
	public static IResponseTO rollbackException(Exception e){
		return new DataBaseLoggerResponseTO("An Error Ocurred implies by an rollback with exception:"
				, DataBaseLoggerResponseTO.ERROR, e);
	}
	
	/**
	 * @return
	 */
	private static String getMethodName() {
		Thread currentThread = Thread.currentThread();
		StackTraceElement element = currentThread.getStackTrace()[3];
		return element.getMethodName();
	}
}
