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
package org.ourgrid.acceptance.util;

import static org.ourgrid.peer.PeerConstants.LOCAL_WORKER_PROVIDER;
import static org.ourgrid.peer.PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT;
import static org.ourgrid.peer.PeerConstants.REMOTE_WORKER_PROVIDER;
import static org.ourgrid.peer.PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT;
import static org.ourgrid.peer.PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.ourgrid.acceptance.peer.PeerAcceptanceTestComponent;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.communication.receiver.DiscoveryServiceClientReceiver;
import org.ourgrid.peer.communication.receiver.LocalWorkerProviderReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerManagementClientReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerProviderClientReceiver;
import org.ourgrid.peer.communication.receiver.RemoteWorkerProviderReceiver;
import org.ourgrid.peer.communication.receiver.WorkerManagementClientReceiver;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.TestObjectsRegistry;

public class PeerAcceptanceUtil extends AcceptanceUtil {

	public static final String BROKER_CERT = "test" + File.separator + "acceptance" + 
	File.separator + "broker" + File.separator + "broker_certificate.cer";
	
	public PeerAcceptanceUtil(ModuleContext context) {
		super(context);
	}

	public static final int DEFAULT_ADVERT_TTL = 10000;
	public static final String TEST_FILES_PATH = "test"+File.separator+"acceptance"+File.separator;
	
    /* Set up and tear down */

	@Before
    public static void setUp() throws Exception {
        System.setProperty("OGROOT", ".");
        Configuration.getInstance(PeerConfiguration.PEER);
    }
	
	/*public static void recreateSchema() {
		HibernateUtil.setUp(CONF_XML_PATH);
		HibernateUtil.recreateSchema();
	}*/
	
	@After
    public static void tearDown() throws Exception {
		
		if (application != null && !application.getContainerDAO().isStopped()) {
			application.stop();
		}
		
		TestObjectsRegistry.reset();
		cleanBD();
    }
	
    public static void cleanBD() {
    	
    	try {  
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");  
  
            String database = "jdbc:derby:db/peer";  
            Connection con = DriverManager.getConnection( database ,"","");  
              
            Statement s = con.createStatement();  
  
            s.execute("DELETE FROM ATTRIBUTE");
            s.execute("DELETE FROM BALANCE_VALUE");
            s.execute("DELETE FROM BALANCE");
            s.execute("DELETE FROM COMMAND");
            s.execute("DELETE FROM EXECUTION");
            s.execute("DELETE FROM WORKER_STATUS_CHANGE");
            s.execute("DELETE FROM PEER_STATUS_CHANGE");
            s.execute("DELETE FROM TASK");
            s.execute("DELETE FROM JOB");
            s.execute("DELETE FROM LOGIN");
            s.execute("DELETE FROM T_USERS");
            s.execute("DELETE FROM WORKER");
            s.execute("DELETE FROM PEER");
            s.close();  
            con.close();  
            
        }  
        catch (Exception e) {  
            System.out.println("Error: " + e);  
        }    
    }
	
    public void deleteNOFRankingFile() {
        deleteFile(context.getProperty(PeerConfiguration.PROP_RANKINGFILE));
    }
    
    public void deleteFile(String filepath) {
    	File localUsersFile = new File(filepath);
    	localUsersFile.delete();
    }
    
    /* Peer component creation */
    public PeerComponent createPeerComponent(ModuleContext context) throws CommuneNetworkException, ProcessorStartException, InterruptedException {
        application = new PeerAcceptanceTestComponent(context);
        
        Thread.sleep(2000);
        
        return (PeerComponent) application;
    }

    /* get peer bound objects */

    public static ServiceManager getServiceManager() {
        ObjectDeployment deployment = application.getObject(Module.CONTROL_OBJECT_NAME);
		return deployment.getServiceManager();
    }
    
    public PeerControl getPeerControl() {
        ObjectDeployment deployment = getPeerControlDeployment();
		return (PeerControl) deployment.getObject();
    }

    public ObjectDeployment getPeerControlDeployment() {
    	return getContainerObject(application, Module.CONTROL_OBJECT_NAME);
    }

    public WorkerManagementClientReceiver getWorkerMonitor() {
        ObjectDeployment deployment = getWorkerMonitorDeployment();
		return (WorkerManagementClientReceiver) deployment.getObject();
    }

