package com.example.app.utils;

/**
 * The AppMetrics class statically defines the metrics that we monitor in the system
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import static com.codahale.metrics.MetricRegistry.name;


public class AppMetrics {

    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static final String NAME = "App";

    public static final String GET = "Get_data";
    public static final String COUNT = "Count";

    //time and count endpoint performance
    public static final Timer getStockList = METRIC_REGISTRY.timer(name(NAME, GET, "StockList"));
    public static final Timer getStockInfo = METRIC_REGISTRY.timer(name(NAME, GET, "StockInfo"));
    public static final Timer getTransactionList = METRIC_REGISTRY.timer(name(NAME, GET, "TransactionList"));
    public static final Timer getMarketIndex = METRIC_REGISTRY.timer(name(NAME, GET, "MarketIndex"));
    //count how many transactions have hit the system
    public static final Counter transactionCounter = METRIC_REGISTRY.counter(name(NAME, COUNT, "Transactions"));


}
