package com.fastgo.rider.fastgo_rider.domain;



public class OrderDetails {
   
    private String idProduct;
    private String nameProduct;
    private int quantity;
    private double priceProduct; //prezzo per il singolo prodotto

    // Getters and Setters
    public String getIdProduct() {
        return idProduct;
    }   
    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }
    public String getNameProduct() {
        return nameProduct;
    }
    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPriceProduct() {
        return priceProduct;
    }
    public void setPriceProduct(double priceProduct) {
        this.priceProduct = priceProduct;
    }
    

}
