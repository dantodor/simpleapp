package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping the stock item response
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class StockReport implements Serializable {
    private String symbol;
    private StockType stockType;
    private int lastDividend;
    private double fixedDividend;
    private int parValue;
    private double dividendYield;
    private double peRatio;
    private double stockPrice;

    public StockReport(String symbol, StockType stockType, int lastDividend, double fixedDividend, int parValue, double dividendYield, double peRatio, double stockPrice) {
        this.symbol = symbol;
        this.stockType = stockType;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
        this.dividendYield = dividendYield;
        this.peRatio = peRatio;
        this.stockPrice = stockPrice;
    }

    public StockReport(Stock stock) {
        this.symbol = stock.getSymbol();
        this.stockType = stock.getStockType();
        this.lastDividend = stock.getLastDividend();
        this.fixedDividend = stock.getFixedDividend();
        this.parValue = stock.getParValue();
        this.dividendYield = 0.0d;
        this.peRatio = 0.0d;
        this.stockPrice = 0.0d;
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

    public double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }
}
