package com.prakhar.moneymanager.service;


import com.prakhar.moneymanager.dto.ExpenseDTO;
import com.prakhar.moneymanager.entity.ProfileEntity;
import com.prakhar.moneymanager.repository.ExpenseRepository;
import com.prakhar.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

  @Scheduled(cron = "0 0 22 * * *", zone = "IST")
 // @Scheduled(cron = "0 * * * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {

        log.info("Job started: sendDailyIncomeExpenseReminder()");

        List<ProfileEntity> profiles = profileRepository.findAll();

      for (ProfileEntity profile : profiles) {

          if (profile.getEmail() == null || profile.getEmail().isEmpty()) {
              log.warn("Skipping user with null email: " + profile.getId());
              continue;
          }

          if (frontendUrl == null) {
              log.error("Frontend URL is NULL!");
              return;
          }

          String name = profile.getFullName() != null ? profile.getFullName() : "User";

          String body = "<div style='font-family:Arial,sans-serif;line-height:1.6;color:#333;'>"
                  + "<h3>Hi " + name + ",</h3>"
                  + "<p>This is a friendly reminder to add your income and expenses for today in <b>Money Manager</b>.</p>"

                  + "<div style='margin:20px 0;'>"
                  + "<a href='" + frontendUrl + "' "
                  + "style='display:inline-block;padding:12px 24px;background-color:#4CAF50;color:#ffffff;"
                  + "text-decoration:none;border-radius:6px;font-weight:bold;'>"
                  + "Go To Money Manager App"
                  + "</a>"
                  + "</div>"

                  + "<p>Best regards,<br><b>Money Manager Team</b></p>"
                  + "</div>";

          try {
              emailService.sendEmail(profile.getEmail(),
                      "Daily Reminder: Add Your Income and Expenses",
                      body);
          } catch (Exception e) {
              log.error("Failed to send email to: " + profile.getEmail(), e);
          }
      }

        log.info("Job completed: sendDailyIncomeExpenseReminder()");

    }

    //send daily expense summary
    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyExpenseSummary(){
        log.info("Job started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='width:100%;border-collapse:collapse;'>");
                table.append("<tr><th style='border:1px solid #ddd;padding:8px;text-align:left;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;text-align:left;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;text-align:left;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;text-align:left;'>Date</th>")
                        .append("</tr>");

                int i=1;
                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryId()!= null? expense.getCategoryName():"N/A") .append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getDate()).append("</td>")
                            .append("</tr>");

                }

                table.append("</table>");
                String body = "Hi " + profile.getFullName() + ",<br><br>"
                        + "Here is a summary of your expenses for today:<br><br>"
                        + table
                        + "<br><br>Best regards,<br>Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
            }
        }

        log.info("Job Completed: sendDailyExpenseSummary()");
    }





}
