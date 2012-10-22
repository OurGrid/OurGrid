/**
 * 
 */
package org.ourgrid.common.statistics.beans.peer.monitor;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.ourgrid.common.statistics.beans.peer.Peer;
import org.ourgrid.common.statistics.beans.status.PeerStatus;


/**
 * @author Marcelo Emanoel
 * 
 */
@Entity
@Table(name="peer_status_change")
public class LocalPeerStatusChange implements Serializable {

	private static final long serialVersionUID = -7344783968277807845L;

	private Integer id;

	private Peer peer;

	private Long timeOfChange;

	private Long lastModified;
	
	private String version;
	
	private PeerStatus currentStatus;
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the timeOfChange
	 */
	public Long getTimeOfChange() {
		return timeOfChange;
	}

	/**
	 * @param timeOfChange the timeOfChange to set
	 */
	public void setTimeOfChange(Long timeOfChange) {
		this.timeOfChange = timeOfChange;
	}

	/**
	 * @return the currentStatus
	 */
	@Enumerated(EnumType.STRING)
	public PeerStatus getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(PeerStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * @return the peer
	 */
	@ManyToOne
	@JoinColumn(name="peer_id")
	public Peer getPeer() {
		return peer;
	}

	/**
	 * @param peer the peer to set
	 */
	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
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
}
