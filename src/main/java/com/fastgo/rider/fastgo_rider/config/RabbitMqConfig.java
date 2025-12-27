package com.fastgo.rider.fastgo_rider.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.sync:sync-exchange}")
    private String syncExchangeName;

    private final String ROUTING_KEY_RIDER_SYNC = "rider.sync.request";
    private final String QUEUE_NAME_RIDER_SYNC = "rider.sync.request.queue";

    @Value("${rabbitmq.exchange.order:orders-exchange}")
    private String orderExchangeName;


    public static final String ORDER_QUEUE = "rider.order.queue";
    public static final String ROUTING_KEY_ORDER = "order.created";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(syncExchangeName);
    }

    @Bean
    public Queue riderSyncQueue() {
        return new Queue(QUEUE_NAME_RIDER_SYNC);
    }

    @Bean
    public Binding riderSyncBinding() {
        return BindingBuilder.bind(riderSyncQueue())
                .to(syncExchange())
                .with(ROUTING_KEY_RIDER_SYNC);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(orderExchangeName, true, false);
    }

    @Bean
    public Queue orderQueue() {
        
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ROUTING_KEY_ORDER);
    }

    public static final String ACCEPT_ROUTING_KEY = "order.accept";
   
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter()); 
        template.setReplyTimeout(5000); 
        return template;
    }
}