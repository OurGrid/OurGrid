package org.ourgrid.common.statistics.beans.peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.ourgrid.common.statistics.beans.status.PeerStatus;

@Entity
@Table(name = "peer")
public class Peer implements Serializable {

	private static final long serialVersionUID = -3145907796541810237L;

	private String address;

	private String label;
	
	private String dNdata;
	
	private String description;

	private String email;

	private String latitude;

	private String longitude;
	
	private String version;
	
	private String timezone;
	
	private Long lastModified;
	
	private List<Worker> workers;

	private List<User> users;
	
	private Long lastUpTime;
	
	private PeerStatus currentStatus;

	public Peer(){
		setWorkers(new ArrayList<Worker>());
		setUsers(new ArrayList<User>());
	}
	
	/**
	 * @return the peerId
	 */
	@Index(name="peeraddress")
	@Column(unique=true)
	public String getAddress() {
		return address;
	}

	/**
	 * @param peerId
	 *            the peerId to set
	 */
	public void setAddress(String peerId) {
		this.address = peerId;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the email
	 */
	@Column(length=45)
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the latitude
	 */
	@Column(length=20)
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	@Column(length=20)
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the workers
	 */
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="peer", fetch=FetchType.LAZY)
	public List<Worker> getWorkers() {
		return workers;
	}

	/**
	 * @param workers the workers to set
	 */
	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the users
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="peer")
	public List<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<User> users) {
		this.users = users;
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

	public Long getLastUpTime() {
		return lastUpTime;
	}

	public void setLastUpTime(Long lastUpTime) {
		this.lastUpTime = lastUpTime;
	}
	
	public void setDNdata(String dNdata) {
		this.dNdata = dNdata;
	}
	
	@Id
	public String getDNdata() {
		return dNdata;
	}
	
	/**
	 * @return the currentStatus
	 */
	@Enumerated(EnumType.STRING)
	@Index(name="peerstatus")
	public PeerStatus getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(PeerStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public boolean equals(Object obj) {
		
		if (!(obj instanceof Peer) || obj == null) {
			return false;
		}
		
		Peer other = (Peer) obj;
		
		return other.getAddress().equals(this.getAddress());
	}

}
