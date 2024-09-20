package org.rb98dps.splitwise.test.integration.creation;

import org.junit.Before;
import org.junit.Test;
import org.rb98dps.splitwise.Splitwise;
import org.rb98dps.splitwise.dto.request.CreateUserRequest;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.managers.UserManager;
import org.rb98dps.splitwise.test.util.SplitwiseTestUtil;

import static org.junit.Assert.assertEquals;

public class UserCreationTest {

    Splitwise splitwise = new Splitwise();
    UserManager userManager = UserManager.getInstance();

    @Before
    public void initTest() {
        splitwise.clear();
    }

    @Test
    public void testUserCreation() {
        User user = splitwise.createUser(new CreateUserRequest("name","password","+919741317783"));
        assertEquals(userManager.getUser(user.getUserId()), user);
        assertEquals(userManager.getUsers().size(), 1);
    }
}
