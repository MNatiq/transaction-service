package com.n26.transaction.service;

import com.n26.transaction.config.Config;

public class Time {
    public long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public long getEarliestValidTransactionTimestamp() {
        return getCurrentTimestamp() - Config.KEEPING_MILLISECONDS + Config.ONE_LUMP_MILLISECONDS;
    }


    public boolean isValidTransactionTimestamp(final long timestamp) {
        return timestamp >= getEarliestValidTransactionTimestamp();
    }


    public boolean areSameLumpTimestamps(final long timestamp1, final long timestamp2) {
        final long lump1 = timestamp1 / Config.ONE_LUMP_MILLISECONDS;
        final long lump2 = timestamp2 / Config.ONE_LUMP_MILLISECONDS;
        return lump1 == lump2;
    }
}