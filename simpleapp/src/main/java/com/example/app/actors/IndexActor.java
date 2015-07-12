package com.example.app.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.example.app.domain.GBCEIndexContainer;
import com.example.app.domain.GetMarketIndex;
import com.example.app.domain.MarketIndex;
import com.example.app.helpers.IndexActorHelper;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * The Index actor is a wrapper that abstracts operations on Market Index
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class IndexActor extends UntypedActor {

    private final org.slf4j.Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    //a collection that holds the spot values for all the stocks in the system
    private HashMap<String,Double> spotValues = new HashMap<>();
    //the helper class that handles calculations
    private IndexActorHelper helper = new IndexActorHelper();
    //initial market index value
    private double indexValue = 0.0;


    @Override
    public void onReceive ( Object message) {

        if ( message instanceof GBCEIndexContainer) {
            GBCEIndexContainer spotValue = (GBCEIndexContainer)message;
            //add index to the save list
            spotValues.put(spotValue.getSymbol(), new Double(spotValue.getValue()));

            //recalculate spot values
            this.indexValue = helper.calculateIndex(spotValues);
            return;

        } else if (message instanceof GetMarketIndex) {

            getSender().tell(new MarketIndex(indexValue), ActorRef.noSender());
            return;

        } else {
            unhandled(message);
            return;
        }

    }
}
