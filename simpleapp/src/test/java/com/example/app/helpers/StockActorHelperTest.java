package com.example.app.helpers;

import com.example.app.domain.StockReport;
import com.example.app.domain.StockType;
import com.example.app.domain.Transaction;
import com.example.app.domain.TransactionType;
import junit.framework.TestCase;

import java.util.HashMap;

public class StockActorHelperTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testCommonTypeTransaction() throws Exception {

        StockReport commonStock = new StockReport("CA", StockType.COMMON,30,2,100,0.0,0.0,0.0);


        Transaction ta1 = new Transaction("CA", TransactionType.BUY,50,15.0);
        Transaction ta2 = new Transaction("CA", TransactionType.BUY,100,16.0);
        Transaction ta3 = new Transaction("CA", TransactionType.SELL,75,14.0);
        Transaction ta4 = new Transaction("CA", TransactionType.BUY,10,15.0);

        HashMap<Long,Transaction> transactions = new HashMap<>();
        transactions.put(0L,ta1);
        transactions.put(0L,ta2);
        transactions.put(0L,ta3);
        transactions.put(0L,ta4);

        StockActorHelper tester = new StockActorHelper();
        StockReport result = tester.addLastTransaction( commonStock, ta4, transactions);

        assertEquals(result.getDividendYield(),2.0,0.0001);
        assertEquals(result.getPeRatio(),0.5,0.0001);
        assertEquals(result.getStockPrice(),15.0,0.0001);




    }

    public void testPrefferedTypeTransaction() throws Exception {

        StockReport prefferedStock = new StockReport("CB", StockType.PREFERRED, 30, 2, 100, 0.0, 0.0, 0.0);

        Transaction tb1 = new Transaction("CB", TransactionType.BUY, 50, 15.0);
        Transaction tb2 = new Transaction("CB", TransactionType.BUY, 100, 16.0);
        Transaction tb3 = new Transaction("CB", TransactionType.SELL, 75, 14.0);
        Transaction tb4 = new Transaction("CB", TransactionType.BUY, 10, 15.0);

        HashMap<Long,Transaction> transactions = new HashMap<>();
        transactions.put(0L,tb1);
        transactions.put(0L,tb2);
        transactions.put(0L,tb3);
        transactions.put(0L,tb4);

        StockActorHelper tester = new StockActorHelper();
        StockReport result = tester.addLastTransaction( prefferedStock, tb4, transactions);

        assertEquals(result.getDividendYield(),0.1333333,0.0001);
        assertEquals(result.getPeRatio(),7.5,0.0001);
        assertEquals(result.getStockPrice(),15.0,0.0001);
    }

}