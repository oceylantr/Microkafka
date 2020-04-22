package com.oceylantr.hardware.eventsourcing.model;

import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventDeserializer implements Deserializer<Event> {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		// TODO Auto-generated method stub

	}
	
	public EventDeserializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Event deserialize(String topic, byte[] bytes) {
		if (Objects.isNull(bytes)) {
			return null;
		}

		Event data;
		try {
			data = objectMapper.treeToValue(objectMapper.readTree(bytes), Event.class);
		} catch (Exception e) {
			throw new SerializationException(e);
		}

		return data;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
