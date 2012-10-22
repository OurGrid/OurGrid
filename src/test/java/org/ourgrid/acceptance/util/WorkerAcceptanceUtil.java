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

import static org.ourgrid.peer.PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT;
import static org.ourgrid.peer.PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.executor.IntegerExecutorHandle;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerExecutionServiceClient;
import org.ourgrid.common.interfaces.control.WorkerControl;
import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.status.WorkerStatusProvider;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.peer.communication.receiver.RemoteWorkerManagementClientReceiver;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.FileTransferDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.communication.actions.PrepareAllocationAction;
import org.ourgrid.worker.communication.receiver.RemoteWorkerManagementReceiver;
import org.ourgrid.worker.communication.receiver.WorkerManagementReceiver;
import org.ourgrid.worker.sysmonitor.interfaces.WorkerSysInfoCollector;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import condor.classad.ClassAdParser;
import condor.classad.RecordExpr;

public class WorkerAcceptanceUtil extends AcceptanceUtil{
	
	public WorkerAcceptanceUtil(ModuleContext context) {
		super(context);
	}

	public static final String SEP = File.separator;
	public static final String WORKER_TEST_DIR = "test" + SEP + "acceptance" + SEP + "worker" + SEP;
	protected static final String PROPERTIES_FILENAME = WORKER_TEST_DIR + "worker.properties";
	
	public static final String DEF_PLAYPEN_ROOT_PATH =
			"test" + File.separator + "tmp" + File.separator + "playpen";
	
	public static final String DEF_INVALID_PLAYPEN_ROOT_PATH =
		"test" + File.separator + "tmp" + File.separator + "invalid_playpen";
	
	public static final String DEF_STORAGE_ROOT_PATH = 
		"test" + File.separator + "tmp" + File.separator + "storage";
	
	public static final String DEF_INVALID_STORAGE_ROOT_PATH =
		"test" + File.separator + "tmp" + File.separator + "inv?#!*_storage";
	
	@Before
    public void setUp() throws Exception {
        System.setProperty("OGROOT", ".");
        Configuration.getInstance(WorkerConfiguration.WORKER);
        deleteEnvDirs();
    }
	
	@After
    public void tearDown() throws Exception {
		deleteEnvDirs();
		if (application != null && !application.getContainerDAO().isStopped()) {
			application.stop();
		}
    }
	
	/* Set up and tear down */

	public String simulateAuthentication() {
		return "peerPublicKey";
	}
	
	public void deleteEnvDirs() throws IOException {
		
		removeDirectory(context.getProperties().get(WorkerConfiguration.PROP_PLAYPEN_ROOT));
		removeDirectory(context.getProperties().get(WorkerConfiguration.PROP_STORAGE_DIR));
	}
	
	public ExecutorHandle createIntegerExecutorHandle(Integer value) {
		return new IntegerExecutorHandle(value);
	}
	
	public ExecutorResult createExecutorResult(int exitValue, String stdout, String stderr) {
		return new ExecutorResult(exitValue, stdout, stderr);
	}
	
	
	public IncomingTransferHandle createIncomingFileTransfer(long handleId,
			ContainerID senderID, String filePath, String logicalFileName,
			String operationType, long fileSize) {
		IncomingTransferHandle opHandle = new IncomingTransferHandle(handleId, logicalFileName, 
				FileTransferHandlerUtils.getTransferDescription(operationType, filePath), fileSize, senderID);
		
		opHandle.setExecutable(true);
		opHandle.setReadable(true);
		opHandle.setWritable(true);
		
		return opHandle;
	}
	
	public OutgoingTransferHandle createOutgoingFileTransfer(long handleId,
			DeploymentID senderID, String logicalFileName, File localFile) {
		
		OutgoingTransferHandle opHandle = new OutgoingTransferHandle(handleId, logicalFileName, localFile,
				"", senderID);
		return opHandle;
	}
	
