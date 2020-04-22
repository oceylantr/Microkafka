package com.example.demo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.services.interfaces.IAccountQueryService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/bank-accounts")
@Api(value = "Query Accounts")
public class AccountQueryController {

	private IAccountQueryService queryService;
	
	public AccountQueryController(IAccountQueryService queryService) {
		this.queryService = queryService;
	}
	
	@GetMapping("/{accountNumber}/events")
	public List<Object> listEventsForAccount(@PathVariable(value = "accountNumber") String accountNumber) {
		return queryService.listEventsForAccount(accountNumber);
		
	}
	
}
