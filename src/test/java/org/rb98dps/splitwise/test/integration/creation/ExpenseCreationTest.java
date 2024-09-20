package org.rb98dps.splitwise.test.integration.creation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rb98dps.splitwise.Splitwise;
import org.rb98dps.splitwise.dto.request.CreateExpenseRequest;
import org.rb98dps.splitwise.dto.response.ExpenseResponse;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.managers.ExpenseManager;

import static org.rb98dps.splitwise.test.util.SplitwiseTestUtil.*;

public class ExpenseCreationTest {
    Splitwise splitwise = new Splitwise();
    ExpenseManager expenseManager = ExpenseManager.getInstance();

    @Before
    public void initTest() {
        splitwise.clear();
    }

    @Test
    public void testSimpleExpenseCreationWithoutGroup() {
        CreateExpenseRequest createExpenseRequest = createRandomExpenseWithoutGroup();
        Assert.assertTrue(testBalanceCreationUtil(createExpenseRequest.getTotExpense(),
                createExpenseRequest.getPayers()));
        ExpenseResponse expenseResponse = splitwise.addExpense(createExpenseRequest);
        Assert.assertTrue(expenseResponse.processed());
        Assert.assertNotNull(expenseResponse.groupId());
        Assert.assertEquals(expenseResponse.expenseId(),
                expenseManager.getExpense(createExpenseRequest.getExpenseCreatorId()).get(0).getExpenseId());
    }

    @Test
    public void testSimpleExpenseCreationWithGroup() {
        Group group = createRandomGroup();
        CreateExpenseRequest createExpenseRequest = createRandomExpenseWithGroup(group.getGroupId());
        Assert.assertTrue(testBalanceCreationUtil(createExpenseRequest.getTotExpense(), createExpenseRequest.getPayers()));
        ExpenseResponse expenseResponse = splitwise.addExpense(createExpenseRequest);
        Assert.assertTrue(expenseResponse.processed());
        Assert.assertEquals(group.getGroupId(), expenseResponse.groupId().intValue());
    }

}
