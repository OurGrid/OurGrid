/**
 * 
 */
package org.ourgrid.peer.ui.sync.command;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.peer.ui.sync.PeerSyncApplicationClient;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

public class PeerQueryCommand extends AbstractCommand<PeerSyncApplicationClient> {

	public PeerQueryCommand(PeerSyncApplicationClient componentClient) {
		super(componentClient);
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Peer is not started.");
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
