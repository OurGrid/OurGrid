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
package org.ourgrid.broker.ui.async.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ourgrid.broker.ui.async.model.BrokerAsyncUIModel;
import org.ourgrid.broker.ui.async.model.BrokerGetCompleteStatusRepeatedAction;
import org.ourgrid.common.interfaces.management.BrokerManager;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.main.CompilerException;
import org.ourgrid.common.specification.main.DescriptionFileCompile;
import org.ourgrid.common.ui.OurGridUIController;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncApplicationClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class BrokerAsyncApplicationClient extends AsyncApplicationClient<BrokerManager, BrokerAsyncManagerClient> implements OurGridUIController {

	private BrokerAsyncUIModel model;
	
	public static final String BROKER_GET_STATUS_ACTION = "BROKER_GET_STATUS_ACTION";
	
	public BrokerAsyncApplicationClient(ModuleContext context, BrokerAsyncUIModel model,  ConnectionListener listener) 
		throws CommuneNetworkException, ProcessorStartException {
		
		super("BROKER_ASYNC_UI", context, listener);
		this.model = model;
	}
	
	protected InitializationContext<BrokerManager, BrokerAsyncManagerClient> createInitializationContext() {
		return new BrokerAsyncInitializationContext();
	}
	
	@Override
	protected void deploymentDone() {
		addActionForRepetition(BROKER_GET_STATUS_ACTION, new BrokerGetCompleteStatusRepeatedAction());
	}
	
	public void loadProperties() {
		model.loadProperties();
	}

	public void saveProperties() throws IOException {
		model.saveProperties();
	}

	public void setProperty(String prop, String value) {
		model.setProperty(prop, value);
	}

	/*
	 * Intuitive enough
	 */
	public void cleanJobHistory() {
		getModel().cleanJobHistory();
	}

	/*
	 * Cleans job of is jodID
	 */
	public void cleanJob(int jobID) {
		getManager().cleanFinishedJob(getManagerClient(), jobID);
	}

	public void cleanAllJobs() {
		getManager().cleanAllFinishedJobs(getManagerClient());
	}

	/*
	 * Cancels job of id jobID
	 */
	public void cancelJob(int jobID) {
		getManager().cancelJob(getManagerClient(), jobID);
	}

	/*
	 * Adds job at "jdf"
	 */
	public void addJob(String jdf, boolean addToHistory) throws CompilerException {
		JobSpecification theJob = DescriptionFileCompile.compileJDF( jdf );
		getManager().addJob(getManagerClient(), theJob);
		
		if (addToHistory)
			getModel().addJobToHistory(jdf);
		
	}

	/**
	 * Submits a new JDL job to the Broker.
	 * 
	 * @param jdl The JDL file path.
	 * @param addToHistory <code>true</code> to add this jdl to the history of submitted files. 
	 * @throws CompilerException When the file path is invalid or the content does not follow 
	 * the JDL specification
	 */
	public void addJDLJob(String jdl, boolean addToHistory) throws CompilerException {
		assert jdl != null: "Null must not be passed by the GUI";
		
		List<JobSpecification> jobs = DescriptionFileCompile.compileJDL( jdl );
		
		for ( JobSpecification jobSpec : jobs ) {
			getManager().addJob(getManagerClient(), jobSpec);
			
			if (addToHistory)
				getModel().addJobToHistory(jdl);
		}
		
	}

	
	/**
	 * Asynchronous request for the broker complete status.
	 */
	public void getCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
	}

	public void setBrokerStartOnRecovery(boolean b) {
		this.model.setBrokerStartOnRecovery(b);
	}

	public BrokerAsyncUIModel getModel() {
		return model;
	}

	public void brokerStarted() {
		getModel().brokerStarted();
		
		if (getModel().isStatusFutureCancelled()) {
			getModel().setStatusFuture(scheduleActionWithFixedDelay(
					BrokerAsyncApplicationClient.BROKER_GET_STATUS_ACTION, 2, 10, 
					TimeUnit.SECONDS, null));
		}
		
	}

	public void brokerStopped() {
		getModel().brokerStopped();
		getModel().cancelStatusFuture();
	}

	public void restoreDefaultPropertiesValues() {
		model.restoreDefaultPropertiesValues();
	}

}
