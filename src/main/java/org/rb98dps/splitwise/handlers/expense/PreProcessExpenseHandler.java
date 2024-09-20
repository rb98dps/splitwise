package org.rb98dps.splitwise.handlers.expense;

import org.rb98dps.splitwise.dto.ExpenseStage;
import org.rb98dps.splitwise.dto.request.ExpenseRequest;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Expense;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.managers.ExpenseManager;

import java.util.ArrayList;
import java.util.List;
/**
 * Pre process expense handler which only executes if the expense stage is PRE
 */
public class PreProcessExpenseHandler extends ExpenseHandler {

    ExpenseManager expenseManager = ExpenseManager.getInstance();

    public PreProcessExpenseHandler(ExpenseHandler expenseHandler) {
        super(expenseHandler);
    }

    /**
     * this method is responsible for creating balance list from the split and expense list and create expense entity
     * from the request
     *
     * @param request expense request
     */
    @Override
    public void handle(ExpenseRequest request) {

        if (request.getStage() == ExpenseStage.PRE) {
            Expense expense = expenseManager.createExpense(request.getExpenseRequest());
            request.setExpense(expense);
            Group group = groupManager.getGroup(expense.getGroupId());
            final List<Balance> balances = new ArrayList<>();
            for (int i = 0; i < expense.getPayers().size(); i++) {
                double value = expense.getPayers().get(i).getSplit() - expense.getPayers().get(i).getBal();
                balances.add(new Balance(group.getUserId(expense.getPayers().get(i).getId()), value));
            }
            request.setBalance(balances);
            request.setStage(ExpenseStage.POST);
            super.handle(request);
        }
    }
}
