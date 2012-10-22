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
package org.ourgrid.aggregator.communication.receiver;

import org.ourgrid.aggregator.AggregatorConfiguration;
import org.ourgrid.aggregator.business.dao.AggregatorDAOFactory;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.aggregator.business.requester.AggregatorRequestControl;
import org.ourgrid.aggregator.request.GetCompleteStatusRequestTO;
import org.ourgrid.aggregator.request.StartAggregatorRequestTO;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.AggregatorControlClient;
import org.ourgrid.common.interfaces.management.AggregatorManager;
import org.ourgrid.common.interfaces.status.AggregatorStatusProviderClient;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.internal.OurGridControlReceiver;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.RequestControlIF;
import org.ourgrid.common.internal.request.QueryRequestTO;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class controls some actions of this Module.
 * 
 */
public class AggregatorControlReceiver extends OurGridControlReceiver implements
		AggregatorManager {

	private static final String DEF_CONF_XML_PATH = "aggregator-hibernate.cfg.xml";

	private final String hibCfgPath;

	private static final String COMPONENT_NAME = "Aggregator";

	/**
	 * Constructor default.
	 */
	public AggregatorControlReceiver() {
		this(DEF_CONF_XML_PATH);
	}

	/**
	 * Constructor of this class.
	 * 
	 * @param hibCfgPath
	 *            {@link String}
	 */
	public AggregatorControlReceiver(String hibCfgPath) {
		this.hibCfgPath = hibCfgPath;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationServerController#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return COMPONENT_NAME;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationServerController#createDAOs()
	 */
	@Override
	protected void createDAOs() {
		AggregatorDAOFactory.getInstance().getAggregatorDAO();
	}

	/**
	 * Method that starts the component Aggregator.
	 */
	@Override
	protected void startComponent() throws Exception {
		StartAggregatorRequestTO request = new StartAggregatorRequestTO();

		ServiceID serviceID = new ServiceID(new ContainerID(getServiceManager()
				.getContainerContext().getProperty(
						AggregatorConfiguration.PROP_DS_USERNAME),
				getServiceManager().getContainerContext().getProperty(
						AggregatorConfiguration.PROP_DS_SERVERNAME),
				DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER);

		request.setDsAddress(serviceID.toString());
		request.setHibernateConfXmlPath(this.hibCfgPath);

		OurGridRequestControl.getInstance().execute(request,
				getServiceManager());
	}

	/**
	 * Method that requests the status of the Aggregator.
	 * 
	 * @param client
	 *            {@link AggregatorStatusProviderClient}
	 */
	public void getCompleteStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME)
									AggregatorStatusProviderClient client) {


		GetCompleteStatusRequestTO to = new GetCompleteStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setContainerContext(getServiceManager().getContainerContext());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setUpTime(getServiceManager().getContainerDAO().getUpTime());
		
		ModuleContext containerContext = getServiceManager().getContainerContext();
		to.setPropConfDir(containerContext.getProperty(ModuleProperties.PROP_CONFDIR) );
		to.setContextString(containerContext.toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());

	}
	
	public void query(@MonitoredBy(Module.CONTROL_OBJECT_NAME) AggregatorControlClient aggControlClient, String query) {
		QueryRequestTO to = new QueryRequestTO();
		
		to.setQuery(query);
		to.setClientAddress(getServiceManager().getStubDeploymentID(
				aggControlClient).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@RecoveryNotification
	public void remoteWorkerProviderIsUp(RemoteWorkerProvider rwp) {

	}

	@FailureNotification
	public void remoteWorkerProviderIsDown(RemoteWorkerProvider rwp) {

	}

	@RecoveryNotification
	public void peerStatusProviderIsUp(PeerStatusProvider provider) {

	}

	@FailureNotification
	public void peerStatusProviderIsDown(PeerStatusProvider provider) {

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateStartSenderPublicKey(ModuleControlClient client,
			String senderPublicKey) {

		if (!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager()
					.getLog()
					.warn(AggregatorControlMessages
							.getUnknownSenderStartingAggregatorMessage(senderPublicKey));
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validateStopSenderPublicKey(ModuleControlClient client,
			String senderPublicKey) {

		if (!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager()
					.getLog()
					.warn(AggregatorControlMessages
							.getUnknownSenderStoppingAggregatorMessage(senderPublicKey));
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RequestControlIF createRequestControl() {
		return new AggregatorRequestControl();
	}

}
