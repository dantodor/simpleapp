package com.example.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Dispatcher actor is the main class of the system
 * It manages a pool of Actors, the number of them been configured by the user
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class DispatcherActor extends UntypedActor {
    
    //how many child actors to spawn
    private int howManyClients;
    //the seed for the time interval between two transactions
    private int interval;
    //the server and the port to which we send the transactions
    private String serverName;
    private String serverPort;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());


    public DispatcherActor(int howManyClients, int interval, String serverName, String serverPort) {
        this.howManyClients = howManyClients;
        this.interval = interval;
        this.serverName = serverName;
        this.serverPort = serverPort;
        LOG.info("Dispatcher created");
    }

    @Override
    public void onReceive (Object message) {
        if (message instanceof String) {
            initChilds();
        }
    }

    private void initChilds() {
        ActorRef tmp;
        for ( int i=0; i<howManyClients;i++){
            tmp = getContext().actorOf(Props.create(WorkerActor.class,i,interval,serverName,serverPort));
            //watch each actors, so if it dies it is restarted by the dispatcher
            getContext().watch(tmp);
        }
        //let the workers do their job for a while before starting asking questions
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for ( int i=0; i<howManyClients;i++){
            tmp = getContext().actorOf(Props.create(AskerActor.class,i,interval,serverName,serverPort));
            //watch each actors, so if it dies it is restarted by the dispatcher
            getContext().watch(tmp);
        }
    }

}
