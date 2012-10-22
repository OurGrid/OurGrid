package org.ourgrid.common.internal;


import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;


/**
 * Requirement 302
 */
public interface SenderIF<T extends IResponseTO> {
	public void execute(T response, ServiceManager manager);
}
