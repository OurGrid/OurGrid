package org.ourgrid.broker.ui.sync;

import java.util.concurrent.BlockingQueue;

import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.JobEndedInterested;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;

/**
 * @author Alan
 *
 */
public class JobEndedListener implements JobEndedInterested {

    private final BlockingQueue<Object> queue;
    private final int jobID;

    public JobEndedListener(int jobID, BlockingQueue<Object> queue) {
        this.jobID = jobID;
        this.queue = queue;
    }
   
    public void jobEnded(int jobid, GridProcessState state) {

        if (this.jobID == jobid) {
            SyncContainerUtil.putResponseObject(queue, jobid);
        }
    }

    public void schedulerHasBeenShutdown() {}
   
}
