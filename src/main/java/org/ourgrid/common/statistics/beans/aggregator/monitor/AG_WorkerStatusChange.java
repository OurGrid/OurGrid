/**
 * 
 */
package org.ourgrid.common.statistics.beans.aggregator.monitor;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.ourgrid.common.statistics.beans.aggregator.AG_Worker;
import org.ourgrid.common.statistics.beans.status.WorkerStatus;


/**
 * @author Marcelo Emanoel
 * 
 */
@Entity
@Table(name = "worker_status_change")
public class AG_WorkerStatusChange implements Serializable {

	private static final long serialVersionUID = -2128589720504527961L;

	private Integer id;

	private AG_Worker worker;

	private Long timeOfChange;

	private Long lastModified;
	
	private WorkerStatus status;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the worker
	 */
	@ManyToOne
	public AG_Worker getWorker() {
		return worker;
	}

	/**
	 * @param worker
	 *            the worker to set
	 */
	public void setWorker(AG_Worker worker) {
		this.worker = worker;
	}

	/**
	 * @return the changeTime
	 */
	public Long getTimeOfChange() {
		return timeOfChange;
	}

	/**
	 * @param changeTime
	 *            the changeTime to set
	 */
	public void setTimeOfChange(Long changeTime) {
		this.timeOfChange = changeTime;
	}

	/**
	 * @return the status
	 */
	@Enumerated(EnumType.STRING)
	@Index(name="wscstatus")
	public WorkerStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(WorkerStatus status) {
		this.status = status;
	}

	/**
	 * @return the lastModified
	 */
	@Index(name="wsclastmodifed")
	public Long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

}
