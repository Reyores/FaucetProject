package com.faucetproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FaucetService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final String faucetContractAddress;
    private final BigDecimal tokenAmount;

    private final long rateLimitMs;
    private final ConcurrentHashMap<String, Long> lastRequestMap = new ConcurrentHashMap<>();

    public FaucetService(
            @Value("${web3j.client-address}") String clientAddress,
            @Value("${wallet.private-key}") String privateKey,
            @Value("${faucet.contract-address}") String faucetContractAddress,
            @Value("${faucet.token-amount}") BigDecimal tokenAmount,
            @Value("${faucet.rate-limit-ms}") long rateLimitMs
    ) {
        this.web3j = Web3j.build(new HttpService(clientAddress));
        this.credentials = Credentials.create(privateKey);
        this.faucetContractAddress = faucetContractAddress;
        this.tokenAmount = tokenAmount;
        this.rateLimitMs = rateLimitMs;
    }

    public String sendTokens(String recipient) throws Exception {
        if (recipient == null || !recipient.startsWith("0x") || recipient.length() != 42) {
            throw new Exception("Adresse invalide");
        }

        long now = Instant.now().toEpochMilli();
        if (lastRequestMap.containsKey(recipient)) {
            long lastRequest = lastRequestMap.get(recipient);
            if (now - lastRequest < rateLimitMs) {
                throw new Exception("Rate limit dépassé.");
            }
        }

        lastRequestMap.put(recipient, now);

        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST).send();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(100_000);

        BigInteger amountToSend = Convert.toWei(tokenAmount, Convert.Unit.ETHER).toBigIntegerExact();

        Function function = new Function(
                "sendPOL",
                Arrays.asList(new Address(recipient), new Uint256(amountToSend)),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                faucetContractAddress,
                BigInteger.ZERO, 
                encodedFunction
        );

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, 80002, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if (ethSendTransaction.hasError()) {
            throw new Exception("Erreur lors de l'envoi de la transaction : " +
                    ethSendTransaction.getError().getMessage());
        }

        return ethSendTransaction.getTransactionHash();
    }
}
