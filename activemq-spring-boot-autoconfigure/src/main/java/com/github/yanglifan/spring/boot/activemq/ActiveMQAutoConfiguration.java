package com.github.yanglifan.spring.boot.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.AbstractJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;

import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;

@Configuration
@AutoConfigureBefore(org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration.class)
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
@ConditionalOnMissingBean(ConnectionFactory.class)
@EnableConfigurationProperties(ActiveMQProperties.class)
public class ActiveMQAutoConfiguration {
	private final ActiveMQProperties properties;

	public ActiveMQAutoConfiguration(ActiveMQProperties properties) {
		this.properties = properties;
	}

	/**
	 * To disable {@link org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration}
	 */
	@Bean
	ConnectionFactory connectionFactory() {
		ActiveMQProperties.Broker broker = properties.getConsumer().getBrokers().get(0);
		return new ActiveMQConnectionFactory(broker.getUsername(), broker.getPassword(), broker.getUrl());
	}

	@Bean
	List<ConnectionFactory> consumerConnectionFactories() {
		List<ConnectionFactory> connectionFactories = new ArrayList<>();

		for (ActiveMQProperties.Broker broker : properties.getConsumer().getBrokers()) {
			ConnectionFactory cf = new ActiveMQConnectionFactory(
					broker.getUsername(), broker.getPassword(), broker.getUrl()
			);

			connectionFactories.add(cf);
		}

		return connectionFactories;
	}

	@Bean
	JmsListenerContainerFactory jmsListenerContainerFactory() {
		return new AbstractJmsListenerContainerFactory() {
			@Override
			protected AbstractMessageListenerContainer createContainerInstance() {
				ProxyMessageListenerContainer proxyMessageListenerContainer =
						new ProxyMessageListenerContainer();
				proxyMessageListenerContainer.setConnectionFactories(consumerConnectionFactories());
				return proxyMessageListenerContainer;
			}
		};
	}
}
