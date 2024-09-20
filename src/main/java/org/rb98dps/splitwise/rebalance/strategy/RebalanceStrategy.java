package org.rb98dps.splitwise.rebalance.strategy;

import java.util.List;

/**
 * interface that exposes balance method to be used according to specific strategy for re-balancing
 */
public interface RebalanceStrategy {
    /**
     * transform the edge list provided according to the strategy used for re-balance
     *
     * @param balances edge list belonging to any expense or group
     * @return edge list belonging to any expense or group
     */
    List<List<Double>> balance(List<List<Double>> balances);
}
