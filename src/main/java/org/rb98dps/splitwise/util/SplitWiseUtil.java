package org.rb98dps.splitwise.util;

import com.github.javafaker.Faker;
import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.entity.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * util class having helper methods used for splitwise implementations
 */
public class SplitWiseUtil {


    private static final Faker FAKER = new Faker(new Locale("en-IN"));

    private SplitWiseUtil() {

    }

    public static Faker faker() {
        return FAKER;
    }

    /**
     * transform the balances to edge list of users where edge[i][j] value signifies the amount to be paid by i to j
     *
     * @param balances list of balances
     * @return 2D edge list with length equal to balance array length
     */
    public static List<List<Double>> createEdgeList(List<Balance> balances) {


        int start = 0;
        int end = balances.size() - 1;
        List<List<Double>> edges = new ArrayList<>();

        for (int i = 0; i <= end; i++) {
            edges.add(new ArrayList<>());
            for (int j = 0; j <= end; j++) {
                edges.get(i).add(0d);
            }
        }


        balances.sort((a, b) -> (int) (a.getBal() - b.getBal()));
        while (start < end) {
            Balance balanceE = balances.get(end);
            Balance balanceS = balances.get(start);
            double x = balanceE.getBal() + balanceS.getBal();
            double y = Math.min(Math.abs(balanceE.getBal()), Math.abs(balanceS.getBal()));
            if (x > 0) {
                addEdge(balances, start, end, edges, balanceE, x, y);
                start++;
            } else if (x < 0) {
                addEdge(balances, start, end, edges, balanceS, x, y);
                end--;
            } else {
                addEdge(balances, start, end, edges, balanceS, x, y);
                start++;
                end--;
            }

        }
        return edges;
    }

    /**
     * @param balances    list of balances
     * @param start       signifies the nodeA for the edge
     * @param end         signifies the nodeB for the edge
     * @param edges       current edge list
     * @param currBalance the balance currently in consideration
     * @param x           amount to be updated in for currBalance
     * @param y           signifies the edge weight
     */
    private static void addEdge(List<Balance> balances, int start, int end, List<List<Double>> edges,
                                Balance currBalance, double x, double y) {
        edges.get(balances.get(end).getId()).set(balances.get(start).getId(), y);
        edges.get(balances.get(start).getId()).set(balances.get(end).getId(), -y);
        currBalance.setBal(x);
    }

    /**
     * helper method to print all the transactions
     *
     * @param transactions list of transactions that needs to be printed
     * @param userId       user to which the transactions belong
     */
    public static void printTransaction(List<Transaction> transactions, Integer userId) {
        System.out.println("Transactions for " + userId);
        transactions.forEach(System.out::println);
    }

    /**
     * helper method to convert map of values(split and balance) against the users to array of balances
     *
     * @param payers map of payers in the group
     * @return list of balances
     */
    public static List<Balance> helper(Map<Integer, Pair> payers) {

        List<Balance> list = new ArrayList<>();
        payers.forEach((k, v) -> list.add(new Balance(k, v.anInt, v.bnInt)));
        return list;
    }


    /**
     * This class is used to store 2 double values that are related in some manner
     */

    public static class Pair {

        double anInt;
        double bnInt;

        public Pair(double anInt, double bnInt) {
            this.anInt = anInt;
            this.bnInt = bnInt;
        }
    }

}
