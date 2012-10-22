package org.ourgrid.acceptance.util.aggregator;

import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.AggregatorAcceptanceUtil;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.business.messages.AggregatorControlMessages;
import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

public class T_606_Util extends AggregatorAcceptanceUtil {

	public T_606_Util(ModuleContext context) {
		super(context);
	}
	
	
	public void hereIsPeerStatusChangeHistory(AggregatorComponent component, boolean communityStatusProviderIsUp) {
		
		CommunityStatusProviderClient communityStatusProviderClient = (CommunityStatusProviderClient) 
		component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME).getObject();
		
		List<DS_PeerStatusChange> statusChanges = new ArrayList<DS_PeerStatusChange>();
		statusChanges.add(null);
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		if (communityStatusProviderIsUp) {
			newLogger.info(AggregatorControlMessages.
					getHereIsPeerStatusChangeHistoryInfoMessage());
		} else {
			newLogger.warn(AggregatorControlMessages.getCommunityStatusProviderIsDownWarningMessage());
		}		
		
		EasyMock.replay(newLogger);
		
		communityStatusProviderClient.hereIsPeerStatusChangeHistory(statusChanges, 0);
		
		EasyMock.verify(newLogger);
		EasyMock.reset(newLogger);
	}
	

}
