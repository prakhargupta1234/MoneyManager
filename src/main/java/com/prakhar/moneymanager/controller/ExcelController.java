package com.prakhar.moneymanager.controller;

import com.prakhar.moneymanager.service.ExcelService;
import com.prakhar.moneymanager.service.ExpenseService;
import com.prakhar.moneymanager.service.IncomeService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @GetMapping("/download/income")
    public void downloadIncomeExcel(HttpServletResponse response ) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=incomes.xlsx");
        excelService.writeIncomeToExcel(response.getOutputStream(), incomeService.getCurrentMonthIncomesForCurrentUser());

    }

    @GetMapping("/download/expense")
    public void downloadExpenseExcel(HttpServletResponse response ) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.xlsx");
        excelService.writeExpenseToExcel(response.getOutputStream(), expenseService.grtCurrentMonthExpensesForCurrentUser());
    }
}
