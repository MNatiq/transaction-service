package com.n26.transaction.model;

public class Transaction {
    private double amount;
    private long timestamp;

    public Transaction() {

    }

    public Transaction(final double amount, final long timestamp) {
        this.setAmount(amount);
        this.setTimestamp(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }
}

