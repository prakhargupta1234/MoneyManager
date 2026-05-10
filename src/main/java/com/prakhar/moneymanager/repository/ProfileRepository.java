package com.prakhar.moneymanager.repository;

import com.prakhar.moneymanager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long>  {
    //jpa will execute the sql query to find the user by email
    // select * from profile_entity where email = ?
    Optional<ProfileEntity> findByEmail(String email);
    //jpa will execute the sql query to find the user by activation token
    Optional<ProfileEntity> findByActivationToken(String activationToken);

}
