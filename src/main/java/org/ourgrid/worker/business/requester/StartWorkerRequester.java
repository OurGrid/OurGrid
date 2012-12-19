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

package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.CreateMessageProcessorsResponseTO;
import org.ourgrid.common.internal.response.CreateRepeatedActionResponseTO;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.internal.response.ScheduleActionWithFixedDelayResponseTO;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.communication.actions.ReportWorkAccountingAction;
import org.ourgrid.worker.communication.actions.ReportWorkerSpecAction;
import org.ourgrid.worker.communication.actions.idlenessdetector.IdlenessDetectorActionFactory;
import org.ourgrid.worker.communication.receiver.IdlenessDetectorWorkerControlClient;
import org.ourgrid.worker.communication.receiver.WorkerExecutionClientReceiver;
import org.ourgrid.worker.communication.receiver.WorkerManagementReceiver;
import org.ourgrid.worker.request.StartWorkerRequestTO;
import org.ourgrid.worker.response.CreateExecutorResponseTO;
import org.ourgrid.worker.sysmonitor.core.SysInfoCollectingController;

/**
 * This class provider a list of {@link IResponseTO} that will be executed.
 * Responsible for start the worker component.
 *
 */
public class StartWorkerRequester implements RequesterIF<StartWorkerRequestTO> {

	/**
	 * {@inheritDoc}
	 */
	public List<IResponseTO> execute(StartWorkerRequestTO request) {
		WorkerDAOFactory.getInstance().reset();
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		createExecutor(responses);
		createServices(responses);

		responses.add(new CreateExecutorResponseTO());
		
		createRepeatedActions(request, responses);
		
		if (request.isPropertiesCollectorOn()) {
			startSysInfoCollectingController(responses);
		}
		
		if (!request.isIdlenessDetectorOn()) {
			ExecutionController.getInstance().beginAllocation(responses);
		}
		
		setMasterPeer(request, responses);
		
		LoggerResponseTO loggerResponseTO = new LoggerResponseTO();
		loggerResponseTO.setMessage(ControlMessages.getSuccessfullyStartedWorkerMessage());
		loggerResponseTO.setType(LoggerResponseTO.INFO);
		responses.add(loggerResponseTO);
		
		WorkerDAOFactory.getInstance().getWorkerStatusDAO().setStatus(
				request.isIdlenessDetectorOn() || request.useIdlenessSchedule() ? 
						WorkerStatus.OWNER : WorkerStatus.IDLE);

		return responses;
	}
	
	private void createExecutor(List<IResponseTO> responses) {
		CreateExecutorResponseTO to = new CreateExecutorResponseTO();
		
		responses.add(to);
	}

	private void createMessageProcessors(List<IResponseTO> responses) {
		responses.add(new CreateMessageProcessorsResponseTO());
	}

	protected void createServices(List<IResponseTO> responses) {
		DeployServiceResponseTO deployPeerMonitorTO = new DeployServiceResponseTO();
		deployPeerMonitorTO.setServiceName(WorkerConstants.LOCAL_WORKER_MANAGEMENT);
		deployPeerMonitorTO.setServiceClass(WorkerManagementReceiver.class);
		responses.add(deployPeerMonitorTO);
		
		DeployServiceResponseTO deployWorkerSysinfoCollectorTO = new DeployServiceResponseTO();
		deployWorkerSysinfoCollectorTO.setServiceName(WorkerConstants.WORKER_SYSINFO_COLLECTOR);
		deployWorkerSysinfoCollectorTO.setServiceClass(SysInfoCollectingController.class);
		responses.add(deployWorkerSysinfoCollectorTO);
		
		DeployServiceResponseTO deployWorkerExecutionClientTO = new DeployServiceResponseTO();
		deployWorkerExecutionClientTO.setServiceName(WorkerConstants.WORKER_EXECUTION_CLIENT);
		deployWorkerExecutionClientTO.setServiceClass(WorkerExecutionClientReceiver.class);
		responses.add(deployWorkerExecutionClientTO);
		
		createMessageProcessors(responses);
	}
	
