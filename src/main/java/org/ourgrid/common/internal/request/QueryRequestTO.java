package org.ourgrid.common.internal.request;


import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.internal.OurGridRequestConstants;

public class QueryRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = OurGridRequestConstants.QUERY;
	private String query;
	private String clientAddress;

	/**
	 * @return the clientAddress
	 */
	public String getClientAddress() {
		return clientAddress;
	}

	/**
	 * @param clientAddress the clientAddress to set
	 */
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	
}
