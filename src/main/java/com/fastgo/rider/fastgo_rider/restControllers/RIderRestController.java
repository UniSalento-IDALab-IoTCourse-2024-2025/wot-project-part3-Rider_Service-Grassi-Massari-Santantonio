package com.fastgo.rider.fastgo_rider.restControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastgo.rider.fastgo_rider.service.RiderService;

import java.util.List;
import java.util.Map;

import com.fastgo.rider.fastgo_rider.domain.Orders;
import com.fastgo.rider.fastgo_rider.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rider")
public class RIderRestController {


    @Autowired
    private RiderService riderService;

    @Autowired
    private OrderService orderService;

    @GetMapping(value = "/order", produces = "application/json")
    public ResponseEntity<?> getMethodName(@RequestHeader("Authorization") String token) {

        if (!riderService.isRiderTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("message","Unauthorized: Invalid Rider Token"));
        }
        
        List<Orders> order = orderService.getByRiderToken(token);
        if(order.size()==0){
            return ResponseEntity.status(200).body(Map.of("message","No orders found for this rider"));
        }


        return ResponseEntity.ok(orderService.convertToListOrderDto(order));
    }
    

     @GetMapping("/picture")
    public ResponseEntity<?> getMyProfilePicture(@RequestHeader("Authorization") String token) {
        try {
            

            if (!riderService.isRiderTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not valid");
            }

            String riderId = riderService.getRiderIdFromToken(token);

            return ResponseEntity.ok(riderService.getRiderProfilePicure(riderId));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    
}
