package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.control.PeerControl;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.request.GetCompleteHistoryStatusRequestTO;
import org.ourgrid.peer.response.HereIsCompleteHistoryStatusResponseTO;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteHistoryStatusBuilder;

public class GetCompleteHistoryStatusRequester extends AbstractGetStatusRequester<GetCompleteHistoryStatusRequestTO> {


	public List<IResponseTO> execute(GetCompleteHistoryStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		if (request.canStatusBeUsed()) {
			long time = request.getTime();
			
			if (time == 0L) {
				time = PeerControl.getInstance().getFirstDBUpdateDate(responses);
			}
			
			long until = Math.min(time + PeerConstants.AGGREGATOR_DATA_INTERVAL, System.currentTimeMillis());
			
			HereIsCompleteHistoryStatusResponseTO to = new HereIsCompleteHistoryStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAddress());
			to.setUntilTime(until);
			to.setPeerCompleteHistoryStatus(getCompleteHistoryStatus(request, time, until, responses));
			
			responses.add(to);
		}
		
		return responses;
	}
	
	private PeerCompleteHistoryStatus getCompleteHistoryStatus(GetCompleteHistoryStatusRequestTO request, long since, long until,
			List<IResponseTO> responses) {

		PeerCompleteHistoryStatusBuilder builder = new PeerCompleteHistoryStatusBuilder();
		PeerCompleteHistoryStatus completeStatus = builder.buildCompleteHistoryStatus(responses,
				since, until, /*getServiceManager().getContainerDAO().getUpTime()*/request.getUpTime(), 
				getDescription(request.getContextString(), request.getPropConfDir(), request.getPropLabel(), request.getPropJoinCommunity(),
						request.isJoinCommunityEnabled()), request.getPeerAddress());
		
		return completeStatus;
	}
	
}
