package com.oceylantr.smartyMessage.stream;

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

import com.oceylantr.smartyMessage.eventsourcing.EventTypes;
import com.oceylantr.smartyMessage.eventsourcing.model.Event;
import com.oceylantr.smartyMessage.eventsourcing.model.EventSerde;

public class InformNewcomerStream {
	
	public static void main(final String[] args) throws Exception {
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "inform_newcomer");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.236.129:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class);
//		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
		
		final StreamsBuilder builder = new StreamsBuilder();
		KStream<String, Event> source = builder.stream("event_table");
		
		KStream<String, Event>[] branches = source.branch((key, event) -> isHardwareSet(event),
														  (key, event) -> isPermissionCreated(event),
														  (key, event) -> isGiftPlaced(event));
		
		KStream<String, Event> hardwareSetEvents = branches[0]; 		//"person#" + event.getEventDetail() + "#hardware#IBM Thinkpad 001"));
		KStream<String, Event> permissionCreatedEvents = branches[1];	//"person#" + personSplit[1] + "#account#" + accountSplit[3] + "#role#admin";
		KStream<String, Event> giftPlacedEvents = branches[2];			//"person#" + person_name_surname + "#location#" + locationSplit[3];
		
		KStream<String, Event> join12Events = hardwareSetEvents.join(permissionCreatedEvents, 
								  (hardwareSetEvent, permissionCreatedEvent) -> new Event( //FIXME
										  					(Long)System.currentTimeMillis(), 
										  					hardwareSetEvent.getTransactionId(), 
										  					EventTypes.stream_joining, 
										  					new Date(), 
										  					getJoinData12(hardwareSetEvent, permissionCreatedEvent)), 
								  JoinWindows.of(3000))
							.filter((key, event) -> event != null);
		
		join12Events.join(giftPlacedEvents, 
								  (joined12Event, giftPlacedEvent) -> new Event(
										  (Long)System.currentTimeMillis(),
										  joined12Event.getTransactionId(),
										  EventTypes.newcomer_informed,
										  new Date(),
										  getJoinData123(joined12Event, giftPlacedEvent)), 
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


	private static String getJoinData123(Event joined12Event, Event giftPlacedEvent) {
		
		//get from join12
		//"person#" + event.getEventDetail() + "#hardware#IBM Thinkpad 001")) "#account#" + permissionCreatedSplit[3] + "#role#" + permissionCreatedSplit[5];
		
		//get from Gift
		//"person#" + person_name_surname + "#location#" + locationSplit[3];
		
		String[] join12Split = joined12Event.getEventDetail().split("#");
		String[] giftSplit = giftPlacedEvent.getEventDetail().split("#");
		
		String join123 = "Sevgili " + join12Split[1] + ", " + join12Split[5] + " no'lu hesabınız " + join12Split[7] + " rolü ile oluşturulmuştur. " +
						giftSplit[3] + " lokasyonunda " + join12Split[3] + " cihazınız ve sizi bekleyen sürprizleri göreceksiniz";
		
		return join123;
	}


	private static String getJoinData12(Event hardwareSetEvent, Event permissionCreatedEvent) {
		
		//get from hardwareSetEvent
		//"person#" + event.getEventDetail() + "#hardware#IBM Thinkpad 001"));
		
		//get from permissionCreatedEvent
		//"person#" + personSplit[1] + "#account#" + accountSplit[3] + "#role#admin";
		
		String join12 = hardwareSetEvent.getEventDetail();
		String[] permissionCreatedSplit = permissionCreatedEvent.getEventDetail().split("#");
		join12 = join12 + "#account#" + permissionCreatedSplit[3] + "#role#" + permissionCreatedSplit[5]; 
		
		return join12;
	}

	private static boolean isPermissionCreated(Event event) {
		return EventTypes.permissions_created.equals(event.getEventType());
	}

	private static boolean isHardwareSet(Event event) {
		return EventTypes.hardware_set.equals(event.getEventType());
	}
	
	private static boolean isGiftPlaced(Event event) {
		return EventTypes.gift_placed.equals(event.getEventType());
	}
	
}
