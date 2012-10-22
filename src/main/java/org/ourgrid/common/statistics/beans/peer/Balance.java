/**
 * 
 */
package org.ourgrid.common.statistics.beans.peer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Marcelo Emanoel
 * 
 */
@Entity
@Table(name="balance",
	   uniqueConstraints = {
			@UniqueConstraint(columnNames={"self", "other", "balance_time"})
	   }
)
public class Balance implements Serializable {

	private static final long serialVersionUID = 4565323570588600202L;

	private Integer id;
	
	private Long lastModified;

	private Date balanceTime;

	private Peer self;

	private Peer other;
	
	private List<BalanceValue> values;

	@OneToMany(cascade={CascadeType.ALL}, mappedBy = "balance", fetch = FetchType.LAZY)
	@OrderBy
	public List<BalanceValue> getValues() {
		return values;
	}

	public void setValues(List<BalanceValue> values) {
		this.values = values;
	}

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
	 * @return the self
	 */
	@ManyToOne
	@JoinColumn(name="self")
	public Peer getSelf() {
		return self;
	}

	/**
	 * @param self the self to set
	 */
	public void setSelf(Peer self) {
		this.self = self;
	}

	/**
	 * @return the other
	 */
	@ManyToOne
	@JoinColumn(name="other")
	public Peer getOther() {
		return other;
	}

	/**
	 * @param other the other to set
	 */
	public void setOther(Peer other) {
		this.other = other;
	}

	/**
	 * @return the balanceTime
	 */
	@Column(name="balance_time")
	public Date getBalanceTime() {
		return balanceTime;
	}

	/**
	 * @param balanceTime the balanceTime to set
	 */
	public void setBalanceTime(Date balanceTime) {
		this.balanceTime = balanceTime;
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
