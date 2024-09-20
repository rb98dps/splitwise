package org.rb98dps.splitwise.test.integration.settle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rb98dps.splitwise.Splitwise;
import org.rb98dps.splitwise.dto.request.CreateExpenseRequest;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.Transaction;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.managers.GroupManager;

import java.util.List;

import static org.rb98dps.splitwise.test.util.SplitwiseTestUtil.createRandomExpenseWithGroup;
import static org.rb98dps.splitwise.test.util.SplitwiseTestUtil.createRandomExpensesWithGroup;

public class TransactionTest {

    Splitwise splitwise = new Splitwise();
    GroupManager groupManager = GroupManager.getInstance();

    @Before
    public void initTest() {
        splitwise.clear();
    }

    @Test
    public void testTransactionsWithSimpleStrategy() {
        double totExpense = getTotExpense(false);
        Assert.assertEquals(totExpense, 0d, 0.001);
    }


    @Test
    public void testTransactionsWithMultipleExpensesWithSimpleStrategy() {

        double totExpense = totExpense(false);
        Assert.assertEquals(totExpense, 0d, 0.001);
    }

    @Test
    public void testTransactionsWithStraightUpStrategy() {

        double totExpense = getTotExpense(true);
        Assert.assertEquals(totExpense, 0d, 0.001);

    }

    @Test
    public void testTransactionsWithMultipleExpensesWithStraightUpStrategy() {
        double totExpense = totExpense(true);
        Assert.assertEquals(totExpense, 0d, 0.001);
    }

    private double totExpense(boolean strategyStraight) {

        List<CreateExpenseRequest> createExpenseRequestList = createRandomExpensesWithGroup();
        CreateExpenseRequest createExpenseRequest = createExpenseRequestList.get(0);
        splitwise.addExpenses(createExpenseRequestList);
        if (strategyStraight) {
            splitwise.changeStrategy(createExpenseRequest.getGroupId(), true);
        }
        Group group = groupManager.getGroup(createExpenseRequest.getGroupId());
        double totExpense = 0d;
        for (User user : group.getUsers()) {
            List<Transaction> transactions = splitwise.getTransactions(user.getUserId(), group.getGroupId());
            double singleTotExpense = transactionHelper(transactions);
            totExpense += singleTotExpense;
            Balance balance = balanceHelper(createExpenseRequestList, user);
            Assert.assertEquals(singleTotExpense, balance.getSplit() - balance.getBal(), 0.001);
        }
        return totExpense;
    }

    private double getTotExpense(boolean strategyStraight) {
        CreateExpenseRequest createExpenseRequest = createRandomExpenseWithGroup();
        splitwise.addExpense(createExpenseRequest);
        if (strategyStraight) {
            splitwise.changeStrategy(createExpenseRequest.getGroupId(), true);
        }
        Group group = groupManager.getGroup(createExpenseRequest.getGroupId());
        double totExpense = 0d;
        for (User user : group.getUsers()) {
            List<Transaction> transactions = splitwise.getTransactions(user.getUserId(), group.getGroupId());
            double singleTotExpense = transactionHelper(transactions);
            totExpense += singleTotExpense;
            Balance balance = balanceHelper(createExpenseRequest, user);
            Assert.assertEquals(singleTotExpense, balance.getSplit() - balance.getBal(), 0.001);
        }
        return totExpense;
    }

    public double transactionHelper(List<Transaction> transactions) {
        return transactions.stream().map(Transaction::getBalance).reduce(0d, (Double::sum));
    }

    public Balance balanceHelper(CreateExpenseRequest createExpenseRequest, User user) {
        return createExpenseRequest.getPayers().stream().filter(balance1 -> balance1.getId() == user.getUserId()).findFirst().get();
    }

    public Balance balanceHelper(List<CreateExpenseRequest> createExpenseRequests, User user) {
        return createExpenseRequests.stream().flatMap(var -> var.getPayers().stream())
                       .filter(balance1 -> balance1.getId() == user.getUserId())
                       .reduce((balance1, balance2) -> new Balance(balance2.getId(),
                               balance2.getBal() + balance1.getBal(), balance2.getSplit() + balance1.getSplit()))
                       .get();
    }

}
