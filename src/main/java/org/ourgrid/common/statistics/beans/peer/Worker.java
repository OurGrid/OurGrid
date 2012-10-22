package org.ourgrid.common.statistics.beans.peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;

@Entity
@Table(name = "worker", 
		uniqueConstraints = {
		@UniqueConstraint(columnNames={"address", "beginTime"})
})
public class Worker implements Serializable {

	private static final long serialVersionUID = -2320362355104940595L;

	private String address;
	
	private Long lastModified;
	
	private Long beginTime;
	
	private Long endTime;
	
	private Peer peer;

	private List<Attribute> attributes;
	
	private long id;
	
	private WorkerStatus status;
	
	private String allocatedFor;
	
	private double cpuTime;
	
	private double dataStored;
	
	public Worker(){
		setAttributes(new ArrayList<Attribute>());
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the idWorker
	 */
	@Column(length = 80)
	@Index(name="workeraddress")
	public String getAddress() {
		return address;
	}

	/**
	 * @param idWorker
	 *            the idWorker to set
	 */
	public void setAddress(String idWorker) {
		this.address = idWorker;
	}

	/**
	 * @return the peer
	 */
	@ManyToOne
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * @return the attributes
	 */
	@OneToMany(cascade={CascadeType.ALL}, mappedBy = "worker", fetch = FetchType.LAZY)
	@OrderBy
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the lastModified
	 */
	public Long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}
	
	public Long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Long benginTime) {
		this.beginTime = benginTime;
	}
	
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	
	public WorkerStatus getStatus() {
		return status;
	}

	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	/**
	 * @param allocatedFor the allocatedFor to set
	 */
	public void setAllocatedFor(String allocatedFor) {
		this.allocatedFor = allocatedFor;
	}

	/**
	 * @return the allocatedFor
	 */
	public String getAllocatedFor() {
		return allocatedFor;
	}
	
	@Column
	public double getCpuTime() {
		return cpuTime;
	}

	public void setCpuTime(double cpuTime) {
		this.cpuTime = cpuTime;
	}

	@Column
	public double getDataStored() {
		return dataStored;
	}

	public void setDataStored(double dataStored) {
		this.dataStored = dataStored;
	}
	
}
