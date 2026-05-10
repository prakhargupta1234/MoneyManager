package com.prakhar.moneymanager.service;
import com.prakhar.moneymanager.dto.ExpenseDTO;
import com.prakhar.moneymanager.dto.IncomeDTO;
import com.prakhar.moneymanager.entity.CategoryEntity;

import com.prakhar.moneymanager.entity.ExpenseEntity;
import com.prakhar.moneymanager.entity.IncomeEntity;
import com.prakhar.moneymanager.entity.ProfileEntity;
import com.prakhar.moneymanager.repository.CategoryRepository;

import com.prakhar.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;


    public IncomeDTO addIncome(IncomeDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));
        IncomeEntity incomeEntity = toEntity(dto, profile, category);
        IncomeEntity savedIncome = incomeRepository.save(incomeEntity);
        return toDTO(savedIncome);
    }

    //helper methods
    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory()!= null? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory()!= null? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }



    //retrive the all the incomes of the current month/ based on the start and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startOfMonth, endOfMonth);
        return  list.stream().map(this::toDTO).toList();
    }


    //delete income by id  for current user
    public void deleteIncomeById(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + incomeId));
        if (!income.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this Income");
        }
        incomeRepository.delete(income);

    }


    //get latest 5 income of the current user

    public List<IncomeDTO> getLatest5IncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> income = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return income.stream().map(this::toDTO).toList();
    }

    //get total income for the current user
    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeRepository.findTotalExpenseAmountByProfileId(profile.getId());
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    //filter income
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return incomes.stream().map(this::toDTO).toList();
    }
}
