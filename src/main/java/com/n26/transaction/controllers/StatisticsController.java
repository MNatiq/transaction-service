package com.n26.transaction.controllers;

import com.n26.transaction.service.TransactionService;
import com.n26.transaction.service.TransactionStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    TransactionService transactionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionStatistics getTransactionStatistics() {
        return transactionService.getTransactionStatistics();
    }
}
