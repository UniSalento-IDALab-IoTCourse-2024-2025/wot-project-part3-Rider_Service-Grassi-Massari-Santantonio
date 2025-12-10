package com.fastgo.rider.fastgo_rider.dto;


import com.fastgo.rider.fastgo_rider.domain.Role;
import com.fastgo.rider.fastgo_rider.domain.State;
import com.fastgo.rider.fastgo_rider.domain.Vehicle;


public class RiderDto {
    private String id;
    private String name;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Role role = Role.RIDER;
    private State status;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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
   
    public State getStatus() {
        return status;
    }
    public void setStatus(State status) {
        this.status = status;
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
}