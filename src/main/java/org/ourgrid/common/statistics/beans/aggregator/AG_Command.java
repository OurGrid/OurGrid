package org.ourgrid.common.statistics.beans.aggregator;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "command")
public class AG_Command implements Serializable {

	private Integer id;

	private String name;

	private String source;

	private String destination;
	
	private Long lastModified;

	private Long fileSize;
	
	private String fileName;
	
	private String status;
	
	private Double progress;
	
	private Double transferRate;
	
	private Long transferBegin;
	
	private Long transferEnd;
	
	private AG_GridProcess process;

	private static final long serialVersionUID = -1345110333473353973L;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
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
	 * @return the name
	 */
	@Column(length = 5)
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}

	/**
	 * @return the replica
	 */
	@ManyToOne
	public AG_GridProcess getProcess() {
		return process;
	}

	/**
	 * @param replica the replica to set
	 */
	public void setProcess(AG_GridProcess replica) {
		this.process = replica;
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
	 * @return the fileSize
	 */
	public Long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the newStatus
	 */
	@Column(length = 20)
	public String getStatus() {
		return status;
	}

	/**
	 * @param newStatus
	 *            the newStatus to set
	 */
	public void setStatus(String newStatus) {
		this.status = newStatus;
	}

	/**
	 * @return the progress
	 */
	public Double getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress to set
	 */
	public void setProgress(Double progress) {
		this.progress = progress;
	}

	/**
	 * @return the transferRate
	 */
	public Double getTransferRate() {
		return transferRate;
	}

	/**
	 * @param transferRate
	 *            the transferRate to set
	 */
	public void setTransferRate(Double transferRate) {
		this.transferRate = transferRate;
	}

	/**
	 * @return the transferBegin
	 */
	public Long getTransferBegin() {
		return transferBegin;
	}

	/**
	 * @param transferBegin
	 *            the transferBegin to set
	 */
	public void setTransferBegin(Long transferBegin) {
		this.transferBegin = transferBegin;
	}

	/**
	 * @return the transferEnd
	 */
	public Long getTransferEnd() {
		return transferEnd;
	}

	/**
	 * @param transferEnd
	 *            the transferEnd to set
	 */
	public void setTransferEnd(Long transferEnd) {
		this.transferEnd = transferEnd;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
