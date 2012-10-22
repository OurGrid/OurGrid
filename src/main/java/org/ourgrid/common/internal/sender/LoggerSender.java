package org.ourgrid.common.internal.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 302
 */
public class LoggerSender implements SenderIF<LoggerResponseTO>{

	public void execute(LoggerResponseTO response, ServiceManager manager) {
		CommuneLogger logger = manager.getLog();
		
		String message = response.getMessage();
		
		switch (response.getType()) {
		
		case LoggerResponseTO.DEBUG:
			logger.debug(message);
			break;
			
		case LoggerResponseTO.WARN:
			logger.warn(message);
			break;
			
		case LoggerResponseTO.ERROR:
			if (response.getError() == null)
				logger.error(message);
			else
				logger.error(message, response.getError());
			break;
			
		case LoggerResponseTO.INFO:
			logger.info(message);
			break;
			
		case LoggerResponseTO.TRACE:
			logger.trace(message);
			break;
			
		case LoggerResponseTO.FATAL:
			logger.fatal(message, response.getError());
			break;
			
		default:
			return;
		}
	}
}
