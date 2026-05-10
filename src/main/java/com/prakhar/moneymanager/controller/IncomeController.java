package com.prakhar.moneymanager.controller;

import com.prakhar.moneymanager.dto.ExpenseDTO;
import com.prakhar.moneymanager.dto.IncomeDTO;
import com.prakhar.moneymanager.service.ExpenseService;
import com.prakhar.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO saved = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpenses(){
        List<IncomeDTO> incomes= incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }
}
