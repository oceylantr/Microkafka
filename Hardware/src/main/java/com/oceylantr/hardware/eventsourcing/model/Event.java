package com.oceylantr.hardware.eventsourcing.model;

import java.io.Serializable;
import java.util.Date;


public class Event implements Serializable{

	private static final long serialVersionUID = 3701346865738455218L;
	private Long id;
	private String transactionId;
	private String eventType;
	private Date creationDate;
	private String eventDetail;
	
	public Event(Long id, String transactionId, String eventType, Date creationDate, String eventDetail) {
		super();
		this.id = id;
		this.transactionId = transactionId;
		this.eventType = eventType;
		this.creationDate = creationDate;
		this.eventDetail = eventDetail;
	}

	public Event() {
		super();
	}
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


	public String getEventType() {
		return eventType;
	}


	public void setEventType(String eventType) {
		this.eventType = eventType;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public String getEventDetail() {
		return eventDetail;
	}


	public void setEventDetail(String eventDetail) {
		this.eventDetail = eventDetail;
	}

}
