package com.github.yanglifan.spring.boot.activemq.samples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableJms
@EnableScheduling
@SpringBootApplication
public class SampleApplication {
	private final JmsTemplate jmsTemplate;

	public SampleApplication(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Scheduled(fixedRate = 5_000)
	public void sendMessage() {
		jmsTemplate.convertAndSend("foobar", "foobar-message");
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

	@JmsListener(destination = "foobar")
	public void foobar(String message) {
		System.out.println("foobar receive -> " + message);
	}

	@JmsListener(destination = "rollback")
	public void rollback(String message) {
		System.out.println("rollback receive -> " + message);
		throw new RuntimeException("for rollback");
	}
}
