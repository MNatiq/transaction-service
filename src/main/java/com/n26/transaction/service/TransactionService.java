package com.n26.transaction.service;

import com.n26.transaction.config.Config;
import com.n26.transaction.model.Transaction;
import com.n26.transaction.service.exception.OlderTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionService {
    private final Time time;

    private final TransactionStatistics[] transactionStatistics;
    private final Object[] transactionStatisticsLocks;

    private final TransactionStatistics cachedTransactionStatistics;
    private final Object cachedTransactionStatisticsLock;

    @Autowired
    public TransactionService(final Time time) {
        this.time = time;
        transactionStatistics = new TransactionStatistics[Config.KEEPING_MILLISECONDS / Config.ONE_LUMP_MILLISECONDS];
        transactionStatisticsLocks = new Object[transactionStatistics.length];
        cachedTransactionStatistics = new TransactionStatistics(time);
        cachedTransactionStatisticsLock = new Object();
        initializeLocks();
    }

    private void initializeLocks() {
        for (int i = 0; i < transactionStatisticsLocks.length; i++) {
            transactionStatisticsLocks[i] = new Object();
        }
    }


    public void addTransaction(final Transaction transaction) throws OlderTransactionException {
        assertValidTransaction(transaction);
        final int index = getTransactionStatisticsIndexForTimestamp(transaction.getTimestamp());

        synchronized (transactionStatisticsLocks[index]) {
            transactionStatistics[index] = new TransactionStatistics(time, transaction, transactionStatistics[index]);
        }
    }

    private int getTransactionStatisticsIndexForTimestamp(final long timestamp) {
        return (int) ((timestamp / Config.ONE_LUMP_MILLISECONDS) % transactionStatistics.length);
    }

    private void assertValidTransaction(final Transaction transaction) throws OlderTransactionException {
        if (!time.isValidTransactionTimestamp(transaction.getTimestamp())) {
            throw new OlderTransactionException();
        }
    }

    public TransactionStatistics getTransactionStatistics() {
        final long timestamp = time.getCurrentTimestamp();
        if (cachedTransactionStatistics.getTimestamp() < timestamp) {
            synchronized (cachedTransactionStatisticsLock) {
                if (cachedTransactionStatistics.getTimestamp() < timestamp) {
                    cachedTransactionStatistics.reset();
                    for (int i = 0; i < transactionStatistics.length; i++) {
                        cachedTransactionStatistics.doMergeTransactionStatistics(transactionStatistics[i]);
                    }
                    cachedTransactionStatistics.setTimestamp(timestamp);
                }
            }
        }
        return cachedTransactionStatistics;
    }
}

