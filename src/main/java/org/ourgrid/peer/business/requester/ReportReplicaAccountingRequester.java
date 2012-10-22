package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.controller.accounting.AccountingController;
import org.ourgrid.peer.request.ReportReplicaAccountingRequestTO;

public class ReportReplicaAccountingRequester implements RequesterIF<ReportReplicaAccountingRequestTO> {

	public List<IResponseTO> execute(ReportReplicaAccountingRequestTO request) {
		List<IResponseTO> responses = new  ArrayList<IResponseTO>();
		
		AccountingController.getInstance().reportReplicaAccounting(responses, 
				request.getAccounting(), request.getUserPublicKey());
		
		return responses;
	}
	
}
