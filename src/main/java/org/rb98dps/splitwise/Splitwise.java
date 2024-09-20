package org.rb98dps.splitwise;

import org.rb98dps.splitwise.dto.ExpenseStage;
import org.rb98dps.splitwise.dto.request.*;
import org.rb98dps.splitwise.dto.response.ExpenseResponse;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.Transaction;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.handlers.expense.ExpenseHandler;
import org.rb98dps.splitwise.handlers.expense.PostProcessExpenseHandler;
import org.rb98dps.splitwise.handlers.expense.PreProcessExpenseHandler;
import org.rb98dps.splitwise.managers.ExpenseManager;
import org.rb98dps.splitwise.managers.GroupManager;
import org.rb98dps.splitwise.managers.UserManager;
import org.rb98dps.splitwise.rebalance.strategy.SimpleRebalanceStrategy;
import org.rb98dps.splitwise.rebalance.strategy.StraightUpRebalanceStrategy;

import java.util.List;

/**
 * Splitwise is an application that can be used to distribute/split an expense cost to
 * multiple users(specified in a group).
 *
 * @author Rahul Bhatnagar
 * @version 1.0
 * @since 2024-09-11
 */
public class Splitwise {


    ExpenseHandler postHandler = new PostProcessExpenseHandler(null);
    ExpenseHandler expenseHandler = new PreProcessExpenseHandler(postHandler);

    GroupManager groupManager = GroupManager.getInstance();

    UserManager userManager = UserManager.getInstance();
    ExpenseManager expenseManager = ExpenseManager.getInstance();

    /**
     * @param expenseRequest request to be sent by client for expense processing
     * @return ExpenseResponse object containing unique Id for the expense and groupId to which the expense belongs
     */
    public ExpenseResponse addExpense(CreateExpenseRequest expenseRequest) {
        ExpenseRequest request = new ExpenseRequest();
        request.setExpenseRequest(expenseRequest);
        request.setStage(ExpenseStage.PRE);
        expenseHandler.handle(request);
        ExpenseResponse expenseResponse;
        if (request.getStage() == ExpenseStage.COMPLETE) {
            expenseResponse = request.getExpenseResponse();
        } else {
            expenseResponse = new ExpenseResponse(true);
        }
        return expenseResponse;
    }

    /**
     * @param transaction class that specify the amount that transferred from sender to receiver
     * @param groupId     specifies the group in which the transaction is executed.
     */
    public void settle(Transaction transaction, Integer groupId) {
        groupManager.settle(transaction, groupId);
    }

    /**
     * @param userId  specifies the user Id for which transactions are required
     * @param groupId specifies the group Id for which the transactions are expected
     * @return list of Transactions, executing these transactions will settle the
     * *               balance between the user and other members of the group
     */
    public List<Transaction> getTransactions(Integer userId, Integer groupId) {
        return groupManager.getGroup(groupId).getFinalBalancesForUser(userId);
    }

    /**
     * @param createUserRequest DTO for User creation
     * @return User object
     */
    public User createUser(CreateUserRequest createUserRequest) {
        return userManager.addUser(createUserRequest);
    }

    /**
     * @param addUserToGroupRequest DTO for addition of user to a group
     * @return boolean, signifying whether user was successfully added to the group or not
     */
    public boolean addUserToGroup(AddUserToGroupRequest addUserToGroupRequest) {
        return groupManager.addGroupUser(addUserToGroupRequest);
    }

    /**
     * @param createExpenseRequestList list of CreateExpenseRequest for creation of multiple expenses
     * @return list of ExpenseResponse specifying the response for each expense.
     */
    public List<ExpenseResponse> addExpenses(List<CreateExpenseRequest> createExpenseRequestList) {
        return createExpenseRequestList.stream().map(this::addExpense).toList();
    }

    /**
     * @param groupRequest DTO for creation of a group
     * @return Group object
     */
    public Group createGroup(CreateGroupRequest groupRequest) {
        return groupManager.createGroup(groupRequest);
    }

    /**
     * @param groupId groupId for which the re-balance strategy is being set
     * @param val     true for straight-up Strategy, false for simple Strategy
     */
    public void changeStrategy(int groupId, boolean val) {
        if (val) {
            groupManager.getGroup(groupId).setRebalanceStrategy(new StraightUpRebalanceStrategy());
        } else {
            groupManager.getGroup(groupId).setRebalanceStrategy(new SimpleRebalanceStrategy());
        }

    }

    /**
     * used to clear all the data in the application
     */
    public void clear() {
        groupManager.clear();
        userManager.clear();
        expenseManager.clear();
    }
}
