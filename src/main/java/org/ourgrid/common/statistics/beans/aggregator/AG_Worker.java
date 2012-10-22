package org.ourgrid.common.statistics.beans.aggregator;

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
public class AG_Worker implements Serializable {

	private static final long serialVersionUID = -2320362355104940595L;

	private String address;
	
	private Long lastModified;
	
	private Long beginTime;
	
	private Long endTime;
	
	private AG_Peer peer;

	private List<AG_Attribute> attributes;
	
	private Long id;
	
	private WorkerStatus status;
	
	private String allocatedFor;
	
	public AG_Worker(){
		setAttributes(new ArrayList<AG_Attribute>());
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(List<AG_Attribute> attributes) {
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
	public AG_Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(AG_Peer peer) {
		this.peer = peer;
	}

	/**
	 * @return the attributes
	 */
	@OneToMany(cascade={CascadeType.ALL}, mappedBy = "worker", fetch = FetchType.LAZY)
	@OrderBy
	public List<AG_Attribute> getAttributes() {
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
	
	@Index(name="workerendtime")
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public WorkerStatus getStatus() {
		return status;
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
}
