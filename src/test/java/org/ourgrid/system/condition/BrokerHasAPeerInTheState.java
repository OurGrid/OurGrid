/* Created at 05/12/2006 */

package org.ourgrid.system.condition;

import java.util.Collection;

import org.ourgrid.broker.status.PeerStatusInfo;
import org.ourgrid.system.PeerTestState;
import org.ourgrid.system.units.BrokerUnit;
import org.ourgrid.system.units.PeerUnit;

public class BrokerHasAPeerInTheState implements Condition {

	private BrokerUnit brokerUnit;

	private PeerTestState state;

	private final PeerUnit peerUnit;
	
	private PeerTestState actualPeerState;
	
	public BrokerHasAPeerInTheState( BrokerUnit brokerUnit, PeerUnit peerUnit, PeerTestState state ) {

		this.brokerUnit = brokerUnit;
		this.peerUnit = peerUnit;
		this.state = state;
	}
	
	public boolean isConditionMet() throws Exception {

		Collection<PeerStatusInfo> peers = this.brokerUnit.getPeers();
		if ( peers != null ) {
			for ( PeerStatusInfo entry : peers ) {
				if ( entry.getPeerSpec().getLocation().equals( this.peerUnit.getLocation() ) ) {
					if (entry.isNotLogged())
						this.actualPeerState = PeerTestState.UP;
					else if (entry.isDown())
						this.actualPeerState = PeerTestState.DOWN;
					else if (entry.isLogged())
						this.actualPeerState = PeerTestState.LOGGED;
					
					return this.state.equals(this.actualPeerState);
				}
			}
		}
		
		return false;
	}


	public String detailMessage() {

		return "Expected Broker: " + brokerUnit.getLocation() + " has the Peer: " + this.peerUnit.getLocation()
				+ " in the state:" + this.state + ". Actual state: " + this.actualPeerState;
	}

}
