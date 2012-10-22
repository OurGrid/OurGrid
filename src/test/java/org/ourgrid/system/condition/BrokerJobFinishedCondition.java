package org.ourgrid.system.condition;

import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.system.units.BrokerUnit;

public class BrokerJobFinishedCondition extends BrokerJobStateCondition implements Condition {

	public BrokerJobFinishedCondition( BrokerUnit brokerUnit, int jobid ) throws Exception {

		super( brokerUnit, jobid, GridProcessState.FINISHED );
	}
}
