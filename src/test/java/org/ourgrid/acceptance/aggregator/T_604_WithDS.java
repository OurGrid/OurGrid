package org.ourgrid.acceptance.aggregator;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.AggegatorUsableAddresses;
import org.ourgrid.acceptance.util.aggregator.T_603_Util;
import org.ourgrid.acceptance.util.aggregator.T_604_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class T_604_WithDS extends AggregatorAcceptanceTestCase {
	
	private T_604_Util t_604_Util = new T_604_Util(getComponentContext());	
	private T_603_Util t_603_Util = new T_603_Util(getComponentContext());
	private AggregatorComponent component;
	
	
	/*
	 * The Aggregator component is started always before these tests above.
	 */
	@Before 
	public void setUp() throws Exception {
		super.setUp();
		component = t_603_Util.startAggregator();
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
	}
	
	/*
	 * Start the same Aggregator again with the correct public key.
	 * Verify if the Control Result Operation contains an exception whose type is:
     *     o br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException
	 *
	 */
	@ReqTest(test = "AT-604.1", reqs = "start component")
	@Test public void test_AT_604_1_Start() throws Exception {
		t_603_Util.startAggregatorAgain(component);		
	}
	
	/*
	 * Stop the same Aggregator with the correct public key whereas the community
	 * status provider is Up.
	 */
	@ReqTest(test = "AT-603.2", reqs = "stop component - alredy started")
	@Test public void test_AT_604_2_StopAggregator() throws Exception {
		t_603_Util.stopAggregatorAfterStart(component);
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Successful operation. The component is down"
	 */
	@ReqTest(test = "AT-604.3", reqs = "community status provider is down")
	@Test public void test_AT_604_3_CommunityStatusProviderIsDown() throws Exception {
		t_604_Util.communityStatusProviderTestCase(component, false, false);
	}

	/*
	 * Verify if the following message was logged:
	 * "The component is already up"
	 */
	@ReqTest(test = "AT-604.4", reqs = "community status provider is already up")
	@Test public void test_AT_604_4_CommunityStatusProviderIsUp() throws Exception {
		t_604_Util.communityStatusProviderTestCase(component, true, false);
	}
	
	/*
	 *  Verify if the following message was logged:
	 * "Unsuccessful data transfer. The list´s status provider address is empty"
	 */
	@ReqTest(test = "AT-604.5", reqs = "here is complete history status")
	@Test public void test_AT_604_5_HereIsCompleteHistoryStatus() throws Exception {
		t_604_Util.hereIsCompleteHistoryStatus(component);		
	}
	
	/*
	 *  Verify if the following message was logged:
	 * "Unsuccessful data transfer. Without status provider list"
	 */
	@ReqTest(test = "AT-604.6", reqs = "peer status provider is up")
	@Test public void test_AT_604_6_PeerStatusProviderIsUp() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
						AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_604_Util.peerStatusProviderIsUp(component, serviceID, false);		
	} 
	
	/*
	 * Verify if the following message was logged:
	 * "Unsuccessful data transfer. The list´s status provider address is empty"
	 */
	@ReqTest(test = "AT-604.7", reqs = "peer status provider is down")
	@Test public void test_AT_604_7_PeerStatusProviderIsDown() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_604_Util.peerStatusProviderIsDown(component,serviceID, false);		
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Successful data transfer. The addresses are now ready"
	 */
	@ReqTest(test = "AT-604.9", reqs = "here is status provider list")
	@Test public void test_AT_604_8_HereIsStatusProviderList() throws Exception {
		t_603_Util.hereIsStatusProviderList(component, true);		
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Successful data transfer. The CommunityStatusProvider is up"
	 */
	@ReqTest(test = "AT-604.10", reqs = "here Is Peer Status Change History Community Is Up")
	@Test public void test_AT_604_9_HereIsPeerStatusChangeHistory() throws Exception {
		t_604_Util.hereIsPeerStatusChangeHistory(component);		
	}
	
	
	
	
}
