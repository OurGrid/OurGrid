package org.ourgrid.common.internal;


import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 301
 */
public interface RequestControlIF {
	public <U extends IRequestTO> void execute(U request, ServiceManager serviceManager);
}
