package org.rb98dps.splitwise.test.integration.creation;

import org.junit.Before;
import org.junit.Test;
import org.rb98dps.splitwise.Splitwise;
import org.rb98dps.splitwise.dto.request.AddUserToGroupRequest;
import org.rb98dps.splitwise.dto.request.CreateGroupRequest;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.managers.GroupManager;
import org.rb98dps.splitwise.test.util.SplitwiseTestUtil;

import java.util.List;

import static org.junit.Assert.*;
import static org.rb98dps.splitwise.util.SplitWiseUtil.faker;


public class GroupCreationTest {

    Splitwise splitwise = new Splitwise();

    GroupManager groupManager = GroupManager.getInstance();

    @Before
    public void initTest() {
        splitwise.clear();
    }


    @Test(expected = NullPointerException.class)
    public void testNoUserGroupCreation() {
        SplitwiseTestUtil.createGroup(new CreateGroupRequest("Flat 1003", null, null));
    }

    @Test
    public void testGroupWithNoUserExceptLeaderCreation() {
        Group group = SplitwiseTestUtil.createRandomGroup(0);
        assertEquals(group, groupManager.getGroup(group.getGroupId()));
        assertEquals(groupManager.getGroups().size(), 1);
    }

    @Test
    public void testGroupWithMultipleNonDuplicateUsersCreation() {
        Group actualGroup = SplitwiseTestUtil.createRandomGroup(2);
        Group expectedGroup = groupManager.getGroup(actualGroup.getGroupId());
        assertEquals(actualGroup, expectedGroup);
        assertEquals(groupManager.getGroups().size(), 1);
        assertEquals(actualGroup.getUsers().size(), 2);
    }

    @Test
    public void testGroupWithMultipleDuplicateUsersCreation() {
        User user = SplitwiseTestUtil.createRandomUser();
        User user1 = SplitwiseTestUtil.createRandomUser();
        Group actualGroup = SplitwiseTestUtil.createRandomGroup(user.getUserId(), List.of(user1.getUserId(), user1.getUserId()));
        Group expectedGroup = groupManager.getGroup(actualGroup.getGroupId());
        assertEquals(actualGroup, expectedGroup);
        assertEquals(groupManager.getGroups().size(), 1);
        assertEquals(actualGroup.getUsers().size(), expectedGroup.getUsers().size());
        assertEquals(actualGroup.getUsers().size(), 2);
    }

    @Test
    public void testAddUserToGroupSuccessCase() {
        User user = SplitwiseTestUtil.createRandomUser();
        Group actualGroup = SplitwiseTestUtil.createRandomGroup(2);
        boolean expectedVal = splitwise.addUserToGroup(new AddUserToGroupRequest(
                actualGroup.getGroupId(), user.getUserId(),
                actualGroup.getLeaderId()));
        Group expectedGroup = groupManager.getGroup(actualGroup.getGroupId());
        assertTrue(expectedVal);
        assertEquals(expectedGroup.getUsers().size(), 3);
        assertTrue(expectedGroup.getUsers().contains(user));
    }

    @Test
    public void testAddUserToGroupFailureCase() {
        int userCount = 10;
        int leaderIndex = faker().random().nextInt(0, userCount - 1);
        int duplicateUserIndex = faker().random().nextInt(0, userCount - 1);
        List<User> users = SplitwiseTestUtil.createRandomUsers(userCount);
        Group actualGroup = SplitwiseTestUtil.createRandomGroup(users.get(leaderIndex).getUserId(),
                users.stream().map(User::getUserId).toList());
        boolean expectedVal = splitwise.addUserToGroup(new AddUserToGroupRequest(
                actualGroup.getGroupId(), users.get(duplicateUserIndex).getUserId(),
                actualGroup.getLeaderId()));
        Group expectedGroup = groupManager.getGroup(actualGroup.getGroupId());
        assertFalse(expectedVal);
        assertEquals(expectedGroup.getUsers().size(), userCount);
        assertTrue(expectedGroup.getUsers().contains(users.get(duplicateUserIndex)));
    }


}
