package org.ourgrid.worker.response;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IResponseTO;

public abstract class AbstractIncomingTransferResponseTO implements IResponseTO {
	
	
	private IncomingHandle incomingHandle;
	
	
	public void setIncomingHandle(IncomingHandle incomingHandle) {
		this.incomingHandle = incomingHandle;
	}

	public IncomingHandle getIncomingHandle() {
		return incomingHandle;
	}
}
