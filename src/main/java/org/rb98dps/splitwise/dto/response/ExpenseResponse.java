package org.rb98dps.splitwise.dto.response;

import java.util.UUID;

/**
 *
 * record DTO for expense response
 */
public record ExpenseResponse(boolean processed, Integer groupId, UUID expenseId) {

    public ExpenseResponse(boolean processed) {
        this(processed, null, null);
    }
}
