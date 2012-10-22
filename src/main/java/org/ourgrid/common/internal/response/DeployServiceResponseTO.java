package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;

/**
 * Requirement 302
 */
public class DeployServiceResponseTO implements IResponseTO {
	
	private final String RESPONSE_TYPE = OurGridResponseConstants.DEPLOY_SERVICE;
	
	
	private String serviceName;
	private Class<?> serviceClass;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
	
	public String getServiceName() {
		return this.serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;

	}

	public void setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
	}

	public Class<?> getServiceClass() {
		return serviceClass;
	}
}
