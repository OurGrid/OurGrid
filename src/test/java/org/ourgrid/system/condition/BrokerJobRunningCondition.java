package org.ourgrid.system.condition;

import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.system.units.BrokerUnit;

public class BrokerJobRunningCondition extends BrokerJobStateCondition implements Condition {

	public BrokerJobRunningCondition( BrokerUnit brokerUnit, int jobid ) {

		super( brokerUnit, jobid, GridProcessState.RUNNING );
	}

}
