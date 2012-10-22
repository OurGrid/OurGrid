/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.util.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.ourgrid.acceptance.util.TestJavaFileUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.worker.WorkerAcceptanceTestComponent;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConfiguration;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class Req_003_Util extends WorkerAcceptanceUtil {

	public Req_003_Util(ModuleContext context) {
		super(context);
	}
	
	public WorkerComponent createWorkerComponent() throws CommuneNetworkException,
			ProcessorStartException, InterruptedException {
		configProperties(false, false, false, false, null, null, false, "", null);
		
		return createWorkerComponent(false); 
	}
	
	public WorkerComponent createWorkerComponent(boolean withIdlenessDetector, boolean invalidPlaypenDir,
			boolean invalidStorageDir) 
		throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		Map<String, String> oldProperties = context.getProperties();
		oldProperties.putAll(configProperties(withIdlenessDetector,
				false, invalidPlaypenDir, invalidStorageDir, null, null, false, "", null));
		context = new ModuleContext(oldProperties);
		
		WorkerComponent component = new WorkerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}

	public WorkerComponent createWorkerComponent(boolean withIdlenessDetector)
			throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		Map<String, String> oldProperties = context.getProperties();
		oldProperties.putAll(configProperties(withIdlenessDetector, false, false, false, null, null,
				false, "", null));
		context = new ModuleContext(oldProperties);
		
		WorkerComponent component = new WorkerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}
	
	public WorkerComponent createWorkerComponent(ServiceID peerID,
			boolean withIdlenessDetector) throws CommuneNetworkException, 
			ProcessorStartException, InterruptedException {
		Map<String, String> oldProperties = context.getProperties();
		oldProperties.putAll(configProperties(withIdlenessDetector, false, false, false, null, null,
				false, "", peerID));
		context = new ModuleContext(oldProperties);
		
		WorkerComponent component = new WorkerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}
	
	public WorkerComponent createWorkerComponent(boolean withReportSpec, String reportTime) throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		Map<String, String> oldProperties = context.getProperties();
		oldProperties.putAll(configProperties(false, false, false, false, null, null,
				withReportSpec, reportTime, null));
		context = new ModuleContext(oldProperties);
		
		WorkerComponent component = new WorkerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}
	

	public WorkerComponent createWorkerComponent(boolean withIdlenessDetector,
			boolean withScheduleIdleness, String scheduleTime, String idlenessTime) throws CommuneNetworkException, ProcessorStartException, InterruptedException {
		
		Map<String, String> oldProperties = context.getProperties();
		oldProperties.putAll(configProperties(
				withIdlenessDetector, withScheduleIdleness, false, false, scheduleTime, idlenessTime,
				false, "", null));
		context = new ModuleContext(oldProperties);
		
		WorkerComponent component = new WorkerAcceptanceTestComponent(context);
	    application = component;
	    
	    Thread.sleep(2000);
	    
	    return component;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> configProperties(boolean withIdlenessDetector, boolean withScheduleIdleness,
			boolean invalidPlaypenDir, boolean invalidStorageDir, String scheduleTime, String idlenessTime,
			boolean withReportSpec, String workerSpecReportTime, ServiceID peerID) {
		
		Properties currentProperties = new Properties();
		
		try {
			InputStream stream = new FileInputStream(PROPERTIES_FILENAME);
			currentProperties.load(stream);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (peerID != null) {
			currentProperties.setProperty(WorkerConfiguration.PROP_PEER_ADDRESS, peerID.getUserName() + "@" + peerID.getServerName());
		}
		
		currentProperties.setProperty(WorkerConfiguration.PROP_WORKER_SPEC_REPORT, withReportSpec ? "yes": "no");
		if(workerSpecReportTime.equals("")) workerSpecReportTime = WorkerConfiguration.DEF_WORKER_SPEC_REPORT_TIME;
		currentProperties.setProperty(WorkerConfiguration.PROP_WORKER_SPEC_REPORT_TIME, workerSpecReportTime);
		
		currentProperties.setProperty(WorkerConfiguration.PROP_IDLENESS_DETECTOR, withIdlenessDetector ? "yes" : "no");
		
		currentProperties.setProperty(WorkerConfiguration.PROP_USE_IDLENESS_SCHEDULE, withScheduleIdleness ? "yes" : "no");
		
		String playpenDir = invalidPlaypenDir ? 
				WorkerAcceptanceUtil.DEF_INVALID_PLAYPEN_ROOT_PATH : WorkerAcceptanceUtil.DEF_PLAYPEN_ROOT_PATH;
		currentProperties.setProperty(WorkerConfiguration.PROP_PLAYPEN_ROOT, playpenDir);
		
		String storageDir = invalidStorageDir ? 
				WorkerAcceptanceUtil.DEF_INVALID_STORAGE_ROOT_PATH : WorkerAcceptanceUtil.DEF_STORAGE_ROOT_PATH;
		currentProperties.setProperty(WorkerConfiguration.PROP_STORAGE_DIR, storageDir);
		
		File filePlaypen = new File(playpenDir);
		filePlaypen.mkdirs();
		
		if (invalidPlaypenDir) {
			filePlaypen.setReadOnly();
		} else {
			TestJavaFileUtil.setReadAndWrite(filePlaypen);
		}
		
		File fileStorage = new File(storageDir);
		fileStorage.mkdirs();
		
		if (invalidStorageDir) {
			fileStorage.setReadOnly();
		} else {
			TestJavaFileUtil.setReadAndWrite(fileStorage);
		}
		
		if (idlenessTime != null) {
			currentProperties.setProperty(WorkerConfiguration.PROP_IDLENESS_TIME, idlenessTime);
		}
		
		try {
			OutputStream output = new FileOutputStream(PROPERTIES_FILENAME);
			currentProperties.store(output, "");
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (scheduleTime != null) {
			currentProperties.setProperty(WorkerConfiguration.PROP_IDLENESS_SCHEDULE_TIME, scheduleTime);
		} else {
			currentProperties.setProperty(
					WorkerConfiguration.PROP_IDLENESS_SCHEDULE_TIME, WorkerConfiguration.DEF_PROP_IDLENESS_SCHEDULE_TIME);
		}
		
		Map<String, String> newMap = new HashMap<String, String>();
		
		for (Iterator<Entry<Object, Object>> iterator = currentProperties.entrySet().iterator(); iterator.hasNext();) {
			Entry<Object, Object> entry = (Entry) iterator.next();
			newMap.put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		return newMap;
	}
	
}
