package com.fastgo.rider.fastgo_rider.restControllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastgo.rider.fastgo_rider.domain.Orders;
import com.fastgo.rider.fastgo_rider.dto.OrderDto;
import com.fastgo.rider.fastgo_rider.dto.OrderStatusDto;
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

        token = token.replace("Bearer ", "");
        if (!riderService.isRiderTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("message","Unauthorized: Invalid Rider Token"));
        }

        //System.out.println("Position received: " + positionDto.getLatitudeRider() + ", " + positionDto.getLongitudeRider());
        List<Orders> orders = orderService.getOrdersByPosition(positionDto, riderService.getRiderFromToken(token));
        if (orders.isEmpty()) {
            return ResponseEntity.status(200).body(Map.of("message", "No orders found near this position"));
        }


        return ResponseEntity.ok(orderService.convertToListOrderDto(orders));
    
    }


    @GetMapping(value = "/active", produces = "application/json")
    public ResponseEntity<?> getActiveOrder(@RequestHeader("Authorization") String token) {
        
        token = token.replace("Bearer ", "");

        if (!riderService.isRiderTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized: Invalid Rider Token"));
        }

        OrderDto activeOrder = orderService.getActiveOrderForRider(token);

        if (activeOrder != null) {
            return ResponseEntity.ok(activeOrder);
        } else {
            
            return ResponseEntity.status(200).body(null); 
        }
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> updateOrderStatus(@RequestHeader("Authorization") String token, 
                                               @RequestBody OrderStatusDto payload) {
        
        String cleanToken = token.replace("Bearer ", "");
        if (!riderService.isRiderTokenValid(cleanToken)) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        String orderId = payload.getOrderId();
        String newStatus = payload.getOrderStatus(); // "DELIVERING" o "COMPLETED"

        boolean success = orderService.updateStatus(orderId, newStatus, cleanToken);

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Status updated to " + newStatus));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Update failed"));
        }
    }
}
