package com.oceylantr.gift.stream;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;

import com.oceylantr.gift.eventsourcing.EventTypes;
import com.oceylantr.gift.eventsourcing.model.Event;
import com.oceylantr.gift.eventsourcing.model.EventSerde;

public class GiftPlaceStream {
	
	public static void main(final String[] args) throws Exception {
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "gift_place");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.236.129:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class);
//		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
		
		final StreamsBuilder builder = new StreamsBuilder();
		KStream<String, Event> source = builder.stream("event_table");
		
		KStream<String, Event>[] branches = source.branch((key, event) -> isGiftPrepared(event),
														  (key, event) -> isPersonLocated(event));
		
		KStream<String, Event> giftPreparedEvents = branches[0];
		KStream<String, Event> personLocatedEvents = branches[1];
		
		giftPreparedEvents.join(personLocatedEvents, 
								  (giftPreparedEvent, personLocatedEvent) -> new Event( //FIXME
										  					(Long)System.currentTimeMillis(), 
										  					giftPreparedEvent.getTransactionId(), 
										  					EventTypes.gift_placed, 
										  					new Date(), 
										  					preparePlacementInfo(giftPreparedEvent, personLocatedEvent)), 
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

	private static String preparePlacementInfo(Event preparedEvent, Event locatedEvent) {
		
		//giftPrepare den gelen input : name_surname
		String person_name_surname = preparedEvent.getEventDetail();
		
		//personLocate  den gelen input :  "person#" + event.getEventDetail() + "#location" + "#Kat2No5"));
		String[] locationSplit = locatedEvent.getEventDetail().split("#");
		
		return "person#" + person_name_surname + "#location#" + locationSplit[3];
		
	}

	private static boolean isPersonLocated(Event event) {
		return EventTypes.person_located.equals(event.getEventType());
	}

	private static boolean isGiftPrepared(Event event) {
		return EventTypes.gift_prepared.equals(event.getEventType());
	}
	
}
