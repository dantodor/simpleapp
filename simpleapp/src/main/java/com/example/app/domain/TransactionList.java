package com.example.app.domain;

import java.io.Serializable;
/**
 * Java bean wrapping the list transactions command response
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class TransactionList implements Serializable {

    private Transaction[] transactions;

    public TransactionList(Transaction[] symbol) {
        this.transactions = symbol;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public void setTransactions(Transaction[] symbol) {
        this.transactions = symbol;
    }

}
