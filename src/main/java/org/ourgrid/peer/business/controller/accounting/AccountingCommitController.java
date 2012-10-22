/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.peer.business.controller.accounting;

import java.util.List;
import java.util.Map;

import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.statistics.beans.peer.GridProcess;
import org.ourgrid.common.statistics.control.AccountingControl;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.Req;

public class AccountingCommitController {

	private static AccountingCommitController instance = null;
	
	private AccountingCommitController() {}
	
	public static AccountingCommitController getInstance() {
		if (instance == null) {
			instance = new AccountingCommitController();
		}
		return instance;
	}

	/**
	 * Commits accountings related to the RequestSpec parameter
	 * @param requestSpecification
	 */
	@Req("REQ035")
	public void commitAccounting(List<IResponseTO> responses, RequestSpecification requestSpecification, String myCertSubjectDN) {
		
		Map<String, List<GridProcess>> processes = AccountingControl.getInstance().
				getProcessesOfRequest(responses, requestSpecification.getRequestId());
		
		AccountingEvaluator evaluator = new AccountingEvaluator(processes, myCertSubjectDN);
		evaluator.evaluate();
		
		for (String providerDN : processes.keySet()) {
			
			if (!myCertSubjectDN.equals(providerDN)) {
				PeerBalance oldBalance = AccountingControl.getInstance().getRemotePeerBalance(responses,
						myCertSubjectDN,
						providerDN);
				
				if (oldBalance == null) {
					oldBalance = new PeerBalance(0., 0.);
				}
				
				PeerBalance balance = new PeerBalance(oldBalance.getCPUTime() + evaluator.getCPU(providerDN),
						oldBalance.getData() + evaluator.getData(providerDN));
				
				AccountingControl.getInstance().setRemotePeerBalance(responses,
						myCertSubjectDN,
						providerDN, balance);
			}
			
		}
	}
}
