package com.payflow.wallet.service;

import com.payflow.common.exception.InsufficientBalanceException;
import com.payflow.user.repository.UserRepository;
import com.payflow.wallet.dto.WalletResponse;
import com.payflow.wallet.entity.Wallet;
import com.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletResponse getWallet(UUID userId) {

        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));

        return toResponse(wallet);
    }

    @Transactional
    public WalletResponse deposit(UUID userId, BigDecimal amount) {

        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(amount));

        return toResponse(wallet);
    }

    @Transactional
    public WalletResponse withdraw(UUID userId, BigDecimal amount) {

        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        return toResponse(wallet);
    }

    private WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(wallet.getId(), wallet.getBalance(), wallet.getCurrency());
    }
}