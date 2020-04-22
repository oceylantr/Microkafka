package com.oceylantr.people.eventsourcing.model;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class EventSerde implements Serde<Event> {

//	private EventSerializer serializer;
//	private EventDeserializer deserializer;
	private Serializer<Event> serializer;
	private Deserializer<Event> deserializer;
	
	public EventSerde() {
		this.serializer = new EventSerializer();
		this.deserializer = new EventDeserializer();
	}
	
	@Override
	public void configure(Map configs, boolean isKey) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void close() {
		this.serializer.close();
		this.deserializer.close();
	}
	
	@Override
	public Serializer serializer() {
		return this.serializer;
	}
	
	@Override
	public Deserializer deserializer() {
		return this.deserializer;
	}
	
	

}
