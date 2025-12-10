package com.fastgo.rider.fastgo_rider.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fastgo.rider.fastgo_rider.domain.Rider;
import com.fastgo.rider.fastgo_rider.dto.ProfilePictureDto;
import com.fastgo.rider.fastgo_rider.dto.RiderDto;
import com.fastgo.rider.fastgo_rider.repositories.RiderRepository;
import com.fastgo.rider.fastgo_rider.security.JwtUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RiderService {
    
    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private JwtUtilities jwtUtilities;

    private static final Logger log = LoggerFactory.getLogger(RiderService.class);


    public boolean isRiderTokenValid(String token) {
        return jwtUtilities.hasRoleRider(token);
    }

    public boolean doesRiderExist(String riderId) {
        return riderRepository.existsById(riderId);
    }

    public Rider getRiderFromToken(String token) {
        String riderUsername = jwtUtilities.extractUsername(token);
        
        Optional<Rider> rider = riderRepository.findByUsername(riderUsername);
        if (rider.isPresent()) {
            return rider.get();
        }

        return null;
        
    }

    public String saveRiderFromDto(RiderDto rider) {
        
        try {
            Rider newRider = new Rider();
            newRider.setId(rider.getId());
            newRider.setUsername(rider.getUsername());
            newRider.setName(rider.getName());
            newRider.setLastName(rider.getLastName());
            newRider.setEmail(rider.getEmail());
            newRider.setVehicleType(rider.getVehicleType());
            newRider.setPictureUrl(rider.getPictureUrl());

            riderRepository.save(newRider);

            return "OK";

        } catch (DataAccessException e) {
            log.error("Database error while saving Rider ID: {}. Details: {}", rider.getId(), e.getMessage());
            return "ERROR: DB_SAVE_FAILED"; 

        } catch (Exception e) {
            log.error("Unexpected error while saving Rider ID: {}", rider.getId(), e);
            return "ERROR: UNEXPECTED_FAILURE";
        }
    }

    public String getRiderIdFromToken(String token) {
        return jwtUtilities.extractUserId(token);
    }

    public ProfilePictureDto getRiderProfilePicure(String riderId) {
         Rider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new RuntimeException("Shopkeeper not found with id: " + riderId));
    ProfilePictureDto dto = new ProfilePictureDto();
    dto.setProfilePicture(rider.getPictureUrl());
    return dto;
    }
}
