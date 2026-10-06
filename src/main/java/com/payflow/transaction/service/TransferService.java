package com.payflow.transaction.service;

import com.payflow.common.exception.InsufficientBalanceException;
import com.payflow.idempotency.entity.IdempotencyKey;
import com.payflow.idempotency.repository.IdempotencyKeyRepository;
import com.payflow.transaction.dto.TransferRequest;
import com.payflow.transaction.dto.TransferResponse;
import com.payflow.transaction.entity.Transaction;
import com.payflow.transaction.repository.TransactionRepository;
import com.payflow.user.entity.User;
import com.payflow.wallet.entity.Wallet;
import com.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public TransferResponse transfer(UUID senderId,  String idempotencyKey,TransferRequest request) {
        IdempotencyKey existingKey =
                idempotencyKeyRepository
                        .findByUserIdAndKey(senderId, idempotencyKey)
                        .orElse(null);
        if (existingKey != null) {

            Transaction transaction = existingKey.getTransaction();

            return new TransferResponse(
                    transaction.getId(),
                    transaction.getReference(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    transaction.getStatus()
            );
        }
        // 1. Prevent transferring to yourself
        if (senderId.equals(request.receiverId())) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }

        // 2. Lock sender wallet
        Wallet senderWallet = walletRepository.findWalletForUpdate(senderId).orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // 3. Lock receiver wallet
        Wallet receiverWallet = walletRepository.findWalletForUpdate(request.receiverId()).orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // 4. Check balance
        if (senderWallet.getBalance().compareTo(request.amount()) < 0) {

            throw new InsufficientBalanceException();
        }

        // 5. Check currency
        if (!senderWallet.getCurrency().equals(receiverWallet.getCurrency())) {

            throw new IllegalArgumentException("Wallet currencies do not match");
        }

        // 6. Debit sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.amount()));

        // 7. Credit receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.amount()));

        // 8. Create transaction
        Transaction transaction = new Transaction();

        transaction.setSenderWallet(senderWallet);
        transaction.setReceiverWallet(receiverWallet);
        transaction.setAmount(request.amount());
        transaction.setCurrency(senderWallet.getCurrency());
        transaction.setStatus("COMPLETED");
        transaction.setReference(UUID.randomUUID().toString());

        Transaction savedTransaction = transactionRepository.save(transaction);
        User user = senderWallet.getUser();

        IdempotencyKey key = new IdempotencyKey();

        key.setUser(user);
        key.setKey(idempotencyKey);
        key.setTransaction(savedTransaction);
        key.setStatus("COMPLETED");
        idempotencyKeyRepository.save(key);
        return new TransferResponse(savedTransaction.getId(), savedTransaction.getReference(), savedTransaction.getAmount(), savedTransaction.getCurrency(), savedTransaction.getStatus());
    }
}