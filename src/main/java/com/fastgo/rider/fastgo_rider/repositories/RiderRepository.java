package com.fastgo.rider.fastgo_rider.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.fastgo.rider.fastgo_rider.domain.Rider;


@Repository
public interface RiderRepository extends MongoRepository<Rider, String> {

    Optional<Rider> findByUsername(String username);
} 
