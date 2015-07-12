package com.example.app.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import com.example.app.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * The StockManager actor is one of the main classes of the system
 * It manages a pool of Actors, one for the each Stock items living in the system
 * Also it routes various requests between the managed actors
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class StockManagerActor extends UntypedActor {

    // a collection of all the Stock actors in the system
    private HashMap<String,ActorRef> stocks = new HashMap<>();
    //initialize the Logging system
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    //a reference to the Index Actor that calculates the Market index
    private ActorRef indexActor;
    //retention period
    private long delta;

    public StockManagerActor(ActorRef indexActor, long delta) {

        this.indexActor = indexActor;
        this.delta = delta;
    }

    /**
    * This is the main method loop of the actor.
    * @param message The message intended to be processed.
    * @return Nothing.
     */
    @Override
    public void onReceive ( Object message) {

        if ( message instanceof Stock) {
            //We have a request to add a new stock item to the system
            Stock stock = (Stock) message;
            if ( stocks.get(stock.getSymbol())==null ) {
                //create a new actor for the stock symbol and add it to the watch list
                ActorRef newStockActor = this.getContext().actorOf(Props.create(StockActor.class, stock, indexActor, delta), stock.getSymbol());
                context().watch(newStockActor);
                stocks.put(stock.getSymbol(), newStockActor);
                LOG.info("Created actor for stock symbol {}", stock.getSymbol());
            } else {
                //stock actor already exist, just log it and do nothing
                LOG.warn("Trying to create existing stock symbol {}", stock.getSymbol());
            }
            return;

        } else if ( message instanceof GetStockList ) {
            //generate a list of all the stock items the system is aware of
            Set<String> keys = stocks.keySet();

            getSender().tell(new StockList(keys.toArray(new String[keys.size()])),ActorRef.noSender());
            return;

        } else if ( message instanceof GetStockInfo) {
            // forward the request to the the appropriate stock actor, if any
            // if none exist, return an empty stock info
            String symbol = ((GetStockInfo)message).getSymbol();

            ActorRef correspondingStockActor = stocks.get(symbol);
            if (correspondingStockActor != null)
                correspondingStockActor.tell(message, getSender());
            else
                getSender().tell(new StockReport(new Stock("",StockType.COMMON,0,0.0,0)) , ActorRef.noSender());
            return;

        }  else if ( message instanceof GetTransactionsList) {
            // forward the request to the the appropriate stock actor, if any
            // if none exist, return an empty transaction list
            String symbol = ((GetTransactionsList)message).getSymbol();

            ActorRef correspondingStockActor = stocks.get(symbol);
            if (correspondingStockActor != null) {
                correspondingStockActor.tell(message, getSender());
            }
            else {
                Transaction[] t = {};
                getSender().tell(new TransactionList(t), ActorRef.noSender());
            }
            return;

        } else if (message instanceof Transaction) {
            // we have to record a new transaction
            Transaction t = (Transaction) message;
            ActorRef stockActor = stocks.get(t.getSymbol());
            if ( stockActor != null ) {
                stockActor.tell(message, ActorRef.noSender());
                LOG.info("Recorded transaction  {}", t.toString());
            } else {
                LOG.warn("Trying to record transaction for non-existing stock symbol {}", t.getSymbol());
            }
            return;

        } else if (message instanceof Terminated) {
            //in case one of the stock actors dies prematurely
            //we have to remove it from the collection
            Terminated terminated = (Terminated) message;
            ActorRef terminatedActor = terminated.getActor();
            stocks.remove(terminatedActor.path().name());
            return;

        } else {
            unhandled(message);
            return;
        }

    }
}
