package com.fastgo.rider.fastgo_rider.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.exchange.sync:sync-exchange}")
    private String syncExchange;
    
    private final String ROUTING_KEY_RIDER_SYNC = "rider.sync.request";
    private final String QUEUE_NAME_RIDER_SYNC = "rider.sync.request.queue";

 
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

  
    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(syncExchange);
    }

   
    @Bean
    public Queue riderSyncQueue() {
        return new Queue(QUEUE_NAME_RIDER_SYNC);
    }

    @Bean
    public Binding riderSyncBinding(Queue ridertSyncQueue, DirectExchange syncExchange) {
        return BindingBuilder.bind(riderSyncQueue())
                .to(syncExchange)
                .with(ROUTING_KEY_RIDER_SYNC);
    }
}
