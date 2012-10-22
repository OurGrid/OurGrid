/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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
package org.ourgrid.peer.business.controller;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.common.statistics.beans.peer.Worker;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.controller.messages.WorkerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.LocalAllocableWorker;
import org.ourgrid.peer.to.LocalWorker;
import org.ourgrid.peer.to.RemoteAllocableWorker;
import org.ourgrid.reqtrace.Req;

import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;

/**
 * Performs Worker Spec Listener Controller actions
 */
public class WorkerSpecListenerController {

	private static WorkerSpecListenerController instance = null;
	
	public static WorkerSpecListenerController getInstance() {
		if (instance == null) {
			instance = new WorkerSpecListenerController();
		}
		return instance;
	}
	
	private WorkerSpecListenerController() {}
	
	/** 
	 * Updates the worker specification, with the new values for dynamic attributes
	 * @param workerSpec New values for dynamic attributes
	 * @param workerPublicKey Worker public key
	 */
	@Req("REQ107")
	public void updateWorkerSpec(List<IResponseTO> responses, WorkerSpecification workerSpec, 
			String workerPublicKey, String workerUserAtServer, String myUserAtServer) {
		
		AllocableWorker allocableWorker = PeerDAOFactory.getInstance().getAllocationDAO().getAllocableWorker(workerPublicKey);
		
		Worker worker = WorkerControl.getInstance().findActiveWorker(responses, workerUserAtServer);
		
		//Local worker
		if (worker != null && myUserAtServer.equals(worker.getPeer().getAddress())) {
			
			LocalWorker localWorker = WorkerControl.getInstance().getLocalWorker(responses,
					workerUserAtServer);
			
			if ((localWorker == null) 
					&& (allocableWorker == null || allocableWorker.isWorkerLocal())) {
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						WorkerMessages.getUnknownWorkerUpdatingSpecMessage(workerPublicKey),
						LoggerResponseTO.DEBUG);
				responses.add(loggerResponse);
				
				return;
			}
			
			if (localWorker != null) {
				String localWorkerDID = localWorker.getWorkerManagementAddress();
				
				WorkerSpecification newWorkerSpec = updateWorkerSpecAttributes(responses, workerSpec, 
						localWorker.getAttributes(), localWorkerDID);
				localWorker.setWorkerSpecification(newWorkerSpec);
				
				LocalAllocableWorker localAllocableWorker = 
					PeerDAOFactory.getInstance().getAllocationDAO().getLocalAllocableWorker(
							localWorker.getPublicKey());
				
				if (localAllocableWorker != null) {
					localAllocableWorker.getLocalWorker().setWorkerSpecification(newWorkerSpec);
				}	
				
				WorkerControl.getInstance().updateWorker(responses, localWorker.getWorkerUserAtServer(), 
						newWorkerSpec.getAttributes(), newWorkerSpec.getAnnotations());
				
				return;
			}
		}	
		
		//Remote Worker
		RemoteAllocableWorker remoteAllocWorker = (RemoteAllocableWorker) allocableWorker;
		
		if (remoteAllocWorker == null) {
			LoggerResponseTO loggerResponse = new LoggerResponseTO(
					WorkerMessages.getUnknownWorkerUpdatingSpecMessage(workerPublicKey),
					LoggerResponseTO.DEBUG);
			responses.add(loggerResponse);
			
			return;
		}
		
		WorkerSpecification rWorkerSpec = remoteAllocWorker.getWorkerSpecification();
		
		WorkerSpecification newWorkerSpec = updateWorkerSpecAttributes(responses, workerSpec, rWorkerSpec.getAttributes(),
				allocableWorker.getWorkerAddress());
		
		remoteAllocWorker.setWorkerSpec(newWorkerSpec);
	}
	
	/**
	 * Updates the worker especification attributes
	 * @param workerSpec New values for dynamic attributes 
	 * @param oldAttributes Old attributes
	 * @param workerDeploymentID Worker object identification
	 * @return New worker especification updated
	 */
	@Req("REQ107")
	private WorkerSpecification updateWorkerSpecAttributes(List<IResponseTO> responses, WorkerSpecification workerSpec, Map<String, String> oldAttributes, 
			String workerAddress) {
		
		Map<String, String> newAttributes = workerSpec.getAttributes();
		Collection<String> newAttributeKeys = newAttributes.keySet();
		Map<String, String> attributes = new LinkedHashMap<String, String>(oldAttributes);
		
		String expression = newAttributes.get( WorkerSpecificationConstants.EXPRESSION );
		if(expression != null){
			String oldExpression = attributes.get( WorkerSpecificationConstants.EXPRESSION );
			RecordExpr expr = (RecordExpr) new ClassAdParser(expression).parse();
			RecordExpr oldExpr = (RecordExpr) new ClassAdParser(oldExpression).parse();
			expr.insertAttribute( OurGridSpecificationConstants.USERNAME, oldExpr.lookup( OurGridSpecificationConstants.USERNAME ));
			expr.insertAttribute( OurGridSpecificationConstants.SERVERNAME, oldExpr.lookup( OurGridSpecificationConstants.SERVERNAME ));
			attributes.put( WorkerSpecificationConstants.EXPRESSION, expr.toString() );
		}else{
			for (String newAttributeKey : newAttributeKeys) {

				if (newAttributeKey.equalsIgnoreCase(OurGridSpecificationConstants.ATT_USERNAME) ||
						newAttributeKey.equalsIgnoreCase(OurGridSpecificationConstants.ATT_SERVERNAME)) {
					newAttributes.remove(newAttributeKey);
					continue;
				}

				String newAttributeValue = newAttributes.get(newAttributeKey);

				if (newAttributeValue == null) {
					attributes.remove(newAttributeKey);
					continue;
				}

				attributes.put(newAttributeKey, newAttributeValue);
			}
		}
		
		LoggerResponseTO loggerResponse = new LoggerResponseTO(
				WorkerMessages.getWorkerSpecUpdatedMessage(workerAddress, newAttributes),
				LoggerResponseTO.DEBUG);
		responses.add(loggerResponse);
		
		return new WorkerSpecification(attributes);
	}
}