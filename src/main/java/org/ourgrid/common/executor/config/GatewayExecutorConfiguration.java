package org.ourgrid.common.executor.config;

import java.io.File;

import org.ourgrid.worker.WorkerConstants;

public class GatewayExecutorConfiguration extends AbstractExecutorConfiguration {

	public GatewayExecutorConfiguration(File rootDir, String[] propNames) {
		super(rootDir, propNames);
	}

	public static enum PROPERTIES{DESTINATION_GRID, PUBLIC_DIR_PATH, PUBLIC_DIR_URL, WS_URL}

	public GatewayExecutorConfiguration(File rootDir) {
		super(rootDir, parseProperties());
	}

	public void setDefaultProperties() {
		this.properties.put(WorkerConstants.PREFIX + PROPERTIES.DESTINATION_GRID.toString(), "EGEE");
		this.properties.put(WorkerConstants.PREFIX + PROPERTIES.PUBLIC_DIR_PATH.toString(), "/home/ourgrid/gateway");
		this.properties.put(WorkerConstants.PREFIX + PROPERTIES.PUBLIC_DIR_URL.toString(), "http://www.lsd.ufcg.edu.br/~ourgrid/gateway");
		this.properties.put(WorkerConstants.PREFIX + PROPERTIES.WS_URL.toString(), "http://localhost:8080");
	}
	
	private static String[] parseProperties(){
		
		String[] properties = new String[PROPERTIES.values().length] ;
		
		for (int i = 0; i < PROPERTIES.values().length; i++) {
			properties[i] = WorkerConstants.PREFIX + PROPERTIES.values()[i].toString();
		}
		
		return properties;
	}

}
