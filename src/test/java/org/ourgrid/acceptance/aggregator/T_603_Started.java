package org.ourgrid.acceptance.aggregator;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.T_603_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.reqtrace.ReqTest;

public class T_603_Started extends AggregatorAcceptanceTestCase {
	
	private T_603_Util t_603_Util = new T_603_Util(getComponentContext());
	private AggregatorComponent component;
	
	
	/*
	 * The Aggregator component is started always before these tests above.
	 */
	@Before 
	public void setUp() throws Exception {
		super.setUp();
		component = t_603_Util.startAggregator();
	}
	
	/*
	 * Start the same Aggregator again with the correct public key.
	 * Verify if the Control Result Operation contains an exception whose type is:
     *     o br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException
	 *
	 */
	@ReqTest(test = "AT-603.1", reqs = "start component - alredy started")
	@Test public void test_AT_603_1_StartAggregatorAlreadyStarted() throws Exception {
		t_603_Util.startAggregatorAgain(component);
	}
	
	/*
	 * Stop the same Aggregator with the correct public key.
	 */
	@ReqTest(test = "AT-603.2", reqs = "stop component - alredy started")
	@Test public void test_AT_603_2_StopAggregator() throws Exception {
		t_603_Util.stopAggregatorAfterStart(component);
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Unsuccessful data transfer. The status provider is down"
	 * 
	 */
	@ReqTest(test = "AT-603.3", reqs = "here is status provider list")
	@Test public void test_AT_603_3_HereIsStatusProviderList() throws Exception {
		t_603_Util.hereIsStatusProviderList(component, false);
	}	
	
	/*
	 * Verify if the following message was logged:
	 * Unsuccessful data transfer. The status provider is down
	 */
	@ReqTest(test = "AT-603.4", reqs = "community status provider - is down")
	@Test public void test_AT_603_4_CommunityStatusProviderIsDown() throws Exception {
		t_603_Util.CommunityStatusProviderIsDownWarning(component); 
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Unsuccessful data transfer. The status provider is down"
	 */
	@ReqTest(test = "AT-603.5", reqs = "community status provider - is down")
	@Test public void test_AT_603_5_CommunityStatusProviderIsDownAgain() throws Exception {
		t_603_Util.CommunityStatusProviderIsDownWarningAgain(component);
	}
	
	/*
	 * Verify if the following message was logged:
	 * Unsuccessful data transfer. The Peer is not already
	 */
	@ReqTest(test = "AT-603.6", reqs = "here is peer status change history")
	@Test public void test_AT_603_6_HereIsPeerStatusChangeHistory() throws Exception {
		t_603_Util.hereIsPeerStatusChangeHistory(component);
	}
	
	/*
	 *  Verify if the following message was logged:
	 * "Unsuccessful data transfer. The status provider is down"
	 */
	@ReqTest(test = "AT-603.7", reqs = "here is complete history status")
	@Test public void test_AT_603_7_HereIsCompleteHistoryStatus() throws Exception {
		t_603_Util.hereIsCompleteHistoryStatus(component);
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Unsuccessful data transfer. The status provider is down"
	 */
	@ReqTest(test = "AT-603.8", reqs = "peer status provider is up")
	@Test public void test_AT_603_8_PeerStatusProviderIsUp() throws Exception {
		t_603_Util.peerStatusProviderStatusNotification(component, true);
	}
	
	/*
	 * Verify if the following message was logged:
	 * "Unsuccessful data transfer. The status provider is down"
	 */
	@ReqTest(test = "AT-603.9", reqs = "peer status provider is down")
	@Test public void test_AT_603_9_PeerStatusProviderIsDown() throws Exception {
		t_603_Util.peerStatusProviderStatusNotification(component, false);
	}

	/*
	 * Verify if the following message was logged:
	 * "Successful data transfer. the component is up"
	 */
	@ReqTest(test = "AT-603.11", reqs = "peer status provider is down")
	@Test public void test_AT_603_10_CommunityStatusProviderIsUp() throws Exception {
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
	}	
}
