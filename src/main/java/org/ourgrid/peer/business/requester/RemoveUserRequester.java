package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.request.RemoveUserRequestTO;


public class RemoveUserRequester implements RequesterIF<RemoveUserRequestTO> {


	public List<IResponseTO> execute(RemoveUserRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(request.getClientAddress());
		
		try {
			UserControl.getInstance().removeUser(responses, request.getLogin());
			to.setResult(request.getLogin());
		} catch (Exception e) {
			to.setErrorCause(e);
		}
		
		responses.add(to);
		
		return responses;
	}

}
