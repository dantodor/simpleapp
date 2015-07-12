package com.example.app.helpers;

import com.example.app.domain.StockReport;
import com.example.app.domain.StockType;
import com.example.app.domain.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * The Stock actor companion class class
 * It implements the calculation of the running values, when a new order comes in
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class StockActorHelper {

    public StockReport addLastTransaction (StockReport stockReport, Transaction lastTransaction, HashMap<Long, Transaction> transactions) {

        StockReport newStock = stockReport;
        if ( newStock.getStockType() == StockType.COMMON )
            newStock.setDividendYield(newStock.getLastDividend() / lastTransaction.getPrice());
        else
            newStock.setDividendYield(newStock.getFixedDividend()*newStock.getParValue()/100/lastTransaction.getPrice());

        if (newStock.getDividendYield()>0.000001)
            newStock.setPeRatio(1 / newStock.getDividendYield());
        else
            newStock.setPeRatio(0.0);

        double productSum = 0.0;
        double simpleSum = 0.0;

        for (Map.Entry<Long,Transaction> transactionEntry : transactions.entrySet()){
            Transaction transaction = transactionEntry.getValue();
            productSum = productSum + transaction.getAmount()*transaction.getPrice();
            simpleSum = simpleSum + transaction.getAmount();
        }

        newStock.setStockPrice(productSum/simpleSum);

        return stockReport;
    }
}
