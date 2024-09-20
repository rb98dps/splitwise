package org.rb98dps.splitwise.handlers.expense;

import org.rb98dps.splitwise.dto.ExpenseStage;
import org.rb98dps.splitwise.dto.request.ExpenseRequest;
import org.rb98dps.splitwise.dto.response.ExpenseResponse;
import org.rb98dps.splitwise.entity.Expense;
import org.rb98dps.splitwise.util.SplitWiseUtil;

import java.util.List;

/**
 * Post process expense handler which only executes if the expense stage is POST
 */
public class PostProcessExpenseHandler extends ExpenseHandler {
    public PostProcessExpenseHandler(ExpenseHandler expenseHandler) {
        super(expenseHandler);
    }

    /**
     * this method is responsible for creating edge list for the expense and updating the group for the expense
     *
     * @param request expense request
     */
    @Override
    public void handle(ExpenseRequest request) {
        if (request.getStage() == ExpenseStage.POST) {
            Expense expense = request.getExpense();
            List<List<Double>> newBalances = SplitWiseUtil.createEdgeList(request.getBalance());
            groupManager.updateGroupExpense(newBalances, expense.getTotExpense(), expense.getGroupId());
            request.setStage(ExpenseStage.COMPLETE);
            ExpenseResponse expenseResponse = new ExpenseResponse(true, expense.getGroupId(), expense.getExpenseId());
            request.setExpenseResponse(expenseResponse);
        }
        super.handle(request);
    }
}
