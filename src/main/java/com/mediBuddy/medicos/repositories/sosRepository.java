package com.mediBuddy.medicos.repositories;

import com.mediBuddy.medicos.model.SOS;
import com.mediBuddy.medicos.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface sosRepository  extends MongoRepository<SOS,String> {
    List<SOS> findByUserId(String userId);
}
