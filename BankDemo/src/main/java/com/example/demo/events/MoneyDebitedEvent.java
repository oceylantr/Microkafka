package com.example.demo.events;

import com.example.demo.basemodel.BaseEvent;

public class MoneyDebitedEvent extends BaseEvent<String> {

	public final double debitAmount;
	public final String currency;
	
	public MoneyDebitedEvent(String id, double debitAmount, String currency) {
		super(id);
		this.debitAmount = debitAmount;
		this.currency = currency;
	}

}
