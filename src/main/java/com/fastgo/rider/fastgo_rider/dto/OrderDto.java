package com.fastgo.rider.fastgo_rider.dto;

import java.time.Instant;
import java.util.List;

import com.fastgo.rider.fastgo_rider.domain.OrderDetails;
import com.fastgo.rider.fastgo_rider.domain.OrderResult;
import com.fastgo.rider.fastgo_rider.domain.OrderStatus;
import com.fastgo.rider.fastgo_rider.domain.Vehicle;

public class OrderDto {
    
    private String id;
    private String riderId;
    private String usernameRider;
    private Vehicle vehicleType;
    private String clientId;
    private String usernameClient;
    private String shopId;
    private String shopName;
    private List<OrderDetails> orderDetails;
    private Instant orderDate;
    private Instant deliveryDate;
    private com.fastgo.rider.fastgo_rider.domain.Address deliveryAddress;
    private com.fastgo.rider.fastgo_rider.domain.Address shopAddress;
    private OrderStatus orderStatus;
    private OrderResult orderResult;


    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRiderId() {
        return riderId;
    }
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }
    public String getUsernameRider() {
        return usernameRider;
    }
    public void setUsernameRider(String usernameRider) {
        this.usernameRider = usernameRider;
    }
    public Vehicle getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(Vehicle vehicleType) {
        this.vehicleType = vehicleType;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getUsernameClient() {
        return usernameClient;
    }
    public void setUsernameClient(String usernameClient) {
        this.usernameClient = usernameClient;
    }
    public String getShopId() {
        return shopId;
    }
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }
    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }
    public double getTotalPrice() {
        double price = 0.0;
        for (OrderDetails item : orderDetails) {
            price = item.getPriceProduct() * item.getQuantity();
            price += price;
        }
        return price;
    }
   
    public Instant getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }
    public Instant getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(Instant deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public com.fastgo.rider.fastgo_rider.domain.Address getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(com.fastgo.rider.fastgo_rider.domain.Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public com.fastgo.rider.fastgo_rider.domain.Address getShopAddress() {
        return shopAddress;
    }
    public void setShopAddress(com.fastgo.rider.fastgo_rider.domain.Address shopAddress) {
        this.shopAddress = shopAddress;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    public OrderResult getOrderResult() {
        return orderResult;
    }
    public void setOrderResult(OrderResult orderResult) {
        this.orderResult = orderResult;
    }
    
}
