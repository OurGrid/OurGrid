package org.ourgrid.worker.response;

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;

public abstract class OutgoingTransferResponseTO implements IResponseTO {
	
	
	private OutgoingHandle outgoingHandle;

	
	public void setOutgoingHandle(OutgoingHandle outgoingHandle) {
		this.outgoingHandle = outgoingHandle;
	}

	public OutgoingHandle getOutgoingHandle() {
		return outgoingHandle;
	}
}
