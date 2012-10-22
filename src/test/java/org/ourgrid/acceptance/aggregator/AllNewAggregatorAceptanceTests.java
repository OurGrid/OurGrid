package org.ourgrid.acceptance.aggregator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)  
@SuiteClasses({
	T_601_Initial.class,
	T_602_Created.class, 
	T_603_Started.class,
	T_604_WithDS.class,
	T_605_With_PeerAddress_And_Ds.class,
	T_606_With_PeerAddress.class,
	T_607_With_Peer.class,
	T_608_With_Peer_and_Ds.class	
	
})

public class AllNewAggregatorAceptanceTests {

}
