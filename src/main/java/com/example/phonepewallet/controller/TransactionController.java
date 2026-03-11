package com.example.phonepewallet.controller;

import com.example.phonepewallet.entity.Transaction;
import com.example.phonepewallet.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{upi}")
    public List<Transaction> getTransactions(@PathVariable String upi) {

        return transactionRepository
                .findBySenderUpiOrReceiverUpi(upi, upi);
    }
}