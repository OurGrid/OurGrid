package org.ourgrid.acceptance.aggregator;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.AggegatorUsableAddresses;
import org.ourgrid.acceptance.util.aggregator.T_603_Util;
import org.ourgrid.acceptance.util.aggregator.T_604_Util;
import org.ourgrid.acceptance.util.aggregator.T_605_Util;
import org.ourgrid.acceptance.util.aggregator.T_606_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class T_606_With_PeerAddress extends AggregatorAcceptanceTestCase {
	
	private T_603_Util t_603_Util = new T_603_Util(getComponentContext());
	private T_604_Util t_604_Util = new T_604_Util(getComponentContext());
	private T_605_Util t_605_Util = new T_605_Util(getComponentContext());
	private T_606_Util t_606_Util = new T_606_Util(getComponentContext());
	
	private AggregatorComponent component;
	
	@Before 
	public void initialization() throws Exception {	
		super.setUp();
		component = t_603_Util.startAggregator();
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
		t_603_Util.hereIsStatusProviderList(component, true);
		t_604_Util.communityStatusProviderTestCase(component, false, false);
	}
	
	@ReqTest(test = "AT-606.1", reqs = "")
	@Test public void test_AT_606_1_Start() throws Exception {
		t_603_Util.startAggregatorAgain(component);		
	}
	
	@ReqTest(test = "AT-606.2", reqs = "")
	@Test public void test_AT_606_2_CommunityStatusProviderIsDown() throws Exception {
		t_604_Util.communityStatusProviderTestCase(component, false, true);		
	}
	
	@ReqTest(test = "AT-606.3", reqs = "")
	@Test public void test_AT_606_3_HereIsStatusProviderList() throws Exception {
		t_603_Util.hereIsStatusProviderList(component, false);		
	}
	
	@ReqTest(test = "AT-606.4", reqs = "")
	@Test public void test_AT_606_4_HereIsPeerStatusChangeHistory() throws Exception {
		t_606_Util.hereIsPeerStatusChangeHistory(component, false);		
	}
	
	@ReqTest(test = "AT-606.5", reqs = "")
	@Test public void test_AT_606_5_HereIsCompleteHistoryStatusWithoutProvider() throws Exception {
		t_605_Util.hereIsCompleteHistoryStatus(component, true, false);		
	}
	
	@ReqTest(test = "AT-606.6", reqs = "")
	@Test public void test_AT_606_6_HereIsCompleteHistoryStatusProviderDown() throws Exception {
		t_605_Util.hereIsCompleteHistoryStatus(component, true, true);		
	}
	
	@ReqTest(test = "AT-606.7", reqs = "")
	@Test public void test_AT_606_7_PeerStatusProviderIsDown() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_605_Util.peerStatusProviderIsDown(component, serviceID, false);		
	}
	
	@ReqTest(test = "AT-606.8", reqs = "")
	@Test public void test_AT_606_8_PeerStatusProviderIsUp() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_605_Util.peerStatusProviderIsUp(component, serviceID);		
	} 
		
	@ReqTest(test = "AT-606.10", reqs = "")
	@Test public void test_AT_606_9_CommunityStatusProviderIsUp() throws Exception {
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
	}

}
