package com.example.demo.events;

import com.example.demo.basemodel.BaseEvent;

public class AccountHeldEvent extends BaseEvent<String> {

	public final String status;
	
	public AccountHeldEvent(String id, String status) {
		super(id);
		this.status = status;
	}

}
