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

package org.ourgrid.broker.communication.operations;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ourgrid.common.CommonConstants;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.job.IOBlock;
import org.ourgrid.common.specification.job.IOEntry;
import org.ourgrid.common.specification.job.TaskSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.matcher.Matcher;
import org.ourgrid.worker.WorkerConfiguration;

/**
 * This class parses the specification of a <code>Replica</code> instance and
 * translates it to a set of <code>Operation</code> instances that represent
 * each command in the replica specification.
 */
public class ReplicaParser {

	private TaskSpecification spec;

	private GridProcess gridProcess;

	private Map<String,String> envVars;

	private Map<GenericTransferHandle,InitOperation> putOperations;

	private RemoteOperation remotePhaseOperation;

	private SabotageCheckOperation sabotageCheckOperation;

	private Map<GenericTransferHandle,GetOperation> finalPhaseOperations;

	private long requestID;

	private final GridProcessHandle replicaHandle;

	private final Matcher matcher;

	private final WorkerSpecification workerSpec;
	
	private final String workerID;


	public ReplicaParser( GridProcess replica, Matcher matcher) {

		this.workerSpec = replica.getWorkerEntry().getWorkerSpecification();
		this.workerID = replica.getWorkerEntry().getWorkerID();
		this.replicaHandle = replica.getHandle();
		this.gridProcess = replica;
		this.matcher = matcher;
		this.spec = replica.getSpec();
		this.requestID = replica.getWorkerEntry().getRequestID();
		
		this.putOperations = CommonUtils.createSerializableMap();
		this.remotePhaseOperation = null;
		this.finalPhaseOperations = CommonUtils.createSerializableMap();
		this.sabotageCheckOperation = null;
		
		createEnvVars();
	}


	/**
	 * Creates a <code>Map</code> of environment variable names and their
	 * respective values based on the <code>Replica</code> and
	 * <code>Worker</code> instances to which this <code>ReplicaParser</code>
	 * is associated.
	 */
	private void createEnvVars() {
		
		envVars = new LinkedHashMap<String,String>( filterWorkerAttributes(workerSpec.getAttributes()) );

		final String username = workerSpec.getAttribute( OurGridSpecificationConstants.ATT_USERNAME );
		final String server = workerSpec.getAttribute( OurGridSpecificationConstants.ATT_SERVERNAME );
		envVars.put( "PROC", username + "@" + server );
		envVars.put( "TASK", Integer.toString( gridProcess.getTaskId() ) );
		envVars.put( "JOB", Integer.toString( gridProcess.getJobId() ) );
		
		List<String> outputs = parseOutputs(false);
		String outputsStr = createFilesStr(outputs);
		
		if (outputsStr != null && outputsStr.length() > 0) {
			envVars.put( WorkerConfiguration.ATT_OUTPUTFILES, outputsStr);
		}
		
		List<String> inputs = parseInputs(false);
		String inputStr = createFilesStr(inputs);
		
		if (inputStr != null && inputStr.length() > 0) {
			envVars.put( WorkerConfiguration.ATT_INPUTFILES, inputStr);
		}
	}


