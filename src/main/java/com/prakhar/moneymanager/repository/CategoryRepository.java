package com.prakhar.moneymanager.repository;

import com.prakhar.moneymanager.entity.CategoryEntity;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    //select * from category_entity where profile_id = ?
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from category_entity where id = ? and profile_id = ?
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //select * from category_entity where profile_id = ? and type = ?
    List<CategoryEntity> findByTypeAndProfileId( String type, Long profileId);

    //select * from category_entity where name = ? and profile_id = ?
    Boolean existsByNameAndProfileId(String name, Long profileId);

}
