/**
 * 
 */
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
import javax.persistence.Table;

import org.hibernate.annotations.Index;

/**
 * @author Marcelo Emanoel
 * 
 */
@Entity
@Table(name="login")
public class Login implements Serializable {

	private static final long serialVersionUID = 3347546685623354304L;

	private Integer id;

	private User user;

	private Long beginTime;

	private Long endTime;
	
	private Long lastModified;

	private String loginResult;
	
	private List<Job> jobs;
	
	public Login(){
		setJobs(new ArrayList<Job>());
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
	 * @return the user
	 */
	@ManyToOne
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the beginTime
	 */
	public Long getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime the beginTime to set
	 */
	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return the endTime
	 */
	@Index(name="loginendtime")
	public Long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the loginResult
	 */
	@Column(length=255)
	public String getLoginResult() {
		return loginResult;
	}

	/**
	 * @param loginResult the loginResult to set
	 */
	public void setLoginResult(String loginResult) {
		this.loginResult = loginResult;
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

	/**
	 * @return the jobs
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "login")
	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

}
