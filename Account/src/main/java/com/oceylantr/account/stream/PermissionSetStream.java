package com.oceylantr.account.stream;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import com.oceylantr.account.eventsourcing.EventTypes;
import com.oceylantr.account.eventsourcing.model.Event;
import com.oceylantr.account.eventsourcing.model.EventSerde;

public class PermissionSetStream {
	
	public static void main(final String[] args) throws Exception {
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "permission_set");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.236.129:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class);
//		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
		
		final StreamsBuilder builder = new StreamsBuilder();
		KStream<String, Event> source = builder.stream("event_table");
		
		KStream<String, Event>[] branches = source.branch((key, event) -> isAccountCreatedEvent(event),
														  (key, event) -> isPersonInfoRetrieved(event));
		
		KStream<String, Event> accountCreatedEvents = branches[0];
		KStream<String, Event> personInfoRetrieveEvents = branches[1];
		
		accountCreatedEvents.join(personInfoRetrieveEvents, 
								  (accountEvent, personInfoRetrieveEvent) -> new Event(
										  					(Long)System.currentTimeMillis(), 
										  					accountEvent.getTransactionId(), 
										  					EventTypes.permissions_created, 
										  					new Date(), 
										  					getAuthorizedPermissions(accountEvent, personInfoRetrieveEvent)), 
								  JoinWindows.of(3000))
							.filter((key, event) -> event != null)
							.to("event_table");
		
		
		final Topology topology = builder.build();
		 
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);
 
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
 
        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

	}

	private static String getAuthorizedPermissions(Event accountEvent, Event personInfoRetrieveEvent) {
		
		//account tan gelen input : "person#" + event.getEventDetail() + "#account_no#" + UUID.randomUUID().toString()));
		String[] accountSplit = accountEvent.getEventDetail().split("#");
		
		//person dan gelen input :  "person#" + event.getEventDetail() + "#" + "gorev" + "#yonetici"
		String[] personSplit = personInfoRetrieveEvent.getEventDetail().split("#");
		
		if (personSplit[3].contentEquals("yonetici")) {
			return "person#" + personSplit[1] + "#account#" + accountSplit[3] + "#role#admin";
		}else {
			return "person#" + personSplit[1] + "#account#" + accountSplit[3] + "#role#standart";
		}
		
	}

	private static boolean isPersonInfoRetrieved(Event event) {
		return EventTypes.person_info_retrieved.equals(event.getEventType());
	}

	private static boolean isAccountCreatedEvent(Event event) {
		return EventTypes.account_created.equals(event.getEventType());
	}
	
}
