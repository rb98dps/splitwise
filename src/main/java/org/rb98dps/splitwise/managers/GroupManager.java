package org.rb98dps.splitwise.managers;

import lombok.Getter;
import org.rb98dps.splitwise.dto.request.AddUserToGroupRequest;
import org.rb98dps.splitwise.dto.request.CreateGroupRequest;
import org.rb98dps.splitwise.entity.Group;
import org.rb98dps.splitwise.entity.Transaction;
import org.rb98dps.splitwise.entity.User;
import org.rb98dps.splitwise.rebalance.strategy.SimpleRebalanceStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Singleton class for managing groups
 */
@Getter
public class GroupManager {

    private static final GroupManager groupManager = new GroupManager();
    private final Map<Integer, Group> groups = new HashMap<>();
    private final UserManager userManager = UserManager.getInstance();

    private GroupManager() {

    }

    public static GroupManager getInstance() {
        return groupManager;
    }

    /**
     * @param addUserToGroupRequest DTO to add a user to the group
     * @return true if the user was successfully added, else false
     */
    public boolean addGroupUser(AddUserToGroupRequest addUserToGroupRequest) {
        Group group = groups.get(addUserToGroupRequest.getGroupId());
        User user = userManager.getUser(addUserToGroupRequest.getUserId());
        int leaderId = addUserToGroupRequest.getLeaderId();
        if (leaderId == group.getLeaderId() && !group.getUserMaps().containsKey(user.getUserId())) {
            group.addUser(user);
            return true;
        }
        return false;
    }

    /**
     * @param group group to be added to the map
     */
    public void addGroup(Group group) {
        groups.put(group.getGroupId(), group);
    }

    /**
     * @param groupRequest DTO for addition of the group
     * @return group object that is added to the map
     */
    public Group createGroup(CreateGroupRequest groupRequest) {
        int leaderId = groupRequest.getLeaderId();
        List<Integer> userIdList = groupRequest.getUsers();
        String name = groupRequest.getName();
        Group group = new Group(name, userManager.getUser(leaderId), new SimpleRebalanceStrategy());
        for (Integer id : userIdList) {
            User user = userManager.getUser(id);
            if (user.getUserId() != group.getLeaderId() && !group.getUserMaps().containsKey(user.getUserId())) {
                group.addUser(user);
            }
        }
        addGroup(group);
        return group;
    }

    /**
     * @param id used to retrieve group from the map
     * @return Group Instance
     */
    public Group getGroup(int id) {
        return groups.get(id);
    }

    /**
     * @param balances edge list created for balances provided in the expense
     * @param total total cost of expense
     * @param groupId expense group Id
     */
    public void updateGroupExpense(List<List<Double>> balances, Double total, Integer groupId) {
        getGroup(groupId).addBalances(balances, total);
    }

    /**
     * @param transaction transaction details that is to be settled
     * @param groupId     Id of the group for which the transaction is executed
     */
    public void settle(Transaction transaction, Integer groupId) {
        getGroup(groupId).settle(transaction.getSenderId(), transaction.getReceiverId(), transaction.getBalance());
    }

    public void clear() {
        groups.clear();
    }
}
