package com.payflow.wallet.controller;

import com.payflow.wallet.dto.MoneyRequest;
import com.payflow.wallet.dto.WalletResponse;
import com.payflow.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public WalletResponse getWallet(Authentication authentication) {

        UUID userId = UUID.fromString(
                authentication.getPrincipal().toString()
        );

        return walletService.getWallet(userId);
    }

    @PostMapping("/deposit")
    public WalletResponse deposit(
            Authentication authentication,
            @Valid @RequestBody MoneyRequest request
    ) {

        UUID userId = UUID.fromString(
                authentication.getPrincipal().toString()
        );

        return walletService.deposit(
                userId,
                request.amount()
        );
    }

    @PostMapping("/withdraw")
    public WalletResponse withdraw(
            Authentication authentication,
            @Valid @RequestBody MoneyRequest request
    ) {

        UUID userId = UUID.fromString(
                authentication.getPrincipal().toString()
        );

        return walletService.withdraw(
                userId,
                request.amount()
        );
    }
}