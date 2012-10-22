package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

public class UndeployServiceResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.UNDEPLOY_SERVICE;
	
	private String serviceName;
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
	
	public String getServiceName() {
		return this.serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;

	}
}
