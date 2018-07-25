# ActiveMQ Spring Boot
## 简介
1. 简化 ActiveMQ 多机房集群配置

## 原因
### ActiveMQ 多机房容灾
ActiveMQ 带有一个 [Failover 配置方式](http://activemq.apache.org/failover-transport-reference.html)。 其中的多台 ActiveMQ Broker 彼此应该使用桥接方式相连，否则可能会出现没有 Consumer 的问题。但在多机房 ActiveMQ 集群中，考虑到跨机房的网络速度，不同机房的 Broker 之间并没有桥接。因此，为了保证每一个机房的 Broker 上都有足够数量的 Consumer，Consumer 需要分机房进行单独部署。这就产生了重复配置的问题。

Spring JMS 和 Spring Boot ActiveMQ Starter 对此场景并没有考虑。本项目的一个主要目的就是解决这一问题，简化多机房 ActiveMQ 的配置。

**注：本项目并不强依赖于 Spring Boot，但为了简化开发使用，设计为 Spring Boot Starter。后续如有需要，可讲实现和 Starter 分离。**

## 配置

假设：amq1/2 为一机房，amq3/4 为另一机房

YAML

```yml
activemq:
  producer:
    broker:
      url: failover:(tcp://amq1:61616,tcp://amq2:61616,tcp://amq3:61616,tcp://amq4:61616)
      username: admin
      password: admin
  consumer:
    brokers:
      - broker:
        url: failover:(tcp://amq1:61616,tcp://amq2:61616)
        username: admin
        password: amdin
      - broker:
        url: failover:(tcp://amq3:61616,tcp://amq4:61616)
        username: admin
        password: admin
```

Producer 利用 ActiveMQ 自带的 Failover 机制即可，Consumer 需分别配置，故 Broker 为列表。