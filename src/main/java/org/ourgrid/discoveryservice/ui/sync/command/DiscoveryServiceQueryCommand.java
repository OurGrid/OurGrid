/**
 * 
 */
package org.ourgrid.discoveryservice.ui.sync.command;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.discoveryservice.ui.sync.DiscoveryServiceSyncComponentClient;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

public class DiscoveryServiceQueryCommand extends AbstractCommand<DiscoveryServiceSyncComponentClient> {

	public DiscoveryServiceQueryCommand(DiscoveryServiceSyncComponentClient componentClient) {
		super(componentClient);
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Discovery Service is not started.");
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			String query = params[0];
			
			
			ControlOperationResult result = getComponentClient().query(query);
			
			if (result != null) {
				
				Exception errorCause = result.getErrorCause();
				if (errorCause == null) {
					System.out.println(result.getResult());
					
				} else {
					throw (Exception) errorCause;
				}
				
			}
			
		} else {
			printNotStartedMessage();
		}
	}

	protected void validateParams(String[] params) throws Exception {
		if ( params == null || params.length != 1 ) {
			throw new IllegalArgumentException( UIMessages.INVALID_PARAMETERS_MSG );
		}
	}
}
