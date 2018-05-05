package com.n26.transaction.service;

import com.n26.transaction.BootApplication;
import com.n26.transaction.config.AppConfigTest;
import com.n26.transaction.model.Transaction;
import com.n26.transaction.service.exception.OlderTransactionException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BootApplication.class, AppConfigTest.class, TransactionServiceTest.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionServiceTest {

    private static final int CORRECT_STATISTICS_TESTCOUNT = 10000;
    private static final double DELTA = 0.001;
    @Autowired
    TransactionService transactionService;
    @Autowired
    FakeTime time;

    @Test(expected = OlderTransactionException.class)
    public void testTransactionServiceThrowsOutdatedTransactionExceptionForInvalidTransaction() throws OlderTransactionException {
        transactionService.addTransaction(new Transaction(Math.random(), time.getRandomlyInvalidTimestamp()));
    }

    @Test
    public void testTransactionServiceReturnsCorrectStatisticsForMultipleValidTransactions() throws OlderTransactionException {
        time.goTwoMinutes();
        time.doFreezeTime();

        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < CORRECT_STATISTICS_TESTCOUNT; i++) {
            final double amount = Math.random() * 1000;
            sum += amount;
            max = Math.max(max, amount);
            min = Math.min(min, amount);
            transactionService.addTransaction(new Transaction(amount, time.getRandomlyValidTimestamp()));
        }

        final double average = sum / CORRECT_STATISTICS_TESTCOUNT;

        final TransactionStatistics transactionStatistics = transactionService.getTransactionStatistics();

        assertEquals(transactionStatistics.getSum(), sum, DELTA);
        assertEquals(transactionStatistics.getAvg(), average, DELTA);
        assertEquals(transactionStatistics.getMin(), min, DELTA);
        assertEquals(transactionStatistics.getMax(), max, DELTA);
        assertEquals(transactionStatistics.getCount(), CORRECT_STATISTICS_TESTCOUNT);

        time.doUnfreezeTime();
    }

    @Test
    public void testTransactionServiceReturnsCorrectStatisticsForMultipleTransactionsIncludingInvalidOnes() throws OlderTransactionException {
        time.doFreezeTime();
        time.goTwoMinutes();

        double sum = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        long count = 0;

        for (int i = 0; i < CORRECT_STATISTICS_TESTCOUNT; i++) {
            final double amount = Math.random() * 1000;
            if (Math.random() < 0.5D) {
                try {
                    transactionService.addTransaction(new Transaction(amount, time.getRandomlyInvalidTimestamp()));
                } catch (final OlderTransactionException e) {/*expected*/}
            } else {
                count++;
                sum += amount;
                max = Math.max(max, amount);
                min = Math.min(min, amount);
                transactionService.addTransaction(new Transaction(amount, time.getRandomlyValidTimestamp()));
            }
        }

        final double average = sum / count;

        final TransactionStatistics transactionStatistics = transactionService.getTransactionStatistics();

        assertEquals(transactionStatistics.getSum(), sum, DELTA);
        assertEquals(transactionStatistics.getAvg(), average, DELTA);
        assertEquals(transactionStatistics.getMin(), min, DELTA);
        assertEquals(transactionStatistics.getMax(), max, DELTA);
        assertEquals(transactionStatistics.getCount(), count);

        time.doUnfreezeTime();
    }

    @Test
    public void testTransactionServiceReturnsEmptyStatisticsAfterTwoMinutesPassedAfterLastValidTransactionWasPosted() throws OlderTransactionException {
        time.doFreezeTime();
        time.goTwoMinutes();

        for (int i = 0; i < CORRECT_STATISTICS_TESTCOUNT; i++) {
            transactionService.addTransaction(new Transaction(Math.random(), time.getRandomlyValidTimestamp()));
        }

        time.goTwoMinutes();

        final TransactionStatistics transactionStatistics = transactionService.getTransactionStatistics();
        assertFalse(transactionStatistics.hasData());

        time.doUnfreezeTime();
    }
}
