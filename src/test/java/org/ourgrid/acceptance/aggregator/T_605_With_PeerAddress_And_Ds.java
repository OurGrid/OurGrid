package org.ourgrid.acceptance.aggregator;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.AggegatorUsableAddresses;
import org.ourgrid.acceptance.util.aggregator.T_603_Util;
import org.ourgrid.acceptance.util.aggregator.T_604_Util;
import org.ourgrid.acceptance.util.aggregator.T_605_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class T_605_With_PeerAddress_And_Ds extends AggregatorAcceptanceTestCase {
	
	private T_603_Util t_603_Util = new T_603_Util(getComponentContext());
	private T_604_Util t_604_Util = new T_604_Util(getComponentContext());
	private T_605_Util t_605_Util = new T_605_Util(getComponentContext());
	
	private AggregatorComponent component;
	
	@Before
	public void setUp() throws Exception {		
		super.setUp();
		component = t_603_Util.startAggregator();
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
		t_603_Util.hereIsStatusProviderList(component, true);
	}
	
	@ReqTest(test = "AT-605.1", reqs = "")
	@Test public void test_AT_605_1_Start() throws Exception {
		t_603_Util.startAggregatorAgain(component);		
	}
	
	@ReqTest(test = "AT-605.2", reqs = "stop component - alredy started")
	@Test public void test_AT_605_2_StopAggregator() throws Exception {
		t_603_Util.stopAggregatorAfterStart(component);
	}
	

	@ReqTest(test = "AT-605.3", reqs = "")
	@Test public void test_AT_605_3_CommunityStatusProviderIsDown() throws Exception {
		t_604_Util.communityStatusProviderTestCase(component, false, false);	
	}
	
	@ReqTest(test = "AT-605.4", reqs = "")
	@Test public void test_AT_605_4_CommunityStatusProviderIsUp() throws Exception {
		t_603_Util.communityStatusProviderIsUpSucessfullWarning(component);		
	}
	
	
	@ReqTest(test = "AT-605.5", reqs = "")
	@Test public void test_AT_605_5_HereIsCompleteHistoryStatusWithoutProvider() throws Exception {
		t_605_Util.hereIsCompleteHistoryStatus(component, true, false);		
	}
	
	@ReqTest(test = "AT-605.6", reqs = "")
	@Test public void test_AT_605_6_HereIsCompleteHistoryStatusProviderDown() throws Exception {
		t_605_Util.hereIsCompleteHistoryStatus(component, true, true);		
	}
	
	@ReqTest(test = "AT-605.7", reqs = "")
	@Test public void test_AT_605_7_PeerStatusProviderIsDown() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);		
		t_605_Util.peerStatusProviderIsDown(component, serviceID, false);		
	}
	
	@ReqTest(test = "AT-605.9", reqs = "")
	@Test public void test_AT_605_8_HereIsStatusProviderList() throws Exception {
	 	t_605_Util.hereIsStatusProviderList(component, true);		
	}
	
	
	@ReqTest(test = "AT-605.10", reqs = "")
	@Test public void test_AT_605_9_HereIsPeerStatusChangeHistory() throws Exception {
		t_604_Util.hereIsPeerStatusChangeHistory(component);
	}
	
	@ReqTest(test = "AT-605.11", reqs = "")
	@Test public void test_AT_605_10_PeerStatusProviderIsUp() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);	
		ServiceID serviceID2 = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02);
		t_605_Util.peerStatusProviderIsUp(component, serviceID);
		t_605_Util.peerStatusProviderIsUp(component, serviceID2);
	} 
	
}
