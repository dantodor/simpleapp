package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping a request for stock information
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class GetStockInfo implements Serializable {
    private String symbol;

    public GetStockInfo(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
