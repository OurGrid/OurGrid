package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class StartTransferResponseTO implements IResponseTO {
	
	
	private final String RESPONSE_TYPE = BrokerResponseConstants.START_TRANSFER;
	
	
	private long handleId;
	private String localFileName;
	private String description;
	private String id;
	
	/**
	 * @return the handleId
	 */
	public long getHandleId() {
		return handleId;
	}

	/**
	 * @param handleId the handleId to set
	 */
	public void setHandleId(long handleId) {
		this.handleId = handleId;
	}

	/**
	 * @return the localFileName
	 */
	public String getLocalFileName() {
		return localFileName;
	}

	/**
	 * @param localFileName the localFileName to set
	 */
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}
}