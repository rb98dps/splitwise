package org.rb98dps.splitwise.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.rb98dps.splitwise.entity.Balance;

import java.util.List;

/**
 * DTO for expense creation
 */
@Data
@Builder
@AllArgsConstructor
public class CreateExpenseRequest {

    private Integer groupId;
    private String name;
    private List<Balance> payers;
    private double totExpense;
    private Integer expenseCreatorId;

}
