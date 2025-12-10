package com.fastgo.rider.fastgo_rider.repositories;
import com.fastgo.rider.fastgo_rider.domain.Orders;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import com.fastgo.rider.fastgo_rider.domain.OrderStatus;

@Repository
public interface OrderRepository extends MongoRepository<Orders, String> {

    Optional <List<Orders>> findByUsernameRider(String riderUsername);
    Optional <List<Orders>> findByOrderStatus(OrderStatus orderStatus);
    Optional<List<Orders>> findByOrderStatusAndOrderDateGreaterThanEqual(
        OrderStatus orderStatus, 
        Instant dataMinima
    );


    
}
