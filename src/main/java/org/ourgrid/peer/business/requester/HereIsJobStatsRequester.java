package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.statistics.control.JobControl;
import org.ourgrid.peer.request.HereIsJobStatsRequestTO;

public class HereIsJobStatsRequester implements RequesterIF<HereIsJobStatsRequestTO> {

	public List<IResponseTO> execute(HereIsJobStatsRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		Long requestId = request.getJobStatusInfo().getPeersToRequests().get(
				request.getMyId());
		
		JobControl.getInstance().hereIsJobStats(
				responses, request.getJobStatusInfo(), requestId);
		
		return responses;
	}

}