	public IncomingTransferHandle createIncomingFileTransfer(
			ContainerID senderID, String filePath, String logicalFileName,
			String operationType, long fileSize) {
		IncomingTransferHandle opHandle = new IncomingTransferHandle(logicalFileName, 
				FileTransferHandlerUtils.getTransferDescription(operationType, filePath), fileSize, senderID);
		return opHandle;
	}


	public RepetitionRunnable createExecutorRunnable(
			Module workerComponent, int executionId) {
		
		ExecutorHandle handle = new IntegerExecutorHandle(executionId);
		
		ObjectDeployment objectDeployment = getWorkerControlDeployment();
		
		return new RepetitionRunnable(
				workerComponent, 
				(ModuleManager) objectDeployment.getObject(),
				WorkerConstants.EXECUTOR_ACTION_NAME, 
				new WorkerExecutionHandle(handle, null));
	}
	
	public RemoteWorkerManagementClient createRemoteWorkerManagementClient(String publicKey) {
		
		RemoteWorkerManagementClient rwmc = EasyMock.createMock(RemoteWorkerManagementClient.class);
		DeploymentID remotePeerID = new DeploymentID(new ContainerID("rusername", "rserver", "rmodule", publicKey),"remotePeer");
		application.createTestStub(rwmc, RemoteWorkerManagementClient.class, remotePeerID, true);
		
		return rwmc;
	}
	
	
	public WorkerManagementClient createWorkerManagementClient(DeploymentID peerID) {
		WorkerManagementClient wmc = EasyMock.createMock(WorkerManagementClient.class);
		createStub(wmc, WorkerManagementClient.class, peerID);
		
		return wmc;
	}
	
	
	public PrepareAllocationAction createBeginAllocationRunnable() {
		return new PrepareAllocationAction(null, null);
	}
	
	public RepetitionRunnable createReportWorkAccountingRunnable(Module workerComponent) {
		ObjectDeployment objectDeployment = getWorkerControlDeployment();
		return new RepetitionRunnable(workerComponent, (ModuleManager) objectDeployment.getObject(),
				WorkerConstants.REPORT_WORK_ACCOUNTING_ACTION_NAME, null);
	}


	
	/* Worker spec */

	public WorkerSpecification createWorkerSpecMock(ServiceID entityIDA) {
		WorkerSpecification workerSpecA = EasyMock.createNiceMock(WorkerSpecification.class);
		org.easymock.classextension.EasyMock.expect(workerSpecA.getServiceID()).andReturn(entityIDA).anyTimes();
		EasyMock.replay(workerSpecA);
		return workerSpecA;
	}

	public WorkerSpecification createWorkerSpec(String userName, String serverName) {
		return createWorkerSpec(userName, serverName, null, null);
	}
	
	public WorkerSpecification createWorkerSpec(String userName, String serverName, Integer memory, String os) {
		Map<String,String> spec = new HashMap<String,String>();
		spec.put( OurGridSpecificationConstants.ATT_USERNAME, userName );
		spec.put( OurGridSpecificationConstants.ATT_SERVERNAME, serverName );
		
		if (memory != null) {
			spec.put( OurGridSpecificationConstants.ATT_MEM, String.valueOf(memory) );
		}
		
		if (os != null) {
			spec.put( OurGridSpecificationConstants.ATT_OS, os );
		}

		return new WorkerSpecification( spec );
	}
	
	public WorkerSpecification createClassAdWorkerSpec(String userName, String serverName) {
		return createClassAdWorkerSpec(userName, serverName, null, null);
	}
	
	public WorkerSpecification createClassAdWorkerSpec(String userName, String serverName, Integer memory, String os) {
		String attributes = "";
		if(memory != null){
			attributes += "MainMemory = " + memory + ";";
		}
		if(os != null){
			attributes += "OS = \"" + os + "\";";
		}
		RecordExpr expr = (RecordExpr) new ClassAdParser("[" +
				"username=\"" + userName + "\";" +
				"servername=\"" + serverName + "\";" +
				attributes + 
				"Requirements=TRUE;" +
				"Rank=0;" +
				"]").parse();
		return new WorkerSpecification( expr );
	}

