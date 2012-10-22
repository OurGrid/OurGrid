package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.accounting.AccountingController;
import org.ourgrid.peer.request.ReportWorkAccountingRequestTO;

public class ReportWorkAccountingRequester implements RequesterIF<ReportWorkAccountingRequestTO> {

	public List<IResponseTO> execute(ReportWorkAccountingRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		AccountingController.getInstance().reportWorkAccounting(responses, request.getAccountings(), 
				request.getWorkerPublicKey(), request.getWorkerAddress(), request.getWorkerUserAtServer(), 
				request.getMyPublicKey(), request.getMyCertSubjectDN());

		return responses;
	}

	
}
