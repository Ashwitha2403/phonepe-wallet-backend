package com.example.phonepewallet.controller;

import com.example.phonepewallet.entity.User;
import com.example.phonepewallet.entity.Wallet;
import com.example.phonepewallet.repository.UserRepository;
import com.example.phonepewallet.repository.WalletRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserController(UserRepository userRepository,
                          WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {

        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            return "Phone number already registered";
        }

        if (userRepository.findByUpiId(user.getUpiId()).isPresent()) {
            return "UPI ID already exists";
        }

        // save user
        User savedUser = userRepository.save(user);

        // create wallet automatically
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(0.0);

        walletRepository.save(wallet);

        return "User registered successfully";
    }
}