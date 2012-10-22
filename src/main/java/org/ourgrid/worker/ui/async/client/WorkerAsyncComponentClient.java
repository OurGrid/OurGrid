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
package org.ourgrid.worker.ui.async.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.interfaces.management.WorkerManager;
import org.ourgrid.common.ui.OurGridUIController;
import org.ourgrid.common.ui.servicesetup.ServiceSetupException;
import org.ourgrid.peer.ui.async.client.PeerAsyncApplicationClient;
import org.ourgrid.worker.ui.async.model.GetWorkerStatusRepeatedAction;
import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncApplicationClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Asynchronous Component Client for Worker Component
 *
 */
public class WorkerAsyncComponentClient extends 
	AsyncApplicationClient<WorkerManager, WorkerAsyncManagerClient> implements OurGridUIController {

	private WorkerAsyncUIModel model;

	public static final String GET_STATUS_ACTION = "GET_STATUS_ACTION";
	
	public WorkerAsyncComponentClient(ModuleContext context, WorkerAsyncUIModel model, ConnectionListener listener) 
		throws CommuneNetworkException, ProcessorStartException {
		
		super("WORKER_ASYNC_UI", context, listener);
		this.model = model;
	}

	@Override
	protected void deploymentDone() {
		addActionForRepetition(GET_STATUS_ACTION, new GetWorkerStatusRepeatedAction());
	}
	
	/**
	 * Requests the Worker Component to pause.
	 * Uses the ControlClient as callback.
	 */
	public void pause() {
		getManager().pause(getManagerClient());
		model.workerPaused();
	}
	
	/**
	 * Requests the Worker Component to resume.
	 * Uses the ControlClient as callback.
	 */
	public void resume() {
		getManager().resume(getManagerClient());
		model.workerResumed();
	}
	
	@Override
	protected InitializationContext<WorkerManager, WorkerAsyncManagerClient> createInitializationContext() {
		return new WorkerAsyncInitializationContext();
	}

	/**
	 * Load properties stored on peer properties file on the model.
	 */
	public void loadProperties() {
		model.loadProperties();
	}

	/**
	 * Saves current properties on its correspondent file
	 * @see WorkerAsyncUIModel.saveProperties
	 */
	public void saveProperties() throws IOException {
		model.saveProperties();
	}

	/**
	 * Sets a property value on the model.
	 * @param prop The property to be set
	 * @param value The new value for this property
	 */
	public void setProperty(String prop, String value) {
		model.setProperty(prop, value);
	}

	/**
	 * @return The model associated to this ComponentClient
	 */
	public WorkerAsyncUIModel getModel() {
		return model;
	}
	
	/**
	 * Requests worker complete status, using the 
	 * WorkerStatusProviderClient as callback object.
	 */
	public void getWorkerCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
	}
	
	/**
	 * Install peer as service
	 * @throws ServiceSetupException
	 */
	public void installAsService() throws ServiceSetupException {
		model.getServiceSetup().installAsService();
	}

	/**
	 * Uninstall peer service
	 * @throws ServiceSetupException
	 */
	public void uninstallService() throws ServiceSetupException {
		model.getServiceSetup().uninstallService();
	}

	public void workerStarted() {
		model.workerStarted();
		if (getModel().isStatusFutureCancelled()) {
			getModel().setStatusFuture(scheduleActionWithFixedDelay(
					PeerAsyncApplicationClient.GET_STATUS_ACTION, 0, 10, 
					TimeUnit.SECONDS, null));
		}
	}
	
	public void workerStopped() {
		model.workerStopped();
		model.cancelStatusFuture();
	}

	public void restoreDefaultPropertiesValues() {
		getModel().restoreDefaultPropertiesValues();
		
	}

}
