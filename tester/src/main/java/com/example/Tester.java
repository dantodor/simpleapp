package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.example.actors.DispatcherActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * The entry point in the test application
 * It sets up the actor system used to generate and post simulated transactions to the server
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class Tester
{
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());



    public static void main( String[] args )
    {
        Tester t = new Tester();

        t.init(args);
    }

    public void init(String[] args) {
        //read the configuration
        Config config = readConfig(args);

        setupActors(config);

    }


    private Config readConfig(String[] args){
        String configFilePath;

        if(args.length == 0) {
            configFilePath = "./tester.conf";
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



    private void setupActors(Config config) {

        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        // create the dispatcher
        ActorRef dispatcherActor = system.actorOf(Props.create(DispatcherActor.class,config.getInt("tester.workers"),
                config.getInt("tester.interval"),config.getString("server.hostname"),config.getString("server.port")),
                "dispatcherActor");
        //give him a go signal
        dispatcherActor.tell("go",ActorRef.noSender());

    }




}
