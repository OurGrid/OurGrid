package org.ourgrid.acceptance.peer;



import org.junit.Assert;
import org.junit.Test;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;

public class AT_0044 {
	
	@Test
	public void testEnumParse(){
		Assert.assertEquals(LocalWorkerState.parse(WorkerStatus.DONATED), LocalWorkerState.DONATED);
		Assert.assertEquals(LocalWorkerState.parse(WorkerStatus.ERROR), LocalWorkerState.ERROR);
		Assert.assertEquals(LocalWorkerState.parse(WorkerStatus.IDLE), LocalWorkerState.IDLE);
		Assert.assertEquals(LocalWorkerState.parse(WorkerStatus.IN_USE), LocalWorkerState.IN_USE);
		Assert.assertEquals(LocalWorkerState.parse(WorkerStatus.OWNER), LocalWorkerState.OWNER);
	}

}
