package com.fastgo.rider.fastgo_rider.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastgo.rider.fastgo_rider.domain.Orders;
import com.fastgo.rider.fastgo_rider.dto.PositionDto;
import com.fastgo.rider.fastgo_rider.service.OrderService;
import com.fastgo.rider.fastgo_rider.service.RiderService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/order")
public class OrderRestController {
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private RiderService riderService;


    @PostMapping(value = "/getByPosition", produces = "application/json", consumes = "application/json" )
    public ResponseEntity<?> getOrdersByPosition(@RequestHeader("Authorization") String token, @RequestBody PositionDto positionDto) {

        if (!riderService.isRiderTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("message","Unauthorized: Invalid Rider Token"));
        }

        List<Orders> orders = orderService.getOrdersByPosition(positionDto, riderService.getRiderFromToken(token));
        if (orders.isEmpty()) {
            return ResponseEntity.status(200).body(Map.of("message", "No orders found near this position"));
        }


        return ResponseEntity.ok(orderService.convertToListOrderDto(orders));
    
    }
}
