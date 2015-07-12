package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping the stock definition
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */

public class Stock implements Serializable {
    private String symbol;
    private StockType stockType;
    private int lastDividend;
    private double fixedDividend;
    private int parValue;

    public Stock(String symbol, StockType stockType, int lastDividend, double fixedDividend, int parValue) {
        this.symbol = symbol;
        this.stockType = stockType;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public StockType getStockType() {
        return stockType;
    }

    public void setStockType(StockType stockType) {
        this.stockType = stockType;
    }

    public int getLastDividend() {
        return lastDividend;
    }

    public void setLastDividend(int lastDividend) {
        this.lastDividend = lastDividend;
    }

    public double getFixedDividend() {
        return fixedDividend;
    }

    public void setFixedDividend(double fixedDividend) {
        this.fixedDividend = fixedDividend;
    }

    public int getParValue() {
        return parValue;
    }

    public void setParValue(int parValue) {
        this.parValue = parValue;
    }



}
