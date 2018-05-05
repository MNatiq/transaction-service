package com.n26.transaction.controllers;

import com.n26.transaction.model.Transaction;
import com.n26.transaction.service.TransactionService;
import com.n26.transaction.service.exception.OlderTransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    @Autowired
    TransactionService transactionService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postTransaction(final Transaction transaction) {
        try {
            transactionService.addTransaction(transaction);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (final OlderTransactionException e) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}

