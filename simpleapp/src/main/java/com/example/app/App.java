package com.example.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.example.app.actors.IndexActor;
import com.example.app.actors.StockManagerActor;
import com.example.app.domain.*;
import com.example.app.utils.AppMetrics;
import com.example.app.utils.JsonTransformer;
import com.google.gson.Gson;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

/**
 * The entry point in the application
 * It sets up the system, comprised of the REST interface, the actor system, the monitoring system and the logging system
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class App 
{

    // the system logger
    private final Logger LOG =LoggerFactory.getLogger(this.getClass().getName());

    //save the command line arguments
    private String[] args;

    //the atcor system
    private ActorSystem system;


    //reference to the stock manager and index actors, they need to be accessible from the REST interface
    private ActorRef stockManagerActor;
    private ActorRef indexActor;

    //a mapper json objects<->java classes
    private final Gson gson = new Gson();



    public App( String[] args )
    {
        this.args = args;

    }

    /**
     * This is the setup of the system.
     * @return Nothing.
     */
    public void init() {
        Config config = readConfig(args);
        setupMonitoring(config);
        setupActors(config);
        //this.restApi = new RestApi(stockManagerActor,indexActor, config);
        initEndPoints(config);
    }


    /**
     * Init the endpoints for the REST API
     * @return Nothing.
     */
    private void initEndPoints(Config config) {

        port(config.getInt("application.port"));

        //for health monitoring only
        get("/ruok", (req, res) -> "I'm OK");



        get("/api/v1/getstocklist", "application/json", (req, res) -> {

            LOG.info("got request for stock list");
            final Timer.Context context = AppMetrics.getStockList.time();

            GetStockList getStockList = new GetStockList();

            StockList stockList = null;

            final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

            Future<Object> future = Patterns.ask(stockManagerActor, getStockList, timeout);
            try {
                stockList = (StockList) Await.result(future, timeout.duration());
            } catch (Exception e) {
                LOG.error("Error at getstocklist: {}", e.getMessage());
            }

            res.type("application/json");

            context.stop();

            return stockList;

        }, new JsonTransformer());



        get("/api/v1/getstockinfo/:stockId", "application/json", (req, res) -> {
            LOG.info("got request for stock id {} info", req.params(":stockId"));
            final Timer.Context context = AppMetrics.getStockInfo.time();

            GetStockInfo getStockInfo = new GetStockInfo(req.params(":stockId"));

            StockReport stockReport = null;

            final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

            Future<Object> future = Patterns.ask(stockManagerActor, getStockInfo, timeout);
            try {
                stockReport = (StockReport) Await.result(future, timeout.duration());
            } catch (Exception e) {
                LOG.error("Error at getstockinfo: {}", e.getMessage());
                res.status(500);
                return stockReport;
            }
            res.type("application/json");
            if (stockReport!=null && stockReport.getSymbol().length()==0) {
                res.status(404);
            }

            context.stop();

            return stockReport;

        }, new JsonTransformer());



        get("/api/v1/gettransactionlist/:stockId", "application/json", (req, res) -> {

            LOG.info("got request for transaction list for {}",req.params(":stockId"));
            final Timer.Context context = AppMetrics.getTransactionList.time();

            GetTransactionsList getTransactionsList = new GetTransactionsList(req.params(":stockId"));

            TransactionList transactionList = null;

            final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

            Future<Object> future = Patterns.ask(stockManagerActor, getTransactionsList, timeout);
            try {
                transactionList = (TransactionList) Await.result(future, timeout.duration());
            } catch (Exception e) {
                LOG.error("Error at gettransactionlist: {}", e.getMessage());
            }


            res.type("application/json");

            context.stop();

            return transactionList;

        }, new JsonTransformer());



        get("/api/v1/getmarketindex", "application/json", (req, res) -> {
            LOG.info("got request for market index");
            final Timer.Context context = AppMetrics.getMarketIndex.time();

            GetMarketIndex getMarketIndex = new GetMarketIndex();

            MarketIndex marketIndex = null;

            final Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
            Future<Object> future = Patterns.ask(indexActor, getMarketIndex, timeout);
            try {
                marketIndex = (MarketIndex) Await.result(future, timeout.duration());
            } catch (Exception e) {
                LOG.error("Error at getmarketindex: {}", e.getMessage());
            }

            res.type("application/json");

            context.stop();

            return marketIndex;

        }, new JsonTransformer());


        post("/api/v1/newstock", (req, res) -> {

                    try {
                        Stock stock = gson.fromJson(req.body(), Stock.class);
                        stockManagerActor.tell(stock, ActorRef.noSender());
                        LOG.info("New Stock created, string received : " + req.body());
                        res.status(201);
                        return "";
                    } catch (Exception e) {
                        // the exception might be caused by a malformed JSON string, it is bubbled up back to the caller
                        res.status(500);
                        LOG.error("Bad NewStock string received : " + req.body());
                        return e.getMessage();
                    }
                }

        );



        post("/api/v1/newtransaction", (req, res) -> {

                    try {
                        Transaction transaction = gson.fromJson(req.body(), Transaction.class);
                        stockManagerActor.tell(transaction, ActorRef.noSender());
                        AppMetrics.transactionCounter.inc();
                        LOG.info("New transaction created string received : " + req.body());
                        res.status(201);
                        return "";
                    } catch (Exception e) {
                        // the exception might be caused by a malformed JSON string, it is bubbled up back to the caller
                        res.status(500);
                        LOG.error("Bad Transaction string received : " + req.body());
                        return e.getMessage();
                    }
                }
        );



    }


    /**
     * Read the configuration from the app.conf file
     * @return Config a mapping of the file contents.
     */
    private Config readConfig(String[] args){
        String configFilePath;

        if(args.length == 0) {
            configFilePath = "./app.conf";
        } else {
            configFilePath = args[0];
        }

        final File configFile = new File(configFilePath);
        if(!configFile.exists()) {
            LOG.error("Config file not found!");
            System.exit(-1);
        }

        final Config config = ConfigFactory.parseFileAnySyntax(configFile, ConfigParseOptions.defaults()
                .setSyntax(ConfigSyntax.CONF));
        return config;

    }


    /**
     * Set up the monitoring system
     * @return Nothing.
     */
    private void setupMonitoring(Config config) {
        Slf4jReporter reporter = Slf4jReporter.forRegistry(AppMetrics.METRIC_REGISTRY)
                .outputTo(org.slf4j.LoggerFactory.getLogger("metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(60, TimeUnit.SECONDS);

        //if system is configured to report performance metrics to a remte Graphite installation, instantiate it
        if (config.getBoolean("monitoring.remote")) {
            Graphite graphite = new Graphite(new InetSocketAddress(config.getString("monitoring.graphiteServer"),
                    config.getInt("mmonitoring.graphitePort")));
            GraphiteReporter reporter2 = GraphiteReporter.forRegistry(AppMetrics.METRIC_REGISTRY)
                    .prefixedWith(config.getString("monitoring.systemID"))
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(graphite);
            reporter2.start(1, TimeUnit.MINUTES);
        }
    }


    /**
     * Set up the Actor system
     * @return Nothing.
     */
    private void setupActors(Config config) {

        system = ActorSystem.create("ClusterSystem", config);
        indexActor = system.actorOf(Props.create(IndexActor.class), "indexActor");
        stockManagerActor = system.actorOf(Props.create(StockManagerActor.class, indexActor,config.getLong("application.delta")), "stockManagerActor");
    }

    public static void main(String[] args) throws Exception {

        final App app = new App(args);
        //start all
        app.init();

    }
}
