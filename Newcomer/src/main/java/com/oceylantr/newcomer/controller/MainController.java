package com.oceylantr.newcomer.controller;

import java.util.Date;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.oceylantr.newcomer.eventsourcing.EventTypes;
import com.oceylantr.newcomer.eventsourcing.model.Event;
import com.oceylantr.newcomer.kafka.KafkaConfig;

@Controller
@RequestMapping("/")
public class MainController {

	@Autowired
	private KafkaConfig kafkaConfig;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView home() {
		return new ModelAndView("index");
	}
	
	@PutMapping("/person/{name_surname}")
	public @ResponseBody ResponseEntity<String> start(@PathVariable String name_surname) {
		
		Event event = new Event(
				(Long)System.currentTimeMillis(), 
				UUID.randomUUID().toString(), 
				EventTypes.newcomer_started, 
				new Date(), 
				name_surname);
		
		kafkaConfig.producerFactory().createProducer().send(new ProducerRecord<String, Event>("event_table", String.valueOf(System.currentTimeMillis()), event));
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ajaxtest", method = RequestMethod.GET)
	@ResponseBody
	public String ajaxtest() {
		return "Test success";
	}
	
	
	
}
