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
public class WorkerActor extends UntypedActor {

    private int clientID;
    private int interval;
    private String serveName;
    private String serverPort;
    private String symbol;
    private String transactionURL;

    private final String POST_NEW_STOCK_URI = "/api/v1/newstock";
    private final String POST_NEW_TRANSACTION_URI = "/api/v1/newtransaction";

    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final HttpUtil httpUtil = new HttpUtil();
    private final Random randomGenerator = new Random();


    public WorkerActor(int clientID, int interval, String serveName, String serverPort) {
        this.clientID = clientID;
        this.interval = interval;
        this.serveName = serveName;
        this.serverPort = serverPort;
        this.transactionURL = "http://"+serveName+":"+serverPort+POST_NEW_TRANSACTION_URI;
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
            sendTransaction();
            getContext().system().scheduler().scheduleOnce(
                    Duration.create((randomGenerator.nextInt(interval)+1)*1000, TimeUnit.MILLISECONDS),
                    getSelf(), "tick", getContext().dispatcher(), null);
        } else {
            unhandled(message);
        }
    }

    private void init() {
        symbol = "stock"+clientID;

        int choice = randomGenerator.nextInt(2);
        String stockType;
        if (choice==0)
            stockType = "PREFERRED";
        else
            stockType = "COMMON";
        int lastDivident = randomGenerator.nextInt(100)+1;
        int fixedDivident = randomGenerator.nextInt(100)+1;
        int parValue = randomGenerator.nextInt(200)+1;

        String json = String.format("{\"symbol\":\"%s\",\"stockType\":\"%s\",\"lastDividend\":%d,\"fixedDividend\":%d,\"parValue\":%d}",
                symbol,stockType,lastDivident,fixedDivident,parValue);
        String url = "http://"+serveName+":"+serverPort+POST_NEW_STOCK_URI;
        httpUtil.post(url,json);
        LOG.info("Created ticker actor with stock ID {}", symbol);

    }



    private void sendTransaction() {
    int choice = randomGenerator.nextInt(2);
        String transactionType;
        if (choice==0)
            transactionType = "BUY";
        else
            transactionType = "SELL";
        int amount = randomGenerator.nextInt(100)+1;
        float price = randomGenerator.nextInt(10000)/100+1;

        String json = String.format("{\"symbol\":\"%s\",\"transactionType\":\"%s\",\"amount\":%d,\"price\":%.2f}",
                symbol,transactionType,amount,price);
        httpUtil.post(transactionURL,json);
        LOG.info("Sent transaction to server: {}", json);

    }

}
