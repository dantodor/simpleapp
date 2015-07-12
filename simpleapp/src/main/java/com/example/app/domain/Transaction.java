package com.example.app.domain;

import com.google.gson.Gson;

/**
 * Created by nutz on 10.07.2015.
 */
public class Transaction {

    private final String symbol;
    private final TransactionType transactionType;
    private final int amount;
    private final double price;

    public Transaction(String symbol, TransactionType transactionType, int amount, double price) {
        this.symbol = symbol;
        this.transactionType = transactionType;
        this.amount = amount;
        this.price = price;

    }


    public String getSymbol() {
        return symbol;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {

        Gson gson = new Gson();
        return (gson.toJson(this));

    }



}
