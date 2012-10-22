package org.ourgrid.broker.communication.actions;

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class HereIsWorkerSpecMessageHandle extends MessageHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WorkerSpecification workerSpec;
	
	public HereIsWorkerSpecMessageHandle(WorkerSpecification workerSpec) {
		super(BrokerConstants.HERE_IS_WORKER_SPEC_ACTION_NAME);
		this.workerSpec = workerSpec;
	}
	
	public WorkerSpecification getWorkerSpec() {
		return this.workerSpec;
	}
	
}
