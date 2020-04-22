package com.example.demo.controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.AccountCreateDTO;
import com.example.demo.dtos.CreditAccountDTO;
import com.example.demo.dtos.DebitAccountDTO;
import com.example.demo.services.interfaces.IAccountCommandService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/bank-accounts")
@Api(value = "Account Commands")
public class AccountCommandController {

	private final IAccountCommandService commandService;
	
	public AccountCommandController(IAccountCommandService commandService) {
		this.commandService = commandService;
	}
	
	@PostMapping
	public CompletableFuture<String> createAccount(@RequestBody AccountCreateDTO dto) {
		return commandService.createAccount(dto);
	}
	
	@PutMapping(value = "/credits/{accountNumber}")
	public CompletableFuture<String> creditMoneyToAccount(
			@PathVariable(value = "accountNumber") String accountNumber,
			@RequestBody CreditAccountDTO dto){
				
		return commandService.creditAccount(accountNumber, dto);
	}
	
	@PutMapping(value = "/debits/{accountNumber}")
	public CompletableFuture<String> debitMoneyFromAccount(
			@PathVariable(value = "accountNumber") String accountNumber,
			@RequestBody DebitAccountDTO dto){
				
		return commandService.debitAccount(accountNumber, dto);
	}
	
}
