package org.rb98dps.splitwise.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
@Setter
@Getter
public class Expense {

    private UUID expenseId;
    private Integer groupId;
    private String name;
    private List<Balance> payers;
    private double totExpense;

    @Setter
    @Getter
    private Integer expenseCreatorId;

    public Expense(Integer groupId, String name, List<Balance> payers,
                   double totExpense, Integer userId) {
        this.expenseId = UUID.randomUUID();
        this.groupId = groupId;
        this.name = name;
        this.payers = payers;
        this.totExpense = totExpense;
        this.expenseCreatorId = userId;
    }


    public Expense(String name, List<Balance> payers,
                   double totExpense, Integer userId) {
        this(null, name, payers, totExpense, userId);
    }

}
