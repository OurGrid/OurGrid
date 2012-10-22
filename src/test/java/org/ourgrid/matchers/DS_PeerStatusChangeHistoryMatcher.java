package org.ourgrid.matchers;

import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;

public class DS_PeerStatusChangeHistoryMatcher implements IArgumentMatcher{

	private List<DS_PeerStatusChange> historyList;
	
	public DS_PeerStatusChangeHistoryMatcher(List<DS_PeerStatusChange> historyList) {
		this.historyList = historyList;
	}
	
	public void appendTo(StringBuffer arg0) {
		
	}

	@SuppressWarnings("unchecked")
	public boolean matches(Object arg0) {
		if(!(arg0 instanceof List)) {
			return false;
		}
		
		List<DS_PeerStatusChange> historyListOnSystem = 
			(List<DS_PeerStatusChange>) arg0;
		
		if(this.historyList.size() != historyListOnSystem.size()) {
			return false;
		}
		
		Iterator<DS_PeerStatusChange> behaviorListIterator = this.historyList.iterator();
		Iterator<DS_PeerStatusChange> onSystemListIterator = historyListOnSystem.iterator();
		
		while(behaviorListIterator.hasNext() && onSystemListIterator.hasNext()) {
			DS_PeerStatusChange behaviorStatusChange = behaviorListIterator.next();
			DS_PeerStatusChange onSystemStatusChange = onSystemListIterator.next();
			
			if(!behaviorStatusChange.getPeerAddress().equals(onSystemStatusChange.getPeerAddress())){
				return false;
			}
  
			if(!behaviorStatusChange.getCurrentStatus().equals(onSystemStatusChange.getCurrentStatus())){
				return false;
			}
		}
		
		return true;
	}
	
	public static List<DS_PeerStatusChange> eqMatcher(List<DS_PeerStatusChange> historyList) {
		EasyMock.reportMatcher(new DS_PeerStatusChangeHistoryMatcher(historyList));
		return null;
	}

}
