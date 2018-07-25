package com.github.yanglifan.spring.boot.activemq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "kit.activemq")
public class ActiveMQProperties {
	private Producer producer;

	private Consumer consumer;

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public static class Broker {
		private String url;
		private String username;
		private String password;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class Producer {
		private Broker broker;

		public Broker getBroker() {
			return broker;
		}

		public void setBroker(Broker broker) {
			this.broker = broker;
		}
	}

	public static class Consumer {
		private List<Broker> brokers = new ArrayList<>();

		public List<Broker> getBrokers() {
			return brokers;
		}

		public void setBrokers(List<Broker> brokers) {
			this.brokers = brokers;
		}
	}
}