	public ObjectDeployment getWorkerMonitorDeployment() {
		return getContainerObject(application, PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
	}
	
    public DiscoveryServiceClientReceiver getDiscoveryServiceMonitor() {
        ObjectDeployment deployment = getDiscoveryServiceMonitorDeployment();
		return (DiscoveryServiceClientReceiver) deployment.getObject();
    }

	public ObjectDeployment getDiscoveryServiceMonitorDeployment() {
    	return getContainerObject(application, PeerConstants.DS_CLIENT);
	}

    public PeerStatusProvider getStatusProvider() {
        ObjectDeployment deployment = getContainerObject(application, Module.CONTROL_OBJECT_NAME);
		return (PeerStatusProvider) deployment.getObject();
    }
    
    public PeerStatusProvider getStatusProviderProxy() {
        ObjectDeployment deployment = getStatusProviderObjectDeployment();
		return (PeerStatusProvider) deployment.getObject();
    }
    
    public ObjectDeployment getStatusProviderObjectDeployment() {
        return getTestProxy(application, Module.CONTROL_OBJECT_NAME);
    }

    public ObjectDeployment getLocalWorkerProviderClientDeployment() {
    	return getTestProxy(application, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
    }
    
	public ObjectDeployment getRemoteWorkerMonitorDeployment() {
		return getContainerObject(application, PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT);
	}

    public RemoteWorkerManagementClientReceiver getRemoteWorkerMonitor() {
        ObjectDeployment deployment = getRemoteWorkerMonitorDeployment();
		return (RemoteWorkerManagementClientReceiver) deployment.getObject();
    }
    
	public ObjectDeployment getWorkerSpecListenerDeployment() {
		return getContainerObject(application, WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
	}

    public WorkerManagementClient getWorkerManagementClient() {
        ObjectDeployment deployment = getContainerObject(application, WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		return (WorkerManagementClient) deployment.getObject();
    }
    
    public WorkerManagementClient getWorkerManagementClientProxy() {
        ObjectDeployment deployment = getWorkerManagementClientDeployment();
		return (WorkerManagementClient) deployment.getObject();
    }
    

	public ObjectDeployment getWorkerManagementClientDeployment() {
		return getTestProxy(application, WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
	}
	
	public ObjectDeployment getWorkerManagementDeployment() {
		return getTestProxy(application, WorkerConstants.LOCAL_WORKER_MANAGEMENT);
	}
	
    public RemoteWorkerManagementClient getRemoteWorkerManagementClient() {
        ObjectDeployment deployment = getContainerObject(application, REMOTE_WORKER_MANAGEMENT_CLIENT);
		return (RemoteWorkerManagementClient) deployment.getObject();
    }

    public RemoteWorkerManagementClient getRemoteWorkerManagementClientProxy() {
		return (RemoteWorkerManagementClient) getRemoteWorkerManagementClientDeployment().getObject();
    }
    
	public ObjectDeployment getRemoteWorkerManagementClientDeployment() {
		return getTestProxy(application, REMOTE_WORKER_MANAGEMENT_CLIENT);
	}

    public LocalWorkerProvider getLocalWorkerProviderProxy() {
        ObjectDeployment deployment = getLocalWorkerProviderDeployment();
		return (LocalWorkerProvider) deployment.getObject();
    }
    
    public LocalWorkerProvider getLocalWorkerProvider() {
    	ObjectDeployment deployment = getContainerObject(application, LOCAL_WORKER_PROVIDER);
		return (LocalWorkerProvider) deployment.getObject();
    }

	public ObjectDeployment getLocalWorkerProviderDeployment() {
		return getTestProxy(application, LOCAL_WORKER_PROVIDER);
	}

    public RemoteWorkerProvider getRemoteWorkerProviderProxy(){
        ObjectDeployment deployment = getRemoteWorkerProviderDeployment();
		return (RemoteWorkerProvider) deployment.getObject();
    }
    
    public RemoteWorkerProvider getRemoteWorkerProvider(){
        ObjectDeployment deployment = getContainerObject(application, REMOTE_WORKER_PROVIDER);
		return (RemoteWorkerProvider) deployment.getObject();
    }

	public ObjectDeployment getRemoteWorkerProviderDeployment() {
		return getTestProxy(application, REMOTE_WORKER_PROVIDER);
	}
	
    public RemoteWorkerProviderClientReceiver getRemoteWorkerProviderClient() {
        ObjectDeployment deployment = getContainerObject(application, PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
		return (RemoteWorkerProviderClientReceiver) deployment.getObject();
    }
    
    public RemoteWorkerProviderClient getRemoteWorkerProviderClientProxy() {
        ObjectDeployment deployment = getRemoteWorkerProviderClientDeployment();
		return (RemoteWorkerProviderClient) deployment.getObject();
    }
    
	public ObjectDeployment getRemoteWorkerProviderClientDeployment() {
		return getTestProxy(application, PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
	}

    public DiscoveryServiceClient getDiscoveryServiceClientProxy() {
    	ObjectDeployment deployment = getTestProxy(application, PeerConstants.DS_CLIENT);
		return (DiscoveryServiceClient) deployment.getObject();
    }
    
    public DiscoveryServiceClientReceiver getDiscoveryServiceClient() {
    	ObjectDeployment deployment = getContainerObject(application, PeerConstants.DS_CLIENT);
		return (DiscoveryServiceClientReceiver) deployment.getObject();
    }
    
    public ObjectDeployment getDiscoveryServiceClientDeployment() {
    	return getContainerObject(application, PeerConstants.DS_CLIENT);
    }
    
    public LocalWorkerProviderReceiver getClientMonitor() {
        ObjectDeployment deployment = getContainerObject(application, LOCAL_WORKER_PROVIDER);
		return (LocalWorkerProviderReceiver) deployment.getObject();
    }
    
    public RemoteWorkerProviderReceiver getRemoteClientMonitor() {
        ObjectDeployment deployment = getRemoteWorkerProviderClientMonitorDeployment();
		return (RemoteWorkerProviderReceiver) deployment.getObject();
    }
    
	public ObjectDeployment getRemoteWorkerProviderClientMonitorDeployment() {
		return getContainerObject(application, REMOTE_WORKER_PROVIDER);
	}
    
    public boolean isPeerInterestedOnBroker(ServiceID workerProviderClientID) {
        ObjectDeployment deployment = getContainerObject(application, LOCAL_WORKER_PROVIDER);
        return AcceptanceTestUtil.isInterested(application, workerProviderClientID, deployment.getDeploymentID());
    } 

    public boolean isPeerInterestedOnLocalWorker(ServiceID workerManagementID) {
        ObjectDeployment deployment = getWorkerMonitorDeployment();
        return AcceptanceTestUtil.isInterested(application, workerManagementID, deployment.getDeploymentID());
    }
    
    public boolean isPeerInterestedOnRemoteClient(ServiceID remoteWorkerProviderClient) {
        ObjectDeployment deployment = getTestProxy(application, REMOTE_WORKER_PROVIDER);
        return AcceptanceTestUtil.isInterested(application, remoteWorkerProviderClient, deployment.getDeploymentID());
    }

    public boolean isPeerInterestedOnRemoteWorker(ServiceID rwmOID) {
    	ObjectDeployment deployment = 
    		getContainerObject(application, REMOTE_WORKER_MANAGEMENT_CLIENT);
		return AcceptanceTestUtil.isInterested(application, rwmOID, deployment.getDeploymentID());
	}
    
    public boolean isPeerInterestedOnRemoteWorkerProvider(ServiceID rwpOID) {
    	ObjectDeployment deployment = 
    		getTestProxy(application, REMOTE_WORKER_PROVIDER_CLIENT);
		return AcceptanceTestUtil.isInterested(application, rwpOID, deployment.getDeploymentID());
    }
    
    /* Other */
    public static void copyTrustFile(String fileName) throws IOException {
		File origFile = new File(TEST_FILES_PATH+File.separator+fileName);
		FileUtils.copyFile(origFile, 
				new File(PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME));
	}
	
	public RepetitionRunnable createRequestWorkersRunnable(PeerComponent peerComponent, Long requestID) {
		
		ObjectDeployment objectDeployment = getPeerControlDeployment();
		
		return new RepetitionRunnable(peerComponent, (ModuleManager) objectDeployment.getObject(), 
				PeerConstants.REQUEST_WORKERS_ACTION_NAME, requestID);
	}

	public static DeploymentID createRemoteConsumerID(String user, String server, String consumer1PublicKey) {
		ContainerID consumer1APID = new ContainerID(user, server, PeerConstants.MODULE_NAME, consumer1PublicKey);
		return new DeploymentID(consumer1APID, PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
	}

	public static void reset(Object... mocks) {
		for (Object mock : mocks) {
			EasyMock.reset(mock);
		}
	}

	public static void replay(Object... mocks) {
		for (Object mock : mocks) {
			EasyMock.replay(mock);
		}
	}
	
	public static void verify(Object... mocks) {
		for (Object mock : mocks) {
			EasyMock.verify(mock);
		}
	}

} 