	/* Get worker bound objects */
    
	public ServiceManager getServiceManager() {
        ObjectDeployment deployment = getWorkerControlDeployment();
		return deployment.getServiceManager();
    }
    
	public WorkerManagement getWorkerManagement() {
		ObjectDeployment deployment = getWorkerManagementDeployment();
		return (WorkerManagement) deployment.getObject();
	}
	
	public ObjectDeployment getWorkerManagementDeployment() {
		return getTestProxy(application, WorkerConstants.LOCAL_WORKER_MANAGEMENT);
	}
	
	public void runRepeatedAction(WorkerComponent component, Serializable handler, String actionName) {
		WorkerControlClient wspc = EasyMock.createMock(WorkerControlClient.class);
		wspc.operationSucceed((ControlOperationResult)EasyMock.notNull());
		EasyMock.replay(wspc);
		ObjectDeployment workerControlDeployment = getWorkerControlDeployment();
		String publickey = workerControlDeployment.getDeploymentID().getPublicKey();
		DeploymentID wspcID = new DeploymentID(new ServiceID(new ContainerID("worker1","xmppServer", "WORKER", publickey), "WorkerControlClient"));
		AcceptanceTestUtil.publishTestObject(application, wspcID, wspc, WorkerControlClient.class);
		ObjectDeployment wmOD = getWorkerManagementDeployment();
		AcceptanceTestUtil.setExecutionContext(application, wmOD, wspcID);
		
		ExecutorService newThreadPool = EasyMock.createMock(ExecutorService.class);
		component.setExecutorThreadPool(newThreadPool);
		
		component.getScheduledAction(actionName).run(handler, getServiceManager());
	}
	
    public WorkerControl getWorkerControl() {
        ObjectDeployment deployment = getWorkerControlDeployment();
		return (WorkerControl) deployment.getObject();
    }

    public ObjectDeployment getWorkerControlDeployment() {
    	return application.getObject(Module.CONTROL_OBJECT_NAME);
    }
	
    public WorkerSysInfoCollector getWorkerSysInfoCollector() {
    	ObjectDeployment deployment = getWorkerSysInfoCollectorObjectDeplyment();
    	return (WorkerSysInfoCollector) deployment.getObject();
    }
    
    public ObjectDeployment getWorkerSysInfoCollectorObjectDeplyment() {
    	return application.getObject(WorkerConstants.WORKER_SYSINFO_COLLECTOR);
    }
    
	public WorkerExecutionServiceClient getWorkerExecutionClient() {
		ObjectDeployment deployment = getWorkerExecutionDeployment();
		
		WorkerExecutionServiceClient workerExecutionClient = null;
		
		if (deployment != null) {
			workerExecutionClient = (WorkerExecutionServiceClient) deployment.getObject();
		}
		return workerExecutionClient;
	}
	
	public ObjectDeployment getWorkerExecutionDeployment() {
		return getContainerObject(application, WorkerConstants.WORKER_EXECUTION_CLIENT);
	}
	
    public WorkerManagementReceiver getMasterPeerMonitor(WorkerComponent component) {
        ObjectDeployment deployment = getMasterPeerMonitorDeployment(component);
		return (WorkerManagementReceiver) deployment.getObject();
    }

	public ObjectDeployment getMasterPeerMonitorDeployment(WorkerComponent component) {
		return component.getObject(WorkerConstants.LOCAL_WORKER_MANAGEMENT);
	}
	
	public WorkAccounting getWorkerAccountingReporter() {
		return (WorkAccounting) 
			getTestProxy(application, WorkerConstants.WORKER_ACCOUNTING_REPORTER).getObject();
	}
	
	public WorkerStatusProvider getWorkerStatusProvider() {
		return (WorkerStatusProvider)
		application.getObject(Module.CONTROL_OBJECT_NAME).getObject();
	}
	
