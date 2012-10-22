package org.ourgrid.acceptance.aggregator;

import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.AggegatorUsableAddresses;
import org.ourgrid.acceptance.util.aggregator.T_603_Util;
import org.ourgrid.acceptance.util.aggregator.T_604_Util;
import org.ourgrid.acceptance.util.aggregator.T_605_Util;
import org.ourgrid.acceptance.util.aggregator.T_606_Util;
import org.ourgrid.acceptance.util.aggregator.T_607_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class T_607_With_Peer extends AggregatorAcceptanceTestCase {
	
	private T_603_Util t_603_Util = new T_603_Util(getComponentContext());
	private T_604_Util t_604_Util = new T_604_Util(getComponentContext());
	private T_605_Util t_605_Util = new T_605_Util(getComponentContext());
	private T_606_Util t_606_Util = new T_606_Util(getComponentContext());
	private T_607_Util t_607_Util = new T_607_Util(getComponentContext());
	
	private AggregatorComponent component;
	
	@Before 
	public void initialization() throws Exception {		
		super.setUp();
		component = t_603_Util.startAggregator();
		t_603_Util.communityStatusProviderIsUpSucessfull(component);
		t_603_Util.hereIsStatusProviderList(component, true);
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_604_Util.peerStatusProviderIsUp(component, serviceID, true);
		serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02);
		t_604_Util.peerStatusProviderIsUp(component, serviceID, true);
		t_604_Util.communityStatusProviderTestCase(component, false, false);
		
	}
	
	@ReqTest(test = "AT-607.1", reqs = "")
	@Test public void test_AT_607_1_Start() throws Exception {
		t_603_Util.startAggregatorAgain(component);		
	}
	
	
	@ReqTest(test = "AT-607.2", reqs = "")
	@Test public void test_AT_607_2_CommunityStatusProviderIsDown() throws Exception {
		t_603_Util.CommunityStatusProviderIsDownWarning(component);		
	}
	
	@ReqTest(test = "AT-607.3", reqs = "")
	@Test public void test_AT_607_3_HereIsStatusProviderList() throws Exception {
		t_603_Util.hereIsStatusProviderList(component, false);		
	}
	
	@ReqTest(test = "AT-607.4", reqs = "")
	@Test public void test_AT_607_4_HereIsPeerStatusChangeHistory() throws Exception {
		t_606_Util.hereIsPeerStatusChangeHistory(component, false);		
	}
	
	@ReqTest(test = "AT-607.5", reqs = "")
	@Test public void test_AT_607_5_HereIsCompleteHistoryStatus() throws Exception {
		t_607_Util.hereIsCompleteHistoryStatus(component);		
	}
	
	@ReqTest(test = "AT-607.6", reqs = "peer status provider is up")
	@Test public void test_AT_607_6_PeerStatusProviderIsUp() throws Exception {
		
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_607_Util.peerStatusProviderIsUp(component, serviceID, false);		
	} 
	
	@ReqTest(test = "AT-607.8", reqs = "")
	@Test public void test_AT_607_7_PeerStatusProviderIsDown() throws Exception {
		ServiceID serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_01);
		t_605_Util.peerStatusProviderIsDown(component, serviceID, true);
		serviceID = AggegatorUsableAddresses.userAtServerToServiceID(
				AggegatorUsableAddresses.PEER_STATUS_PROVIDER_02);
		t_605_Util.peerStatusProviderIsDown(component, serviceID, true);
	}
	
	@ReqTest(test = "AT-607.9", reqs = "")
	@Test public void test_AT_607_8_CommunityStatusProviderIsUp() throws Exception {
		t_604_Util.communityStatusProviderTestCase(component, true, true);
	}
	

}
