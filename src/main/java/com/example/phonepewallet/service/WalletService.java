package com.example.phonepewallet.service;

import com.example.phonepewallet.dto.SendMoneyRequest;
import com.example.phonepewallet.dto.AddMoneyRequest;
import com.example.phonepewallet.entity.Wallet;
import com.example.phonepewallet.entity.Transaction;
import com.example.phonepewallet.repository.WalletRepository;
import com.example.phonepewallet.repository.TransactionRepository;
import com.example.phonepewallet.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Double checkBalance(Long userId) {

        Optional<Wallet> wallet = walletRepository.findByUserId(userId);

        return wallet.map(Wallet::getBalance).orElse(0.0);
    }

    public Double addMoney(AddMoneyRequest request) {

        Optional<Wallet> walletOptional =
                walletRepository.findByUserId(request.getUserId());

        Wallet wallet;

        if(walletOptional.isPresent()) {
            wallet = walletOptional.get();
        } else {

            wallet = new Wallet();

            User user = new User();
            user.setId(request.getUserId());

            wallet.setUser(user);
            wallet.setBalance(0.0);
        }

        wallet.setBalance(wallet.getBalance() + request.getAmount());

        walletRepository.save(wallet);

        return wallet.getBalance();
    }

    public String sendMoney(SendMoneyRequest request) {

        Optional<Wallet> senderWalletOptional =
                walletRepository.findByUserId(request.getFromUserId());

        Optional<Wallet> receiverWalletOptional =
                walletRepository.findByUserId(request.getToUserId());

        if(senderWalletOptional.isEmpty() || receiverWalletOptional.isEmpty()) {
            return "User wallet not found";
        }

        Wallet senderWallet = senderWalletOptional.get();
        Wallet receiverWallet = receiverWalletOptional.get();

        if(senderWallet.getBalance() < request.getAmount()) {
            return "Insufficient balance";
        }

        senderWallet.setBalance(senderWallet.getBalance() - request.getAmount());
        receiverWallet.setBalance(receiverWallet.getBalance() + request.getAmount());

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Save transaction history
        Transaction transaction = new Transaction();
        transaction.setSenderUpi(senderWallet.getUser().getUpiId());
        transaction.setReceiverUpi(receiverWallet.getUser().getUpiId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("SUCCESS");
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return "Money transferred successfully";
    }
}