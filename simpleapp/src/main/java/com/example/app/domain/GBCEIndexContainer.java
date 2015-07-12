package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping an index spot value
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class GBCEIndexContainer implements Serializable {
    private String symbol;
    private double value;

    public GBCEIndexContainer(String symbol, double value) {
        this.symbol = symbol;
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
