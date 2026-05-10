package com.prakhar.moneymanager.repository;

import com.prakhar.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    //select * from expenses where profile_id = ? order by date desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from expenses where profile_id = ? order by date desc limit 5
    List<ExpenseEntity>  findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT Sum(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseAmountByProfileId(@Param("profileId") Long profileId);

    //select * from expenses where profile_id = ? and date between ? and ? and name like %?%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    //select * from expenses where profile_id = ? and date between ? and ?
    List<ExpenseEntity> findByProfileIdAndDateBetween( Long profileId, LocalDate startDate, LocalDate endDate);


    //select * from expenses where profile_id = ? and date = ?
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

}
