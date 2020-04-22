package com.example.demo.services;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import com.example.demo.commands.CreateAccountCommand;
import com.example.demo.commands.CreditMoneyCommand;
import com.example.demo.commands.DebitMoneyCommand;
import com.example.demo.dtos.AccountCreateDTO;
import com.example.demo.dtos.CreditAccountDTO;
import com.example.demo.dtos.DebitAccountDTO;
import com.example.demo.services.interfaces.IAccountCommandService;

@Service
public class AccountCommandServiceImpl implements IAccountCommandService{

	private final CommandGateway commandGateway;
	
	public AccountCommandServiceImpl(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}
	
	@Override
	public CompletableFuture<String> createAccount(AccountCreateDTO accountCreateDTO) {
		return commandGateway.send(
				new CreateAccountCommand(
						UUID.randomUUID().toString(), 
						accountCreateDTO.getStartingBalance(), 
						accountCreateDTO.getCurrency()));
	}

	@Override
	public CompletableFuture<String> creditAccount(String accountNo, CreditAccountDTO creditAccountDTO) {
		return commandGateway.send(
				new CreditMoneyCommand(
						accountNo, 
						creditAccountDTO.getCreditAmount(),
						creditAccountDTO.getCurrency()));
	}

	@Override
	public CompletableFuture<String> debitAccount(String accountNo, DebitAccountDTO debitAccountDTO) {
		return commandGateway.send(
				new DebitMoneyCommand(
						accountNo, 
						debitAccountDTO.getDebitAmount(), 
						debitAccountDTO.getCurrency()));
	}

}
