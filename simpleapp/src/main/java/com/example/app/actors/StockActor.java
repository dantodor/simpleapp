package com.example.app.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.example.app.domain.*;
import com.example.app.helpers.StockActorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Stock actor is a wrapper that abstracts operations on a specific stock item
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class StockActor extends UntypedActor {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    private StockReport stockReport;

    private HashMap<Long,Transaction> transactions = new HashMap<>();
    private StockActorHelper helper = new StockActorHelper();
    private long DELTA = 60*1000l;
    private ActorRef indexActor;


    public StockActor(Stock stock, ActorRef indexActor, long delta) {
        this.stockReport = new StockReport(stock);
        this.indexActor = indexActor;
        this.DELTA = delta;
    }

    @Override
    public void onReceive ( Object message) {

        if ( message instanceof Transaction) {
            // first, remove old entries from the recorded transactions
            Transaction transaction = (Transaction) message;
            long spot = System.currentTimeMillis();
            ArrayList<Long> toRemove = new ArrayList<>();
            for (Long datetime : transactions.keySet()) {
                if ( spot - datetime.longValue() > DELTA )
                    toRemove.add(datetime);
            }
            for (Long key: toRemove)
                    transactions.remove(key);

            //add transaction to the save list
            transactions.put(new Long(spot), transaction);

            //recalculate spot values
            this.stockReport = helper.addLastTransaction(stockReport,transaction,transactions);
            // tell the indexActor the new spot price so it will update his values
            indexActor.tell(new GBCEIndexContainer(transaction.getSymbol(), stockReport.getStockPrice()), ActorRef.noSender());
            return;

        } else if (message instanceof GetStockInfo ) {

            getSender().tell(stockReport, ActorRef.noSender());
            return;

        } else if (message instanceof GetTransactionsList ) {

            Transaction[] transactionsArray = transactions.values().toArray(new  Transaction[0]);

            getSender().tell(new TransactionList(transactionsArray),ActorRef.noSender());
            return;

        } else {
            unhandled(message);
            return;
        }

    }
}
