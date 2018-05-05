package com.n26.transaction.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.n26.transaction.model.Transaction;

public class TransactionStatistics {
    private final Time time;
    private double sum;
    private double avg;
    private double max;
    private double min;
    private long count;
    private long timestamp;

    protected TransactionStatistics(final Time time) {
        this.time = time;
        reset();
    }

    protected TransactionStatistics(final Time time, final Transaction transaction, final TransactionStatistics otherTransactionStatistics) {
        this.time = time;
        this.timestamp = transaction.getTimestamp();
        this.sum = transaction.getAmount();
        this.count = 1;
        this.avg = transaction.getAmount();
        this.max = transaction.getAmount();
        this.min = transaction.getAmount();
        doMergeTransactionStatistics(otherTransactionStatistics);
    }

    public void reset() {
        this.sum = 0;
        this.avg = 0;
        this.max = Long.MIN_VALUE;
        this.min = Long.MAX_VALUE;
        this.count = 0;
        this.timestamp = 0;
    }

    public void doMergeTransactionStatistics(final TransactionStatistics otherTransactionStatistics) {
        if (isMergableOtherTransactionStatistics(otherTransactionStatistics)) {
            this.avg = ((otherTransactionStatistics.avg * otherTransactionStatistics.count) + (this.avg * this.count)) / (otherTransactionStatistics.count + this.count);
            this.sum = otherTransactionStatistics.sum + this.sum;
            this.count = otherTransactionStatistics.count + this.count;
            this.max = Math.max(this.max, otherTransactionStatistics.max);
            this.min = Math.min(this.min, otherTransactionStatistics.min);
        }
    }

    private boolean isMergableOtherTransactionStatistics(final TransactionStatistics otherTransactionStatistics) {
        return
                otherTransactionStatistics != null &&
                        otherTransactionStatistics.hasData() &&
                        time.isValidTransactionTimestamp(otherTransactionStatistics.getTimestamp()) &&
                        (!this.hasData() || this.getTimestamp() == 0 || time.areSameLumpTimestamps(this.timestamp, otherTransactionStatistics.getTimestamp()));
    }

    @JsonIgnore
    public boolean hasData() {
        return count > 0;
    }

    @JsonIgnore
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public double getSum() {
        return hasData() ? sum : 0;
    }

    public double getAvg() {
        return hasData() ? avg : 0;
    }

    public double getMax() {
        return hasData() ? max : 0;
    }

    public double getMin() {
        return hasData() ? min : 0;
    }

    public long getCount() {
        return hasData() ? count : 0;
    }
}
