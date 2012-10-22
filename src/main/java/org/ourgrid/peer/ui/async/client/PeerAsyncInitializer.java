package org.ourgrid.peer.ui.async.client;

import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class PeerAsyncInitializer {
	
	private static PeerAsyncInitializer instance;
	private PeerAsyncApplicationClient componentClient;
	private PeerAsyncUIModel model;
	
	
	private PeerAsyncInitializer() {
	}
	
	public static PeerAsyncInitializer getInstance() {
		if (instance == null) {
			instance = new PeerAsyncInitializer();
		}
		return instance;
	}
	
	public void setModel(PeerAsyncUIModel model) {
		this.model = model;
	}

	public PeerAsyncApplicationClient initComponentClient(ModuleContext context, final PeerAsyncUIModel model) 
		throws CommuneNetworkException, ProcessorStartException {
		
		stopComponentClient();
		
		this.model = model;
		this.componentClient = new PeerAsyncApplicationClient(context, model, new ConnectionListener() {
			
			public void connectionFailed(Exception e) {
				model.peerInitedFailed();
				
			}
			
			public void connected() {
				model.peerStopped();
				
			}

			public void disconnected() {
				if(model.isPeerEditing()){
					model.peerEditing();
				} else {
					model.peerInited();
				}
			}

			public void reconnected() {
				if(model.isPeerUp()){
					model.peerRestarted();
				}else{
					model.peerStopped();
				}
			}

			public void reconnectedFailed() {
				model.peerInitedFailed();
				
			}
		});
		return this.componentClient;
	}
	
	public void stopComponentClient() {
		if (this.componentClient != null) {
			try {
				this.componentClient.stop();
			} catch (CommuneNetworkException e) {
				e.printStackTrace();
			}
		}
	}

	public PeerAsyncUIModel getModel() {
		if (this.model == null) {
			this.model = new PeerAsyncUIModel();
		}
		
		return model;
	}

	public PeerAsyncApplicationClient getComponentClient() {
		return componentClient;
	}
}
