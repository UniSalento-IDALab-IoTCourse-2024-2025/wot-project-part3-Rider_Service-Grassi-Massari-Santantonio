package com.fastgo.rider.fastgo_rider.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("riders")
public class Rider {
    
    @Id
    private String id;
    private String name;
    private String lastName;
    private String username;
    private String email;
    private Role role = Role.RIDER;
    private Vehicle vehicleType;
    private String pictureUrl;


    // Getters and Setters
     public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Role getRole() {
        return role;
    }
   
    public Vehicle getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(Vehicle vehicleType) {
        this.vehicleType = vehicleType;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
}
