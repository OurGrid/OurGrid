package org.ourgrid.matchers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.broker.communication.actions.HereIsFileInfoMessageHandle;
import org.ourgrid.common.filemanager.FileInfo;

import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;

public class HereIsFileInfoMessageHandleMatcher implements IArgumentMatcher{
	
	private String fileDigest;
	private String filePath;
	private TransferHandle transferHandle;
	
	private HereIsFileInfoMessageHandleMatcher(TransferHandle transferHandle, FileInfo fileInfo) {
		this.transferHandle = transferHandle;
		this.fileDigest = fileInfo.getFileDigest();
		this.filePath = fileInfo.getFilePath();
	}
	
	public boolean matches(Object arg0) {
		
		if(!HereIsFileInfoMessageHandle.class.isInstance(arg0)){
			return false;
		}
		
		if (arg0 == null) {
			return false;
		}
		
		HereIsFileInfoMessageHandle other = (HereIsFileInfoMessageHandle) arg0;
		
		long handlerId = other.getHandlerId();
		
		if(this.transferHandle != null){
			if(handlerId == 0 || handlerId != this.transferHandle.getId()) {
				return false;
			}
		}
		
		FileInfo fileInfoOther = other.getFileInfo();
		if(fileInfoOther == null) {
			return false;
		}else{
			if(!fileInfoOther.getFileDigest().equals(this.fileDigest)) {
				return false;
			}
			if(!fileInfoOther.getFilePath().equals(this.filePath)) {
				return false;
			}
		}
		
		return true;
	}

	public void appendTo(StringBuffer arg0) {
		
	}

	public static HereIsFileInfoMessageHandle eqMatcher(TransferHandle transferHandle, FileInfo fileInfo) {
		EasyMock.reportMatcher(new HereIsFileInfoMessageHandleMatcher(transferHandle, fileInfo));
		return null;
	}
	

}
