package org.ourgrid.common.internal.response;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.OurGridResponseConstants;


public class ReleaseResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = OurGridResponseConstants.RELEASE;
	
	
	private String stubAddres;

	
	public void setStubAddress(String stubAddres) {
		this.stubAddres = stubAddres;
	}

	public String getStubAddress() {
		return stubAddres;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}