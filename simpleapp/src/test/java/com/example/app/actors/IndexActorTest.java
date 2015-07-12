package com.example.app.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.example.app.domain.GBCEIndexContainer;
import com.example.app.domain.GetStockInfo;
import com.example.app.domain.MarketIndex;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class IndexActorTest  {

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
    public void canReceiveData() throws Exception {

        new JavaTestKit(system) {{

            final ActorRef subject = system.actorOf(Props.create(IndexActor.class), "indexActor");

            subject.tell(new GBCEIndexContainer("CA",0.79), getRef());
            subject.tell(new GBCEIndexContainer("CA",0.79), getRef());
            subject.tell(new GBCEIndexContainer("CA",0.79), getRef());
            subject.tell(new GBCEIndexContainer("CA",0.79), getRef());
            subject.tell(new GetStockInfo("CA"), getRef());


            while (!msgAvailable()) Thread.sleep(100);
            MarketIndex msg1 = expectMsgAnyClassOf(MarketIndex.class);
            assertEquals(msg1.getIndexValue(),0.79,0.1);


        }};


    }
}