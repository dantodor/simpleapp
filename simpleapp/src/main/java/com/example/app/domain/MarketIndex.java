package com.example.app.domain;

import java.io.Serializable;

/**
 * Java bean wrapping the Market Index response
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class MarketIndex implements Serializable {
    private double indexValue;

    public MarketIndex(double symbol) {
        this.indexValue = symbol;
    }

    public double getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(double indexValue) {
        this.indexValue = indexValue;
    }
}
