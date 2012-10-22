/**
 * 
 */
package org.ourgrid.peer.ui.sync.command;

import java.util.Map;

import org.ourgrid.common.command.UIMessages;
import org.ourgrid.common.specification.OurGridSpecificationConstants;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;
import org.ourgrid.peer.ui.async.util.SDFDefaults;
import org.ourgrid.peer.ui.sync.PeerSyncApplicationClient;
import org.ourgrid.peer.ui.sync.PeerUIMessages;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command.AbstractCommand;

/**
 * @author alan
 *
 */
public class PeerRemoveWorkerCommand extends AbstractCommand<PeerSyncApplicationClient> {

	public PeerRemoveWorkerCommand(PeerSyncApplicationClient componentClient) {
		super(componentClient);
	}

	private void printNotStartedMessage() {
		System.out.println("Ourgrid Peer is not started.");
	}

	protected void execute(String[] params) throws Exception {
		if (isComponentStarted()) {
			String[] tokens = params[0].split("@");
			String username = tokens[0];
			String servername = tokens[1];
			
			Map<String, String> worker = CommonUtils.createSerializableMap();
			
			worker.put(OurGridSpecificationConstants.ATT_USERNAME, username);
			worker.put(OurGridSpecificationConstants.ATT_SERVERNAME, servername);
			worker.put(OurGridSpecificationConstants.ATT_COPY_FROM, SDFDefaults.COPY_FROM);
			worker.put(OurGridSpecificationConstants.ATT_COPY_TO, SDFDefaults.COPY_TO);
			worker.put(OurGridSpecificationConstants.ATT_REM_EXEC, SDFDefaults.REM_EXEC);
			
			getComponentClient().removeWorker(new WorkerSpecification(worker));
			
			System.out.println( PeerUIMessages.getSuccessMessage( "The worker <" + username + 
					"@" + servername + "> was successfuly removed from the peer." ) );
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
