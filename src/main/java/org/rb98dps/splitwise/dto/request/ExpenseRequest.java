package org.rb98dps.splitwise.dto.request;

import lombok.Data;
import org.rb98dps.splitwise.dto.ExpenseStage;
import org.rb98dps.splitwise.dto.response.ExpenseResponse;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Expense;

import java.util.List;

/**
 * DTO for expense handler
 */
@Data
public class ExpenseRequest {

    private CreateExpenseRequest expenseRequest;
    private Expense expense;
    private ExpenseStage stage;
    private List<Balance> balance;
    private ExpenseResponse expenseResponse;
}
