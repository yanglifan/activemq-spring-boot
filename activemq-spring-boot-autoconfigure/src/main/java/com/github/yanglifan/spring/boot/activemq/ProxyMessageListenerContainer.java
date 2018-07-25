package com.github.yanglifan.spring.boot.activemq;

import org.springframework.context.Lifecycle;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class ProxyMessageListenerContainer extends DefaultMessageListenerContainer {
	private volatile boolean notCustomizeAckMode = true;

	private List<DefaultMessageListenerContainer> messageListenerContainers = new ArrayList<>();

	@Override
	public void initialize() {
		messageListenerContainers.forEach(DefaultMessageListenerContainer::initialize);
	}

	// TODO try to remove
	@Override
	public void afterPropertiesSet() {
		// Avoid ex since no connection factory
		validateConfiguration();
		initialize();

		if (notCustomizeAckMode) {
			all(c -> c.setSessionTransacted(true));
		}
	}

	@Override
	public void setDestinationName(String destinationName) {
		super.setDestinationName(destinationName);
		messageListenerContainers.forEach(mlc -> mlc.setDestinationName(destinationName));
	}

	@Override
	public void start() throws JmsException {
		messageListenerContainers.forEach(Lifecycle::start);
	}

	@Override
	public void setMessageListener(Object messageListener) {
		this.messageListenerContainers.forEach(mlc -> mlc.setMessageListener(messageListener));
	}

	@Override
	public void setSessionAcknowledgeMode(int sessionAcknowledgeMode) {
		all(c -> c.setSessionAcknowledgeMode(sessionAcknowledgeMode));
		notCustomizeAckMode = false;
	}

	private void all(Consumer<DefaultMessageListenerContainer> messageListenerContainerConsumer) {
		this.messageListenerContainers.forEach(messageListenerContainerConsumer);
	}

	void setConnectionFactories(List<ConnectionFactory> connectionFactories) {
		connectionFactories.forEach(cf -> {
			DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
			messageListenerContainer.setConnectionFactory(cf);
			messageListenerContainers.add(messageListenerContainer);
		});
	}
}
