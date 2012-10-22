package org.ourgrid.worker.ui.async.client;

import org.ourgrid.worker.ui.async.model.WorkerAsyncUIModel;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class WorkerAsyncInitializer {
	
	private static WorkerAsyncInitializer instance;
	private WorkerAsyncComponentClient componentClient;
	private WorkerAsyncUIModel model;
	
	
	private WorkerAsyncInitializer() {
	}
	
	public static WorkerAsyncInitializer getInstance() {
		if (instance == null) {
			instance = new WorkerAsyncInitializer();
		}
		return instance;
	}
	
	public void setModel(WorkerAsyncUIModel model) {
		this.model = model;
	}

	public WorkerAsyncComponentClient initComponentClient(ModuleContext context, final WorkerAsyncUIModel model) 
		throws CommuneNetworkException, ProcessorStartException {
		
		stopComponentClient();
		
		this.model = model;
		this.componentClient = new WorkerAsyncComponentClient(context, model, new ConnectionListener() {
			
			public void connectionFailed(Exception e) {
				model.workerInitedFailed();
			}
			
			public void connected() {
				model.workerStopped();
				
			}

			public void disconnected() {
				if(model.isWorkerEditing()){
					model.workerEditing();
				} else {
					model.workerInited();
				}
			}

			public void reconnected() {
				if(model.isWorkerUp()){
					model.workerRestarted();
				}else{
					model.workerStopped();
				}
			}

			public void reconnectedFailed() {
				model.workerInitedFailed();
				
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

	public WorkerAsyncUIModel getModel() {
		if (this.model == null) {
			this.model = new WorkerAsyncUIModel();
		}
		
		return model;
	}

	public WorkerAsyncComponentClient getComponentClient() {
		return componentClient;
	}
}
