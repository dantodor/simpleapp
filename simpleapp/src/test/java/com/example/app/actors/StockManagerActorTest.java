package com.example.app.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.example.app.domain.*;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



public class StockManagerActorTest extends TestCase {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown(){
        system.shutdown();
    }

    @Test
    public void testCommonData() throws Exception {

        new JavaTestKit(system) {{

            final ActorRef indexHelper = system.actorOf(Props.create(IndexActor.class), "indexActor");
            final ActorRef subject = system.actorOf(Props.create(StockManagerActor.class,indexHelper), "stockManagerActor");

            Stock commonStock = new Stock("CA", StockType.COMMON,30,2,100);


            Transaction ta1 = new Transaction("CA", TransactionType.BUY,50,15.0);
            Transaction ta2 = new Transaction("CA", TransactionType.BUY,100,16.0);
            Transaction ta3 = new Transaction("CA", TransactionType.SELL,75,14.0);
            Transaction ta4 = new Transaction("CA", TransactionType.BUY,10,15.0);
            subject.tell(commonStock, ActorRef.noSender());
            subject.tell(ta1,ActorRef.noSender());
            subject.tell(ta2,ActorRef.noSender());
            subject.tell(ta3,ActorRef.noSender());
            subject.tell(ta4,ActorRef.noSender());
            // test stock list
            subject.tell(new GetStockList(),getRef());
            while (!msgAvailable()) Thread.sleep(100);
            StockList msg1 = expectMsgAnyClassOf(StockList.class);
            String[] response = msg1.getSymbol();
            assertEquals(response[0],"CA");

            // check that stock report is calculated correctly after the transactions
            subject.tell(new GetStockInfo("CA"),getRef());
            while (!msgAvailable()) Thread.sleep(100);
            StockReport msg2 = expectMsgAnyClassOf(StockReport.class);
            assertEquals(msg2.getDividendYield(),2.0,0.0001);
            assertEquals(msg2.getPeRatio(),0.5,0.0001);
            assertEquals(msg2.getStockPrice(),15.0,0.0001);



            while (!msgAvailable()) Thread.sleep(100);
            MarketIndex msg3 = expectMsgAnyClassOf(MarketIndex.class);
            assertEquals(msg3.getIndexValue(),0.0,0.1);


            Stock prefferedStock = new Stock("CB", StockType.PREFERRED,30,2,100);


            Transaction tb1 = new Transaction("CB", TransactionType.BUY, 50, 15.0);
            Transaction tb2 = new Transaction("CB", TransactionType.BUY, 100, 16.0);
            Transaction tb3 = new Transaction("CB", TransactionType.SELL, 75, 14.0);
            Transaction tb4 = new Transaction("CB", TransactionType.BUY, 10, 15.0);

            subject.tell(prefferedStock, ActorRef.noSender());
            subject.tell(tb1,ActorRef.noSender());
            subject.tell(tb2,ActorRef.noSender());
            subject.tell(tb3,ActorRef.noSender());
            subject.tell(tb4,ActorRef.noSender());

            // test stock list
            subject.tell(new GetStockList(),getRef());
            while (!msgAvailable()) Thread.sleep(1000);
            StockList msg4 = expectMsgAnyClassOf(StockList.class);
            String[] response2 = msg4.getSymbol();
            assertEquals(response2[10],"CA");

            // check that stock report is calculated correctly after the transactions
            subject.tell(new GetStockInfo("CA"),getRef());
            while (!msgAvailable()) Thread.sleep(1000);
            StockReport msg5 = expectMsgAnyClassOf(StockReport.class);
            assertEquals(msg5.getDividendYield(),2.0,0.0001);
            assertEquals(msg5.getPeRatio(),0.5,0.0001);
            assertEquals(msg5.getStockPrice(),15.0,0.0001);



            while (!msgAvailable()) Thread.sleep(1000);
            MarketIndex msg6 = expectMsgAnyClassOf(MarketIndex.class);
            assertEquals(msg6.getIndexValue(),0.0,0.1);



        }};


    }



}