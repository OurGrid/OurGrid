package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.accounting.AccountingController;
import org.ourgrid.peer.request.SaveRankingRequestTO;

public class SaveRankingRequester implements RequesterIF<SaveRankingRequestTO> {
	
	public List<IResponseTO> execute(SaveRankingRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		AccountingController.getInstance().saveRanking(responses, request.isThisMyPublicKey(), 
				request.getSenderPublicKey(), request.getRankingFilePath());
		
		return responses;
	}
}
