package com.n26.transaction.service;

import com.n26.transaction.config.Config;

public class FakeTime extends Time {
    private long timeOffset = 0;
    private long frozenTime = 0;
    private boolean isFrozenTime = false;

    @Override
    public long getCurrentTimestamp() {
        if (isFrozenTime) {
            return frozenTime + timeOffset;
        } else {
            return super.getCurrentTimestamp() + timeOffset;
        }
    }

    public long getRandomlyValidTimestamp() {
        return getEarliestValidTransactionTimestamp() + (long) (Math.random() * (Config.KEEPING_MILLISECONDS - Config.ONE_LUMP_MILLISECONDS));
    }

    public long getRandomlyInvalidTimestamp() {
        return getEarliestValidTransactionTimestamp() - 1 - (long) (Math.random() * 1000L * 60L * 60L * 24L * 30L);
    }

    public void goTwoMinutes() {
        timeOffset += 120000;
    }

    public void doFreezeTime() {
        frozenTime = super.getCurrentTimestamp();
        isFrozenTime = true;
    }

    public void doUnfreezeTime() {
        isFrozenTime = false;
    }
}

