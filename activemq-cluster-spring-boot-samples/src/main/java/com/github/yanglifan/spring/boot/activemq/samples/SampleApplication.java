package com.github.yanglifan.spring.boot.activemq.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SampleApplication {
//	@Autowired
//	private JmsTemplate jmsTemplate;
//
//	@Scheduled(fixedDelay = 1000)
//	public void sendMessage() {
//		try {
//			jmsTemplate.convertAndSend("foobar", "haha");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}

	@JmsListener(destination = "foobar")
	public void foobar(String message) {
		System.out.println("foobar Q recv -> " + message);
	}
}
