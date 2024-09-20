package org.rb98dps.splitwise.entity;

import lombok.Data;

@Data
public class Balance {
    private int id;
    private double bal;

    private double split;

    public Balance(int id, double bal, double split) {
        this.id = id;
        this.bal = bal;
        this.split = split;
    }

    public Balance(int id, double bal) {
        this.id = id;
        this.bal = bal;
    }

}
