package org.rb98dps.splitwise.rebalance.strategy;

import org.rb98dps.splitwise.entity.Balance;
import org.rb98dps.splitwise.util.SplitWiseUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * This class extends Re-balance strategy interface and implements simplified strategy to reduce number of
 * transactions
 */
public class StraightUpRebalanceStrategy implements RebalanceStrategy {

    /**
     * This method is used for creation of edge list that with
     *
     * @param balances edge list belonging to any expense or group
     * @return simplified edge list having all the balances that needs to be paid by the users
     */
    @Override
    public List<List<Double>> balance(List<List<Double>> balances) {

        List<Balance> prunedBalances = new ArrayList<>();
        for (int i = 0; i < balances.size(); i++) {
            prunedBalances.add(new Balance(i, balances.get(i).stream().reduce(0d, Double::sum)));
        }

        return SplitWiseUtil.createEdgeList(prunedBalances);
    }
}
