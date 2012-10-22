package org.ourgrid.common.interfaces.to;

import java.io.File;
import java.io.Serializable;

public class GenericTransferHandle implements Serializable, Comparable<GenericTransferHandle> {

	
	private static final long serialVersionUID = 1L;

	
	private Long id;
	private String logicalFileName;
	private transient File localFile;
	private String description;
	private long fileSize;
	
	public GenericTransferHandle() {}

	public GenericTransferHandle(Long id, String logicalFileName, File localFile, String description) {
		this.id = id;
		this.logicalFileName = logicalFileName;
		this.description = description;
		this.localFile = localFile;
	}


	public Long getId() {
		return id;
	}

	public String getLogicalFileName() {
		return logicalFileName;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( !(obj instanceof GenericTransferHandle) )
			return false;
		final GenericTransferHandle other = (GenericTransferHandle) obj;
		return other.getId().equals(this.getId());
	}

	@Override
	public String toString() {
		return String.valueOf( id );
	}
	
	protected static Long randomID() {
		return Long.valueOf((long) (Math.random() * Long.MAX_VALUE));
	}
	
	public String getOppositeID() {
		return null;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLogicalFileName(String fileName) {
		this.logicalFileName = fileName;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public File getLocalFile() {
		return localFile;
	}
	
	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileSize() {
		return fileSize;
	}

	public int compareTo(GenericTransferHandle o) {
		return this.getId().compareTo(o.getId());
	}
}