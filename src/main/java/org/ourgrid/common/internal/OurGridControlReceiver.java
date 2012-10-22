package org.ourgrid.common.internal;

import br.edu.ufcg.lsd.commune.container.control.ServerModuleController;

/**
 * Requirement 301
 */
public abstract class OurGridControlReceiver extends ServerModuleController {

	
	/**
	 * Default constructor.
	 */
	public OurGridControlReceiver() {
		OurGridRequestControl.setInstance(createRequestControl());
	}
	

	/**
	 * 
	 * @return
	 */
	protected abstract RequestControlIF createRequestControl();

}
