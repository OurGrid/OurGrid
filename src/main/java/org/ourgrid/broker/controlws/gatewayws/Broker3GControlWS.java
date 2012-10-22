package org.ourgrid.broker.controlws.gatewayws;

import java.net.URL;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.ourgrid.broker.controlws.WSJobSpec;
import org.ourgrid.broker.controlws.gatewayws.transferserver.Broker3GTransferServer;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;

@WebService()
public class Broker3GControlWS {
	
	private static final String PROPERTIES_FILENAME = "/resources/webservice/broker3g.properties";
	
	private Broker3GControlWSFacade facade;

	private ModuleContext context;
	
	public Broker3GControlWS() {
		URL resource = Broker3GContextFactory.class.getResource(PROPERTIES_FILENAME);
		this.context = new Broker3GContextFactory(
				new PropertiesFileParser(resource.getPath())).createContext();
		new Thread(new Broker3GTransferServer(context)).start();
	}
	
	private Broker3GControlWSFacade getFacade() {
		if (facade == null) {
			try {
				facade = new Broker3GControlWSFacade(context);
			} catch (Throwable e) {
				facade = null;
				throw new IllegalArgumentException(e);
			} 
		}
		
		return facade;
	}
	
	@WebMethod
	public int submitJob(WSJobSpec job) {
		return getFacade().submitJob(job);
	}

	@WebMethod
	public String getStatus(int jobId) {
		return getFacade().getJobStatus(jobId);
	}

	@WebMethod
	public boolean cancelJob(int jobId) {
		return getFacade().cancelJob(jobId);
	}

	@WebMethod
	public boolean cleanJob(int jobID) {
		return getFacade().cleanJob(jobID);
	}
	
}
