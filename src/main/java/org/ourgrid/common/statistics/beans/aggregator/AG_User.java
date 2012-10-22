/**
 * 
 */
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

/**
 * @author Marcelo Emanoel
 * 
 */
@Entity
@Table(name = "t_users", 
		uniqueConstraints = {
		@UniqueConstraint(columnNames={"address", "creationDate"})
})
public class AG_User implements Serializable {

	private static final long serialVersionUID = -7336946260535130290L;

	private AG_Peer peer;

	private String address;
	
	private String password;
	
	private String publicKey;
	
	private Long creationDate;

	private Long deletionDate;
	
	private Long lastModified;
	
	private List<AG_Login> logins;
	
	private long id;
	
	public AG_User(){
		setLogins(new ArrayList<AG_Login>());
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
	 * @return the address
	 */
	@Column(length=80)
	@Index(name="useraddress")
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the creationDate
	 */
	public Long getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the deletionDate
	 */
	public Long getDeletionDate() {
		return deletionDate;
	}

	/**
	 * @param deletionDate
	 *            the deletionDate to set
	 */
	public void setDeletionDate(Long deletionDate) {
		this.deletionDate = deletionDate;
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
	
	@Column(length=30)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(length=512)
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "user")
	public List<AG_Login> getLogins() {
		return logins;
	}

	public void setLogins(List<AG_Login> logins) {
		this.logins = logins;
	}

}
