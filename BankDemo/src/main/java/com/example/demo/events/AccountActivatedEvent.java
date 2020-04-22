package com.example.demo.events;

import com.example.demo.basemodel.BaseEvent;

public class AccountActivatedEvent extends BaseEvent<String> {

	public final String status;
	
	public AccountActivatedEvent(String id, String status) {
		super(id);
		this.status = status;
	}

}
