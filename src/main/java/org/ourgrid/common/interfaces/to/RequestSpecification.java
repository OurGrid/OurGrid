/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.common.interfaces.to;

import java.io.Serializable;
import java.util.Map;

import org.ourgrid.common.specification.job.JobSpecification;

import sun.security.provider.certpath.X509CertPath;

/**
 * The class represents a request made by a consumer. It stores information
 * about a request.
 */
public class RequestSpecification implements Serializable, Comparable<RequestSpecification> {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 40L;

	/** An ID that uniquely identifies this request. */
	private long requestId;

	/** Represents the expression that must be satisfied by processor. */
	private String requirements;

	/**
	 * This is useful for limiting the consumption of resources by the consumer,
	 * if it is needed. Otherwise, if it's not needed, the consumer can request
	 * an unlimited number of processors. Integer.MAX_VALUE means an unlimited
	 * number of processors.
	 */
	private int requiredWorkers;
	private long jobId;
	private JobSpecification jobSpecification;
	private int maxFails;
	private int maxReplicas;
	
	private X509CertPath requesterCertPath;
	
	public RequestSpecification() {
	}
	

	/**
	 * Default constructor.
	 * 
	 * @param requestId request's identifier.
	 * @param requirements represents the expression that must be satisfied by
	 *        processor.
	 * @param maxNumberOfWorkers number of processors requested. Integer.MAX_VALUE
	 *        is unlimited number of processors.
	 */
	public RequestSpecification(long jobId, JobSpecification jobSpecification, long requestId, String requirements, int maxNumberOfWorkers, 
			int maxFails, int maxReplicas, X509CertPath requesterPath) {
		
		this.jobId = jobId;
		this.jobSpecification = jobSpecification;
		this.requestId = requestId;
		this.requirements = requirements;
		this.requiredWorkers = maxNumberOfWorkers;
		this.maxFails = maxFails;
		this.maxReplicas = maxReplicas;
		this.requesterCertPath = requesterPath;
	}

	public RequestSpecification(long jobId, JobSpecification jobSpecification, long requestId, String requirements, int maxNumberOfWorkers, 
			int maxFails, int maxReplicas) {
		
		this(jobId, jobSpecification, requestId, requirements, maxNumberOfWorkers, maxFails, maxReplicas, null);
	}
	
	public long getRequestId() {
		return this.requestId;
	}

	public long getJobId() {
		return this.jobId;
	}
	
	public JobSpecification getJobSpecification() {
		return this.jobSpecification;
	}

	public String getRequirements() {
		return requirements;
	}
	
	public Map<String,String> getAnnotations() {
		return this.jobSpecification.getAnnotations();
	}

	public int getMaxFails() {
		return maxFails;
	}

	public int getMaxReplicas() {
		return maxReplicas;
	}


	public int getRequiredWorkers() {
		return this.requiredWorkers;
	}


	@Override
	public String toString() {

		return "ID: " + String.valueOf( this.getRequestId() );
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof RequestSpecification) {
			RequestSpecification otherspec = (RequestSpecification) o;
			return getRequestId() == otherspec.getRequestId();
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return (int) getRequestId();
	}


	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}

	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public void setJobSpecification(JobSpecification jobSpecification) {
		this.jobSpecification = jobSpecification;
	}

	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}

	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	/**
	 * @param requesterCertPath the requester certification path to set
	 */
	public void setRequesterDN(X509CertPath requesterCertPath) {
		this.requesterCertPath = requesterCertPath;
	}


	/**
	 * @return the requester certificate path
	 */
	public X509CertPath getRequesterCertPath() {
		return requesterCertPath;
	}


	public int compareTo(RequestSpecification o) {
		return (int)(requestId - o.requestId);
	}
}