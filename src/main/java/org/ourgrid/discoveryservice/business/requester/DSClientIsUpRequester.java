package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.discoveryservice.request.DSClientIsUpRequestTO;

public class DSClientIsUpRequester implements RequesterIF<DSClientIsUpRequestTO>{

	public List<IResponseTO> execute(DSClientIsUpRequestTO request) {

		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		return responses;
	}
}