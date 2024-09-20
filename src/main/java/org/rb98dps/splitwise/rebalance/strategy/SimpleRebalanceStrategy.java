package org.rb98dps.splitwise.rebalance.strategy;

import java.util.List;

/**
 * This class extends Re-balance strategy interface and implements simple strategy
 */
public class SimpleRebalanceStrategy implements RebalanceStrategy {

    /**
     * This method returns the edge list as it is without any re-balance
     *
     * @param balances edge list belonging to any expense or group
     * @return edge list having all the balances that needs to be paid by the users
     */
    @Override
    public List<List<Double>> balance(List<List<Double>> balances) {
        return balances;
    }
}
