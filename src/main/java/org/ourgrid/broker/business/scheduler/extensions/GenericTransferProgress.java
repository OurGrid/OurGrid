package org.ourgrid.broker.business.scheduler.extensions;


import java.io.Serializable;

import org.ourgrid.common.interfaces.to.GenericTransferHandle;

public class GenericTransferProgress implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private GenericTransferHandle handle;
	private String newStatus;
	private long amountWritten;
	private double progress;
	private double transferRate;
	private String fileName;
	private long fileSize;
	private boolean outgoing;
	
	public GenericTransferProgress() {}


	public GenericTransferProgress(GenericTransferHandle handle, String fileName, long fileSize, 
			String newStatus, long amountWritten, double progress, double transferRate, boolean outgoing) {

		this.handle = handle;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.newStatus = newStatus;
		this.amountWritten = amountWritten;
		this.progress = progress;
		this.transferRate = transferRate;
		this.outgoing = outgoing;
	}


	public GenericTransferHandle getHandle() {
		return this.handle;
	}

	public String getNewStatus() {
		return this.newStatus;
	}

	public long getAmountWritten() {
		return this.amountWritten;
	}

	public double getProgress() {
		return this.progress;
	}

	public String getFileName() {
		return this.fileName;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public double getTransferRate() {
		return transferRate;
	}

	public boolean isOutgoing() {
		return this.outgoing;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.handle == null) ? 0 : this.handle.hashCode());
		long temp;
		temp = Double.doubleToLongBits( this.progress );
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( !(obj instanceof GenericTransferProgress) )
			return false;
		final GenericTransferProgress other = (GenericTransferProgress) obj;
		if ( !this.handle.equals( other.handle ) )
			return false;
		if ( Double.doubleToLongBits( this.progress ) != Double.doubleToLongBits( other.progress ) )
			return false;
		return true;
	}

	public void setHandle(GenericTransferHandle handle) {
		this.handle = handle;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

	public void setTransferRate(double transferRate) {
		this.transferRate = transferRate;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setOutgoing(boolean outgoing) {
		this.outgoing = outgoing;
	}
}