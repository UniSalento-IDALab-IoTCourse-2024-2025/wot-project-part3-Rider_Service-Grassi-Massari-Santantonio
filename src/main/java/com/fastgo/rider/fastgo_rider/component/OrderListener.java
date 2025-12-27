package com.fastgo.rider.fastgo_rider.component;

import com.fastgo.rider.fastgo_rider.config.RabbitMqConfig;
import com.fastgo.rider.fastgo_rider.dto.OrderDto;
import com.fastgo.rider.fastgo_rider.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);

    @Autowired
    private OrderService orderService; 

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE)
    public void handleNewOrder(OrderDto orderDto) {
        logger.info("==========================================");
        logger.info("NUOVO ORDINE RICEVUTO VIA RABBITMQ");
        logger.info("ID Ordine: {}", orderDto.getId());
        logger.info("Ristorante: {}", orderDto.getShopName());
        
        try {
            orderService.saveOrderFromDto(orderDto);
            logger.info("Ordine elaborato e salvato correttamente.");
        } catch (Exception e) {
            logger.error("Errore durante il salvataggio dell'ordine ricevuto", e);
        }
        
        logger.info("==========================================");
    }
}