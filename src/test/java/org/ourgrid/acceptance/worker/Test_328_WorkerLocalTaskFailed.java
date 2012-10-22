/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.acceptance.worker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.worker.Req_003_Util;
import org.ourgrid.acceptance.util.worker.Req_004_Util;
import org.ourgrid.acceptance.util.worker.Req_006_Util;
import org.ourgrid.acceptance.util.worker.Req_079_Util;
import org.ourgrid.acceptance.util.worker.Req_080_Util;
import org.ourgrid.acceptance.util.worker.Req_081_Util;
import org.ourgrid.acceptance.util.worker.Req_082_Util;
import org.ourgrid.acceptance.util.worker.Req_084_Util;
import org.ourgrid.acceptance.util.worker.Req_087_Util;
import org.ourgrid.acceptance.util.worker.Req_088_Util;
import org.ourgrid.acceptance.util.worker.Req_092_Util;
import org.ourgrid.acceptance.util.worker.Req_094_Util;
import org.ourgrid.acceptance.util.worker.Req_120_Util;
import org.ourgrid.acceptance.util.worker.Req_125_Util;
import org.ourgrid.acceptance.util.worker.Req_126_Util;
import org.ourgrid.acceptance.util.worker.Req_128_Util;
import org.ourgrid.acceptance.util.worker.Req_129_Util;
import org.ourgrid.acceptance.util.worker.Req_130_Util;
import org.ourgrid.common.CommonConstants;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.dao.FileTransferDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Test_328_WorkerLocalTaskFailed extends WorkerAcceptanceTestCase {

	private Req_003_Util req_003_Util = new Req_003_Util(getComponentContext());
	private Req_004_Util req_004_Util = new Req_004_Util(getComponentContext());
	private Req_006_Util req_006_Util = new Req_006_Util(getComponentContext());
	private Req_079_Util req_079_Util = new Req_079_Util(getComponentContext());
	private Req_080_Util req_080_Util = new Req_080_Util(getComponentContext());
	private Req_081_Util req_081_Util = new Req_081_Util(getComponentContext());
	private Req_082_Util req_082_Util = new Req_082_Util(getComponentContext());
	private Req_084_Util req_084_Util = new Req_084_Util(getComponentContext());
	private Req_087_Util req_087_Util = new Req_087_Util(getComponentContext());
	private Req_088_Util req_088_Util = new Req_088_Util(getComponentContext());
	private Req_092_Util req_092_Util = new Req_092_Util(getComponentContext());
	private Req_094_Util req_094_Util = new Req_094_Util(getComponentContext());
	private Req_120_Util req_120_Util = new Req_120_Util(getComponentContext());
	private Req_128_Util req_128_Util = new Req_128_Util(getComponentContext());
	private Req_125_Util req_125_Util = new Req_125_Util(getComponentContext());
	private Req_126_Util req_126_Util = new Req_126_Util(getComponentContext());
	private Req_129_Util req_129_Util = new Req_129_Util(getComponentContext());
	private Req_130_Util req_130_Util = new Req_130_Util(getComponentContext());
	
	private DeploymentID peerID = null;
	private WorkerComponent component = null;
	private Future<?> prepFuture = null;
	private TestStub testStub = null;
	private String peerPubKey = null;
	private WorkerManagementClient wmc;
	private DeploymentID brokerID = null;
	private WorkerClient workerClient = null;
	private TransferHandle handle = null;
	private FileTransferDAO fileTransferDAO = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();
		peerPubKey = workerAcceptanceUtil.simulateAuthentication();
		peerID = new DeploymentID(new ContainerID("peerUser", "peerServer",
				PeerConstants.MODULE_NAME, peerPubKey), 
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		
		component = req_003_Util.createWorkerComponent(peerID.getServiceID(), false);
		prepFuture = req_004_Util.startWorker(component);
		req_092_Util.prepareAllocationCompletedOnPreparingWorker(component);
		testStub = req_126_Util.notifyPeerRecoveryAtWorkerWithoutPeer(component, peerID, workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID());
		req_129_Util.loginCompleteIdle(component,peerPubKey,peerID, testStub);
		wmc = workerAcceptanceUtil.createWorkerManagementClient(peerID);
		req_092_Util.workForBrokerOnIdleWorkerLoggedOnPeer(component, wmc, "brokerPublicKey" );
		brokerID = new DeploymentID(new ContainerID("brokerUserName", "brokerServer", "brokerModule", 
				"brokerPublicKey"), "broker");
		workerClient = req_079_Util.startWorkSuccessfully(component, 
				workerAcceptanceUtil.getWorker(), brokerID);

		String fileName = "file";
		String filePath = "root" + File.separator + fileName;
		String operationType = CommonConstants.PUT_TRANSFER;

		handle = req_080_Util.requestToTransferFile(component, 1, brokerID.getContainerID(),
				filePath, fileName, operationType, 0, 256);

		Worker worker = workerAcceptanceUtil.getWorker();
		IncomingTransferHandle incomingHandle = (IncomingTransferHandle) handle;
		
		req_080_Util.receiveIncomingTransferFailedDownloadingStateWorker(component,
				worker, workerClient, incomingHandle, new Exception(), 12, 
				brokerID);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test public void test_328_1_Start() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertTrue(workerStatus.isLogged());

		req_004_Util.startWorkerAlreadyStarted(component);

		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}

	@Test public void test_328_2_Resume() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(workerStatus.isLogged());

		req_088_Util.resumeNotOwnerWorker(component);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}

	@Test public void test_328_3_Status() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);

		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}

	@Test public void test_328_4_WorkerManagementClientIsUp() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		
		req_126_Util.notifyPeerRecoveryAtWorkerWithPeer(component, peerID, workerAcceptanceUtil.getPeerMonitorDeployment().getDeploymentID());
		
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_5_LoginSucceeded() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());

		req_129_Util.loginAlreadyLoggedInIdle(component, peerPubKey, peerID, testStub);

		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_6_ReadyForAllocation() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

		req_092_Util.prepareAllocationCompletedOnAllocatedForBrokerWorker(component, wmc, prepFuture);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_7_PreparationError() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

		req_125_Util.allocationErrorOnNotPreparingWorker(component, WorkerStatus.ALLOCATED_FOR_BROKER);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_8_GetFileInfo() throws Exception {
		String storageDir = WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir();
		String file1Name = "test_080_14_1.txt";
		String file1Path =  storageDir + File.separator + file1Name;
		WorkerAcceptanceUtil.createFile(file1Path);

		//Client "brokerPublicKey1" sends a getFileInfo "$STORAGE/test_080_14_1.txt" message
		String fileDigest = Req_082_Util.getFileDigest(file1Path);
		req_082_Util.getFileInfoWithIncomingFile(component, workerClient, brokerID.getPublicKey(),
				"$STORAGE" +File.separator+ file1Name, file1Path, fileDigest);
		
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_9_RemoteExecute() throws Exception {
		Worker worker = workerAcceptanceUtil.getWorker();

		String playpenPath = WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir();
		File pDir = new File(playpenPath);

		String storagePath = WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir();
		File sDir = new File(storagePath);

		Map<String, String> envVars = new HashMap<String, String>();
		String playpenDir = pDir.getAbsolutePath();
		String storageDir = sDir.getAbsolutePath();
		envVars.put("STORAGE", storageDir);
		envVars.put("PLAYPEN", playpenDir);

		long requestID = 1;

		req_084_Util.remoteExecutionWithNoExecutionResultOnTaskFailed(component, worker, workerClient,
				brokerID, requestID, envVars, "echo echo", 0);

		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_10_TransferRequestReceived() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO();

		String fileName = "file";
		String filePath = "root" + File.separator + fileName;
		String operationType = CommonConstants.PUT_TRANSFER;

		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

		req_080_Util.requestToTransferFileWithTaskFailed(component, 1, brokerID.getContainerID(),
				filePath, fileName, operationType, 0, 256);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

	}
	
	@Test public void test_328_11_IncomingTransferFailed() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		ContainerID senderID = brokerID.getContainerID();
		Worker worker = workerAcceptanceUtil.getWorker();
		IncomingTransferHandle handle = new IncomingTransferHandle("file.a",
				CommonConstants.PUT_TRANSFER + ":file.a", 1204, senderID);
		
		//These booleans are only set when the transfer is accepted
		handle.setReadable(false);
		handle.setWritable(false);
		handle.setExecutable(false);

		req_080_Util.receiveIncomingTransferFailedTaskFailedWorker(component,
				worker, workerClient, handle, new Exception(), 12, brokerID);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_12_IncomingTransferCompleted() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		Worker worker = workerAcceptanceUtil.getWorker();

		IncomingTransferHandle handle = new IncomingTransferHandle("file.a",
				CommonConstants.PUT_TRANSFER + ":file.a", 1204, brokerID.getContainerID());

		//These booleans are only set when the transfer is accepted
		handle.setReadable(false);
		handle.setWritable(false); 
		handle.setExecutable(false);
		
		req_080_Util.receiveIncomingTransferCompletedWorkingWorker(component,
				worker, handle, 12, brokerID.getPublicKey());
		
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isFileTransferErrorState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
	}
	
	@Test public void test_328_13_UpdateTransferProgress() throws Exception {
		//Not necessary
	}
	
	@Test public void test_328_14_GetFiles() throws Exception {
		req_081_Util.requestToRecoverFilesBeforeExecutionFinish(component, 1,
				"brokerPublicKey", "$PLAYPEN/file1");

		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertTrue(workerStatus.isWorkingState());
	}
	
	@Test public void test_328_15_ConcurrentExecutionError() throws Exception {
		//Not necessary
	}
	
	@Test public void test_328_16_ExecutionError() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		TestCase.assertTrue(workerStatus.isLogged());

		req_128_Util.executionErrorOnOwnerWorker(component);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertTrue(workerStatus.isWorkingState());
	}
	
	@Test public void test_328_17_WorkerClientIsUp() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertNotNull(workerStatus.getConsumerAddress());
		TestCase.assertNotNull(workerStatus.getConsumerDeploymentID());

		req_130_Util.workerClientIsUp(component, brokerID);

		TestCase.assertNotNull(workerStatus.getConsumerAddress());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertNotNull(workerStatus.getConsumerDeploymentID());
	}
	
	@Test public void test_328_18_TransferRejected() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);
		
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());

		req_081_Util.receiveFileRejectWorkingWorker(component, workerClient, brokerID.getPublicKey(), 
				new OutgoingTransferHandle(2L,"", new File(""), "", brokerID), brokerID);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);

		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
	}
	
	@Test public void test_328_19_OutgoingTransferCancelled() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		OutgoingTransferHandle outGoingTransferHandle = new OutgoingTransferHandle(2L, "",
				new File(""), "", brokerID);

		String filePath = outGoingTransferHandle.getLocalFile().getAbsolutePath();

		req_081_Util.receiveFileTransferCancelledWorkingWorker(component, 
				workerClient, brokerID.getPublicKey(),
				outGoingTransferHandle, filePath, 12, brokerID);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);

		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_20_OutgoingTransferFailed() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);

		OutgoingTransferHandle outGoingTransferHandle = new OutgoingTransferHandle(1L, "file1", new File(
				WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir() + 
				File.separator + "file1"), "", brokerID);
		
		String filePath = outGoingTransferHandle.getLocalFile().getAbsolutePath();

		req_081_Util.receiveFileTransferFailedTaskFailed(component, 
				workerClient, brokerID.getPublicKey(),
				outGoingTransferHandle, filePath, 12, new Exception(), brokerID);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);

		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
	}
	
	@Test public void test_328_21_OutgoingTransferCompleted() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);

		OutgoingTransferHandle outGoingTransferHandle = new OutgoingTransferHandle(2L, "", new File(""),
				"", brokerID);

		String filePath = outGoingTransferHandle.getLocalFile().getAbsolutePath();

		req_081_Util.receiveFileTransferCompletedOnRemoteTaskFailedWorker(component, 
				workerClient, brokerID.getPublicKey(),
				outGoingTransferHandle, filePath, 12);

		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);

		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(fileTransferDAO.getIncomingFileHandles().isEmpty());
		TestCase.assertTrue(workerStatus.isWorkingState());
	}
	
	@Test public void test_328_22_WorkForPeer() throws Exception {
		WorkerManagementClient wmc = workerAcceptanceUtil.createWorkerManagementClient(peerID);
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocatedForBroker());
		TestCase.assertFalse(workerStatus.isAllocatedForRemotePeer());
		TestCase.assertTrue(workerStatus.isWorkingState());
		TestCase.assertFalse(workerStatus.isPreparingAllocationState());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), 
				peerID.getServiceID().toString());	
		
		req_006_Util.workForPeerOnLocalTaskFailedWorker(component, wmc,
				peerID.getPublicKey(), "brokerPublicKey2");
		
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		
		TestCase.assertTrue(workerStatus.isAllocatedForRemotePeer());
		TestCase.assertFalse(workerStatus.isAllocatedForBroker());
		TestCase.assertTrue(workerStatus.isPreparingAllocationState());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertFalse(workerStatus.isWorkingState());
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());	
	}
	
	@Test public void test_328_23_StartWork() throws Exception {
		// Creating read-write playpen root directory
		WorkerAcceptanceUtil.createDirectory(WorkerAcceptanceUtil.DEF_PLAYPEN_ROOT_PATH, false);

		// Client [mgPubKey1] sends a startWork message - expect an info to be logged

		req_079_Util.startWorkSuccessfullyCleaning(component, workerAcceptanceUtil.getWorker(),
				brokerID);

		TestCase.assertTrue(WorkerDAOFactory.getInstance().getWorkerStatusDAO().isWorkingState());
	}
	
	@Test public void test_328_24_WorkForBroker() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();

		TestCase.assertNotNull(workerStatus.getConsumerAddress());
		TestCase.assertNotNull(workerStatus.getConsumerDeploymentID());

		req_130_Util.workerClientIsUp(component, brokerID);

		TestCase.assertNotNull(workerStatus.getConsumerAddress());
		TestCase.assertNotNull(workerStatus.getConsumerDeploymentID());
	}
	
	@Test public void test_328_25_Pause() throws Exception {
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		WorkerManagementClient wmc = workerAcceptanceUtil.createWorkerManagementClient(peerID);
		req_094_Util.getWorkerStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertTrue(workerStatus.isAllocated());
		TestCase.assertTrue(workerStatus.isWorkingState());

		req_087_Util.pauseWorkerOnWorkingState(component, wmc, prepFuture);

		req_094_Util.getWorkerStatus(WorkerStatus.OWNER);
		TestCase.assertNotNull(workerStatus.getMasterPeerAddress());
		TestCase.assertEquals(workerStatus.getMasterPeerAddress(), peerID.getServiceID().toString());
		TestCase.assertTrue(workerStatus.isLogged());
		TestCase.assertFalse(workerStatus.isAllocated());
		TestCase.assertFalse(workerStatus.isWorkingState());
	}
	
	@Test public void test_328_26_Stop() throws Exception {
		req_120_Util.stopWorkerOnWorkingState(component, prepFuture);
	}

}
