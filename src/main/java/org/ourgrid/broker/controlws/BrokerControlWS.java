package org.ourgrid.broker.controlws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;

@WebService()
public class BrokerControlWS {
	

	private BrokerControlWSFacade facade;
	
	public BrokerControlWS() {
	}
	
	@WebMethod
	public String start() {
		facade = BrokerWSContextCreator.createWSFacade();
		ControlOperationResult startResult = facade.start();
		
		if (startResult.hasAnErrorOcurred()) {
			return startResult.getErrorCause().toString();
		}
		
		return "";
	}

	@WebMethod
	public String stop() {
		facade.stop(true, true);
		return "Broker Stopped";
	}
	
	@WebMethod
	public String addJob( WSJobSpec job ) {
		return facade.addJob(job).toString();
	}

	@WebMethod
	public String cancelJob( int jobID ) {
		return facade.cancelJob(jobID).toString();
	}

	@WebMethod
	public String cleanAllFinishedJobs() {
		return facade.cleanAllFinishedJobs().toString();
	}

	@WebMethod
	public String cleanFinishedJob( int jobID ) {
		return facade.cleanFinishedJob(jobID).toString();
	}

	@WebMethod
	public BrokerCompleteStatus getBrokerCompleteStatus() {
		return facade.getBrokerCompleteStatus();
	}

	@WebMethod
	public void sendFile(byte[] file, String fileName) {
	   facade.sendFile(file, fileName);
	}

	@WebMethod
	public byte[] getFile(String fileName) {
		return facade.getFile(fileName);
	}	 
	
}
