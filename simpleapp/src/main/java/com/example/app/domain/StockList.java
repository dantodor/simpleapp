package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping the list stocks command response
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class StockList implements Serializable {
    private String[] symbol;

    public StockList(String[] symbol) {
        this.symbol = symbol;
    }

    public String[] getSymbol() {
        return symbol;
    }

    public void setSymbol(String[] symbol) {
        this.symbol = symbol;
    }

}