	private String createFilesStr(List<String> outputs) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (String output : outputs) {
			if (!first) {
				builder.append(WorkerConfiguration.SEPARATOR_CHAR);
			} else {
				first = false;
			}
			builder.append(output);
		}
		return builder.toString();
	}


	private Map< String, String> filterWorkerAttributes( Map<String, String> attributes ) {

		Map<String, String> filteredAtt = CommonUtils.createSerializableMap();
		
		Set<Entry<String, String>> entrySet = attributes.entrySet();
		for (Entry<String, String> entry : entrySet) {
			
			String key = entry.getKey();
			String value = entry.getValue();
			if(key.startsWith(WorkerConfiguration.PREFIX)) {
				filteredAtt.put(key.replaceFirst(WorkerConfiguration.PREFIX, ""), value);
			}
			
			if (key.equals(OurGridSpecificationConstants.ATT_USERNAME)) {
				filteredAtt.put( key, value );
			}
			
			if (key.equals(OurGridSpecificationConstants.ATT_SERVERNAME)) {
				filteredAtt.put( key, value );
			}
		}
		
		return filteredAtt;
	}


	/**
	 * Parses all replica init phase operations.
	 */
	private List<String> parseInputs(boolean createInitOperation) {

		IOBlock input = spec.getInitBlock();
		List<String> inputs = new LinkedList<String>();
		
		if ( input != null ) {

			Iterator<String> conditionIterator = input.getConditions();

			while ( conditionIterator.hasNext() ) {

				String condition = conditionIterator.next();
				
				//Evaluating using JDL expressions or JDF attributes
				boolean result;
				if(workerSpec.usingClassAd()){
					result = true;
				}else{
					result = matcher.match( condition, workerSpec.getAttributes() ); 
				}
				
				if ( result ) {

					List<IOEntry> ioEntries = input.getEntry( condition );

					if ( ioEntries != null ) {

						Iterator<IOEntry> ioEntriesIterator = ioEntries.iterator();

						while ( ioEntriesIterator.hasNext() ) {

							IOEntry ioEntry = ioEntriesIterator.next();

							String localFile = ioEntry.getSourceFile();
							String remoteFile = ioEntry.getDestination();

							localFile = StringUtil.replaceVariables( localFile, envVars );
							remoteFile = StringUtil.replaceVariables( remoteFile, envVars );

							inputs.add(remoteFile);
							
							if (createInitOperation) {
								String operation = ioEntry.getCommand();
								
								InitOperation putOperation = null;
								
								if ( operation.trim().equalsIgnoreCase( CommonConstants.PUT_TRANSFER ) ) {
									putOperation = new InitOperation( replicaHandle, requestID, workerID,
											localFile, remoteFile, 
											FileTransferHandlerUtils.getTransferDescription(CommonConstants.PUT_TRANSFER, remoteFile), 
											gridProcess.getResult());
									
								} else if ( operation.trim().equalsIgnoreCase( CommonConstants.STORE_TRANSFER ) ) {
									putOperation = new InitOperation( replicaHandle, requestID, workerID,
											localFile, remoteFile, 
											FileTransferHandlerUtils.getTransferDescription(CommonConstants.STORE_TRANSFER, remoteFile),
											gridProcess.getResult());
								}
								putOperations.put( putOperation.getHandle(), putOperation );
								
								gridProcess.getResult().addInitOperation(putOperation);
							}
							

						}

					}

				}

			}

		}
		
		return inputs;

	}

	/**
	 * Parses all replica remote phase operations.
	 */
	private void remotePhase() {

		remotePhaseOperation = new RemoteOperation( replicaHandle, requestID, workerID,  
				spec.getRemoteExec(), envVars );
	}


	/**
	 * Parses all replica final phase operations.
	 */
	private void finalPhase() {
		parseOutputs(true);
	}

	private List<String> parseOutputs(boolean createFinalOperation) {
		
		IOBlock output = spec.getFinalBlock();
		List<String> outputs = new LinkedList<String>();
		
		if ( output != null ) {

			Iterator<String> conditionIterator = output.getConditions();

			while ( conditionIterator.hasNext() ) {

				String condition = conditionIterator.next();
				
				//Evaluating using JDL expressions or JDF attributes
				boolean result;
				if(workerSpec.usingClassAd()){
					result = true;
				}else{
					result = matcher.match( condition, workerSpec.getAttributes() ); 
				}
				
				if ( result ) {

					List<IOEntry> ioEntries = output.getEntry( condition );

					if ( ioEntries != null ) {

						Iterator<IOEntry> ioEntriesIterator = ioEntries.iterator();

						while ( ioEntriesIterator.hasNext() ) {

							IOEntry ioEntry = ioEntriesIterator.next();

							String operation = ioEntry.getCommand();

							if ( operation.trim().equalsIgnoreCase( "GET" ) ) {
								
								String localFilePath = StringUtil.replaceVariables( ioEntry.getDestination(), envVars );
								String remoteFilePath = StringUtil.replaceVariables( ioEntry.getSourceFile(), envVars );
								
								outputs.add(remoteFilePath);

								if (createFinalOperation) {
									GetOperation getOperation = new GetOperation(replicaHandle, requestID, workerID,
											localFilePath, remoteFilePath, CommonConstants.GET_TRANSFER, 
											gridProcess.getResult() );
									
									gridProcess.getResult().addGetOperation(getOperation);
									
									finalPhaseOperations.put(getOperation.getHandle(), getOperation);
								}
							}
						}
					}
				}
			}
		}
		
		return outputs;
	}

	/**
	 * Parses all replica sabotage check phase operations.
	 */
	private void sabotageCheckPhase() {

		final String command = spec.getSabotageCheck();
		if ( command != null ) {
			sabotageCheckOperation = new SabotageCheckOperation( replicaHandle, requestID, workerID,
				command, spec.getSourceParentDir(), envVars);
		}
	}


	/**
	 * Parses the specification of the replica associated to this
	 * <code>ReplicaParser</code>, translating each command in the replica
	 * specification to a set of <code>Operation</code> instances that
	 * represent them.
	 */
	public void parse() {

		initPhase();
		remotePhase();
		finalPhase();
		sabotageCheckPhase();
	}

	

	private void initPhase() {
		parseInputs(true);
	}


	/**
	 * Gets parsed final operations in a collection.
	 * 
	 * @return parsed final operations in a collection.
	 */
	public Map<GenericTransferHandle, GetOperation> getFinalPhaseOperations() {

		return finalPhaseOperations;
	}
	
	/**
	 * Gets parsed put operations in a collection.
	 * 
	 * @return parsed final operations in a collection.
	 */
	public Map<GenericTransferHandle,InitOperation> getPutOperations() {

		return putOperations;
	}


	/**
	 * Gets parsed remote operations in a collection.
	 * 
	 * @return parsed final operations in a collection.
	 */
	public RemoteOperation getRemotePhaseOperation() {

		return remotePhaseOperation;
	}


	/**
	 * Gets parsed sabotage check operations.
	 * 
	 * @return parsed sabotage check operations.
	 */
	public SabotageCheckOperation getSabotagePhaseOperation() {

		return sabotageCheckOperation;
	}
	
	/**
	 * @return
	 */
	public GridProcessOperations getExecutionOperations() {
		
		Map<GenericTransferHandle,InitOperation> putOperations = getPutOperations();
		Map<GenericTransferHandle, GetOperation> finalPhaseOperation = getFinalPhaseOperations();
		RemoteOperation remotePhaseOperation = getRemotePhaseOperation();
		SabotageCheckOperation sabotageCheckOperation = getSabotagePhaseOperation();

		return new GridProcessOperations(putOperations, remotePhaseOperation, finalPhaseOperation, sabotageCheckOperation);

	}
}
