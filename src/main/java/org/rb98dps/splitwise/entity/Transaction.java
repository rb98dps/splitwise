package org.rb98dps.splitwise.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

    private int senderId;
    private double balance;
    private int receiverId;

    public Transaction(int senderId, double balance, int receiverId) {
        this.senderId = senderId;
        this.balance = balance;
        this.receiverId = receiverId;
    }


    @Override
    public String toString() {

        if (balance > 0) {
            return senderId + ", owes = " + balance + ", to " + receiverId;
        } else if (balance < 0) {
            return senderId + " will receive = " + -balance + " from " + receiverId;
        }
        return senderId + " settled with " + receiverId;
    }
}
