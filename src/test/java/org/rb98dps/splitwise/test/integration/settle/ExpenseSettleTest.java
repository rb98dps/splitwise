package org.rb98dps.splitwise.test.integration.settle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rb98dps.splitwise.Splitwise;
import org.rb98dps.splitwise.dto.request.CreateExpenseRequest;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.Transaction;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.managers.GroupManager;

import java.util.List;

import static org.rb98dps.splitwise.test.util.SplitwiseTestUtil.createRandomExpensesWithGroup;

public class ExpenseSettleTest {

    Splitwise splitwise = new Splitwise();
    GroupManager groupManager = GroupManager.getInstance();

    @Before
    public void initTest() {
        splitwise.clear();
    }

    @Test
    public void testFullValueSettleForAllUsersWithGroup() {
        processSettleForAllUsers(false, true);
    }

    @Test
    public void testInCompleteValueSettleForAllUsersWithGroup() {
        processSettleForAllUsers(false, false);
    }

    @Test
    public void testInCompleteValueSettleForAllUsersWithGroupStraightUp() {
        processSettleForAllUsers(true, false);
    }

    @Test
    public void testFullValueSettleForAllUsersWithGroupStraightUp() {
        processSettleForAllUsers(true, true);
    }

    private void processSettleForAllUsers(boolean isStraightUp, boolean isFullSettle) {
        List<CreateExpenseRequest> createExpenseRequests = createRandomExpensesWithGroup();
        CreateExpenseRequest createExpenseRequest = createExpenseRequests.get(0);
        Group group = groupManager.getGroup(createExpenseRequest.getGroupId());

        if (isStraightUp) {
            splitwise.changeStrategy(createExpenseRequest.getGroupId(), true);
        }

        for (User user : group.getUsers()) {
            List<Transaction> transactions = splitwise.getTransactions(user.getUserId(), group.getGroupId());

            for (Transaction transaction : transactions) {
                if (transaction.getSenderId() != transaction.getReceiverId()) {
                    handleTransaction(transaction, group, isFullSettle);
                    List<Transaction> afterTransactions = splitwise.getTransactions(user.getUserId(), group.getGroupId());
                    checkTransactionOutcome(afterTransactions, transaction, isFullSettle);
                    checkNoEffectOnOtherTransactions(transaction, afterTransactions, transactions);
                }
            }

            if (isFullSettle) {
                checkAllBalanceSettledForUser(user, group);
            }
        }
    }

    private void checkTransactionOutcome(List<Transaction> afterTransactions, Transaction transaction, boolean isFullSettle) {
        if (isFullSettle) {
            checkTransactionSettledForUser(afterTransactions, transaction, 0d);
        } else {
            checkTransactionNotSettledForUser(afterTransactions, transaction);
        }
    }

    private void handleTransaction(Transaction transaction, Group group, boolean isFullSettle) {
        if (!isFullSettle) {
            transaction.setBalance(transaction.getBalance() - 1);
        }
        splitwise.settle(transaction, group.getGroupId());
    }


    private void checkTransactionSettledForUser(List<Transaction> afterTransactions, Transaction transaction, double actual) {
        afterTransactions.stream()
                .filter(transaction1 -> checkCondition(transaction, transaction1))
                .forEach(transaction1 -> Assert.assertEquals(transaction1.getBalance(), actual, 0.001));
    }

    private void checkTransactionNotSettledForUser(List<Transaction> afterTransactions, Transaction transaction) {
        afterTransactions.stream()
                .filter(transaction1 -> checkCondition(transaction, transaction1))
                .forEach(transaction1 -> Assert.assertNotEquals(transaction1.getBalance(), 0.0, 0.001));
    }

    public boolean checkCondition(Transaction transaction, Transaction transaction1) {
        return ((transaction1.getReceiverId() == transaction.getReceiverId() &&
                         transaction1.getSenderId() == transaction.getSenderId())
                        && (transaction1.getReceiverId() == transaction.getSenderId()
                                    && transaction1.getSenderId() == transaction.getReceiverId()));
    }

    private void checkNoEffectOnOtherTransactions(Transaction transaction,
                                                  List<Transaction> afterTransactions,
                                                  List<Transaction> transactions) {
        afterTransactions.stream()
                .filter(transaction1 -> !checkCondition(transaction, transaction1))
                .forEach(t -> checkTransactionSettledForUser(transactions, t, t.getBalance()));
    }


    private void checkAllBalanceSettledForUser(User user, Group group) {
        List<Transaction> afterTransactions = splitwise.getTransactions(user.getUserId(), group.getGroupId());
        afterTransactions.forEach(transaction1 -> Assert.assertEquals(transaction1.getBalance(), 0d, 0.001));
    }

}
