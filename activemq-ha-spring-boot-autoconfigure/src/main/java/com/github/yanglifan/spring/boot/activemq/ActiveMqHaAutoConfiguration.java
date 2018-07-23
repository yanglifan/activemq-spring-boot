package com.github.yanglifan.spring.boot.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.JmsException;
import org.springframework.jms.config.AbstractJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
@ConditionalOnMissingBean(ConnectionFactory.class)
public class ActiveMqHaAutoConfiguration {
	@Bean
	ConnectionFactory connectionFactory1() {
		return new ActiveMQConnectionFactory("admin", "", "tcp://localhost:61616");
	}

	@Bean
	ConnectionFactory connectionFactory2() {
		return new ActiveMQConnectionFactory("admin", "", "tcp://localhost:61616");
	}

	@Bean
	JmsListenerContainerFactory jmsListenerContainerFactory(List<ConnectionFactory> connectionFactories) {
		return new AbstractJmsListenerContainerFactory() {
			@Override
			protected AbstractMessageListenerContainer createContainerInstance() {
				MultiDcActiveMqListenerContainer multiDcActiveMqListenerContainer =
						new MultiDcActiveMqListenerContainer();
				multiDcActiveMqListenerContainer.setConnectionFactories(connectionFactories);
				return multiDcActiveMqListenerContainer;
			}
		};
	}
}

class MultiDcActiveMqListenerContainer extends DefaultMessageListenerContainer {
	private List<ConnectionFactory> connectionFactories;
	private List<MessageListenerContainer> messageListenerContainers = new ArrayList<>();

	@Override
	public void initialize() {
		connectionFactories.forEach(cf -> {
			DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
			messageListenerContainer.setConnectionFactory(cf);

			// TODO Set others
			messageListenerContainer.setDestinationName(getDestinationName());

			messageListenerContainers.add(messageListenerContainer);
			messageListenerContainer.initialize();
		});
	}

	@Override
	public void afterPropertiesSet() {
		// Avoid ex since no connection factory
		validateConfiguration();
		initialize();
	}

	@Override
	protected void doInitialize() throws JMSException {
		super.doInitialize();
	}

	@Override
	public void start() throws JmsException {
		messageListenerContainers.forEach(Lifecycle::start);
	}

	void setConnectionFactories(List<ConnectionFactory> connectionFactories) {
		this.connectionFactories = connectionFactories;
	}
}