package com.example.actors;

import akka.actor.UntypedActor;
import com.example.httputil.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The work horse of the tester system
 * Each instance will first send a new stock request, then generate fake transactions with random data at random time intervals
 * as instructed by the seeds
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class AskerActor extends UntypedActor {

    private int clientID;
    private int interval;
    private String serveName;
    private String serverPort;
    private String symbol;
    private String getStockInfoURL;
    private String getTransactionListURL;
    private String getMarketIndexURL;

    private final String GET_STOCK_INFO_URI = "/api/v1/getstockinfo";
    private final String GET_TRANSACTION_LIST_URI = "/api/v1/gettransactionlist";
    private final String GET_MARKET_INDEX_URI = "/api/v1/getmarketindex";

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final HttpUtil httpUtil = new HttpUtil();
    private final Random randomGenerator = new Random();


    public AskerActor(int clientID, int interval, String serveName, String serverPort) {
        this.clientID = clientID;
        this.interval = interval;
        this.serveName = serveName;
        this.serverPort = serverPort;
        symbol = "stock"+clientID;
        this.getStockInfoURL = "http://"+serveName+":"+serverPort+GET_STOCK_INFO_URI+"/"+symbol;
        this.getTransactionListURL = "http://"+serveName+":"+serverPort+GET_TRANSACTION_LIST_URI+"/"+symbol;
        this.getMarketIndexURL = "http://"+serveName+":"+serverPort+GET_MARKET_INDEX_URI;
        init();
    }


    @Override
    public void preStart() {
        getContext().system().scheduler().scheduleOnce(
                Duration.create(5000, TimeUnit.MILLISECONDS),
                getSelf(), "tick", getContext().dispatcher(), null);
    }

    @Override
    public void onReceive ( Object message) {
        if (message instanceof String ) {
            sendQuestions();
            getContext().system().scheduler().scheduleOnce(
                    Duration.create((randomGenerator.nextInt(interval)+1)*1000, TimeUnit.MILLISECONDS),
                    getSelf(), "tick", getContext().dispatcher(), null);
        } else {
            unhandled(message);
        }
    }

    private void init() {

        LOG.info("Created asker actor with stock ID {}, let it run", symbol);

    }



    private void sendQuestions() {
        // we don't care here about the responses, maybe in the future
        // all we need is a stress tool for the server
        httpUtil.get(this.getStockInfoURL);
        httpUtil.get(this.getTransactionListURL);
        httpUtil.get(this.getMarketIndexURL);
        LOG.info("Asking for market index, URL: {}", this.getMarketIndexURL);

    }

}
