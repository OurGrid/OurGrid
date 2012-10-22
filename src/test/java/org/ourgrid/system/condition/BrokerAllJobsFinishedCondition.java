package org.ourgrid.system.condition;

import org.ourgrid.system.units.BrokerUnit;

public class BrokerAllJobsFinishedCondition implements Condition {

	private final BrokerUnit brokerUnit;


	public BrokerAllJobsFinishedCondition( BrokerUnit brokerUnit ) {

		this.brokerUnit = brokerUnit;
	}


	public boolean isConditionMet() throws Exception {

		brokerUnit.showStatus();
		return !brokerUnit.areThereJobsRunning();
	}


	public String detailMessage() {

		return "Broker still has running jobs";
	}

}
