package com.fastgo.rider.fastgo_rider.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastgo.rider.fastgo_rider.domain.OrderDetails;
import com.fastgo.rider.fastgo_rider.domain.OrderStatus;
import com.fastgo.rider.fastgo_rider.domain.Orders;
import com.fastgo.rider.fastgo_rider.domain.Rider;
import com.fastgo.rider.fastgo_rider.domain.Vehicle;
import com.fastgo.rider.fastgo_rider.dto.ListOrderDto;
import com.fastgo.rider.fastgo_rider.dto.OrderAcceptDto;
import com.fastgo.rider.fastgo_rider.dto.OrderDto;
import com.fastgo.rider.fastgo_rider.dto.OrderStatusUpdateDto;
import com.fastgo.rider.fastgo_rider.dto.PositionDto;
import com.fastgo.rider.fastgo_rider.repositories.OrderRepository;
import com.fastgo.rider.fastgo_rider.security.JwtUtilities;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    @Autowired
    private GeoCodingService geocodingService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final String ORDER_EXCHANGE = "orders-exchange";
    private final String ACCEPT_ROUTING_KEY = "order.accept";
    private final String UPDATE_STATUS_ROUTING_KEY = "order.status.update";

    public void saveOrderFromDto(OrderDto orderDto) {
        if (orderRepository.existsById(orderDto.getId())) {
            logger.info("Ordine {} già presente nel DB. Aggiornamento...", orderDto.getId());
        }

        Orders orderEntity = convertToOrder(orderDto);

        orderRepository.save(orderEntity);
        logger.info("Ordine {} salvato correttamente con coordinate.", orderEntity.getId());
    }

    public List<Orders> getByRiderToken(String token) {
        String riderUsername = jwtUtilities.extractUsername(token);
        Optional<List<Orders>> order = orderRepository.findByUsernameRider(riderUsername);
        if (order.isPresent()) {
            return order.get();
        } else {
            return null;
        }
    }

    public Orders getOrderById(String orderId) {
        Optional<Orders> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        } else {
            return null;
        }
    }

    public ListOrderDto convertToListOrderDto(List<Orders> orders) {
        ListOrderDto listOrderDto = new ListOrderDto();
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (Orders order : orders) {
            orderDtoList.add(convertToOrderDto(order));
        }
        listOrderDto.setOrders(orderDtoList);
        return listOrderDto;
    }

    public OrderDto convertToOrderDto(Orders order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setRiderId(order.getRiderId());
        orderDto.setRiderName(order.getUsernameRider());
        orderDto.setClientId(order.getClientId());
        orderDto.setUsernameClient(order.getUsernameClient());
        orderDto.setShopId(order.getShopId());
        orderDto.setShopName(order.getShopName());

        
        orderDto.setTotalPrice(order.getTotalPrice());

        
        if (order.getOrderDate() != null) {
            orderDto.setOrderDate(order.getOrderDate().toString());
        }

        
        if (order.getOrderStatus() != null) {
            orderDto.setOrderStatus(order.getOrderStatus().name());
        }

        
        if (order.getDeliveryAddress() != null) {
            OrderDto.AddressDto deliveryAddrDto = new OrderDto.AddressDto();
            deliveryAddrDto.setStreet(order.getDeliveryAddress().getStreet());
            deliveryAddrDto.setCity(order.getDeliveryAddress().getCity());
            deliveryAddrDto.setZipCode(order.getDeliveryAddress().getPostalCode());
            orderDto.setDeliveryAddress(deliveryAddrDto);
        }

        
        if (order.getShopAddress() != null) {
            OrderDto.AddressDto shopAddrDto = new OrderDto.AddressDto();
            shopAddrDto.setStreet(order.getShopAddress().getStreet());
            shopAddrDto.setCity(order.getShopAddress().getCity());
            shopAddrDto.setZipCode(order.getShopAddress().getPostalCode());
            orderDto.setShopAddress(shopAddrDto);
        }

        
        if (order.getOrderDetails() != null) {

            List<OrderDto.OrderDetailsDto> detailsDtoList = order.getOrderDetails().stream()
                    .map(item -> {
                        OrderDto.OrderDetailsDto d = new OrderDto.OrderDetailsDto();
                        d.setProductName(item.getNameProduct());
                        d.setQuantity(item.getQuantity());
                        d.setPriceProduct(item.getPriceProduct());
                        return d;
                    })
                    .collect(Collectors.toList());
            orderDto.setOrderDetails(detailsDtoList);
        }

        // --- CAMPI NON PRESENTI NEL DTO ---
        // orderDto.setVehicleType(order.getVehicleType());
        // orderDto.setOrderResult(order.getOrderResult());
        // DeliveryDate manca nel DTO

        return orderDto;
    }

    public Orders convertToOrder(OrderDto orderDto) {
        Orders order = new Orders();

        
        order.setId(orderDto.getId());
        order.setRiderId(orderDto.getRiderId());
        order.setUsernameRider(orderDto.getRiderName());
        order.setClientId(orderDto.getClientId());
        order.setUsernameClient(orderDto.getUsernameClient());
        order.setShopId(orderDto.getShopId());
        order.setShopName(orderDto.getShopName());

        if (orderDto.getOrderDate() != null) {
            try {
                order.setOrderDate(Instant.parse(orderDto.getOrderDate()));
            } catch (Exception e) {

                order.setOrderDate(Instant.now());
            }
        }

        if (orderDto.getOrderStatus() != null) {
           order.setOrderStatus(OrderStatus.IN_PROGRESS);
        }

        

        if (orderDto.getDeliveryAddress() != null) {
            com.fastgo.rider.fastgo_rider.domain.Address domainDeliveryAddr = new com.fastgo.rider.fastgo_rider.domain.Address();
            domainDeliveryAddr.setStreet(orderDto.getDeliveryAddress().getStreet());
            domainDeliveryAddr.setCity(orderDto.getDeliveryAddress().getCity());
            domainDeliveryAddr.setPostalCode(orderDto.getDeliveryAddress().getZipCode());

            GeoCodingService.Coordinates coords = resolveCoordinates(
                orderDto.getDeliveryAddress().getStreet(), 
                orderDto.getDeliveryAddress().getCity(), 
                orderDto.getDeliveryAddress().getZipCode()
            );

            if (coords != null) {
                domainDeliveryAddr.setLatitude(coords.lat);
                domainDeliveryAddr.setLongitude(coords.lon);
            } else {
                domainDeliveryAddr.setLatitude("0.0");
                domainDeliveryAddr.setLongitude("0.0");
            }
            order.setDeliveryAddress(domainDeliveryAddr);
        }

        // Shop Address
        if (orderDto.getShopAddress() != null) {
            com.fastgo.rider.fastgo_rider.domain.Address domainShopAddr = new com.fastgo.rider.fastgo_rider.domain.Address();
            domainShopAddr.setStreet(orderDto.getShopAddress().getStreet());
            domainShopAddr.setCity(orderDto.getShopAddress().getCity());
            domainShopAddr.setPostalCode(orderDto.getShopAddress().getZipCode());

            GeoCodingService.Coordinates coords = resolveCoordinates(
                orderDto.getShopAddress().getStreet(), 
                orderDto.getShopAddress().getCity(), 
                orderDto.getShopAddress().getZipCode()
            );


            if (coords != null) {
                domainShopAddr.setLatitude(coords.lat);
                domainShopAddr.setLongitude(coords.lon);
            } else {
                domainShopAddr.setLatitude("0.0");
                domainShopAddr.setLongitude("0.0");
            }
            order.setShopAddress(domainShopAddr);
        }

        // Order Details
        if (orderDto.getOrderDetails() != null) {
            List<OrderDetails> domainDetails = orderDto.getOrderDetails().stream()
                    .map(dtoItem -> {
                        OrderDetails item = new OrderDetails();
                        item.setNameProduct(dtoItem.getProductName());
                        item.setQuantity(dtoItem.getQuantity());
                        item.setPriceProduct(dtoItem.getPriceProduct());
                        return item;
                    })
                    .collect(Collectors.toList());
            order.setOrderDetails(domainDetails);
        }

        // order.setVehicleType(orderDto.getVehicleType());
        // order.setDeliveryDate(orderDto.getDeliveryDate());
        // order.setOrderResult(orderDto.getOrderResult());

        return order;
    }


    private GeoCodingService.Coordinates resolveCoordinates(String street, String city, String zip) {
 
        GeoCodingService.Coordinates coords = geocodingService.getCoordinatesStructured(street, city, zip);

        if (coords == null) {
            System.out.println("Geocoding preciso fallito per: " + street + ". Provo fallback città.");
            coords = geocodingService.getCoordinatesStructured(null, city, zip);
        }

        return coords;
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

        System.out.println("Rider Vehicle Type: " + rider.getVehicleType());
        System.out.println("Rider Position: " + positionDto.getLatitudeRider() + ", " + positionDto.getLongitudeRider());
        Instant minimumDate = Instant.now().minus(90, ChronoUnit.MINUTES);

        Optional<List<Orders>> optionalOrders = orderRepository
                .findByOrderStatusAndOrderDateGreaterThanEqual(OrderStatus.IN_PROGRESS, minimumDate);

        List<Orders> nearbyOrders = new ArrayList<>();
        if (optionalOrders.isPresent()) {
            List<Orders> orders = optionalOrders.get();
            for (Orders order : orders) {

                System.out.println("Checking Order ID: " + order.getId());
                Double latRider = (Double) positionDto.getLatitudeRider();
                Double lonRider = (Double) positionDto.getLongitudeRider();

                System.out.println("Rider Coordinates: " + latRider + ", " + lonRider

                );
                Double latShop = safeParseDouble(order.getShopAddress().getLatitude());
                Double lonShop = safeParseDouble(order.getShopAddress().getLongitude());

                System.out.println("Shop Coordinates: " + latShop + ", " + lonShop);
                Double latClient = safeParseDouble(order.getDeliveryAddress().getLatitude());
                Double lonClient = safeParseDouble(order.getDeliveryAddress().getLongitude());

                System.out.println("Client Coordinates: " + latClient + ", " + lonClient);


                double distanceRiderShop = calculateDistance(
                        latRider,
                        lonRider,
                        latShop,
                        lonShop);

                System.out.println("Distance Rider-Shop: " + distanceRiderShop + " km");
                double distanceShopClient = calculateDistance(
                        latShop,
                        lonShop,
                        latClient,
                        lonClient);

                System.out.println("Distance Shop-Client: " + distanceShopClient + " km");

                if (isNearby(distanceRiderShop, distanceShopClient, rider.getVehicleType())) {
                    nearbyOrders.add(order);
                }

            }
        }

        System.out.println("Found " + nearbyOrders.size() + " nearby orders.");

        return nearbyOrders;

    }

    private Double safeParseDouble(String value) {
        try {
            if (value == null)
                return 0.0;
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
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



    public boolean acceptOrder(String orderId, String token) {
 
        Rider rider = riderService.getRiderFromToken(token);
        if (rider == null) return false;

        boolean hasActiveOrder = orderRepository.existsByRiderIdAndOrderStatus(rider.getId(), OrderStatus.DELIVER);
        
        if (hasActiveOrder) {
            logger.warn("Rider {} ha già un ordine in consegna. Richiesta rifiutata.", rider.getUsername());
            return false; 
        }

        Optional<Orders> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            logger.error("Ordine {} non trovato in locale", orderId);
            return false;
        }
        Orders localOrder = orderOpt.get();

        if (localOrder.getOrderStatus() != OrderStatus.IN_PROGRESS) {
            return false; 
        }

        OrderAcceptDto acceptDto = new OrderAcceptDto(
            orderId,
            rider.getId(),
            rider.getName() + " " + rider.getLastName(),
            rider.getVehicleType().toString()
        );

        logger.info("Inviando richiesta accettazione RPC per ordine {}", orderId);

        try {
      
            Object response = rabbitTemplate.convertSendAndReceive(
                ORDER_EXCHANGE,
                ACCEPT_ROUTING_KEY,
                acceptDto
            );

           
            if (response != null) {
                String responseStr = response.toString(); 
                logger.info("Risposta RPC dallo Shop: {}", responseStr);

                if ("OK".equals(responseStr)) {
                    localOrder.setRiderId(rider.getId());
                    localOrder.setUsernameRider(rider.getUsername());
                    localOrder.setOrderStatus(OrderStatus.DELIVER); 
                    
                    
                    orderRepository.save(localOrder);
                    return true;
                }
            } else {
                logger.error("Timeout o nessuna risposta dallo Shop RPC");
            }

        } catch (Exception e) {
            logger.error("Errore comunicazione RPC", e);
        }

        return false;
    }


    public OrderDto getActiveOrderForRider(String token) {
        String riderUsername = jwtUtilities.extractUsername(token);
        
        
        Optional<List<Orders>> orders = orderRepository.findByUsernameRider(riderUsername);
        
        if (orders.isPresent()) {
            for (Orders order : orders.get()) {
                if (order.getOrderStatus() == OrderStatus.DELIVER  || order.getOrderStatus() == OrderStatus.DELIVERING) {
                    return convertToOrderDto(order);
                }
            }
        }
        return null; 
    }

    public boolean updateStatus(String orderId, String newStatusStr, String token) {
        
    
        com.fastgo.rider.fastgo_rider.domain.Rider rider = riderService.getRiderFromToken(token);
        if (rider == null) return false;
        
        String riderId = rider.getId();

        System.out.println("Updating status for order " + orderId + " to " + newStatusStr + " by rider " + riderId);
   
        boolean isValidTransition = 
            (newStatusStr.equalsIgnoreCase("DELIVERING") && 
             orderRepository.existsByRiderIdAndOrderStatus(riderId, OrderStatus.DELIVER)) 
            || 
            ((newStatusStr.equalsIgnoreCase("COMPLETED") || newStatusStr.equalsIgnoreCase("DELIVERED")) && 
             orderRepository.existsByRiderIdAndOrderStatus(riderId, OrderStatus.DELIVERING));

        if (isValidTransition) {
            Optional<Orders> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) return false;

            Orders order = orderOpt.get();

            
            OrderStatusUpdateDto updateDto = new OrderStatusUpdateDto(orderId, newStatusStr);
            
            logger.info("Inviando aggiornamento stato RPC per ordine {} a {}", orderId, newStatusStr);

            try {
                
                Object response = rabbitTemplate.convertSendAndReceive(
                    ORDER_EXCHANGE,
                    UPDATE_STATUS_ROUTING_KEY,
                    updateDto
                );

                
                if (response != null) {
                    String responseStr = response.toString();
                    logger.info("Risposta RPC dallo Shop (Update): {}", responseStr);

                    if ("OK".equals(responseStr)) {
                       
                        try {
                            OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);
                            order.setOrderStatus(newStatus);
                            orderRepository.save(order);
                            return true;
                        } catch (IllegalArgumentException e) {
                            logger.error("Stato non valido: {}", newStatusStr);
                            return false;
                        }
                    }
                } else {
                    logger.error("Timeout o nessuna risposta dallo Shop RPC per updateStatus");
                }

            } catch (Exception e) {
                logger.error("Errore comunicazione RPC Update Status", e);
            }
        } else {
            logger.warn("Transizione di stato non valida per ordine {} e rider {}", orderId, riderId);
        }
        
        return false;
    }
}
