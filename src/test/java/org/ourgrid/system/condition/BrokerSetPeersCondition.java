package org.ourgrid.system.condition;

import java.util.Collection;

import org.ourgrid.broker.status.PeerStatusInfo;
import org.ourgrid.system.units.BrokerUnit;

public class BrokerSetPeersCondition implements Condition {

	private final BrokerUnit brokerUnit;

	private final int expectedNumPeers;

	private final boolean returnOnlyWhenPeersAlive;

	private int actualNumPeers;

	private boolean allAlive;


	public BrokerSetPeersCondition( BrokerUnit brokerUnit, int numPeers, boolean returnOnlyWhenPeersAlive ) {

		this.brokerUnit = brokerUnit;
		this.expectedNumPeers = numPeers;
		this.returnOnlyWhenPeersAlive = returnOnlyWhenPeersAlive;
		this.actualNumPeers = -1;
		this.allAlive = false;
	}


	public boolean isConditionMet() throws Exception {

		Collection<PeerStatusInfo> peers = brokerUnit.getPeers();
		actualNumPeers = peers.size();
		if ( actualNumPeers == expectedNumPeers ) {
			if ( returnOnlyWhenPeersAlive ) {
				for ( PeerStatusInfo entry : peers ) {
					if ( entry.isDown() ) {
						allAlive = false;
						return false;
					}
				}
				allAlive = true;
				return true;
			}
			return true;
		}

		return false;
	}


	public String detailMessage() {

		return "Number of peers expected: " + expectedNumPeers + ", actual: " + actualNumPeers + ", all alive: "
				+ allAlive;
	}

}