	public Worker getWorker() {
		ObjectDeployment deployment = getWorkerDeployment();
		Worker worker = null;
		
		if (deployment != null) {
			worker = (Worker) deployment.getObject();
		}
		
		return worker;
	}
	
	public ObjectDeployment getWorkerDeployment() {
    	return application.getObject(WorkerConstants.WORKER);
    }
	
	public RemoteWorkerManagement getRemoteWorkerManagement() {
		ObjectDeployment deployment = getRemoteWorkerManagementDeployment();
		
		RemoteWorkerManagement rmw = null;
		
		if (deployment != null) {
			rmw = (RemoteWorkerManagement) deployment.getObject();
		}
		return rmw;
	}
	
    public ObjectDeployment getRemoteWorkerManagementDeployment() {
    	return application.getObject(WorkerConstants.REMOTE_WORKER_MANAGEMENT);
    }
	
	public boolean isWorkerBound() {
		return (getWorker() != null);
	}
	
	public boolean isRemoteWorkerManagementBound() {
		return (getRemoteWorkerManagement() != null);
	}
	
	public ServiceID createWorkerServiceID() {
		return  new ServiceID(getServiceManager().getMyDeploymentID().
				getContainerID(), WorkerConstants.WORKER);
	}
	
	public ServiceID createRemoteWorkerManagementServiceID() {
		return  new ServiceID(getServiceManager().getMyDeploymentID().
				getContainerID(), WorkerConstants.REMOTE_WORKER_MANAGEMENT);
	}
	
	/* Worker Properties creation */
	
	public Map<String, Object> createWorkerProperties() {
		return createWorkerProperties(false);
	}
	
	public Map<String, Object> createWorkerProperties(boolean withIdlenessDetector) {
        return createWorkerProperties(DEF_PLAYPEN_ROOT_PATH, DEF_STORAGE_ROOT_PATH, withIdlenessDetector);
    }
	
	public Map<String, Object> createWorkerProperties(String playpenRootPath, String storageRootPath,
			boolean withIdlenessDetector) {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(SignatureProperties.PROP_PUBLIC_KEY, context.getProperty(SignatureProperties.PROP_PUBLIC_KEY));
        
        if (withIdlenessDetector) {
        	properties.put(WorkerConfiguration.PROP_IDLENESS_DETECTOR, "yes");
        } else {
        	properties.put(WorkerConfiguration.PROP_IDLENESS_DETECTOR, WorkerConfiguration.DEF_PROP_IDLENESS_DETECTOR);
        }
        
        properties.put(WorkerConfiguration.PROP_PLAYPEN_ROOT, playpenRootPath.replace("\\\\", "\\"));
        properties.put(WorkerConfiguration.PROP_STORAGE_DIR, storageRootPath);

        return properties;
	}

	/* Environment actions and checks */
	
	public static File createDirectory(String path, boolean readOnly) {
		File playpenDir = new File(path);
		
		if(!playpenDir.exists()) {
			playpenDir.mkdirs();
		}
		
		if (readOnly) {
			playpenDir.setReadOnly();
			 playpenDir.setReadable(true, false);
			playpenDir.setWritable(false, false);
		} else {
			TestJavaFileUtil.setReadAndWrite(playpenDir);
		}
		
		return playpenDir;
	}

	public static void removeDirectory(String path) throws IOException {
		File playPenDir = new File(path);
		
		TestJavaFileUtil.setReadAndWrite(playPenDir);
		
		if(playPenDir.exists() && playPenDir.isDirectory()) {
			deleteFilesInDir(playPenDir);
			playPenDir.delete();
		}
	}
	
	private static void deleteFilesInDir(File directory) throws IOException {
		File[] files = directory.listFiles();
		
		if (files != null) {
			for (File file : files) {
				
				TestJavaFileUtil.setReadAndWrite(file);
				
				if (file.isDirectory()) {
					deleteFilesInDir(file);
				}
				file.delete();
			}
		}
	}
	
