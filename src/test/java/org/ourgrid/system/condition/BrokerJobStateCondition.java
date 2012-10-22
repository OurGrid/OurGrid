package org.ourgrid.system.condition;

import org.ourgrid.broker.util.UtilConverter;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.system.units.BrokerUnit;

public class BrokerJobStateCondition implements Condition {
	
	private final BrokerUnit brokerUnit;

	private final int jobid;

	private final GridProcessState expectedState;

	private GridProcessState actualState;


	public BrokerJobStateCondition( BrokerUnit brokerUnit, int jobid, GridProcessState expectedState ) {

		this.brokerUnit = brokerUnit;
		this.jobid = jobid;
		this.expectedState = expectedState;
	}


	public boolean isConditionMet() throws Exception {

		actualState = UtilConverter.getJobState(brokerUnit.getJob( jobid ).getState());
		return actualState == this.expectedState;
	}


	public String detailMessage() {

		return "Expected job state: " + this.expectedState + ", Actual job state: " + this.actualState;
	}

}
