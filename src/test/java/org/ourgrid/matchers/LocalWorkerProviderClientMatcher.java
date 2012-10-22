package org.ourgrid.matchers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;

public class LocalWorkerProviderClientMatcher  implements IArgumentMatcher {

	LocalWorkerProviderClient provider;
	
	public LocalWorkerProviderClientMatcher(LocalWorkerProviderClient provider) {
		this.provider = provider;
	}
	
	public void appendTo(StringBuffer arg0) {
		
	}

	public boolean matches(Object arg0) {
		if ( !(arg0 instanceof ControlOperationResult) ) {
			return false;
		}
		
		LocalWorkerProviderClient peer = (LocalWorkerProviderClient) arg0;
		
		return peer == provider;
	}
	
	public static LocalWorkerProviderClient eqMatcher(LocalWorkerProviderClient provider) {
		EasyMock.reportMatcher(new LocalWorkerProviderClientMatcher(provider));
		return null;
	}

}
