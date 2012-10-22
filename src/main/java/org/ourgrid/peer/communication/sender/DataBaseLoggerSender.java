package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.peer.response.DataBaseLoggerResponseTO;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class DataBaseLoggerSender implements SenderIF<DataBaseLoggerResponseTO>{

	private CommuneLogger logger = CommuneLoggerFactory.getInstance().gimmeALogger(getClass());

	public void execute(DataBaseLoggerResponseTO response, ServiceManager manager) {
		
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
			logger.fatal(message, new Throwable(message));
			break;
			
		default:
			return;
		}
	}
}
