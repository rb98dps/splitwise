package org.rb98dps.splitwise.managers;

import org.rb98dps.splitwise.dto.request.CreateExpenseRequest;
import org.rb98dps.splitwise.dto.request.CreateGroupRequest;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Expense;
import org.rb98dps.splitwise.entity.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.rb98dps.splitwise.util.SplitWiseUtil.faker;

/**
 * Singleton class for managing expenses
 */
public class ExpenseManager {
    private static final ExpenseManager expenseManager = new ExpenseManager();
    private final Map<Integer, List<Expense>> expenseMap = new HashMap<>();

    private final GroupManager groupManager = GroupManager.getInstance();

    private ExpenseManager() {

    }

    public static ExpenseManager getInstance() {
        return expenseManager;
    }

    /**
     * this is the
     *
     * @param expense add object to the map
     */
    public void addExpense(Expense expense) {
        List<Expense> expenses = getExpense(expense.getExpenseCreatorId());
        expenses.add(expense);
        expenseMap.put(expense.getExpenseCreatorId(), expenses);
    }

    /**
     * provides all the expenses created by the user
     *
     * @param userId id of the user
     * @return list of Expenses retrieved from the map
     */
    public List<Expense> getExpense(int userId) {
        List<Expense> expenses = new ArrayList<>();
        if (expenseMap.containsKey(userId)) {
            expenses = expenseMap.get(userId);
        }
        return expenses;
    }

    public void clear() {
        expenseMap.clear();
    }

    /**
     * create the expense from DTO provided, also creates if the group is not specified for the expense
     *
     * @param expenseRequest DTO for expense creation
     * @return Expense object
     */
    public Expense createExpense(CreateExpenseRequest expenseRequest) {
        Group group;
        Expense expense = new Expense(expenseRequest.getGroupId(), expenseRequest.getName(), expenseRequest.getPayers()
                , expenseRequest.getTotExpense(), expenseRequest.getExpenseCreatorId());

        if (expenseRequest.getGroupId() == null) {
            CreateGroupRequest groupRequest =
                    new CreateGroupRequest(faker().name().username(), expense.getExpenseCreatorId(),
                            new ArrayList<>(expense.getPayers().stream().map(Balance::getId).distinct().collect(Collectors.toList())));
            group = groupManager.createGroup(groupRequest);
            expense.setGroupId(group.getGroupId());
        }
        addExpense(expense);
        return expense;
    }
}
