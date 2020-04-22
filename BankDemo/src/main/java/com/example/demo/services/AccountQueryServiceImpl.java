package com.example.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Service;

import com.example.demo.services.interfaces.IAccountQueryService;

@Service
public class AccountQueryServiceImpl implements IAccountQueryService{

	private final EventStore eventStore;
	
	public AccountQueryServiceImpl(EventStore eventStore) {
		this.eventStore = eventStore;
	}
	
	@Override
	public List<Object> listEventsForAccount(String accountNo) {
		return eventStore
				.readEvents(accountNo)
				.asStream()
				.map(s -> s.getPayload())
				.collect(Collectors.toList());
	}

}
