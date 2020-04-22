package com.oceylantr.hardware.eventsourcing.model;

import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSerializer implements Serializer<Event> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public EventSerializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] serialize(String topic, Event data) {
		if (Objects.isNull(data)) {
			return null;
		}
		try {
			return objectMapper.writeValueAsBytes(data);
		} catch (Exception e) {
			throw new SerializationException("Error serializing message", e);
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
