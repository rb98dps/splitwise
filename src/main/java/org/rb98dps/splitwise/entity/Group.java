package org.rb98dps.splitwise.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rb98dps.splitwise.rebalance.strategy.RebalanceStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@ToString
public class Group {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final Map<Integer, Integer> userReverseMaps = new HashMap<>();
    private Map<Integer, Integer> userMaps = new HashMap<>();
    private String name;
    private int groupId;
    private List<List<Double>> balances = new ArrayList<>();
    private int leaderId;
    private List<User> users = new ArrayList<>();
    private double totExpense;
    private RebalanceStrategy rebalanceStrategy;

    public Group(String name, User user, RebalanceStrategy rebalanceStrategy) {
        this.name = name;
        this.leaderId = user.getUserId();
        this.groupId = atomicInteger.getAndIncrement();
        this.rebalanceStrategy = rebalanceStrategy;
        addUser(user);

    }

    /**
     * increases the size of 2d edge list by 1
     */
    private void incrementBalanceArray() {

        List<Double> list = new ArrayList<>();
        for (int i = 0; i < balances.size(); i++) {
            list.add(0d);
        }
        balances.add(list);

        for (List<Double> balance : balances) {
            balance.add(0d);
        }
    }

    /**
     * Adds user to the group
     *
     * @param user user instance to added to the group
     */
    public void addUser(User user) {

        users.add(user);
        userMaps.put(user.getUserId(), users.size() - 1);
        userReverseMaps.put(users.size() - 1, user.getUserId());
        incrementBalanceArray();
    }

    /**
     * This method is used to create list of transactions for all the users from the edge list using the re-balance
     * strategy
     *
     * @return map of userId and list of transactions for that user
     */
    public HashMap<Integer, List<Transaction>> getFinalBalances() {
        List<List<Double>> finalBalances = rebalanceStrategy.balance(balances);
        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        userMaps.forEach((k, v) -> {
            List<Transaction> list = new ArrayList<>();
            for (int i = 0; i < finalBalances.get(v).size(); i++) {
                Transaction transaction = new Transaction(k, finalBalances.get(v).get(i), userReverseMaps.get(i));
                list.add(transaction);
            }
            map.put(k, list);
        });

        return map;

    }

    public List<Transaction> getFinalBalancesForUser(int userId) {
        return getFinalBalances().get(userId);
    }

    public Integer getUserId(Integer id) {
        return userMaps.get(id);
    }

    /**
     * This method adds the new edge list values to the existing edge list
     *
     * @param newBalances balances received after processing of the expense request
     * @param tot         total value of the expense
     */
    public void addBalances(List<List<Double>> newBalances, double tot) {
        int n = userMaps.size();
        for (int i = 0; i < n; i++) {
            for (int i1 = 0; i1 < n; i1++) {
                double balance = balances.get(i).get(i1) + newBalances.get(i).get(i1);
                balances.get(i).set(i1, balance);
            }
        }
        totExpense += tot;
    }

    /**
     * This method is used to settle transaction done by a user
     *
     * @param sender   sender of the transaction
     * @param receiver receiver of the transaction
     * @param val      value of the transaction
     */
    public void settle(Integer sender, Integer receiver, Double val) {
        sender = userMaps.get(sender);
        receiver = userMaps.get(receiver);
        Double newValue = balances.get(sender).get(receiver) + val;
        balances.get(sender).set(receiver, newValue);
        balances.get(receiver).set(sender, -newValue);
    }
}