	private void startSysInfoCollectingController(List<IResponseTO> responses) {
		ScheduleActionWithFixedDelayResponseTO scheduleTO = new ScheduleActionWithFixedDelayResponseTO();
		scheduleTO.setActionName(WorkerConstants.SYS_INFO_GATHERING_ACTION_NAME);
		scheduleTO.setDelay(WorkerConfiguration.DEF_SYS_INFO_GATHERING_TIME);
		scheduleTO.setInitialDelay(WorkerConfiguration.DEF_SYS_INFO_GATHERING_TIME);
		scheduleTO.setTimeUnit(TimeUnit.SECONDS);
		responses.add(scheduleTO);
	}

	private void createRepeatedActions(StartWorkerRequestTO request, List<IResponseTO> responses) {
		
		CreateRepeatedActionResponseTO reportWorkAccountingTO = new CreateRepeatedActionResponseTO();
		reportWorkAccountingTO.setActionName(WorkerConstants.REPORT_WORK_ACCOUNTING_ACTION_NAME);
		reportWorkAccountingTO.setRepeatedAction(new ReportWorkAccountingAction());
		responses.add(reportWorkAccountingTO);

		CreateRepeatedActionResponseTO reportWorkerSpecTO = new CreateRepeatedActionResponseTO();
		reportWorkerSpecTO.setActionName(WorkerConstants.REPORT_WORKER_SPEC_ACTION_NAME);
		reportWorkerSpecTO.setRepeatedAction(new ReportWorkerSpecAction());
		responses.add(reportWorkerSpecTO);
			
		if (request.isIdlenessDetectorOn() || request.useIdlenessSchedule()) {
			createIdlenessDetectorAction(request, responses);
		} 
	}

	private void createIdlenessDetectorAction(StartWorkerRequestTO request, List<IResponseTO> responses) {
		List<ScheduleTime> scheduleTimes = new LinkedList<ScheduleTime>();
		
		if (request.useIdlenessSchedule()) {
			ScheduleTimeParser scheduleTimeParser = new ScheduleTimeParser(request.getIdlenessScheduleTime());
			scheduleTimes = scheduleTimeParser.parseScheduleTimes();
		}
	
		DeployServiceResponseTO deployServiceResponseTO = new DeployServiceResponseTO();
		deployServiceResponseTO.setServiceName(WorkerConstants.IDLENESS_DETECTOR_WORKER_CONTROL_CLIENT);
		deployServiceResponseTO.setServiceClass(IdlenessDetectorWorkerControlClient.class);
		responses.add(deployServiceResponseTO);
		
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory.getInstance().getIdlenessDetectorDAO();
		idlenessDetectorDAO.setActive(request.isIdlenessDetectorOn());
		idlenessDetectorDAO.setIdlenessTime(request.getIdlenessTime());
		idlenessDetectorDAO.setScheduleTimes(scheduleTimes);
		
		CreateRepeatedActionResponseTO idlenessDetectorTO = new CreateRepeatedActionResponseTO();
		idlenessDetectorTO.setActionName(WorkerConstants.IDLENESSDETECTOR_ACTION_NAME);
		idlenessDetectorTO.setRepeatedAction(new IdlenessDetectorActionFactory().createIdlenessDetectorAction());
		responses.add(idlenessDetectorTO);
		
		ScheduleActionWithFixedDelayResponseTO scheduleTO = new ScheduleActionWithFixedDelayResponseTO();
		scheduleTO.setActionName(WorkerConstants.IDLENESSDETECTOR_ACTION_NAME);
		scheduleTO.setDelay(WorkerConstants.IDLENESSDETECTOR_VERIFICATION_TIME);
		scheduleTO.setInitialDelay(WorkerConstants.IDLENESSDETECTOR_VERIFICATION_TIME);
		scheduleTO.setTimeUnit(TimeUnit.MILLISECONDS);
		responses.add(scheduleTO);
	}
	
	private void setMasterPeer(StartWorkerRequestTO request, List<IResponseTO> responses) {
		WorkerDAOFactory.getInstance().getWorkerStatusDAO().setMasterPeerAddress(
				request.getMasterPeerAddress());
		
		RegisterInterestResponseTO to = new RegisterInterestResponseTO();
		to.setMonitorName(WorkerConstants.LOCAL_WORKER_MANAGEMENT);
		to.setMonitorableAddress(request.getMasterPeerAddress());
		to.setMonitorableType(WorkerManagementClient.class);
		
		responses.add(to);
	}
}
