package com.example.demo.services.interfaces;

import java.util.concurrent.CompletableFuture;

import com.example.demo.dtos.AccountCreateDTO;
import com.example.demo.dtos.CreditAccountDTO;
import com.example.demo.dtos.DebitAccountDTO;

public interface IAccountCommandService {

	public CompletableFuture<String> createAccount(AccountCreateDTO accountCreateDTO);
	public CompletableFuture<String> creditAccount(String accountNo, CreditAccountDTO creditAccountDTO);
	public CompletableFuture<String> debitAccount(String accountNo, DebitAccountDTO debitAccountDTO);
		
}
