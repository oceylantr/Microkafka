package com.oceylantr.hardware.stream;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import com.oceylantr.hardware.eventsourcing.EventTypes;
import com.oceylantr.hardware.eventsourcing.model.Event;
import com.oceylantr.hardware.eventsourcing.model.EventSerde;

public class HardwareOrderStream {
	
	public static void main(final String[] args) throws Exception {
		
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "hardware_order");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.236.129:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, EventSerde.class);
//		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
		
		final StreamsBuilder builder = new StreamsBuilder();
		KStream<String, Event> source = builder.stream("event_table");
		KStream<String, Event> filtered = source.filter(
				(key, event) -> isNewcomerStartedEvent(event));
		
		filtered.map((key, event) -> {
			return new KeyValue<String, Event>(
					key, new Event(
							(Long)System.currentTimeMillis(), 
							event.getTransactionId(), 
							EventTypes.hardware_ordered, 
							new Date(), 
							"person#" + event.getEventDetail() + "#hardware#IBM Thinkpad 001"));
		})
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

	private static boolean isNewcomerStartedEvent(Event event) {
		return EventTypes.newcomer_started.equals(event.getEventType());
	}
	
}
