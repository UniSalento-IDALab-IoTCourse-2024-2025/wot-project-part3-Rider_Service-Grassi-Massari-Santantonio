package com.fastgo.rider.fastgo_rider.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastgo.rider.fastgo_rider.dto.*;
import com.fastgo.rider.fastgo_rider.service.RiderService;



@Component
public class SyncListener {


    @Autowired
    RiderService riderService;

    private static final Logger log = LoggerFactory.getLogger(SyncListener.class);

   
    @RabbitListener(queues = "rider.sync.request.queue")
    public String handleRiderSyncRequest(SyncRiderDto syncRiderDto) {
        
        
        log.info("Richiesta di sincronizzazione ricevuta per: {}", syncRiderDto.toString());

        if (!riderService.isRiderTokenValid(syncRiderDto.getToken())) {
            return "ERROR: Unauthorized - Invalid Rider Token";
        }

        if (riderService.doesRiderExist(syncRiderDto.getRider().getId())){
            return "ERROR: Rider already exists";
        }
       
        return riderService.saveRiderFromDto(syncRiderDto.getRider());
    }
}