package org.ourgrid.common.internal;

import java.util.List;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * Requirement 301
 */
public interface ResponseControlIF {
	public void execute(List<IResponseTO> responses, ServiceManager manager);
}