	public static boolean directoryExists(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	public static boolean directoryContainsFiles(String path) {
		File file = new File(path);
		
		if(file.isDirectory()) {
			 File[] files = file.listFiles();
			if (files == null) {
				return false;
			}
			
			return files.length != 0;
		}
		return false;
	}
	
	public static boolean createFile(String filePath) throws IOException {
		File file = new File(filePath);
		if(!file.exists()) {
			return file.createNewFile();
		}
		return false;
	}
	
	/* Else */
	
	public static String generateHexadecimalCodedString(String stringToBeCoded) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		digest.update(stringToBeCoded.getBytes());
		byte[] hashedKey = digest.digest();
		
		final int radix = 16;
		String result = "";
		for(byte b : hashedKey) {
			int unsignedByte = b + 128; 
			result += Integer.toString(unsignedByte, radix);
		}
		return result;
	}
	
	public OutgoingHandle getOutgoingTransferHandle(WorkerComponent component, String fileName) {
		FileTransferDAO ftDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		List<OutgoingHandle> handles = ftDAO.getUploadingFileHandles();
		
		for (OutgoingHandle handle : handles) {
			if (handle.getLocalFile().getName().equals(fileName)) {
				return handle;
			}
		}
		
		return null;
	}
	
	public IncomingHandle getIncomingTransferHandle(WorkerComponent component, String fileName) {
		FileTransferDAO ftDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		List<IncomingHandle> handles = ftDAO.getIncomingFileHandles();
		
		for (IncomingHandle handle : handles) {
			if (handle.getLocalFile().getName().equals(fileName)) {
				return handle;
			}
		}
		
		return null;
	}

	public static void setAnnotationsWorkerSpec( WorkerSpecification workerSpec, List< String > tagsWorker ) {
        for(String tag: tagsWorker){
            workerSpec.addAnnotation( tag, tag );
        }
    }
        
    public static void setAnnotationsWorkerSpec( WorkerSpecification workerSpec, Map< String, String > annotationsWorker) {
           workerSpec.setAnnotations( annotationsWorker);
    }
 
    public WorkerManagementClient getWorkerManagementClient() {
        ObjectDeployment deployment = getWorkerManagementClientDeployment();
		return (WorkerManagementClient) deployment.getObject();
    }
    
    public ObjectDeployment getWorkerManagementClientDeployment() {
		return getContainerObject(application, WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
	}
    
    public WorkerManagementReceiver getPeerMonitor() {
        ObjectDeployment deployment = getPeerMonitorDeployment();
		return (WorkerManagementReceiver) deployment.getObject();
    }

    public RemoteWorkerManagementReceiver getRemoteWorkerManagementReceiver() {
        ObjectDeployment deployment = getRemoteWorkerManagementDeployment();
        RemoteWorkerManagementReceiver rwmr = null;
        if (deployment != null) {
        	rwmr = (RemoteWorkerManagementReceiver) deployment.getObject();
        }
		return rwmr ;
    }
    
	public ObjectDeployment getPeerMonitorDeployment() {
		return getContainerObject(application, WorkerConstants.LOCAL_WORKER_MANAGEMENT);
	}
	
	public RemoteWorkerManagementClientReceiver getRemotePeerMonitor() {
        ObjectDeployment deployment = getRemotePeerMonitorDeployment();
        
        RemoteWorkerManagementClientReceiver rwmcr = null;
        
        if (deployment != null) {
        	rwmcr = (RemoteWorkerManagementClientReceiver) deployment.getObject();
        }
		
        return rwmcr;
    }
	
	public ObjectDeployment getRemotePeerMonitorDeployment() {
		return getContainerObject(application, REMOTE_WORKER_MANAGEMENT_CLIENT);
	}

	public ExecutorException createExecutorException() {
		return new ExecutorException();
	}
	
}