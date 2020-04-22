package com.example.demo.model;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateLifecycle;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.example.demo.commands.CreateAccountCommand;
import com.example.demo.commands.CreditMoneyCommand;
import com.example.demo.commands.DebitMoneyCommand;
import com.example.demo.events.AccountActivatedEvent;
import com.example.demo.events.AccountCreatedEvent;
import com.example.demo.events.AccountHeldEvent;
import com.example.demo.events.MoneyCreditedEvent;
import com.example.demo.events.MoneyDebitedEvent;

@Aggregate
public class AccountAggregate {

	@AggregateIdentifier
	private String id;	
	private double accountBalance;
	private String currency;
	private String status;
	
	public AccountAggregate() {}
	
	//creation
	@CommandHandler
	public AccountAggregate(CreateAccountCommand command) {
		AggregateLifecycle.apply(
				new AccountCreatedEvent(
						command.id, 
						command.accountBalance, 
						command.currency));
	}
	
	@EventSourcingHandler
	protected void on(AccountCreatedEvent event) {
		this.id = event.id;
		this.accountBalance = event.accountBalance;
		this.currency = event.currency;
		this.status = "CREATED";
		
		AggregateLifecycle.apply(new AccountActivatedEvent(this.id, "ACTIVATED"));
	}
	
	//activation
	@EventSourcingHandler
	protected void on(AccountActivatedEvent event) {
		this.status = event.status;
	}
	
	//credit process
	@CommandHandler
	protected void on(CreditMoneyCommand command) {
		AggregateLifecycle.apply(
				new MoneyCreditedEvent(
						command.id, 
						command.creditAmount, 
						command.currency));
	}
	
	@EventSourcingHandler
	protected void on(MoneyCreditedEvent event) {
		if (this.accountBalance < 0 & (this.accountBalance + event.creditAmount) >= 0) {
			AggregateLifecycle.apply(new AccountActivatedEvent(event.id, "ACTIVATED"));
		}
		this.accountBalance += event.creditAmount;
	}
	
	//debit process
	@CommandHandler
	protected void on(DebitMoneyCommand command) {
		AggregateLifecycle.apply(
				new MoneyDebitedEvent(
						command.id, 
						command.debitAmount, 
						command.currency));
	}
	
	@EventSourcingHandler
	protected void on(MoneyDebitedEvent event) {
		if(this.accountBalance >= 0 & (this.accountBalance - event.debitAmount) < 0) {
			AggregateLifecycle.apply(new AccountHeldEvent(event.id, "HOLD"));
		}
		this.accountBalance -= event.debitAmount;
	}
	
	//Hold process
	@EventSourcingHandler
	protected void on(AccountHeldEvent event) {
		this.status = event.status;
	}
	
}
