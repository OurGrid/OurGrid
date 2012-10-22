package org.ourgrid.acceptance.util.broker;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.broker.business.dao.Request;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.job.Job;
import org.ourgrid.common.specification.job.JobSpecification;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class TestJob {
	private Job job;
	private JobSpecification jobSpec;
	
	public void setJob(Job job) {
		this.job = job;
	}

	public TestJob(Job job, JobSpecification jobSpec) {
		this.job = job;
		this.jobSpec = jobSpec;
	}

	public Job getJob() {
		return job;
	}

	public JobSpecification getJobSpec() {
		return jobSpec;
	}
	
	public RequestSpecification getRequestByPeer(Module application, LocalWorkerProvider lwp) {
		
		List<Request> requests = new ArrayList<Request>(job.getRequests());
		for (Request request : requests) {
			
			LocalWorkerProvider other = AcceptanceTestUtil.getStub(application, new DeploymentID(request.getPeerID()),
					LocalWorkerProvider.class);
			
			if (lwp == other) {
				return request.getSpecification();
			}
		}
		
		return null;
	}

}
