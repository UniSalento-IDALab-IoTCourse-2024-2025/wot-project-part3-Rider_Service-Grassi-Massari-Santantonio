package com.fastgo.rider.fastgo_rider.dto;

public class OrderStatusDto {
    private String orderId;
    private String orderStatus;

    public OrderStatusDto() {}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
}
