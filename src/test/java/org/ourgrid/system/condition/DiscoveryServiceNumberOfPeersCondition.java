package org.ourgrid.system.condition;

import org.ourgrid.system.units.DiscoveryServiceUnit;

public class DiscoveryServiceNumberOfPeersCondition implements Condition {

	private final DiscoveryServiceUnit dsUnit;

	private final int expectedNumberOfPeers;

	private int actualNumberOfPeers;


	public DiscoveryServiceNumberOfPeersCondition( DiscoveryServiceUnit dsUnit, int numberOfPeers ) {

		this.dsUnit = dsUnit;
		this.expectedNumberOfPeers = numberOfPeers;
		this.actualNumberOfPeers = 0;
	}


	public boolean isConditionMet() throws Exception {

		actualNumberOfPeers = dsUnit.getConnectedPeers().size();
		return actualNumberOfPeers == expectedNumberOfPeers;
	}


	public String detailMessage() {

		return "Number of peers expected: " + expectedNumberOfPeers + ", actual: " + actualNumberOfPeers;
	}

}
