package com.fyp.stellar;

import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageDataOperation;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Stellar {

    public static KeyPair pair = null;

    public static String getTransactionhashStellar() {
        return transactionhashStellar;
    }

    public static void setTransactionhashStellar(String transactionhashStellar) {
        Stellar.transactionhashStellar = transactionhashStellar;
    }

    public static String transactionhashStellar = null;

    public static void CreateAccount() throws IOException {
        pair = KeyPair.random();

        System.out.println(new String(pair.getSecretSeed()));
        System.out.println(pair.getAccountId());

        String friendbotUrl = String.format(
                "https://friendbot.stellar.org/?addr=%s",
                pair.getAccountId());
        InputStream response = new URL(friendbotUrl).openStream();
        String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
        System.out.println("SUCCESS! You have a new account :)\n" + body);


    }


    public static void CheckBalance() throws IOException {
        Server server = new Server("https://horizon-testnet.stellar.org");
        AccountResponse account = server.accounts().account(pair.getAccountId());
        System.out.println("Balances for account " + pair.getAccountId());
        for (AccountResponse.Balance balance : account.getBalances()) {
            System.out.println(String.format(
                    "Type: %s, Code: %s, Balance: %s",
                    balance.getAssetType(),
                    balance.getAssetCode(),
                    balance.getBalance()));
        }

    }

    public static SubmitTransactionResponse doManageData(String name, String value) throws Exception {
        Server server = new Server("https://horizon-testnet.stellar.org");
        KeyPair keyPair = KeyPair.fromSecretSeed("SDPXZM27HKB5BFHS33GDBYWDCZFRQN3S32QQA4XVFCGUE4NMGA3CBFKD");
        AccountResponse sourceAccount = server.accounts().account(keyPair.getAccountId());

        ManageDataOperation operation = new ManageDataOperation.Builder(name, value.getBytes())
                .setSourceAccount(keyPair.getAccountId())
                .build();
        Transaction transaction = new Transaction.Builder(sourceAccount, Network.TESTNET)
                .addOperation(operation)
                .setTimeout(2000)
                .setOperationFee(100)
                .build();
        try {
            transaction.sign(keyPair);
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            System.out.println("--------------------------");
            System.out.println(response.getHash());
            setTransactionhashStellar(response.getHash());
            return response;
        } catch (Exception e) {
            String msg = "Failed to manageData: ";
            System.out.println(msg + e.getMessage());
            throw new Exception(msg, e);
        }
    }


    public KeyPair getPair() {
        return pair;
    }

}
