package org.rb98dps.splitwise.managers;

import lombok.Getter;
import org.rb98dps.splitwise.dto.request.CreateUserRequest;
import org.rb98dps.splitwise.entity.User;

import java.util.HashMap;
import java.util.Map;
/**
 * Singleton class for managing users
 */
@Getter
public class UserManager {
    private static final UserManager userManager = new UserManager();
    private final Map<Integer, User> users = new HashMap<>();

    private UserManager() {

    }

    public static UserManager getInstance() {
        return userManager;
    }

    /**
     * @param createUserRequest DTO for addition of user
     * @return user object
     */
    public User addUser(CreateUserRequest createUserRequest) {
        User user = new User(createUserRequest.getName(), createUserRequest.getPassword(), createUserRequest.getPhoneNo());
        users.put(user.getUserId(), user);
        return user;
    }

    /**
     * @param userId used to retrieve a user
     * @return User stored in map
     */
    public User getUser(int userId) {
        return users.get(userId);
    }

    public void clear() {
        users.clear();
    }
}
