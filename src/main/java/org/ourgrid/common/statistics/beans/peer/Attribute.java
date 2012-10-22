package org.ourgrid.common.statistics.beans.peer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "attribute")
public class Attribute implements Serializable {

	private static final long serialVersionUID = 3016518495576909851L;

	private Integer id;

	private String property;

	private String value;
	
	private boolean isAnnotation;

	private Long beginTime;

	private Long endTime;
	
	private Long lastModified;
	
	private Worker worker;

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
	 * @return the property
	 */
	@Column(length = 45)
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set whether it is an annotation.
	 * @param boolean
	 * the value to set
	 */
	public void setIsAnnotation(boolean isAnnotation) {
		this.isAnnotation = isAnnotation;
	}
	
	/**
	 * Return whether it is an annotation.
	 * @return a boolean indicating whether this attribute is an annotation. 
	 */
	public boolean getIsAnnotation() {
		return this.isAnnotation;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the worker
	 */
	@ManyToOne
	public Worker getWorker() {
		return worker;
	}

	/**
	 * @param worker
	 *            the worker to set
	 */
	public void setWorker(Worker worker) {
		this.worker = worker;
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
	
	@Override
	public String toString(){
			return property  + "\t" + value + "\t" + isAnnotation + "\t" + beginTime+ "\t" + endTime+ "\t" + lastModified + "\t" + worker;
	}

}
