package org.rb98dps.splitwise.test.util;

import com.github.javafaker.Faker;
import org.rb98dps.splitwise.dto.request.CreateExpenseRequest;
import org.rb98dps.splitwise.dto.request.CreateGroupRequest;
import org.rb98dps.splitwise.dto.request.CreateUserRequest;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.managers.GroupManager;
import org.rb98dps.splitwise.managers.UserManager;
import org.rb98dps.splitwise.util.SplitWiseUtil;

import java.util.*;
import java.util.stream.IntStream;

public class SplitwiseTestUtil {

    public static final Faker FAKER = Faker.instance(new Locale("en-IN"));
    private static final GroupManager groupManager = GroupManager.getInstance();

    private static final UserManager userManager = UserManager.getInstance();

    private SplitwiseTestUtil() {

    }

    public static Group createRandomGroup() {
        int userSize = FAKER.random().nextInt(0, 100);
        return createRandomGroup(userSize);
    }

    public static Group createRandomGroup(Integer leaderId, List<Integer> userIds) {
        return createGroup(new CreateGroupRequest(FAKER.name().username(), leaderId, userIds));
    }

    public static Group createRandomGroup(int userSize) {
        User leader = createRandomUser();
        List<Integer> userIds = new ArrayList<>();
        IntStream.range(1, userSize).forEach(data -> {
            User user = createRandomUser();
            userIds.add(user.getUserId());
        });
        return createGroup(new CreateGroupRequest(FAKER.name().username(), leader.getUserId(), userIds));
    }

    public static Group createGroup(CreateGroupRequest groupRequest) {
        return groupManager.createGroup(groupRequest);
    }

    public static User createRandomUser() {
        return userManager.addUser(new CreateUserRequest(FAKER.name().fullName(), FAKER.internet().password(),
                FAKER.phoneNumber().phoneNumber()));
    }

    public static List<User> createRandomUsers(int usersCount) {
        if (usersCount <= 0)
            throw new IllegalArgumentException("users can not be less than 0");
        List<User> list = new ArrayList<>();
        for (int i = 0; i < usersCount; i++) {
            list.add(createRandomUser());
        }
        return list;
    }

    public static CreateExpenseRequest createRandomExpenseWithGroup(int groupId) {
        Group group = groupManager.getGroup(groupId);
        return createExpense(group.getUsers(), groupId, FAKER.name().username());
    }

    public static CreateExpenseRequest createRandomExpenseWithGroup() {
        Group group = createRandomGroup();
        return createExpense(group.getUsers(), group.getGroupId(), FAKER.name().username());
    }

    public static boolean testBalanceCreationUtil(double totExpense, List<Balance> payers) {
        double expense = payers.stream().map(Balance::getBal).reduce(0d, Double::sum);
        double expense1 = payers.stream().map(Balance::getSplit).reduce(0d, Double::sum);
        return expense1 == totExpense && expense == totExpense;
    }

    public static CreateExpenseRequest createRandomExpenseWithoutGroup() {
        int usersCount = FAKER.random().nextInt(1, 100);
        List<User> users = createRandomUsers(usersCount);
        return createExpense(users, null, FAKER.name().username());
    }

    public static List<CreateExpenseRequest> createRandomExpensesWithGroup() {
        CreateExpenseRequest createExpenseRequest = createRandomExpenseWithGroup();
        List<CreateExpenseRequest> createExpenseRequestList = new ArrayList<>();
        createExpenseRequestList.add(createExpenseRequest);
        int times = FAKER.random().nextInt(100, 1000);
        for (int i = 0; i < times; i++) {
            createExpenseRequest = createRandomExpenseWithGroup(createExpenseRequest.getGroupId());
            createExpenseRequestList.add(createExpenseRequest);
        }
        return createExpenseRequestList;
    }

    public static CreateExpenseRequest createExpense(List<User> users, Integer groupId, String name) {
        int usersCount = users.size();
        double expense = FAKER.random().nextInt(1000) * 1.0;
        double expense2 = expense;
        double expense1 = expense;
        Map<Integer, SplitWiseUtil.Pair> payers = new HashMap<>();
        for (int i = 0; i < usersCount - 1; i++) {
            double contribution = FAKER.random().nextInt(0, (int) expense) * 1.0;
            expense -= contribution;
            double ex = FAKER.random().nextInt(0, (int) expense1) * 1.0;
            expense1 -= ex;
            payers.put(users.get(i).getUserId(), new SplitWiseUtil.Pair(contribution, ex));
        }
        payers.put(users.get(usersCount - 1).getUserId(), new SplitWiseUtil.Pair(expense, expense1));

        return new CreateExpenseRequest(groupId, name, SplitWiseUtil.helper(payers), expense2,
                users.get(0).getUserId());
    }


}
