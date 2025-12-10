package com.fastgo.rider.fastgo_rider.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastgo.rider.fastgo_rider.domain.OrderStatus;
import com.fastgo.rider.fastgo_rider.domain.Orders;
import com.fastgo.rider.fastgo_rider.domain.Vehicle;
import com.fastgo.rider.fastgo_rider.dto.ListOrderDto;
import com.fastgo.rider.fastgo_rider.dto.OrderDto;
import com.fastgo.rider.fastgo_rider.dto.PositionDto;
import com.fastgo.rider.fastgo_rider.repositories.OrderRepository;
import com.fastgo.rider.fastgo_rider.security.JwtUtilities;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    public List<Orders> getByRiderToken(String token) {
        String riderUsername = jwtUtilities.extractUsername(token);
        Optional <List<Orders>> order = orderRepository.findByUsernameRider(riderUsername);
        if (order.isPresent()) {
            return order.get();
        } else {
            return null;
        }
    }

    public Orders getOrderById(String orderId) {
        Optional <Orders> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        } else {
            return null;
        }
    }

    public ListOrderDto convertToListOrderDto(List<Orders> orders) {
        ListOrderDto listOrderDto = new ListOrderDto();
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(Orders order : orders) {
            orderDtoList.add(convertToOrderDto(order));
        }
        listOrderDto.setOrders(orderDtoList);
        return listOrderDto;
    }

    public OrderDto convertToOrderDto(Orders order) {
        OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setRiderId(order.getRiderId());
            orderDto.setUsernameRider(order.getUsernameRider());
            orderDto.setVehicleType(order.getVehicleType());
            orderDto.setClientId(order.getClientId());
            orderDto.setUsernameClient(order.getUsernameClient());
            orderDto.setShopId(order.getShopId());
            orderDto.setShopName(order.getShopName());
            orderDto.setOrderDetails(order.getOrderDetails());
            orderDto.setOrderDate(order.getOrderDate());
            orderDto.setDeliveryDate(order.getDeliveryDate());
            orderDto.setDeliveryAddress(order.getDeliveryAddress());
            orderDto.setShopAddress(order.getShopAddress());
            orderDto.setOrderStatus(order.getOrderStatus());
            orderDto.setOrderResult(order.getOrderResult());
    
        return orderDto;
    }

    public Orders convertToOrder(OrderDto orderDto) {
    Orders order = new Orders();
    
    order.setId(orderDto.getId());
    order.setRiderId(orderDto.getRiderId());
    order.setUsernameRider(orderDto.getUsernameRider());
    order.setVehicleType(orderDto.getVehicleType());
    order.setClientId(orderDto.getClientId());
    order.setUsernameClient(orderDto.getUsernameClient());
    order.setShopId(orderDto.getShopId());
    order.setShopName(orderDto.getShopName());
    order.setOrderDetails(orderDto.getOrderDetails());
    order.setOrderDate(orderDto.getOrderDate());
    order.setDeliveryDate(orderDto.getDeliveryDate());
    order.setDeliveryAddress(orderDto.getDeliveryAddress());
    order.setShopAddress(orderDto.getShopAddress());
    order.setOrderStatus(orderDto.getOrderStatus());
    order.setOrderResult(orderDto.getOrderResult());
    
    return order;
}

    public List<Orders> convertToListOrder(ListOrderDto listOrderDto) {
    List<Orders> ordersList = new ArrayList<>();
    if (listOrderDto != null && listOrderDto.getOrders() != null) {
        for (OrderDto orderDto : listOrderDto.getOrders()) {
            ordersList.add(convertToOrder(orderDto));
        }
    }
    
    return ordersList;
}

    public List<Orders> getOrdersByPosition(PositionDto positionDto, com.fastgo.rider.fastgo_rider.domain.Rider rider) {

        Instant minimumDate = Instant.now().minus(90, ChronoUnit.MINUTES);

        Optional<List<Orders>> optionalOrders = orderRepository.findByOrderStatusAndOrderDateGreaterThanEqual(OrderStatus.PENDING, minimumDate);

        List<Orders> nearbyOrders = new ArrayList<>();
        if (optionalOrders.isPresent()) {
            List<Orders> orders = optionalOrders.get();
            for (Orders order : orders) {

                Double latRider = (Double) positionDto.getLatitudeRider();
                Double lonRider = (Double) positionDto.getLongitudeRider();
                Double latShop = Double.parseDouble(order.getShopAddress().getLatitude());
                Double lonShop = Double.parseDouble(order.getShopAddress().getLongitude());
                Double latClient = Double.parseDouble(order.getDeliveryAddress().getLatitude());
                Double lonClient = Double.parseDouble(order.getDeliveryAddress().getLongitude());

                double distanceRiderShop = calculateDistance(
                    latRider,
                    lonRider,
                    latShop,
                    lonShop
                );

                double distanceShopClient = calculateDistance(
                    latShop,
                    lonShop,
                    latClient,
                    lonClient
                );

                if (isNearby(distanceRiderShop, distanceShopClient, rider.getVehicleType())) {
                    nearbyOrders.add(order);
                }
                
            }
        }

        return nearbyOrders;

    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        final int RAGGIO_TERRA_KM = 6371; 

        double radLat1 = Math.toRadians(lat1);
        double radLon1 = Math.toRadians(lon1);
        double radLat2 = Math.toRadians(lat2);
        double radLon2 = Math.toRadians(lon2);

       
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;

        
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.pow(Math.sin(deltaLon / 2), 2);

        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAGGIO_TERRA_KM * c;
    }

    private boolean isNearby(double distanceRiderShop, double distanceShopClient, Vehicle vehicleType) {
        double maxDistanceRiderShop;
        double maxDistanceShopClient;

        switch (vehicleType) {
            case BIKE:
                maxDistanceRiderShop = 5.0; 
                maxDistanceShopClient = 10.0; 
                break;
            case MOTORCYCLE:
                maxDistanceRiderShop = 10.0; 
                maxDistanceShopClient = 10.0; 
                break;
            case CAR:
                maxDistanceRiderShop = 15.0; 
                maxDistanceShopClient = 25.0; 
                break;
            default:
                return false;
        }

        return distanceRiderShop <= maxDistanceRiderShop && distanceShopClient <= maxDistanceShopClient;
    }
}
